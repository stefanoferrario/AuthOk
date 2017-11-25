package zeromq;

public interface IServer {
    String receive();
    void send(String string) throws Exception;
}
