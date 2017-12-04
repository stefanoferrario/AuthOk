package authorizer.GestoreToken;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class GestoreToken {

    //Singleton design pattern
    private static GestoreToken instance = null;
    private GestoreToken() {
        // Exists only to defeat instantiation.
    }
    public static GestoreToken getInstance() {
        if(instance == null) {
            instance = new GestoreToken();
        }
        return instance;
    }

    private HashMap<String, Token> tokens;


    public String creaToken (String chiave, int idRisorsa){
        String stringaToken=null;
        boolean chiaveValida=false;
        boolean liveloSufficiente=false;
        /// /verifica della Chiave
        //verifica del livello della risorsa

        if (chiaveValida && liveloSufficiente){
            stringaToken=instance.generaCodice(20,true);
            Token newToken= new Token(stringaToken,idRisorsa, (System.currentTimeMillis()));
            tokens.put(stringaToken,newToken);
        }

        return stringaToken;
    }

    //Verifica validità del token da parte della risorsa che ritorna il tempo di validità restante.

    public Date verificaToken(String aString, int idRisorsa){
        Date tempoRestante=null;

        return tempoRestante;
    }

    public void cancellaTokenscaduti(){

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
        long timeStamp = rand.nextInt(20);
        stringaRandom += timeStamp;

        // restituisco la stringa generata
        return stringaRandom;
    }


    public static void main(String [] args){

        GestoreToken gestoreToken=instance.getInstance();
        System.out.println(instance.creaToken("apple.StefanoTestClea",23492));

    }


}