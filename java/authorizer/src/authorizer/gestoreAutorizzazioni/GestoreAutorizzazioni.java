package authorizer.gestoreAutorizzazioni;

import authorizer.gestoreRisorse.GestoreRisorse;
import authorizer.gestoreRisorse.ResourceException;
import authorizer.gestoreToken.GestoreToken;
import authorizer.Server;
import jsonrpc.Member;
import jsonrpc.StructuredMember;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class GestoreAutorizzazioni {

    private static GestoreAutorizzazioni instance = null;
    private static HashMap<String,Autorizzazione> autorizzazioni = new HashMap<>();

    //Singleton Design pattern
    private GestoreAutorizzazioni(){}

    public static GestoreAutorizzazioni getInstance(){
        return (instance==null) ? new GestoreAutorizzazioni() : instance;
    }

    private String genera_chiave_unica(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String creaAutorizzazione(String nomeUtente,int livello, Date scadenza) throws AuthorizationException {
        if (scadenza.before(new Date())) {
            throw new InvalidParameterException("Data scadenza già passata");
        }

        if (verificaEsistenzaAutorizzazione(nomeUtente) != null) {
            throw new AuthorizationException("Utente già autorizzato");
        }

        String key = genera_chiave_unica();
        Autorizzazione auth = new Autorizzazione(nomeUtente,livello,scadenza);
        autorizzazioni.put(key,auth);

        if (Server.isTest()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Generata autorizzazione").append(System.lineSeparator());
            sb.append("Key: ").append(key);
            sb.append(" - Utente: ").append(nomeUtente);
            sb.append(" - Liv: ").append(livello);
            sb.append(" - Scadenza: ").append(Server.DATE.format(scadenza));
            System.out.println(sb.toString());
        }

        return key;
    }

    //restituisce se era presente un'autorizzazione con quella chiave
    public boolean revocaAutorizzazione(String chiave){
        boolean removed = autorizzazioni.remove(chiave) != null;
        if (removed) {
            //se è stata revocata un'autorizzazione vengono rimossi anche tutti i token relativi
            GestoreToken.getInstance().cancellaTokenChiave(chiave);
        }
        if (Server.isTest()) {
            if (removed) {
                System.out.println("Autorizzazione " + chiave + " revocata");
            } else {
                System.out.println(chiave + " inesistente");
            }
        }
        return removed;
    }

    public String verificaEsistenzaAutorizzazione(String nomeUtente){
        for (HashMap.Entry<String, Autorizzazione> entry : autorizzazioni.entrySet()) {
            if(entry.getValue().getUtente().equals(nomeUtente)){
                if (Server.isTest()) {
                    System.out.println("Autorizzazione per l'utente " + nomeUtente + ": " + entry.getKey());
                }
                return entry.getKey();
            }
        }

        if (Server.isTest()) {
            System.out.println("Nessuna autorizzazione per l'utente " + nomeUtente);
        }
        return null;
    }

    public enum Validity {VALID, KEY_NON_EXISTENT, EXPIRED, INSUFFICIENT_LEVEL, RESOURCE_NON_EXISTENT};

    public Validity verificaValiditaAutorizzazione(String chiave, int idRisorsa) {
        Autorizzazione value = autorizzazioni.get(chiave);

        if (value == null){ //Se non è presente nessuna autorizzazione corrispondente la chiave non è valida
            if (Server.isTest()) {
                System.out.println(chiave + " non esistente");
            }
            return Validity.KEY_NON_EXISTENT;
        }else{
            try {
                Date today = new Date();
                if (today.after(value.getScadenza())) {
                    if (Server.isTest()) {
                        System.out.println(chiave + " scaduta");
                    }
                    return Validity.EXPIRED;
                } else if (GestoreRisorse.getInstance().getLivelloRisorsa(idRisorsa) <= value.getLivello()) {
                    if (Server.isTest()) {
                        System.out.println(chiave + " valida per risorsa " + idRisorsa);
                    }
                    return Validity.VALID;
                } else {
                    if (Server.isTest()) {
                        System.out.println("Livello autorizzazione non sufficiente per risorsa " + idRisorsa);
                    }
                    return Validity.INSUFFICIENT_LEVEL;
                }
            } catch (ResourceException e) {
                //idRisorsa non esistente: autorizzazione non valida
                if (Server.isTest()) {
                    System.out.println("Risorsa " + idRisorsa + " non esistente");
                }
                return Validity.RESOURCE_NON_EXISTENT;
            }
        }
    }

    public Member getState() {
        ArrayList<Member> auths = new ArrayList<>();
        for (HashMap.Entry<String, Autorizzazione> a : autorizzazioni.entrySet()) {
            HashMap<String, Member> autValues = new HashMap<>();
            autValues.put("Key", new Member(a.getKey()));
            autValues.put("Utente", new Member(a.getValue().getUtente()));
            autValues.put("Livello", new Member(a.getValue().getLivello()));
            autValues.put("Scadenza", new Member(Server.DATE.format(a.getValue().getScadenza())));
            auths.add(new Member(new StructuredMember(autValues)));
        }
        if (auths.size()==0)
            return new Member();
        else
            return new Member(new StructuredMember(auths));
    }
}
