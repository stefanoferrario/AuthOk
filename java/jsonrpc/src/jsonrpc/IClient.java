package jsonrpc;

public interface IClient {
    Response sendRequest(Request request) throws Exception;
    void sendNotify(Request notify);
}
