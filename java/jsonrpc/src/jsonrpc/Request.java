package jsonrpc;

public class Request extends AbstractRequest{

    public Request(String method, String params[]) {
        super(method, params);
    }
    public Request(String method, String params[], int id) {
        super(method, params, id);
    }

    /*public Request(String jsonRpcString) {
        this.jsonRpcString = jsonRpcString;
    }*/

    @Override
    protected String toJsonRpc() {
        //...
        return "...";
    }
}
