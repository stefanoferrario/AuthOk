package jsonrpc;
import org.json.JSONException;
import zeromq.IZmqClient;
import zeromq.ZmqClient;
import java.util.HashMap;

public class Client implements IClient {
    @Override
    public Response sendRequest(Request request){
        IZmqClient zmqClient = new ZmqClient();
        String returnedString = zmqClient.request(request.getJsonString());

        try {
            return new Response(returnedString);
        } catch (JSONException e) {
            try {
                HashMap<String, Member> errorData = new HashMap<>();
                errorData.put("Invalid response received", new Member(e.getMessage()));
                Error err = new Error(Error.Errors.PARSE, new Member(new StructuredMember(errorData)));
                return new Response(request.getId(), err);
            } catch (JSONException j) {
                return Response.getInternalErrorResponse(request.getId());
            }
        }
    }

    @Override
    public void sendNotify(Request notify) {
        IZmqClient zmqClient = new ZmqClient();
        zmqClient.send(notify.getJsonString());
    }
}
