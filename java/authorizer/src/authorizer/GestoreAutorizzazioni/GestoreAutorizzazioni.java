package authorizer.GestoreAutorizzazioni;

import authorizer.GestoreRisorse.GestoreRisorse;
import authorizer.GestoreRisorse.ResourceException;
import authorizer.GestoreToken.GestoreToken;
import authorizer.Server;
import jsonrpc.Member;
import jsonrpc.StructuredMember;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class GestoreAutorizzazioni {

    private static GestoreAutorizzazioni instance = null;
    private static HashMap<String,Autorizzazione> autorizzazioni = new HashMap<>();

    //Singleton Design pattern
    private GestoreAutorizzazioni(){
    }

    public static GestoreAutorizzazioni getInstance(){
        return (instance==null) ? new GestoreAutorizzazioni() : instance;
    }

    private String genera_chiave_unica(){

        return UUID.randomUUID().toString().replace("-", "");
        
    }

    public String creaAutorizzazione(String nomeUtente,int livello, String scadenza) throws ParseException, AuthorizationException {
        Date date = Server.DATE.parse(scadenza);
        if (date.before(new Date())) {
            throw new InvalidParameterException("Data scadenza già passata");
        }

        if (verificaEsistenzaAutorizzazione(nomeUtente) != null) {
            throw new AuthorizationException("Utente già autorizzato");
        }

        String key = genera_chiave_unica();

        Autorizzazione auth = new Autorizzazione(nomeUtente,livello,date);
        autorizzazioni.put(key,auth);

        return key;


    }

    //restituisce se era presente un'autorizzazione con quella chiave
    public boolean revocaAutorizzazione(String chiave){
        GestoreToken.getInstance().cancellaTokenChiave(chiave);
        return autorizzazioni.remove(chiave) != null;
    }

    public String verificaEsistenzaAutorizzazione(String nomeUtente){

        for (HashMap.Entry<String, Autorizzazione> entry : autorizzazioni.entrySet()) {

            if(entry.getValue().getUtente().equals(nomeUtente)){
                return entry.getKey();
            }
        }
        return null;
    }

    public enum Validity {VALID, KEY_NON_EXISTENT, EXPIRED, INSUFFICIENT_LEVEL, RESOURCE_NON_EXISTENT};

    public Validity verificaValiditaAutorizzazione(String chiave, int idRisorsa) {
        Autorizzazione value = autorizzazioni.get(chiave);

        if (value == null){ //Se non è presente nessuna autorizzazione corrispondente la chiave non è valida
            return Validity.KEY_NON_EXISTENT;
        }else{
            try {
                int level = GestoreRisorse.getInstance().getLivelloRisorsa(idRisorsa);
                Date today = new Date();
                if (today.after(value.getScadenza()))
                    return Validity.EXPIRED;
                if (level <= value.getLivello())
                    return Validity.VALID;
                else
                    return Validity.INSUFFICIENT_LEVEL;

            } catch (ResourceException e) {
                //idRisorsa non esistente: autorizzazione non valida
                return Validity.RESOURCE_NON_EXISTENT;
            }
        }
    }

    //Necessario al gestore token per poter verificare il livello a cui è possibile accedere.
    public int getLivelloAutorizzazione(String chiave){
        return autorizzazioni.get(chiave).getLivello();
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
