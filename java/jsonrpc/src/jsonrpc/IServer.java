package jsonrpc;

import java.util.ArrayList;

public interface IServer {
    ArrayList<Request> receive() throws JSONRPCException;
    void reply(ArrayList<Response> response) throws JSONRPCException;
}
