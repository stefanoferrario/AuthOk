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
    public Member(String string) throws JSONRPCException {
        if (string == null) {throw new JSONRPCException("Member value is null");}
        if (string.isEmpty()) {throw new JSONRPCException("Member value is empty");}
        value = string;
        type = Types.STRING;

    }
    public Member(Number num) throws JSONRPCException {
        if (num == null) {throw new JSONRPCException("Member value is null");}
        value = num;
        type = Types.NUMBER;
    }
    public Member(boolean bool) {
        value = bool;
        type = Types.BOOL;
    }
    public Member(JSONObject obj) throws JSONRPCException {
        if (obj == null) {throw new JSONRPCException("Member value is null");}
        value = toMap(obj);
        type = Types.OBJ;
    }
    public Member(JSONArray array) throws JSONRPCException {
        if (array == null) {throw new JSONRPCException("Member value is null");}
        value = toList(array);
        type = Types.ARRAY;
    }
    public Member(StructuredMember m) throws JSONRPCException {
        if (m == null) {throw new JSONRPCException("Member value is null");}
        if (m.isArray()) {
            value = m.getList();
            type = Types.ARRAY;
        }
        else {
            value = m.getMap();
            type = Types.OBJ;
        }
    }

    public Types getType() {
        return type;
    }
    /*public boolean isStructured() {
        return (type == Types.OBJ || type == Types.ARRAY);
    }*/
    /*public boolean isNull() {
        return type == Types.NULL;
    }*/
    public boolean getBool() throws ClassCastException{
        if (type != Types.BOOL) {throw new ClassCastException("Not a boolean");}
        return (boolean)value;
    }
    public Number getNumber() throws ClassCastException {
        if (type != Types.NUMBER) {throw new ClassCastException("Not a number");}
        return (Number)value;
    }
    public int getInt() throws ClassCastException {
        if (!(value instanceof Integer)) {throw new ClassCastException("Not an integer");}
        return (int)value;
    }
    public String getString() throws ClassCastException {
        if (type != Types.STRING) {throw new ClassCastException("Not a string");}
        return (String)value;
    }
    public HashMap<String,Member> getMap() throws ClassCastException {
        if (type != Types.OBJ) {throw new ClassCastException("Not a json object");}
        return (HashMap<String, Member>)value;
    }
    public ArrayList<Member> getList() throws ClassCastException {
        if (type != Types.ARRAY) {throw new ClassCastException("Not a json array");}
        return (ArrayList<Member>)value;
    }

    private static HashMap<String, Member> toMap(JSONObject object) throws JSONRPCException{
        HashMap<String, Member> map = new HashMap<>();
        Iterator<?> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = (String)keysItr.next();
            try {
                Object value = object.get(key);
                map.put(key, parse(value));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return map;
    }

    private static ArrayList<Member> toList(JSONArray array) throws JSONRPCException {
        ArrayList<Member> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            try {
                Object value = array.get(i);
                list.add(parse(value));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return list;
    }

    private static Member parse(Object value) throws JSONRPCException {
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
            throw new JSONRPCException("Not a valid parameter type");
        }
    }

    public static Member toMember(Object obj) throws JSONRPCException{
        return parse(obj);
    }
}
