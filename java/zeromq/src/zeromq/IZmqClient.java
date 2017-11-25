package zeromq;

public interface IZmqClient {
    String request(String req, int port);
    void send(String msg, int port);
}
