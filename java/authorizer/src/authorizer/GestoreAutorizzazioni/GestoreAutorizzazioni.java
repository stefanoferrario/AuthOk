package authorizer.GestoreAutorizzazioni;

import java.util.HashMap;
import authorizer.GestoreAutorizzazioni.Autorizzazione;

class GestoreAutorizzazioni {
    private static GestoreAutorizzazioni instance = null;
    private HashMap<String,Autorizzazione> autorizzazioni = null;

    //Singleton Design pattern
    private GestoreAutorizzazioni(){
        autorizzazioni = new HashMap<String, Autorizzazione>();
    }

    static GestoreAutorizzazioni getInstance(){
       return (instance==null) ? new GestoreAutorizzazioni() : instance;
    }
}
