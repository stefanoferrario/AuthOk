package jsonrpc;
import zeromq.IZmqClient;
import zeromq.ZmqClient;

public class Client implements IClient {
    @Override
    public Response sendRequest(Request request) throws Exception {
        IZmqClient zmqClient = new ZmqClient();
        return new Response(zmqClient.request(request.getJsonString()));
    }

    @Override
    public void sendNotify(Request notify) {
        IZmqClient zmqClient = new ZmqClient();
        zmqClient.send(notify.getJsonString());
    }
}
