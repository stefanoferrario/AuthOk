package zeromq;

import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class Server implements IServer{
    private ZMQ.Socket socket;
    private ZFrame identity;
    private ZFrame empty; //trovare come costruirlo, non serve salvarlo

    public Server(int port) {
        ZMQ.Context ctx = ZMQ.context(1);
        socket = ctx.socket(ZMQ.ROUTER);
        socket.bind("tcp://*:" + String.valueOf(port));
    }

    @Override
    public String receive() {
        //verificare se esiste una identity. se sì arriva da un req e bisogna rispondere, altrimenti da un dealer (notifica)
        ZMsg msg = ZMsg.recvMsg(socket);
        identity = msg.pop();
        empty = msg.pop(); //empty
        return msg.pop().toString();
    }

    @Override
    public void send(String string) throws Exception{
        if (identity == null) { throw new Exception(); /*NoIdentityException*/ }
        ZMsg msg = new ZMsg();
        msg.push(new ZFrame(string.getBytes()));
        msg.push(empty);
        msg.push(identity);
        msg.send(socket);
        identity = null;
    }
}
