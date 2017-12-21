package authorizer;

import authorizer.GestoreAutorizzazioni.AuthorizationException;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import authorizer.GestoreRisorse.GestoreRisorse;
import authorizer.GestoreRisorse.ResourceException;
import authorizer.GestoreRisorse.ResourceTypes;
import authorizer.GestoreToken.GestoreToken;
import authorizer.GestoreToken.TokenException;
import jsonrpc.Error;
import jsonrpc.*;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    private static Server instance = null;
    private GestoreToken tokenManager;
    private GestoreRisorse resourceManager;
    private GestoreAutorizzazioni authManager;
    private IServer serverJsonRpc;
    private static boolean testEnabled = false;
    private static ReentrantLock lock = new ReentrantLock();
    public static final short PORT = 5001;
    public static final DateFormat DATE = new SimpleDateFormat("dd/MM/yyyy");

    private Server() {
        tokenManager = GestoreToken.getInstance();
        resourceManager = GestoreRisorse.getInstance();
        authManager = GestoreAutorizzazioni.getInstance();
        serverJsonRpc = new jsonrpc.Server(PORT);

        if (testEnabled) {
            System.out.println("Impostazioni di test abilitate");
            System.out.println("Impostata durata token di 3 minuti");
            try {
                resourceManager.addRisorsa(1, 4, ResourceTypes.LINK);
                resourceManager.addRisorsa(2, 6, ResourceTypes.LINK);
                resourceManager.addRisorsa(3, 1, ResourceTypes.FIBO);
                resourceManager.addRisorsa(4, 3, ResourceTypes.DICE);
                System.out.println("Caricate risorse di prova");
            } catch (ResourceException e) {
                e.printStackTrace();
            }
        }
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private static void setTest(boolean isTest) {testEnabled = isTest;}
    public static boolean isTest() {return testEnabled;}

    private void receive() {
        ArrayList<Request> reqs = serverJsonRpc.receive();
        ArrayList<Response> resps = new ArrayList<>();
        lock.lock();
        for (Request req : reqs) {
            Member result = null;
            Error error = null;
            try {
                result = selectMethod(Methods.valueOf(req.getMethod()), req.getParams());
            } catch (InvalidParameterException e) {
                error = new Error(Error.Errors.INVALID_PARAMS, new Member(e.getMessage()));
            } catch (IllegalArgumentException e) {
                //lanciata dal Methods.valueOf() se la stringa non corrisponde a un metodo
                error = new Error(Error.Errors.METHOD_NOT_FOUND);
            } catch (TokenException e) {
                error = new Error("Server error", -32001, new Member(e.getMessage()));
            } catch (AuthorizationException e) {
                error = new Error("Server error", -32002, new Member(e.getMessage()));
            } catch (ResourceException e) {
                error = new Error("Server error", -32003, new Member(e.getMessage()));
            } finally {
                if (!req.isNotify()) {
                    if (result != null) {
                        resps.add(new Response(req.getId(), result));
                    } else {
                        resps.add(new Response(req.getId(), error));
                    }
                }
                if (isTest()) {
                    System.out.println(System.lineSeparator()); //per separare le azioni fatte dal server ad ogni richiesta
                }
            }
        }
        lock.unlock();
        try {
            //una richiesta singola può essere inviata come un arraylist di dimensione 1
            serverJsonRpc.reply(resps);
        } catch (JSONRPCException e) {
            System.out.println(e.getMessage());
        }
    }

    private Member selectMethod(Methods method, StructuredMember params) throws TokenException, AuthorizationException, ResourceException {
        ArrayList<Member> p = new ArrayList<>();
        if (params!=null) {//i parametri sono opzionali
            try {
                p = params.getList();
            } catch (ClassCastException c) {
                throw new InvalidParameterException("Not a parameters list");
            }
        }

        if (p.size() != method.getParamsNum()) {throw new InvalidParameterException("Wrong parameters number");}

        try {
            switch (method) {
                case CREA_TOKEN:
                    String token = tokenManager.creaToken(p.get(0).getString(), p.get(1).getInt());
                    return new Member(token);
                case VERIFICA_TOKEN:
                    long time = tokenManager.verificaToken(p.get(0).getString(), p.get(1).getInt());
                    return new Member(time);
                case CREA_AUTORIZZAZIONE:
                    return new Member(authManager.creaAutorizzazione(p.get(0).getString(), p.get(1).getInt(), p.get(2).getString()));
                case VERIFICA_ESISTENZA_AUTORIZZAZIONE:
                    String key = authManager.verificaEsistenzaAutorizzazione(p.get(0).getString());
                    ArrayList<Member> result = new ArrayList<>();
                    result.add(new Member(key != null));
                    if (key != null) {
                        result.add(new Member(key));
                    } else {
                        result.add(new Member());
                    }
                    return new Member(new StructuredMember(result));
                case REVOCA_AUTORIZZAZIONE:
                    return new Member(authManager.revocaAutorizzazione(p.get(0).getString()));
                case CREA_RISORSA:
                    resourceManager.addRisorsa(p.get(0).getInt(), p.get(1).getInt(), ResourceTypes.valueOf(p.get(2).getString()));
                    return new Member(true); //risultato a buon fine
                case MODIFICA_ID_RISORSA:
                    resourceManager.modificaIDRisorsa(p.get(0).getInt(), p.get(1).getInt());
                    return new Member(true);
                case MODIFICA_LIV_RISORSA:
                    resourceManager.modificaLivRisorsa(p.get(0).getInt(), p.get(1).getInt());
                    return new Member(true);
                case CANCELLA_RISORSA:
                    return new Member(resourceManager.cancellaRisorsa(p.get(0).getInt()));
                case SERVER_STATE:
                    return getServerState(); //non richiesta da specifica. server a controllare il funzionamento del progetto
            }
        } catch (ClassCastException e) {
            throw new InvalidParameterException("Wrong parameter type: " + e.getMessage());
        } catch (ParseException e) {
            throw new InvalidParameterException("Invalid date format: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // chiamata dalla ResourceTypes.valueOf() se stringa non valida
            throw new InvalidParameterException(e.getMessage());
        }

        throw new IllegalArgumentException(); //non viene mai chiamata ma deve esserci un ritorno per ogni ramo di esecuzione
    }

    private Member getServerState() {
        ArrayList<Member> states = new ArrayList<>();
        states.add(resourceManager.getState());
        states.add(authManager.getState());
        states.add(tokenManager.getState());
        return new Member(new StructuredMember(states));
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String args[]) {
        if (args.length > 0) {
            if (args.length > 1 || !args[0].equals("test")) {
                System.out.println("Invalid arguments");
                return;
            } else {
                setTest(true);
            }
        }

        Server s;
        try {
            s = Server.getInstance();
        } catch (UnsupportedOperationException e) {
            //porta occupata
            System.out.println(e.getMessage());
            return;
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
                lock.lock();
                if (isTest()) {
                    System.out.println("Cancellazione automatica token scaduti");
                }
                GestoreToken.getInstance().cancellaTokenScaduti();
                lock.unlock();
            }
        }, s.tokenManager.getTokenDuration(), s.tokenManager.getTokenDuration());//ogni 3m/24h cancella i token per non occupare inutilmente la memoria
        // non è necessario cancellare i token nell'esatto momento in cui scadono perché sono comunque invalidi

        System.out.println("In ascolto...");
        while (true) {
            s.receive();
            if (isTest()) {
                //se modalità test stampa dopo ogni richiesta eseguita, altrimenti solo la prima volta
                System.out.println("In ascolto...");
            }
        }
    }
}