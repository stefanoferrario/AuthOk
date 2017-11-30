package jsonrpc;
import org.json.JSONObject;
import zeromq.IZmqServer;
import zeromq.ZmqServer;

public class Server implements IServer {
    private IZmqServer server;

    Server(int port) {
        this.server = new ZmqServer(port);
    }

    @Override
    public Request receive() {
        try{
            return new Request(server.receive());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void reply(Response response) throws Exception{
        server.reply(response.getJsonString());
    }

    @Override
    public void replyToInvalidRequest(Request invalidRequest) throws Exception{
        JSONObject error = invalidRequest.createErrorObj();
        Response resp = new Response(invalidRequest.getIdInt(), error);
        server.reply(resp.getJsonString());
    }
}
