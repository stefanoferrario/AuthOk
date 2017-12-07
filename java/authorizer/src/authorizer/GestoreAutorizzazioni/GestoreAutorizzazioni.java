package authorizer.GestoreAutorizzazioni;

import authorizer.GestoreAutorizzazioni.Autorizzazione;
import authorizer.GestoreToken.GestoreToken;
import authorizer.GestoreRisorse.GestoreRisorse;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

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

    public String creaAutorizzazione(String nomeUtente,int livello,Date scadenza){

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

    public void revocaAutorizzazione(String chiave){

        GestoreToken.getInstance().cancellaTokenChiave(chiave);
        autorizzazioni.remove(chiave); //String key = autorizzazioni.keySet().iterator().next(); --> Questo per testare la cancellazione
    }

    public boolean verificaEsistenzaAutorizzazione(String nomeUtente){

        for (Autorizzazione value : autorizzazioni.values()) {

            if(value.getUtente().equals(nomeUtente)){
                return true;
            }
        }
        return false;
    }

    public boolean verificaValiditaAutorizzazione(String chiave,int idRisorsa) throws Exception{
        // String key = autorizzazioni.keySet().iterator().next(); chiave = key;

        Autorizzazione value = autorizzazioni.get(chiave);

        if (value == null){ //Se non è presente nessuna autorizzazione sollevo una eccezione
            throw new Exception();
        }else{
            Date today = new Date();
            int level = GestoreRisorse.getInstance().getLivelloRisorsa(idRisorsa);
            return (today.before(value.getScadenza()) && level <= value.getLivello());
        }
    }

    //Necessario al gestore token per poter verificare il livello a cui è possibile accedere.
    public int getLivelloAutorizzazione(String chiave){

        return autorizzazioni.get(chiave).getLivello();
    }

    public static void main(String args[]) {

        GestoreAutorizzazioni auth =  GestoreAutorizzazioni.getInstance();

        auth.creaAutorizzazione("Tay",2,new Date());
        auth.creaAutorizzazione("Anna",4,new Date());
        auth.creaAutorizzazione("Corti",4,new Date());

        boolean esistenza = auth.verificaEsistenzaAutorizzazione("Anna");
        auth.revocaAutorizzazione("");

        try{
            boolean valid = auth.verificaValiditaAutorizzazione(" ",2);

        }catch (Exception e){
            System.out.println("Autorizzazione non trovata..");
        }

     }
}
