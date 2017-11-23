package authorizer;
import jsonrpc.*;

public class Test {

    public static void main(String args[]) {
        System.out.println("Test moduli");

        Request r = new Request(2,2);

        System.out.println(String.valueOf(r.getX()));
    }

}