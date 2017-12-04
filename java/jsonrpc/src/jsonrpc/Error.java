package jsonrpc;

import org.json.JSONException;
import org.json.JSONObject;

public class Error extends JsonRpcObj{
    enum ErrMembers {
        CODE("code"), MESSAGE("message"), DATA("data");

        private final String text;
        ErrMembers(final String text) {
            this.text = text;
        }
        @Override
        public String toString() {return text;}
    }
    public enum Errors {
        PARSE(-32700, "Parse error"),
        INVALID_REQUEST(-32600, "Invalid Request"),
        METHOD_NOT_FOUND(-32601, "Method not found"),
        INVALID_PARAMS(-32602, "Invalid params"),
        INTERNAL_ERROR(-32603, "Internal error");

        private final int code;
        private final String message;
        Errors(final int code, final String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {return code;}
        public String getMessage() {return message;}
    }
    private String message;
    private Integer code; //da specifica deve essere intero
    private Member data;//primitive o structure

    public Error(String errorMessage, int errorCode, Member errorData) throws JSONException {
        if (errorMessage == null || errorMessage.isEmpty()) {throw new JSONException("Error message not defined");}
        this.message = errorMessage;
        this.code = errorCode;
        this.data = errorData;
        this.obj = toJsonObj();
        this.jsonRpcString = obj.toString();
    }
    public Error(String errorMessage, int errorCode) throws JSONException {
        this(errorMessage, errorCode, null);
    }
    public Error(Errors error) throws JSONException {
        this(error.getMessage(), error.getCode(), null);
    }
    public Error(Errors error, Member errorData) throws JSONException {
        this(error.getMessage(), error.getCode(), errorData);
    }

    public String getErrorMessage() {
        return message;
    }
    public int getErrorCode() {
        return code;
    }
    public Member getErrorData() throws JSONException {
        if (data == null) {throw new JSONException("No error data defined");}
        return data;
    }
    public boolean hasErrorData() {
        return data!=null;
    }

    protected JSONObject toJsonObj() throws JSONException{
        if (code == null) {throw new JSONException("Error code not defined");} //obbligatori
        if (message == null) { throw new JSONException("Error message not defined");}
        JSONObject object = new JSONObject();
        object.put(ErrMembers.CODE.toString(), code);
        object.put(ErrMembers.MESSAGE.toString(), message);

        if (data != null) { //opzionale
            putMember(object, ErrMembers.DATA.toString(), data);
        }

        return object;
    }

    Error(JSONObject error) throws JSONException{
        this.obj = error;
        if (obj.has(ErrMembers.CODE.toString())) {
            code = error.getInt(ErrMembers.CODE.toString());
        } else {
            throw new JSONException("Error code not found");
        }
        if (obj.has(ErrMembers.MESSAGE.toString())) {
            message = error.getString(ErrMembers.MESSAGE.toString());
        } else {
            throw new JSONException("Error message not found");
        }

        if (obj.has(ErrMembers.DATA.toString())) {
            data = Member.toMember(obj.get(ErrMembers.DATA.toString()));
        } else {
            data = null;
        }

        //verifica che non ci siano altri parametri
        if (!checkMembersSubset(ErrMembers.values(), obj)) {throw new JSONException("Unexpected paramater");}

        this.jsonRpcString = obj.toString();
    }
}
