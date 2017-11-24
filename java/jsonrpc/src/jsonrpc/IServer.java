package jsonrpc;

public interface IServer {
    Request receive();
    void reply(Response response);
    void replyToInvalidRequest(Request invalidRequest);
}
