package jsonrpc;

public interface IServer {
    Request receive() throws JSONRPCException;
    void reply(Response response);
}
