package jsonrpc;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class Test {
    public static void main(String args[]) {
        testRequest();
        testResponse();
    }

    private static void testResponse() {
        ArrayList<String> testStrings = new ArrayList<>();
        testStrings.add("{\"jsonrpc\": \"2.0\", \"result\": 19, \"id\": 1}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"error\": {\"code\": -32601, \"message\": \"Method not found\"}, \"id\": \"1\"}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"error\": {\"code\": -32700, \"message\": \"Parse error\"}, \"id\": null}");

        for (String ts : testStrings) {
            try {
                System.out.println(ts);
                AbstractResponse resp = new Response(ts);
                System.out.println("ID: " + resp.getId());
                if (resp.hasError()) {
                    System.out.println("Error code: " + resp.getErrorCode());
                    System.out.println("Error message: " + resp.getErrorMessage());
                    System.out.println("Error data: ");
                    readStructured(resp.getErrorData());
                } else {
                    System.out.println("Risultato: ");
                    readStructured(resp.getResult());
                }
            } catch (Exception e) {
                System.out.println(e.getClass().toString() + " - " + e.getMessage());
            }
        }
    }

    private static void testRequest() {
        ArrayList<String> testStrings = new ArrayList<>();
        //testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}");
        //testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"update\", \"params\": [1,2,3,4,5]}");
        //testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"subtrahend\": 23, \"minuend\": 42}, \"id\": 3}");
        //testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"test\", \"params\": {\"subtrahend\": 23, \"minuend\": 42, \"subobj\": {\"par1\": 34, \"par2\": \"value\", \"array\": [1,2,3]}}, \"id\": 3}");
        testStrings.add("");
        testStrings.add("test");
        testStrings.add("{\"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}"); //no jsonrpc 2.0
        testStrings.add("{\"jsonrpc\": \"3.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}"); //jsonrpc <> 2.0
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": 42, \"id\": 1}"); //parametri non structure
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": null}"); //id nullo

        for (String ts : testStrings) {
            try {
                System.out.println(ts);
                AbstractRequest req = new Request(ts);
                System.out.println("Notifica: " + req.isNotify());
                if (!req.isNotify()) {
                    System.out.println("ID: " + req.getIdInt());
                }
                System.out.println("Method: " + req.getMethod());
                System.out.println("Params: ");
                readStructured(req.getParams());
                System.out.println("--");
            } catch (Exception e) {
                System.out.println(e.getClass().toString() + " - " + e.getMessage());
            }
        }
    }
    private static void readStructured(Object tmpParams) throws org.json.JSONException {
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
            readStructured(params.get(i));
        }
    }
    private static void readObj(JSONObject params) throws org.json.JSONException {
        JSONArray names = params.names();
        for (int i = 0; i<names.length(); i++) {
            System.out.println(names.get(i)+ ": ");
            readStructured(params.get((String)names.get(i)));
        }
    }
}
