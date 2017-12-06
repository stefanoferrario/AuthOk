package authorizer.GestoreAutorizzazioni;

import authorizer.GestoreToken.GestoreToken;

import java.util.Date;
import java.util.HashMap;
//import java.util.UUID;

public class GestoreAutorizzazioni {
    private static GestoreAutorizzazioni instance = null;

    private HashMap<String,Autorizzazione> autorizzazioni = null;

    //Singleton Design pattern
    private GestoreAutorizzazioni(){
        this.autorizzazioni = new HashMap<String, Autorizzazione>();
    }

    public static GestoreAutorizzazioni getInstance(){
       return (instance==null) ? new GestoreAutorizzazioni() : instance;
    }

    String creaAutorizzazione(String nomeUtente,int livello,Date scadenza){

       if(this.verificaEsistenzaAutorizzazione(nomeUtente)){
            return "";
       }

       Autorizzazione auth = new Autorizzazione(nomeUtente,livello,scadenza);
       //String uniqueID = UUID.randomUUID().toString();
       autorizzazioni.put(nomeUtente,auth);

       return (nomeUtente+"-"+livello+"-"+scadenza);
    }

    public void revocaAutorizzazione(String nomeUtente){
        //Quando viene revocata un'autorizzazione devono essere cancellati anche i token ad essa collegati
        GestoreToken.getInstance().cancellaTokenChiave(nomeUtente);

        autorizzazioni.remove(nomeUtente);
    }

    public boolean verificaEsistenzaAutorizzazione(String nomeUtente){

        return autorizzazioni.containsKey(nomeUtente);

    }

    //Necessario al gestore token per poter verificare il livello a cui è possibile accedere.
    public Integer getLivelloAutorizzazione(String nomeUtente){
        return autorizzazioni.get(nomeUtente).getLivello();
    }

}
