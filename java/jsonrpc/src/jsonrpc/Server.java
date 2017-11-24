package jsonrpc;

public class Server implements IServer {
    private int port;

    Server(int port) {
        this.port = port;
    }

    @Override
    public Request receive() {
        return null;
    }

    @Override
    public void reply(Response response) {

    }

    @Override
    public void replyToInvalidRequest(Request invalidRequest) {

    }
}
