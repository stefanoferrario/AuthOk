package jsonrpc;

public abstract class AbstractRequest  extends JsonRpcMessage {
    protected boolean notify;
    protected String method;
    protected String params[];

    public AbstractRequest(String method, String params[], int id) {
        notify = false;
        this.id = id;
        this.method = method;
        this.params = params;
        jsonRpcString = toJsonRpc();
    }
    public AbstractRequest(String method, String params[]) {
        notify = true;
        this.id = 0;
        this.method = method;
        this.params = params;
        jsonRpcString = toJsonRpc();
    }


    public String getMethod() {
        return method;
    }

    public String[] getParams() {
        return params;
    }

    public boolean isNotify() {
        return notify;
    }
}
