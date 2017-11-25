package zeromq;

public interface IZmqServer {
    String receive();
    void send(String string) throws Exception;
}
