package jsonrpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class StructuredMember {
    private Object value;
    private boolean isArray;

    public StructuredMember(JSONObject obj) {
        value = obj;
        isArray = false;
    }
    public StructuredMember(JSONArray array) {
        value = array;
        isArray = true;
    }

    public boolean isArray() {
        return isArray;
    }
    public boolean isMap() {
        return !isArray;
    }

    public HashMap<String,Member> toMap() throws ClassCastException, JSONException {
        if (isArray) {throw new ClassCastException("Not a json object");}
        return toMap((JSONObject)value);
    }

    public ArrayList<Member> toList() throws ClassCastException, JSONException {
        if (!isArray) {throw new ClassCastException("Not a json array");}
        return toList((JSONArray)value);
    }

    private static HashMap<String, Member> toMap(JSONObject object) throws JSONException{
        HashMap<String, Member> map = new HashMap<>();
        Iterator<?> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = (String)keysItr.next();
            Object value = object.get(key);
            map.put(key, parse(value));
        }
        return map;
    }

    private static ArrayList<Member> toList(JSONArray array) throws JSONException {
        ArrayList<Member> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            list.add(parse(value));
        }
        return list;
    }

    private static Member parse(Object value) throws JSONException {
        if (value.equals(JSONObject.NULL)) {
            return new Member();
        } else if(value instanceof JSONArray) {
            return new Member((JSONArray)value);
        } else if(value instanceof JSONObject) {
            return new Member((JSONObject)value);
        } else if(value instanceof Number) {
            return new Member((Number) value);
        } else if(value instanceof String) {
            return new Member((String)value);
        } else if(value instanceof Boolean) {
            return new Member((boolean)value);
        } else {
            throw new JSONException("Not a valid parameter type");
        }
    }

    public static StructuredMember toStructureMember(Object obj) throws JSONException{
        if (obj instanceof JSONArray) {
            return new StructuredMember((JSONArray)obj);
        } else if (obj instanceof  JSONObject) {
            return new StructuredMember((JSONObject)obj);
        } else {
            throw new JSONException("Not a structured member");
        }
    }


}
