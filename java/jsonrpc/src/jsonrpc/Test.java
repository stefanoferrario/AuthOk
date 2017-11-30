package jsonrpc;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class Test {
    public static void main(String args[]) {
        ArrayList<String> testStrings = new ArrayList<>();
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"update\", \"params\": [1,2,3,4,5]}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"subtrahend\": 23, \"minuend\": 42}, \"id\": 3}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"test\", \"params\": {\"subtrahend\": 23, \"minuend\": 42, \"subobj\": {\"par1\": 34, \"par2\": \"value\", \"array\": [1,2,3]}}, \"id\": 3}");

        for (String ts : testStrings) {
            try {
                AbstractRequest req1 = new Request(ts);
                System.out.println(req1.getJsonString());
                System.out.println("Notifica: " + req1.isNotify());
                if (!req1.isNotify()) {
                    System.out.println("ID: " + req1.getIntId());
                }
                System.out.println("Method: " + req1.getMethod());
                System.out.println("Params: ");
                readParams(req1.getParams());
                System.out.println("--");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void readParams(Object tmpParams) throws org.json.JSONException {
        if (tmpParams instanceof JSONArray) {
            readArray((JSONArray) tmpParams);
        } else if (tmpParams instanceof  JSONObject) {
            readObj((JSONObject) tmpParams);
        } else {
            System.out.println(tmpParams);
        }
    }
    private static void readArray(JSONArray params) throws org.json.JSONException {
        for (int i = 0; i < params.length(); i++) {
            readParams(params.get(i));
        }
    }
    private static void readObj(JSONObject params) throws org.json.JSONException {
        JSONArray names = params.names();
        for (int i = 0; i<names.length(); i++) {
            System.out.println(names.get(i)+ ": ");
            readParams(params.get((String)names.get(i)));
        }
    }
}
