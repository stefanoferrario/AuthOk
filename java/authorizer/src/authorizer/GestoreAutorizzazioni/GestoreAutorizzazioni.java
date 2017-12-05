package authorizer.GestoreAutorizzazioni;

import authorizer.GestoreAutorizzazioni.Autorizzazione;
import java.util.Date;
import java.util.HashMap;
//import java.util.UUID;

class GestoreAutorizzazioni {
    private static GestoreAutorizzazioni instance = null;

    private HashMap<String,Autorizzazione> autorizzazioni = null;

    //Singleton Design pattern
    private GestoreAutorizzazioni(){
        this.autorizzazioni = new HashMap<String, Autorizzazione>();
    }

    static GestoreAutorizzazioni getInstance(){
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

    void revocaAutorizzazione(String nomeUtente){
        autorizzazioni.remove(nomeUtente);
    }

    boolean verificaEsistenzaAutorizzazione(String nomeUtente){

        return autorizzazioni.containsKey(nomeUtente);

    }

}
