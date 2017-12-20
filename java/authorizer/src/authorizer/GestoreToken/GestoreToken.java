package authorizer.GestoreToken;

import java.text.SimpleDateFormat;
import java.util.*;
import authorizer.GestoreAutorizzazioni.AuthorizationException;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import jsonrpc.Member;
import jsonrpc.StructuredMember;
import static authorizer.MethodsUtils.DATE_HOUR_FORMAT;

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
                System.out.println("Token generato: " + token);
                tokens.put(token, newToken);
                return token;
            default: throw new TokenException();
        }
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
        /*Iterator<HashMap.Entry<String, Token>> iterator = tokens.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<String, Token> entry = iterator.next();
            if ((System.currentTimeMillis()-entry.getValue().getData().getTime())>TOKEN_DURATION) {
                iterator.remove();
            }
        }*/
        tokens.entrySet().removeIf((HashMap.Entry<String, Token> p) -> System.currentTimeMillis() - p.getValue().getData().getTime() > TOKEN_DURATION);

    }
    public void cancellaTokenChiave(String chiave){
        /*Iterator<HashMap.Entry<String, Token>> iterator = tokens.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<String, Token> entry = iterator.next();
            if (chiave.equals(entry.getValue().getChiave())) {
                iterator.remove();
            }
        }*/
        tokens.entrySet().removeIf((HashMap.Entry<String, Token> p) -> p.getValue().getChiave().equals(chiave));
    }


    public Member getState() {
        ArrayList<Member> tokensList = new ArrayList<>();
        for (HashMap.Entry<String, Token> t : tokens.entrySet()) {
            HashMap<String, Member> tokValues = new HashMap<>();
            tokValues.put("Token", new Member(t.getKey()));
            tokValues.put("Chiave", new Member(t.getValue().getChiave()));
            tokValues.put("ID Risorsa", new Member(t.getValue().getIdRisorsa()));
            tokValues.put("Data ora concessione", new Member(DATE_HOUR_FORMAT.format(t.getValue().getData())));
            tokensList.add(new Member(new StructuredMember(tokValues)));
        }
        if (tokensList.size() == 0)
            return new Member();
        else
            return new Member(new StructuredMember(tokensList));
    }

    public static void main(String [] args) throws TokenException, AuthorizationException {

        GestoreToken gestoreToken=instance.getInstance();
        gestoreToken.creaToken(GestoreAutorizzazioni.getInstance().creaAutorizzazione("Stefano",9 ,new Date()),23492);


    }


}
