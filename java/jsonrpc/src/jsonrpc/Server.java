package jsonrpc;
import org.json.JSONException;
import org.json.JSONObject;
import zeromq.IZmqServer;
import zeromq.ZmqServer;
import java.util.HashMap;

public class Server implements IServer {
    private IZmqServer server;

    Server() {
        this.server = new ZmqServer();
    }

    @Override
    public Request receive() {
        String receivedString = server.receive();
        try{
            return new Request(receivedString);
        } catch (JSONException e) {
            Id id = getIdFromRequest(receivedString);
            try {
                HashMap<String, Member> errorData = new HashMap<>();
                errorData.put("Invalid request received", new Member(e.getMessage()));
                Error err = new Error(Error.Errors.PARSE, new Member(new StructuredMember(errorData)));
                Response errorResp = new Response(id, err);
                server.reply(errorResp.getJsonString());
            } catch (JSONException j) {
                server.reply(Response.getInternalErrorResponse(id).getJsonString());
            }
            return null;
        }
    }

    @Override
    public void reply(Response response) throws Exception{
        server.reply(response.getJsonString());
    }

    private Id getIdFromRequest(String request) {
        try {
            JSONObject obj = new JSONObject(request);
            return Id.toId(obj.get(AbstractRequest.Members.ID.toString()));
        } catch (JSONException e) {
            return new Id(); //se non è possibile recuperarlo della richiesta si crea un id nullo
        }
    }


    /*@Override
    public void replyToInvalidRequest(Request invalidRequest) throws Exception{
        Error error = invalidRequest.createErrorObj();
        Response resp = new Response(invalidRequest.getId(), error);
        server.reply(resp.getJsonString());
    }*/
}
