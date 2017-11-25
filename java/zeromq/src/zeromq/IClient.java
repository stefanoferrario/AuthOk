package zeromq;

public interface IClient {
    String request(String req, int port);
    void send(String msg, int port);
}
