package jsonrpc;

import org.json.JSONException;

public interface IClient {
    Response sendRequest(Request request) throws JSONException;
    void sendNotify(Request notify);
}
