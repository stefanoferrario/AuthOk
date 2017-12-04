package jsonrpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Member {
    public enum Types {NULL, STRING, NUMBER, BOOL, OBJ, ARRAY}

    //estendere id per avere già value, toString e to toInt, estendere o usare structured member ?
    //definire interfacce
    private Object value;
    private Types type;

    public Member() {
        value = JSONObject.NULL;
        type = Types.NULL;
    }
    public Member(String string) {
        value = string;
        type = Types.STRING;
    }
    public Member(Number num) {
        value = num;
        type = Types.NUMBER;
    }
    public Member(boolean bool) {
        value = bool;
        type = Types.BOOL;
    }
    public Member(JSONObject obj) {
        value = obj;
        type = Types.OBJ;
    }
    public Member(JSONArray array) {
        value = array;
        type = Types.ARRAY;
    }

    public Types getType() {
        return type;
    }
    public boolean isStructured() {
        return (type == Types.OBJ || type == Types.ARRAY);
    }
    public boolean isNull() {
        return value.equals(JSONObject.NULL);
    }
    public boolean toBool() throws ClassCastException{
        if (type != Types.BOOL) {throw new ClassCastException("Not a boolean");}
        return (boolean)value;
    }
    public Number toNumber() throws ClassCastException {
        if (type != Types.NUMBER) {throw new ClassCastException("Not a number");}
        return (Number)value;
    }
    public int toInt() throws ClassCastException {
        if (!(value instanceof Integer)) {throw new ClassCastException("Not an integer");}
        return (int)value;
    }
    public String toString() throws ClassCastException {
        if (type != Types.STRING) {throw new ClassCastException("Not a string");}
        return (String)value;
    }
    public HashMap<String,Member> toMap() throws ClassCastException, JSONException {
        if (type != Types.OBJ) {throw new ClassCastException("Not a json object");}
        return toMap((JSONObject)value);
    }
    public ArrayList<Member> toList() throws ClassCastException, JSONException {
        if (type != Types.ARRAY) {throw new ClassCastException("Not a json array");}
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
        if (value == null) {
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

    public static Member toMember(Object obj) throws JSONException{
        return parse(obj);
    }
}
