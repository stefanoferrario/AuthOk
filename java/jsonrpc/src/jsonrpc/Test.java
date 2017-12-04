package jsonrpc;

import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;

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
        testStrings.add("");
        testStrings.add("test");
        testStrings.add("{\"result\": 19, \"id\": 1}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"result\": 19, \"id\": 1}, \"unexpected\": \"member\"");
        testStrings.add("{\"jsonrpc\": \"2\", \"result\": 19, \"id\": 1}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"result\": 19, \"id\": \"stringid\"}");

        for (String ts : testStrings) {
            AbstractResponse resp;
            try {
                System.out.println(ts);
                resp = new Response(ts);

                try {
                    switch (resp.getId().getType()) {
                        case NULL:
                            System.out.println("ID nullo");
                            break;
                        case INT:
                            System.out.println("ID: " + String.valueOf(resp.getId().getInt()));
                            break;
                        case STRING:
                            System.out.println("ID: " + resp.getId().getString());
                    }


                    if (resp.hasError()) {
                        Error e = resp.getError();
                        System.out.println("Error code: " + e.getErrorCode());
                        System.out.println("Error message: " + e.getErrorMessage());
                        if (e.hasErrorData()) {System.out.println("Error data: " + readMember(e.getErrorData()));}
                    } else {
                        System.out.println("Risultato: " + readMember(resp.getResult()));
                    }
                } catch (Exception e) {
                    System.out.println("NON COSTRUTTORE: " + e.getClass().toString() + " - " + e.getMessage());
                }
            } catch (JSONException e) {
                System.out.println("COSTRUTTORE: " + e.getClass().toString() + " - " + e.getMessage());
            } finally {
                System.out.println(System.lineSeparator());
                System.out.println(System.lineSeparator());
                System.out.println(System.lineSeparator());
            }
        }
    }

    private static void testRequest() {
        ArrayList<String> testStrings = new ArrayList<>();
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"update\", \"params\": [1,2,3,4,5]}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"subtrahend\": 23, \"minuend\": 42}, \"id\": 3}");
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"test\", \"params\": {\"subtrahend\": 23, \"minuend\": 42, \"subobj\": {\"par1\": 34, \"par2\": \"value\", \"array\": [1,2,3]}}, \"id\": 3}");
        testStrings.add("");
        testStrings.add("test");
        testStrings.add("{\"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}"); //no jsonrpc 2.0
        testStrings.add("{\"jsonrpc\": \"3.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": 1}"); //jsonrpc <> 2.0
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": 42, \"id\": 1}"); //parametri non structure
        testStrings.add("{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42, 23], \"id\": null}"); //id nullo

        for (String ts : testStrings) {
            AbstractRequest req;
            try {
                System.out.println(ts);
                req = new Request(ts);


                try {
                    System.out.println("Notifica: " + req.isNotify());
                    if (!req.isNotify()) {
                        switch (req.getId().getType()) {
                            case NULL:
                                System.out.println("ID nullo");
                                break;
                            case INT:
                                System.out.println("ID: " + String.valueOf(req.getId().getInt()));
                                break;
                            case STRING:
                                System.out.println("ID: " + req.getId().getString());
                        }
                    }
                    System.out.println("Method: " + req.getMethod());
                    System.out.println("Params: " + readStructured(req.getParams()));
                } catch (Exception e) {
                    System.out.println("NON COSTRUTTORE: " + e.getClass().toString() + " - " + e.getMessage());
                }

            } catch (JSONException e) {
                System.out.println("COSTRUTTORE: " + e.getClass().toString() + " - " + e.getMessage());
            } finally {
                System.out.println(System.lineSeparator());
                System.out.println(System.lineSeparator());
                System.out.println(System.lineSeparator());
            }

        }
    }


    private static String readStructured(StructuredMember params) throws JSONException {
        if (params.isArray()) {
            return readArray(params.toList());
        } else {
            return readObj(params.toMap());
        }

    }
    private static String readArray(ArrayList<Member> params) throws JSONException {
        StringBuilder val= new StringBuilder();
        for (Member m : params) {
            val.append(readMember(m));
            val.append(System.lineSeparator());
        }
        return val.toString();
    }
    private static String readObj(HashMap<String, Member> params) throws JSONException {
        StringBuilder val= new StringBuilder();
        for (HashMap.Entry<String, Member> par : params.entrySet()) {
            val.append(par.getKey());
            val.append(": ");
            val.append(readMember(par.getValue()));
            val.append(System.lineSeparator());
        }
        return val.toString();
    }

    private static String readMember(Member m) throws JSONException{
        switch (m.getType()) {
            case NULL: return "NULL";
            case STRING: return m.toString();
            case NUMBER: return String.valueOf(m.toNumber());
            case BOOL: return String.valueOf(m.toBool());
            case OBJ: return readObj(m.toMap());
            case ARRAY: return readArray(m.toList());
            default: throw new JSONException("Unexpected member type");
        }
    }

}
