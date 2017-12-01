package jsonrpc;

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
