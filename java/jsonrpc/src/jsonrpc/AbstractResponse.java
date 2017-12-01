package jsonrpc;

import org.json.JSONObject;

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

    private void setup(Object id, Object result, String errorMessage, Integer errorCode, Object errorData) throws Exception{
        if (!(id == null || id instanceof Integer || id instanceof String)) {throw new Exception();}
        if (result != null && (errorMessage != null || errorCode != null)) {throw new Exception();} //non possono essere dichiarati sia errore sia risultato
        this.id = id;
        this.result = result;
        this.errMessage = errorMessage;
        this.errCode = errorCode;
        this.errData = errorData;
        this.errObj = toErrJsonRpc();
        this.obj = toJsonRpc();
        this.jsonRpcString = obj.toString();
    }
    AbstractResponse(Object id, Object result) throws Exception {
        setup(id, result, null, null, null);
    }
    AbstractResponse(Object id, String errorMessage, int errorCode) throws Exception {
        setup(id, null, errorMessage, errorCode, null);
    }
    AbstractResponse(Object id, String errorMessage, int errorCode, Object errorData) throws Exception {
        setup(id, null, errorMessage, errorCode, errorData);
    }
    AbstractResponse(Object id, Errors error) throws Exception {
        setup(id, null, error.getMessage(), error.getCode(), null);
    }
    AbstractResponse(Object id, Errors error, Object errorData) throws Exception {
        setup(id, null, error.getMessage(), error.getCode(), errorData);
    }
    AbstractResponse() {
        super();
    }

    //chiedere se è il modo migliore per gestire un risultato da vari possibili tipi
    public Object getResult() {
        return result;
    }
    public Number getResultNumber() {
        if (result == null) {throw new NullPointerException();}
        if (!(result instanceof Number)) {throw new ClassCastException();}
        return (Number)id;
    }
    public String getResultString() {
        if (result == null) {throw new NullPointerException();}
        if (!(result instanceof String)) {throw new ClassCastException();}
        return (String)result;
    }
    public boolean getResultBool() {
        if (result == null) {throw new NullPointerException();}
        if (!(result instanceof Boolean)) {throw new ClassCastException();}
        return (boolean)result;
    }
    public boolean hasNullResult() {
        if (result == null) {throw new NullPointerException();} // creare eccezione per le stringhe con errore e non con risultato
        //risultato null significa che è una risposta con errore, risultato JSONObject.NULL è un possibile risultato della richiesta
        return result == JSONObject.NULL;
    }

    public String getErrorMessage() {
        return errMessage;
    }
    public int getErrorCode() {
        return errCode;
    }
    public Object getErrorData() {
        return errData;
    }
    //getter dei vari tipi di errData come per l'id

    abstract JSONObject toErrJsonRpc() throws Exception;

    public boolean hasError() {
        return this.result == null;
    }

}
