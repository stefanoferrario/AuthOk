package jsonrpc;

import java.util.ArrayList;

public abstract class AbstractRequest  extends JsonRpcMessage {
    boolean notify;
    String method;
    ArrayList<String> params;

    AbstractRequest(String method, ArrayList<String> params, int id) {
        notify = false;
        this.id = id;
        this.method = method;
        this.params = params;
        jsonRpcString = toJsonRpc();
    }
    AbstractRequest(String method, ArrayList<String> params) {
        notify = true;
        this.id = 0;
        this.method = method;
        this.params = params;
        jsonRpcString = toJsonRpc();
    }

    AbstractRequest(String jsonRpcString) {
        this.jsonRpcString = jsonRpcString;
    }

    public String getMethod() {
        return method;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public boolean isNotify() {
        return notify;
    }
}
