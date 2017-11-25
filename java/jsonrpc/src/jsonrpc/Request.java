package jsonrpc;

import java.util.ArrayList;

public class Request extends AbstractRequest{

    public Request(String method, ArrayList<String> params) {
        super(method, params);
    }
    public Request(String method, ArrayList<String> params, int id) {
        super(method, params, id);
    }

    Request(String jsonRpcString) {
        super(jsonRpcString);
    }

    @Override
    protected String toJsonRpc() {
        //...
        return "...";
    }

    int createErrorCode() {
        //analizza la propria stinga jsonrpc e restituisce l'errore del caso
        //se non ci sono errori lancia eccezione
        return 0;
    }

    String createErrorMessage() {
        return "...";
    }

    ArrayList<String> createErrorData() {
        //può essere vuoto
        return null;
    }
}
