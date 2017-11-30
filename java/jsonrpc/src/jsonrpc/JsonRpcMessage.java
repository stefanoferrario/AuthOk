package jsonrpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

abstract class JsonRpcMessage {
    JSONObject obj;
    Object id; //può essere String o Integer (o null in alcuni casi (non notifica))
    //private boolean valid;
    String jsonRpcString;

    public String getJsonString() {
        return jsonRpcString;
    }

    public Object getId() {
        return id;
    }
    public int getIntId() throws NullPointerException, ClassCastException {
        return (int)id;
    }
    public String getStringId() throws NullPointerException, ClassCastException {
        return (String)id;
    }

    /*public boolean isValid() {
        return valid;
    }*/

    abstract protected JSONObject toJsonRpc() throws org.json.JSONException; //crea oggetto json rpc utilizzando attributi. implementata in maniera differente in richiesta e risposta

    static HashMap<String, Object> toMap(JSONObject object) throws JSONException {
        HashMap<String, Object> map = new HashMap<>();
        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            map.put(key, parse(value));
        }
        return map;
    }

    static ArrayList<Object> toList(JSONArray array) throws JSONException {
        ArrayList<Object> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            list.add(parse(value));
        }
        return list;
    }

    private static Object parse(Object value) throws JSONException {
        if(value instanceof JSONArray) {
            value = toList((JSONArray) value);
        } else if(value instanceof JSONObject) {
            value = toMap((JSONObject) value);
        }
        return value;
    }
}
