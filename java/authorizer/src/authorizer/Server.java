package authorizer;

import authorizer.GestoreRisorse.GestoreRisorse;
import authorizer.GestoreToken.GestoreToken;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import authorizer.GestoreToken.TokenException;
import jsonrpc.*;
import jsonrpc.IServer;
import jsonrpc.Error;
import jsonrpc.Member;
import jsonrpc.StructuredMember;
import jsonrpc.Request;
import jsonrpc.Response;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class Server {
    private Server instance = null;
    private GestoreToken tokenManager;
    private GestoreRisorse resourceManager;
    private GestoreAutorizzazioni authManager;
    private IServer serverJsonRpc;

    private Server() {
        tokenManager = GestoreToken.getInstance();
        resourceManager = GestoreRisorse.getInstance();
        authManager = GestoreAutorizzazioni.getInstance();
        serverJsonRpc = new jsonrpc.Server(MethodsUtils.PORT);
    }

    public Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private void receive() {
        ArrayList<Request> reqs = serverJsonRpc.receive();
        ArrayList<Response> resps = new ArrayList<>();

        for (Request req : reqs) {
            Member result = null;
            Error error = null;
            try {
                result = selectMethod(MethodsUtils.Methods.valueOf(req.getMethod()), req.getParams());
            } catch (InvalidParameterException e) {
                error = new Error(Error.Errors.INVALID_PARAMS, new Member(e.getMessage()));
            } catch (IllegalArgumentException e) {
                //lanciata dal Methods.valueOf() se la stringa non corrisponde a un metodo
                error = new Error(Error.Errors.METHOD_NOT_FOUND);
            } catch (TokenException e) {
                error = new Error("Server error", -32001, new Member(e.getMessage()));
            } finally {
                if (!req.isNotify()) {
                    if (result != null) {
                        resps.add(new Response(req.getId(), result));
                    } else {
                        resps.add(new Response(req.getId(), error));
                    }
                }
            }
        }

        try {
            //una richiesta singola può essere inviata come un arraylist di dimensione 1
            serverJsonRpc.reply(resps);
        } catch (JSONRPCException e) {
            System.out.println(e.getMessage());
        }
    }

    private Member selectMethod(MethodsUtils.Methods method, StructuredMember params) throws TokenException {
        ArrayList<Member> p = new ArrayList<>();
        try {
            if (params!=null) //i parametri sono opzionali
                p = params.getList();
        } catch (ClassCastException c) {
            throw new InvalidParameterException("Not a parameters list");
        }

        if (p.size() != method.getParamsNum()) {throw new InvalidParameterException("Wrong parameters number");}

        try {
            switch (method) {
                case CREA_TOKEN:
                    String token = tokenManager.creaToken(p.get(0).getString(), p.get(1).getInt());
                    return new Member(token);
                case VERIFICA_TOKEN:
                    //far restituire un tipo data
                    long time = tokenManager.verificaToken(p.get(0).getString(), p.get(1).getInt());
                    return new Member(time);
                case CREA_AUTORIZAZIONE:
                    Date date = MethodsUtils.DATE_FORMAT.parse(p.get(2).getString());
                    String key = authManager.creaAutorizzazione(p.get(0).getString(), p.get(1).getInt(), date);
                    return new Member(key);
                case VERIFICA_ESISTENZA_AUTORIZZAZIONE:
                    boolean existence = authManager.verificaEsistenzaAutorizzazione(p.get(0).getString());
                    return new Member(existence);
                case CREA_RISORSA:
                    //resourceManager.creaRisorsa(...);
                    return new Member(); //TODO
                case MODIFICA_RISORSA:
                    //resourceManager.modificaRisorsa(...);
                    return new Member(); //TODO
                case CANCELLA_RISORSA:
                    //resourceManager.cancellaRisorsa(...);
                    return new Member(); //TODO
                default:
                    throw new IllegalArgumentException(); //non viene mai chiamata ma deve esserci un ritorno per ogni ramo dello switch
            }
        } catch (ClassCastException e) {
            throw new InvalidParameterException("Wrong parameter type: " + e.getMessage());
        } catch (ParseException e) {
            throw new InvalidParameterException("Invalid date format: " + e.getMessage());
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String args[]) {
        Server s = new Server();

        Date date=new Date();
        Timer timer = new Timer();

        timer.schedule(new TimerTask(){
            public void run(){
                GestoreToken.getInstance().cancellaTokenScaduti();
            }
        },date, 24*60*60*1000);//24 ore in millisecondi

        while (true) {
            s.receive();
        }
    }
}
