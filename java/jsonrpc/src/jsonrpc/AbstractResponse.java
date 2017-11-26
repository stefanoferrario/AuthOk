package jsonrpc;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class AbstractResponse extends JsonRpcMessage {
    public enum Members {
        JSONRPC("jsonrpc"), RESULT("result"), ERROR("error"), ID("id");

        private final String text;
        Members(final String text) {
            this.text = text;
        }
        @Override
        public String toString() {return text;}
    }
    public enum ErrMembers {
        CODE("code"), MESSAGE("message"), DATA("data");

        private final String text;
        ErrMembers(final String text) {
            this.text = text;
        }
        @Override
        public String toString() {return text;}
    }

    Object result; //primitive o structure
    JSONObject errObj;
    String errMessage;
    Integer errCode; //da specifica deve essere intero
    Object errData;//primitive o structure

    AbstractResponse(int id, String result) throws org.json.JSONException {
        this.id = id;
        this.result = result;
        this.errObj = null;
        this.errMessage = null;
        this.errCode = null;
        this.errData = null;
        this.obj = toJsonRpc();
        this.jsonRpcString = obj.toString();
    }
    AbstractResponse(int id, String errorMessage, int errorCode) throws org.json.JSONException {
        this.id = id;
        this.result = null;
        this.errMessage = errorMessage;
        this.errCode = errorCode;
        this.errData = null;
        this.errObj = toErrJsonRpc();
        this.obj = toJsonRpc();
        this.jsonRpcString = obj.toString();
    }
    AbstractResponse(int id, String errorMessage, int errorCode, Object errorData) throws org.json.JSONException {
        this.id = id;
        this.result = null;
        this.errMessage = errorMessage;
        this.errCode = errorCode;
        this.errData = errorData;
        this.errObj = toErrJsonRpc();
        this.obj = toJsonRpc();
        this.jsonRpcString = obj.toString();
    }
    AbstractResponse() {
        super();
    }

    public Object getResult() {
        return result;
    }
    public String getMessage() {
        return errMessage;
    }
    public int getErrorCode() {
        return errCode;
    }
    public Object getErrorData() {
        return errData;
    }
    //getter dei vari tipi di risultato e errData come per l'id

    abstract JSONObject toErrJsonRpc() throws org.json.JSONException;
}
