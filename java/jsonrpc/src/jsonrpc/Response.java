package jsonrpc;

import java.util.ArrayList;

public class Response extends AbstractResponse {
    public Response(int id, String result) {
        super(id, result);
    }

    public Response(int id, String message, int errorCode) {
        super(id, message, errorCode);
    }

    public Response(int id, String message, int errorCode, ArrayList<String> errorData) {
        super(id, message, errorCode, errorData);
    }

    Response(String jsonRpcString) {
        super(jsonRpcString);
    }

    @Override
    protected String toJsonRpc() {
        return null;
    }
}
