package jsonrpc;

import java.security.InvalidParameterException;

public abstract class AbstractResponse extends JsonRpcMessage {
    enum Members {
        JSONRPC("jsonrpc"), RESULT("result"), ERROR("error"), ID("id");

        private final String text;
        Members(final String text) {
            this.text = text;
        }
        @Override
        public String toString() {return text;}
    }

    Member result; //primitive o structure
    Error error;

    //setup
    private AbstractResponse(Id id, Member result, Error error) {
        if (id == null) {id = new Id();}
        if (result != null && (error != null) || (result == null && error == null)) {
            throw new InvalidParameterException("Response has to have either a result or an error");
        }
        this.id = id;
        this.result = result;
        this.error = error;
        try {
            this.obj = toJsonObj();
        } catch (JSONRPCException e) {
            throw new InvalidParameterException(e.getMessage());
        }
        this.jsonRpcString = obj.toString();
    }
    AbstractResponse(Id id, Member result) {
        this(id, result, null);
    }
    AbstractResponse(Id id, Error error) {
        this(id, null, error);
    }
    AbstractResponse() {
        super();
    }

    public Member getResult() {
        if (result == null) {throw new NullPointerException("No result");}
        return result;
    }
    public Error getError() {
        if (error == null) {throw new NullPointerException("No error");}
        return error;
    }
    public boolean hasError() {
        return this.result == null;
    }
}
