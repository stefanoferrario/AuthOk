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
        String addr = "tcp://*:" + String.valueOf(port);
        socket.bind(addr);
        identity = null;
        empty = null;
    }

    @Override
    public String receive() {
        ZMsg msg = ZMsg.recvMsg(socket);
        identity = msg.pop(); //i messaggi in arrivo dal dealer hanno una identity ed è quindi possibile rispondere
        empty = msg.size() == 2 ? msg.pop() : null; //i messaggi inviati dal dealer non hanno frame vuoto
        return msg.pop().toString();
    }

    @Override
    public void reply(String string) throws UnsupportedOperationException{
        if (identity == null) { throw new UnsupportedOperationException("Receiver undefined"); } //cambiare tipo eccezione
        ZMsg msg = new ZMsg();
        msg.push(new ZFrame(string.getBytes()));
        if (empty!=null) {msg.push(empty);} //i messaggi da inviare al dealer non devono avere empty frame
        msg.push(identity);
        msg.send(socket);
        identity = null;
        empty = null;
    }
}
