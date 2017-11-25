package jsonrpc;
import zeromq.IZmqServer;
import zeromq.ZmqServer;

public class Server implements IServer {
    private IZmqServer server;

    Server(int port) {
        this.server = new ZmqServer(port);
    }

    @Override
    public Request receive() {
        return new Request(server.receive());
    }

    @Override
    public void reply(Response response) throws Exception{
        server.send(response.getJsonString());
    }

    @Override
    public void replyToInvalidRequest(Request invalidRequest) throws Exception{
        Response resp = new Response(invalidRequest.getId(), invalidRequest.createErrorMessage(), invalidRequest.createErrorCode(), invalidRequest.createErrorData());
        server.send(resp.getJsonString());
    }
}
