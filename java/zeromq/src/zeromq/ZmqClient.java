package zeromq;

import org.zeromq.ZMQ;
//import org.zeromq.ZMsg;

public class ZmqClient implements IZmqClient {
    private ZMQ.Context ctx;
    private ZMQ.Socket socket;

    public ZmqClient() {

        ctx = ZMQ.context(1);
    }

    @Override
    public String request(String req, int port) {
        socket = ctx.socket(ZMQ.REQ);
        socket.connect("tcp://localhost:"+String.valueOf(port));
        socket.send(req.getBytes());
        String s = socket.recvStr();
        socket.close();
        return s;
    }

    @Override
    public void send(String msg, int port) {
        socket = ctx.socket(ZMQ.DEALER);
        socket.connect("tcp://localhost:"+String.valueOf(port));
        socket.send(msg.getBytes());
        socket.close();
    }
}