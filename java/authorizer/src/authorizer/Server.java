package authorizer;

import authorizer.GestoreRisorse.GestoreRisorse;
import authorizer.GestoreToken.GestoreToken;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import jsonrpc.*;
import jsonrpc.IServer;
import jsonrpc.Error;
import jsonrpc.Member;
import jsonrpc.StructuredMember;
import jsonrpc.Request;
import jsonrpc.Response;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static final int PORT = 5001;

    private Server() {
        tokenManager = GestoreToken.getInstance();
        resourceManager = GestoreRisorse.getInstance();
        authManager = GestoreAutorizzazioni.getInstance();
        serverJsonRpc = new jsonrpc.Server(PORT);
    }

    public Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private void receive() {
        ArrayList<Request> reqs;
        try {
            reqs = serverJsonRpc.receive();
        } catch (JSONRPCException e) {
            System.out.println(e.getMessage());
            return;
        }

        ArrayList<Response> resps = new ArrayList<>();

        for (Request req : reqs) {
            Response resp = null;
            try {
                try {
                    Member result = selectMethod(Methods.valueOf(req.getMethod()), req.getParams());
                    resp = new Response(req.getId(), result);
                } catch (InvalidParameterException e) {
                    Error error = new Error(Error.Errors.INVALID_PARAMS);
                    resp = new Response(req.getId(), error);
                } catch (IllegalArgumentException e) {
                    //lanciata dal Methods.valueOf() se la stringa non corrisponde a un metodo
                    Error error = new Error(Error.Errors.METHOD_NOT_FOUND);
                    resp = new Response(req.getId(), error);
                } catch (UnsupportedOperationException e) {
                    //errore
                }
            } catch (JSONRPCException e) {
                System.out.println(e.getMessage());
                resp = null;
            } finally {
                if (!req.isNotify()) {
                    resps.add(resp);
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

    private Member selectMethod(Methods method, StructuredMember params)  throws InvalidParameterException {
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
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = dateFormat.parse(p.get(2).getString());
                    String key = authManager.creaAutorizzazione(p.get(0).getString(), p.get(1).getInt(), date);
                    return new Member(key);
                case VERIFICA_ESISTENZA_AUTORIZZAZIONE:
                    boolean existance = authManager.verificaEsistenzaAutorizzazione(p.get(0).getString());
                    return new Member(existance);
                case CREA_RISORSA:
                    //resourceManager.creaRisorsa(...);
                    break;
                case MODIFICA_RISORSA:
                    //resourceManager.modificaRisorsa(...);
                    break;
                case CANCELLA_RISORSA:
                    //resourceManager.cancellaRisorsa(...);
                    break;
            }
        } catch (ClassCastException e) {
            throw new InvalidParameterException("Wrong parameter type: " + e.getMessage());
        } catch (ParseException e) {
            throw new InvalidParameterException("Invalid date format: " + e.getMessage());
        } catch (JSONRPCException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void main(String args[]) {
        Server s = new Server();

        Date date=new Date();
        Timer timer = new Timer();

        timer.schedule(new TimerTask(){
            public void run(){
                GestoreToken.getInstance().cancellaTokenScaduti();
            }
        },date, 24*60*60*1000);//24*60*60*1000 add 24 hours delay between job executions.

        while (true) {
            s.receive();
        }
    }
}
