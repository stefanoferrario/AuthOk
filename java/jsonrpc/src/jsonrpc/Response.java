package jsonrpc;

public class Response extends AbstractResponse {
    public Response(int id, String result) {
        super(id, result);
    }

    public Response(int id, String message, int errorCode) {
        super(id, message, errorCode);
    }

    public Response(int id, String message, int errorCode, String errorData[]) {
        super(id, message, errorCode, errorData);
    }

    /*public Response(String jsonRpcString) {
        this.jsonRpcString = jsonRpcString;
    }*/

    @Override
    protected String toJsonRpc() {
        return null;
    }
}
