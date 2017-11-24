package jsonrpc;

abstract class JsonRpcMessage {
    String jsonRpcString;
    int id;
    boolean valid;

    public String getJsonString() {
        return jsonRpcString;
    }

    public int getId() {
        return id;
    }

    public boolean isValid() {
        return valid;
    }

    abstract protected String toJsonRpc(); //crea stringa json rpc utilizzando attributi. implementata in maniera differente in richiesta e risposta

}
