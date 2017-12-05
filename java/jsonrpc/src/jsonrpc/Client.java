package jsonrpc;
import org.json.JSONException;
import zeromq.IZmqClient;
import zeromq.ZmqClient;
import java.util.HashMap;

public class Client implements IClient {
    private IZmqClient zmqClient;
    public Client(int port) {
        zmqClient = new ZmqClient(port);
    }

    @Override
    public Response sendRequest(Request request) throws JSONException {
        String returnedString = zmqClient.request(request.getJsonString());

        try {
            return new Response(returnedString);
        } catch (JSONException e) {
            HashMap<String, Member> errorData = new HashMap<>();
            errorData.put("Invalid response received", new Member(e.getMessage()));
            Error err = new Error(Error.Errors.PARSE, new Member(new StructuredMember(errorData)));
            return new Response(request.getId(), err);
        }
    }

    @Override
    public void sendNotify(Request notify) {
        zmqClient.send(notify.getJsonString());
    }
}
