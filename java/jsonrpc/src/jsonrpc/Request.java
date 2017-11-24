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
}
