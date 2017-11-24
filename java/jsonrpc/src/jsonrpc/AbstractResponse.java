package jsonrpc;

import java.util.ArrayList;

public abstract class AbstractResponse extends JsonRpcMessage {
    String result;
    String message;
    int errorCode;
    ArrayList<String> errorData;

    AbstractResponse(int id, String result) {
        this.id = id;
        this.result = result;
        this.message = null;
        this.errorCode = 0;
        this.errorData = null;
        this.jsonRpcString = toJsonRpc();
    }

    AbstractResponse(int id, String message, int errorCode) {
        this.id = id;
        this.result = null;
        this.message = message;
        this.errorCode = errorCode;
        this.errorData = null;
        this.jsonRpcString = toJsonRpc();
    }

    AbstractResponse(int id, String message, int errorCode, ArrayList<String> errorData) {
        this.id = id;
        this.result = null;
        this.message = message;
        this.errorCode = errorCode;
        this.errorData = errorData;
        this.jsonRpcString = toJsonRpc();
    }

    AbstractResponse(String jsonRpcString) {
        this.jsonRpcString = jsonRpcString;
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
    public ArrayList<String> getErrorData() {
        return errorData;
    }
}
