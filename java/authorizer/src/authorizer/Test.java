package authorizer;
import jsonrpc.*;

import java.util.ArrayList;

public class Test {

    public static void main(String args[]) {
        System.out.println("Test moduli");

        ArrayList<String> params = new ArrayList<String>();
        AbstractRequest r = new Request("method",params,3);

        System.out.println(String.valueOf(r.getId()));

    }

}