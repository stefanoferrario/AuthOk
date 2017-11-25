package jsonrpc;
import zeromq.IZmqClient;
import zeromq.ZmqClient;

public class Client implements IClient {
    @Override
    public Response sendRequest(Request request, int port) {
        IZmqClient zmqClient = new ZmqClient();
        return new Response(zmqClient.request(request.getJsonString(), port));
    }

    @Override
    public void sendNotify(Request notify, int port) {
        IZmqClient zmqClient = new ZmqClient();
        zmqClient.send(notify.getJsonString(), port);
    }
}
