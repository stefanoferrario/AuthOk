package authorizer.GestoreToken;

import java.text.SimpleDateFormat;
import java.util.*;

import authorizer.GestoreAutorizzazioni.AuthorizationException;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import authorizer.GestoreRisorse.GestoreRisorse;


public class GestoreToken {
    private static final long TOKEN_DURATION = 82800000;
    private static GestoreToken instance = null;
    private HashMap<String, Token> tokens = null;


    //Singleton design pattern
    private GestoreToken() {
        tokens= new HashMap<String,Token>();
    }
    public static GestoreToken getInstance() {
        if(instance == null) {
            instance = new GestoreToken();
        }
        return instance;
    }

    //Metodo per la creazione di un nuovo token
    public String creaToken (String chiave, int idRisorsa) throws TokenException {
        String stringaToken = null;
        boolean chiaveValida = false;
        int livelloRisorsa = 0;

        int livelloChiave = 0;
        //verifica della Chiave

        chiaveValida = GestoreAutorizzazioni.getInstance().verificaValiditaAutorizzazione(chiave, idRisorsa);
        if (!chiaveValida) {
            throw new TokenException("La chiave fornita non è valida");
        } else {
            //Se la chiave è valida estrae il livello dell'autorizzazione
            livelloChiave = GestoreAutorizzazioni.getInstance().getLivelloAutorizzazione(chiave);
        }
        //Analisi della Risorsa
        if (GestoreRisorse.getInstance().contieneRisorsa(idRisorsa)) {
            livelloRisorsa = GestoreRisorse.getInstance().getLivelloRisorsa(idRisorsa);
        } else {
            throw new TokenException("ID Risorsa fornito non valido");
        }

        if (livelloRisorsa > livelloChiave) {
            throw new TokenException("Livello autorizzazione insufficiente");
        }
        UUID token= UUID.randomUUID(); //generazione codice random
        Token newToken = new Token(chiave, idRisorsa, System.currentTimeMillis());
        System.out.println("Token generato: " + token.toString());
        tokens.put(token.toString(), newToken);

        return token.toString();
    }

    //Verifica validità del token da parte della risorsa che ritorna il tempo di validità restante.

    public long verificaToken(String aString, int idRisorsa) {
        Token temp= tokens.get(aString);
        if (temp != null) {
            if (System.currentTimeMillis() - temp.getData().getTime() > TOKEN_DURATION) {
                System.out.println("Il token relativo alla risorsa " + temp.getIdRisorsa() + " è scaduto");
                return 0; //token scaduto
            } else {
                long tempoRestante = TOKEN_DURATION - (System.currentTimeMillis() - temp.getData().getTime());
                Date _tempoRestante = new Date(tempoRestante);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String risultato = sdf.format(_tempoRestante);
                System.out.println("Tempo di validità restante del token relativo alla risorsa " + temp.getIdRisorsa() + ": " + risultato);
                return tempoRestante;
            }
        }
        return 0; //token inesistente
    }

    public void cancellaTokenScaduti(){
        Iterator<HashMap.Entry<String, Token>> iterator = tokens.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<String, Token> entry = iterator.next();
            if ((System.currentTimeMillis()-entry.getValue().getData().getTime())>TOKEN_DURATION) {
                iterator.remove();
            }
        }
    }
    public void cancellaTokenChiave(String chiave){
        Iterator<HashMap.Entry<String, Token>> iterator = tokens.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<String, Token> entry = iterator.next();
            if (chiave==entry.getValue().getChiave()) {
                iterator.remove();
            }
        }
    }

    public static void main(String [] args) throws TokenException, AuthorizationException {

        GestoreToken gestoreToken=instance.getInstance();
        gestoreToken.creaToken(GestoreAutorizzazioni.getInstance().creaAutorizzazione("Stefano",9 ,new Date()),23492);


    }


}
