package authorizer.GestoreToken;

import java.text.SimpleDateFormat;
import java.util.*;
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
        stringaToken = instance.generaCodice(20, true);
        Token newToken = new Token(chiave, idRisorsa, System.currentTimeMillis());
        System.out.println("Token generato: " + stringaToken);
        tokens.put(stringaToken, newToken);

        return stringaToken;
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


    //Generazione codice del token
    //Fino a quando il numero dei caratteri non è maggiore o uguale a quello desiderato si aggiunge un carattere,
    //un numero e un simbolo casuali. In caso di stringa troppo lunga si taglia

    private String generaCodice(int numeroCaratteriRandom,boolean conSpeciali) {

        // array delle lettere
        String[] Caratteri = {"a", "b", "c", "d", "e", "f", "g", "h", "i",
                "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "z", "y",
                "j", "k", "x", "w", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "Z", "Y",
                "J", "K", "X", "W", "à", "è", "é", "ì", "ò", "ù"};

        // array dei numeri
        String[] Numeri = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

        // array dei caratteri speciali
        String[] Speciali = {"!", "£", "$", "%", "&", "@", "*", ",", "_",
                "-", "#", ";", "^", "\\", "/", ":", ".", "+", "§", "?", "ç"};

        // creo l'oggetto random
        Random rand = new Random();


        // ottengo la lunghezza di ogni array
        int lunghezzaCaratteri = Caratteri.length;
        int lunghezzaNumeri = Numeri.length;
        int lunghezzaSpeciali = Speciali.length;

        // istanzio la variabile che conterrà il prodotto finale
        String stringaRandom = "";

        while (stringaRandom.length() < numeroCaratteriRandom) {

            // ottengo un elemento casuale per ogni array
            int c = rand.nextInt(lunghezzaCaratteri);
            int n = rand.nextInt(lunghezzaNumeri);
            int s = rand.nextInt(lunghezzaSpeciali);

            // aggiungo una lettera casuale
            stringaRandom += Caratteri[c];
            // aggiungo un numero random
            stringaRandom += Numeri[n];
            // se l'opzione conSpeciali è true aggiungo un carattere speciale
            if (conSpeciali) {
                stringaRandom += Speciali[s];
            }
        }

        // se la stringa generata dovesse superare il numero di caratteri
        // richiesto, la taglio.
        if (stringaRandom.length() > numeroCaratteriRandom) {
            stringaRandom = stringaRandom.substring(0, numeroCaratteriRandom);
        }
        // recupero il timestamp
        long time = System.currentTimeMillis(); //millisecondi atuali
        rand.setSeed(time);
        long timeStamp = rand.nextInt(100);
        stringaRandom += timeStamp;

        // restituisco la stringa generata
        return stringaRandom;
    }


    public static void main(String [] args) throws TokenException {

        GestoreToken gestoreToken=instance.getInstance();
        gestoreToken.creaToken(GestoreAutorizzazioni.getInstance().creaAutorizzazione("Stefano",9 ,new Date()),23492);


    }


}
