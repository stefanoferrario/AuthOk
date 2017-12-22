package authorizer.GestoreToken;

import authorizer.GestoreAutorizzazioni.AuthorizationException;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import authorizer.Server;
import jsonrpc.Member;
import jsonrpc.StructuredMember;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import static authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni.Validity.VALID;

public class GestoreToken {
    private static final long TOKEN_DURATION = 24*60*60*1000; //24 ore in millisecs
    private static final long TEST_TOKEN_DURATION = 3*60*1000; //3 minuti in millisecs
    private static final DateFormat DATE_HOUR = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    private static GestoreToken instance = null;
    private HashMap<String, Token> tokens = new HashMap<>();

    //Singleton design pattern
    private GestoreToken() {}

    public static GestoreToken getInstance() {
        if(instance == null) {
            instance = new GestoreToken();
        }
        return instance;
    }

    //Metodo per la creazione di un nuovo token
    public String creaToken (String chiave, int idRisorsa) throws TokenException {
        switch (GestoreAutorizzazioni.getInstance().verificaValiditaAutorizzazione(chiave, idRisorsa)) {
            case EXPIRED:
                throw new TokenException("Chiave scaduta");
            case KEY_NON_EXISTENT:
                throw new TokenException("Chiave inesistente");
            case INSUFFICIENT_LEVEL:
                throw new TokenException("Livello di autorizzazione non sufficiente");
            case RESOURCE_NON_EXISTENT:
                throw new TokenException("Risorsa non trovata");
            case VALID:
                String token = UUID.randomUUID().toString(); //generazione codice random
                Token newToken = new Token(chiave, idRisorsa, System.currentTimeMillis());
                tokens.put(token, newToken);

                if (Server.isTest()) {
                    System.out.println("Token generato: " + token);
                }

                return token;
            default: throw new TokenException();
        }
    }

    //Verifica validità del token da parte della risorsa che ritorna il tempo di validità restante.
    public long verificaToken(String aString, int idRisorsa) {
        Token temp= tokens.get(aString);
        if (temp != null) {
            if (temp.getIdRisorsa() != idRisorsa) {
                if (Server.isTest()) {
                    System.out.println("Token " + aString + " relativo alla risorsa errata(" + idRisorsa + ")");
                }
                return 0;
            }

            if (GestoreAutorizzazioni.getInstance().verificaValiditaAutorizzazione(temp.getChiave(), temp.getIdRisorsa()) != VALID) {
                if (Server.isTest()) {
                    System.out.println("Token errato");
                }
                return 0;
            }

            long tokenDuration = Server.isTest() ? TEST_TOKEN_DURATION : TOKEN_DURATION;
            long tempoRestante = tokenDuration - (System.currentTimeMillis() - temp.getData().getTime());
            if (tempoRestante < 0) {
                System.out.println("Il token relativo alla risorsa " + temp.getIdRisorsa() + " è scaduto");
                return 0; //token scaduto
            } else {
                String t = String.format("%02d : %02d : %02d",
                        TimeUnit.MILLISECONDS.toHours(tempoRestante),
                        TimeUnit.MILLISECONDS.toMinutes(tempoRestante) -
                                TimeUnit.MILLISECONDS.toMinutes(TimeUnit.MILLISECONDS.toHours(tempoRestante)),
                        TimeUnit.MILLISECONDS.toSeconds(tempoRestante) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tempoRestante))
                );
                System.out.println("Tempo di validità restante del token relativo alla risorsa " + temp.getIdRisorsa() + ": " + t);
                return tempoRestante;
            }
        }
        if (Server.isTest()) {
            System.out.println("Token inesistente");
        }
        return 0; //token inesistente
    }

    public void cancellaTokenScaduti(){
        /*Iterator<HashMap.Entry<String, Token>> iterator = tokens.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<String, Token> entry = iterator.next();
            if ((System.currentTimeMillis()-entry.getValue().getData().getTime())>TOKEN_DURATION) {
                iterator.remove();
            }
        }*/
        boolean r =tokens.entrySet().removeIf((HashMap.Entry<String, Token> p) -> System.currentTimeMillis() - p.getValue().getData().getTime() > TOKEN_DURATION);
        if (Server.isTest()) {
            System.out.println( r ? "Rimossi token scaduti" : "Nessun token scaduto rimosso");
        }
    }
    public void cancellaTokenChiave(String chiave){
        /*Iterator<HashMap.Entry<String, Token>> iterator = tokens.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<String, Token> entry = iterator.next();
            if (chiave.equals(entry.getValue().getChiave())) {
                iterator.remove();
            }
        }*/
        boolean r = tokens.entrySet().removeIf((HashMap.Entry<String, Token> p) -> p.getValue().getChiave().equals(chiave));
        if (Server.isTest()) {
            System.out.println( r ? "Rimossi token relativi alla chiave " + chiave : "Nessun token relativo alla chiave " + chiave + " rimosso");
        }
    }


    public Member getState() {
        ArrayList<Member> tokensList = new ArrayList<>();
        for (HashMap.Entry<String, Token> t : tokens.entrySet()) {
            HashMap<String, Member> tokValues = new HashMap<>();
            tokValues.put("Token", new Member(t.getKey()));
            tokValues.put("Chiave", new Member(t.getValue().getChiave()));
            tokValues.put("ID Risorsa", new Member(t.getValue().getIdRisorsa()));
            tokValues.put("Data ora concessione", new Member(DATE_HOUR.format(t.getValue().getData())));
            tokensList.add(new Member(new StructuredMember(tokValues)));
        }
        if (tokensList.size() == 0)
            return new Member();
        else
            return new Member(new StructuredMember(tokensList));
    }

    public long getTokenDuration() {
        return Server.isTest() ? TEST_TOKEN_DURATION : TOKEN_DURATION;
    }

    public static void main(String [] args) throws TokenException, AuthorizationException {

        //GestoreToken gestoreToken=instance.getInstance();
        //gestoreToken.creaToken(GestoreAutorizzazioni.getInstance().creaAutorizzazione("Stefano",9 ,new Date()),23492);


    }


}
