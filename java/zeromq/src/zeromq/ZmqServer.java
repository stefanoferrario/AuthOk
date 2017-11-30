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
        identity = null;
        empty = null;
    }

    @Override
    public String receive() {
        ZMsg msg = ZMsg.recvMsg(socket);
        identity = msg.pop();
        empty = msg.size() == 2 ? msg.pop() : null; //i messaggi inviati dal dealer non hanno frame vuoto
        return msg.pop().toString();
    }

    @Override
    public void reply(String string) throws Exception{
        if (identity == null) { throw new Exception(); /*NoIdentityException*/ }
        ZMsg msg = new ZMsg();
        msg.push(new ZFrame(string.getBytes()));
        if (empty!=null) {msg.push(empty);} //i messaggi da inviare al dealer non devono avere empty frame
        msg.push(identity);
        msg.send(socket);

        //in teoria un router può rispondere tante volte a un solo messaggio, non sapendo di che tipo è il client (non è detto sia un req)
        //nel caso si voglia rendere possibile non vanno settati a null identity ed empty, ma il server potrebbe rispondere più volte solo all'ultimo client
        identity = null;
        empty = null;
    }
}
