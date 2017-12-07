package jsonrpc;
import org.json.JSONArray;
import org.json.JSONException;
import zeromq.IZmqClient;
import zeromq.ZmqClient;
import java.util.ArrayList;
import java.util.HashMap;

public class Client implements IClient {
    private IZmqClient zmqClient;
    public Client(int port) {
        zmqClient = new ZmqClient(port);
    }

    @Override
    public Response sendRequest(Request request) throws JSONRPCException {
        if (request.isNotify()) {throw new JSONRPCException("Not a request");}

        String returnedString = zmqClient.request(request.getJsonString());

        try {
            return new Response(returnedString);
        } catch (JSONException | JSONRPCException e) {
            HashMap<String, Member> errorData = new HashMap<>();
            errorData.put("Invalid response received", new Member(e.getMessage()));
            Error err = new Error(Error.Errors.PARSE, new Member(new StructuredMember(errorData)));
            return new Response(request.getId(), err);
        }
    }

    @Override
    public void sendNotify(Request notify) throws JSONRPCException {
        if (!notify.isNotify()) {throw new JSONRPCException("Not a notify");} //funzionerebbe ma la specifica jsonrpc prevede che se è una richiesta deve essere restituita una risposta

        zmqClient.send(notify.getJsonString());
    }

    public ArrayList<Response> sendBatch(ArrayList<Request> requests) {
        Batch batch = new Batch(requests);
        String returnedString = zmqClient.request(batch.getRequestJSON());

        try {
            JSONArray arr = new JSONArray(returnedString);
            batch.put(arr);
            return batch.getValidResponses();
        } catch (JSONException | JSONRPCException e) {
            Id id = new Id(); //da un batch di richieste non è possibile recuperare UN id
            HashMap<String, Member> errorData = new HashMap<>();
            try {
                //errorData.put("Invalid response received", new Member(e.getMessage()));
                Error err = new Error(Error.Errors.PARSE/*, new Member(new StructuredMember(errorData))*/);
                Response errorResp = new Response(id, err);
                ArrayList<Response> resp = new ArrayList<>();
                resp.add(errorResp);
                return resp;
            } catch (JSONRPCException j) {
                System.out.println(j.getMessage());
                return new ArrayList<>();
            }
        }
    }
}
