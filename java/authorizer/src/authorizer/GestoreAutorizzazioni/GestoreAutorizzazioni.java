package authorizer.GestoreAutorizzazioni;

import authorizer.GestoreRisorse.ResourceException;
import authorizer.GestoreToken.GestoreToken;
import authorizer.GestoreRisorse.GestoreRisorse;
import authorizer.MethodsUtils;
import jsonrpc.Member;
import jsonrpc.StructuredMember;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static authorizer.MethodsUtils.DATE_FORMAT;

public class GestoreAutorizzazioni {

    private static GestoreAutorizzazioni instance = null;
    private HashMap<String,Autorizzazione> autorizzazioni = null;

    //Singleton Design pattern
    private GestoreAutorizzazioni(){
        autorizzazioni = new HashMap<String, Autorizzazione>();
    }

    public static GestoreAutorizzazioni getInstance(){
        return (instance==null) ? new GestoreAutorizzazioni() : instance;
    }

    private String genera_chiave_unica(int min_length,int max_length) throws Exception{

        if (min_length > max_length)
            throw new Exception();
        else{
            Random rand = new Random();
            //Genera un numero tra min_length e max_length
            int  n = rand.nextInt(max_length - min_length + 1) + min_length;

            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder strBuild = new StringBuilder();
            Random rnd = new Random();
            while (strBuild.length() < n) {
                int index = (int) (rnd.nextFloat() * chars.length()); // Prodotto tra i Float(range tra 0 e 1) e la lunghezza della stringa di caratteri
                strBuild.append(chars.charAt(index));
            }
            return strBuild.toString();
        }
    }

    public String creaAutorizzazione(String nomeUtente,int livello,Date scadenza) throws AuthorizationException {
        if (verificaEsistenzaAutorizzazione(nomeUtente) != null) {throw new AuthorizationException("Utente già autorizzato");}
        try{
            String key = genera_chiave_unica(5,15);

            Autorizzazione auth = new Autorizzazione(nomeUtente,livello,scadenza);
            autorizzazioni.put(key,auth);

            return key;

        }catch (Exception ex){
            System.out.println("Minima e massima lunghezza non corretti...");
            return "";
        }
    }

    //restituisce se era presente un'autorizzazione con quella chiave
    public boolean revocaAutorizzazione(String chiave){
        GestoreToken.getInstance().cancellaTokenChiave(chiave);
        return autorizzazioni.remove(chiave) != null; //String key = autorizzazioni.keySet().iterator().next(); --> Questo per testare la cancellazione
    }

    public String verificaEsistenzaAutorizzazione(String nomeUtente){

        for (HashMap.Entry<String, Autorizzazione> entry : autorizzazioni.entrySet()) {

            if(entry.getValue().getUtente().equals(nomeUtente)){
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean verificaValiditaAutorizzazione(String chiave,int idRisorsa) {
        // String key = autorizzazioni.keySet().iterator().next(); chiave = key;

        Autorizzazione value = autorizzazioni.get(chiave);

        if (value == null){ //Se non è presente nessuna autorizzazione non è valida
            return false;
        }else{
            Date today = new Date();
            try {
                int level = GestoreRisorse.getInstance().getLivelloRisorsa(idRisorsa);
                return (today.before(value.getScadenza()) && level <= value.getLivello());
            } catch (ResourceException e) {
                //idRisorsa non esistente: autorizzazione non valida
                return false;
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
            autValues.put("Scadenza", new Member(DATE_FORMAT.format(a.getValue().getScadenza())));
            auths.add(new Member(new StructuredMember(autValues)));
        }
        if (auths.size()==0)
            return new Member();
        else
            return new Member(new StructuredMember(auths));
    }

    public static void main(String args[]) {

        GestoreAutorizzazioni auth =  GestoreAutorizzazioni.getInstance();

        try {
            auth.creaAutorizzazione("Tay",2,new Date());
            auth.creaAutorizzazione("Anna",4,new Date());
            auth.creaAutorizzazione("Corti",4,new Date());

            String chiave = auth.verificaEsistenzaAutorizzazione("Anna");
            auth.revocaAutorizzazione("");


            boolean valid = auth.verificaValiditaAutorizzazione(" ",2);

        }catch (Exception e){
            System.out.println("Autorizzazione non trovata..");
        }

    }
}
