package jsonrpc;

abstract class AbstractResponse extends JsonRpcMessage {
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
    private AbstractResponse(Id id, Member result, Error error) throws JSONRPCException{
        if (id == null) {id = new Id();}
        if (result != null && (error != null) || (result == null && error == null)) {
            throw new JSONRPCException("Response has to have either a result or an error");
        }
        this.id = id;
        this.result = result;
        this.error = error;
        this.obj = toJsonObj();
        this.jsonRpcString = obj.toString();
    }
    AbstractResponse(Id id, Member result) throws JSONRPCException {
        this(id, result, null);
    }
    AbstractResponse(Id id, Error error) throws JSONRPCException {
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
