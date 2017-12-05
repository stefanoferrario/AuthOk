package jsonrpc;

import java.util.ArrayList;

public interface IServer {
    ArrayList<Request> receive() throws JSONRPCException;
    void reply(Response response) throws JSONRPCException;
}
