package authorizer;
import jsonrpc.*;

public class Test {

    public static void main(String args[]) {
        System.out.println("Test moduli");

        String params[] = new String[2];
        AbstractRequest r = new Request("method",params,3);

        System.out.println(String.valueOf(r.getId()));

    }

}