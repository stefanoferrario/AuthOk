package jsonrpc;

public class Request {
    private int x;
    private int y;
    public Request(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
