package authorizer.GestoreAutorizzazioni;

import authorizer.GestoreAutorizzazioni.Autorizzazione;
import java.util.Date;
import java.util.HashMap;

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

       return (nomeUtente+"-"+livello+"-"+scadenza);
    }
}
