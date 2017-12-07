package authorizer;

import authorizer.GestoreRisorse.GestoreRisorse;
import authorizer.GestoreToken.GestoreToken;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import jsonrpc.*;
import jsonrpc.Error;
import org.json.JSONException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

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
            if (req.isNotify()) {
                try {
                    selectMethod(req.getMethod(), req.getParams());
                } catch (Exception e) {
                    //le notifiche non restituiscono errori
                }
            } else {
                try {
                    try {
                        Member result = selectMethod(req.getMethod(), req.getParams());
                        resps.add(new Response(req.getId(), result));
                    } catch (NoSuchMethodException e) {
                        Error error = new Error(Error.Errors.METHOD_NOT_FOUND);
                        resps.add(new Response(req.getId(), error));
                    } catch (InvalidParameterException e) {
                        Error error = new Error(Error.Errors.INVALID_PARAMS);
                        resps.add(new Response(req.getId(), error));
                    } catch (UnsupportedOperationException e) {
                        //errore
                    }
                } catch (JSONRPCException e) {
                    System.out.println(e.getMessage());
                    resps.add(null);
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


    private static final String CREA_TOKEN = "creatoken";
    private static final String VERIFICA_TOKEN = "verificatoken";
    private static final String CREA_AUTORIZAZIONE = "creaautorizzazione";
    private static final String VERIFICA_ESISTENZA_AUTORIZZAZIONE = "verificaesistenzaautorizzazione";
    private static final String CREA_RISORSA = "crearisorsa";
    private static final String MODIFICA_RISORSA = "modificarisorsa";
    private static final String CANCELLA_RISORSA = "cancellarisorsa";


    private Member selectMethod(String method, StructuredMember params)  throws InvalidParameterException, NoSuchMethodException {
        ArrayList<Member> p;
        try {
            p = params.getList();
        } catch (ClassCastException c) {
            throw new InvalidParameterException();
        }

        switch (method) {
            case CREA_TOKEN:
                try {
                    //verificare non ci siano troppi parametri
                    return Member.toMember(tokenManager.creaToken(p.get(0).getString(), p.get(1).getInt()));
                } catch (JSONRPCException e) {
                    throw new InvalidParameterException();
                }
                /*altre eccezioni interne al server non gestite dalla select method ma dal receive che crea risposta con errore corrispondente*/

            case VERIFICA_TOKEN:
                try {
                    return Member.toMember(tokenManager.verificaToken(p.get(0).getString(), p.get(1).getInt()));
                } catch (JSONRPCException e) {
                    throw new InvalidParameterException();
                }

            case CREA_AUTORIZAZIONE:

                break;
            case VERIFICA_ESISTENZA_AUTORIZZAZIONE:
                break;
            case CREA_RISORSA:
                break;
            case MODIFICA_RISORSA:
                break;
            case CANCELLA_RISORSA:
                break;
            default: throw new NoSuchMethodException("Method " + method + " not found");
        }
        return null;
    }

    public static void main(String args[]) {
        Server s = new Server();
        while (true) {
            s.receive();
        }
    }
}
