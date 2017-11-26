package jsonrpc;

public interface IClient {
    Response sendRequest(Request request, int port) throws Exception;
    void sendNotify(Request notify, int port);
}
