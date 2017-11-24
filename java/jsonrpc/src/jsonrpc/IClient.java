package jsonrpc;

public interface IClient {
    Response sendRequest(Request request, int port);
    void sendNotify(Request notify);
}
