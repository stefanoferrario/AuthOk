package jsonrpc;

//spostare tutte le implementazioni nella classe concreta?
public abstract class AbstractRequest  extends JsonRpcMessage {
    public enum Members {
        JSONRPC("jsonrpc"), METHOD("method"), ID("id"), PARAMS("params");

        private final String text;
        Members(final String text) {
            this.text = text;
        }
        @Override
        public String toString() {return text;}
    }

    boolean notify;
    String method;
    Object params; //è un oggetto strutturato che può essere array o mappa key-value

    AbstractRequest(String method, Object params, Object id) throws org.json.JSONException{
        this.notify = false;
        this.id = id;
        this.method = method;
        this.params = params;
        this.obj = toJsonRpc();
        this.jsonRpcString = obj.toString();
    }
    AbstractRequest(String method, Object params)  throws org.json.JSONException{
        this.notify = true;
        this.id = null;
        this.method = method;
        this.params = params;
        this.obj = toJsonRpc();
        this.jsonRpcString = obj.toString();
        //chiamare this(method, params, null) per non ripetere il codice non funzionerebbe perché il toJsonRpc leggere il parametro notify false
    }
    AbstractRequest() {
        super();
    }


    public String getMethod() {
        return method;
    }
    public Object getParams() {
        return params;
    }
    //definire vari getter dei parametri (arraylist<object>, mappa)
    public boolean isNotify() {
        return notify;
    }
}
