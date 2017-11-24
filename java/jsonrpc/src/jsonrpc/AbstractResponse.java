package jsonrpc;

public abstract class AbstractResponse extends JsonRpcMessage {
    protected String result;
    protected String message;
    protected int errorCode;
    protected String errorData[];

    public AbstractResponse(int id, String result) {
        this.id = id;
        this.result = result;
        this.message = null;
        this.errorCode = 0;
        this.errorData = null;
        this.jsonRpcString = toJsonRpc();
    }

    public AbstractResponse(int id, String message, int errorCode) {
        this.id = id;
        this.result = null;
        this.message = message;
        this.errorCode = errorCode;
        this.errorData = null;
        this.jsonRpcString = toJsonRpc();
    }

    public AbstractResponse(int id, String message, int errorCode, String errorData[]) {
        this.id = id;
        this.result = null;
        this.message = message;
        this.errorCode = errorCode;
        this.errorData = errorData;
        this.jsonRpcString = toJsonRpc();
    }

    public String getResult() {
        return result;
    }
    public String getMessage() {
        return message;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public String[] getErrorData() {
        return errorData;
    }
}
