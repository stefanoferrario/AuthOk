package user;

import jsonrpc.Error;

public class AuthorizerException extends Exception {
    public AuthorizerException(String message) {super(message);}
    public AuthorizerException(Error error) {
        super(error.toString());
    }
}
