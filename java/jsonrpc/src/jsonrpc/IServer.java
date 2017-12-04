package jsonrpc;

public interface IServer {
    Request receive();
    void reply(Response response) throws Exception;
    //void replyToInvalidRequest(Request invalidRequest) throws Exception;
}
