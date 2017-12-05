package jsonrpc;

import org.json.JSONException;

public interface IServer {
    Request receive() throws JSONException ;
    void reply(Response response);
}
