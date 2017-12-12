package jsonrpc;

import java.security.InvalidParameterException;

public abstract class AbstractRequest  extends JsonRpcMessage {
    enum Members {
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
    StructuredMember params; //è un oggetto strutturato che può essere array o mappa key-value

    AbstractRequest(String method, StructuredMember params, Id id) {
        this.notify = id == null;
        this.id = id;
        this.method = method;
        this.params = params;
        try {
            this.obj = toJsonObj();
        } catch (JSONRPCException e) {
            throw new InvalidParameterException(e.getMessage());
        }
        this.jsonRpcString = obj.toString();
    }

    /*AbstractRequest(String method, StructuredMember params)  throws JSONRPCException{
        this(method, params, null);

        this.notify = true;
        this.id = null;
        this.method = method;
        this.params = params;
        this.obj = toJsonObj();
        this.jsonRpcString = obj.toString();

        //chiamare this(method, params, null) per non ripetere il codice non funzionerebbe perché il toJsonRpc leggere il parametro notify false
    }*/
    AbstractRequest() {
        super();
    }

    public String getMethod() {
        return method;
    }
    public StructuredMember getParams() {
        return params;
    }
    public boolean isNotify() {
        return notify;
    }
}
