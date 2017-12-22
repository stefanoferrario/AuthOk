package authorizer.gestoreToken;

public class TokenException extends Exception {
    TokenException(String message) {
        super(message);
    }
    TokenException() {super();}
}
