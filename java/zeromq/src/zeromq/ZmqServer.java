package zeromq;

import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class ZmqServer implements IZmqServer{
    private ZMQ.Socket socket;
    private ZFrame identity;
    private ZFrame empty;

    public ZmqServer(int port) {
        ZMQ.Context ctx = ZMQ.context(1);
        socket = ctx.socket(ZMQ.ROUTER);
        socket.bind("tcp://*:" + String.valueOf(port));
    }

    @Override
    public String receive() {
        ZMsg msg = ZMsg.recvMsg(socket);
        identity = msg.pop();
        empty = msg.size() == 2 ? msg.pop() : null; //i messaggi inviati dal dealer non hanno frame vuoto
        return msg.pop().toString();
    }

    @Override
    public void send(String string) throws Exception{
        if (identity == null) { throw new Exception(); /*NoIdentityException*/ }
        ZMsg msg = new ZMsg();
        msg.push(new ZFrame(string.getBytes()));
        if (empty!=null) {msg.push(empty);} //i messaggi da inviare al dealer non devono avere empty frame
        msg.push(identity);
        msg.send(socket);
        identity = null;
    }
}
