package jsonrpc;

import java.util.ArrayList;

public interface IClient {
    Response sendRequest(Request request) throws JSONRPCException;
    void sendNotify(Request notify) throws JSONRPCException;
    ArrayList<Response> sendBatch(ArrayList<Request> requests) throws JSONRPCException;
}
