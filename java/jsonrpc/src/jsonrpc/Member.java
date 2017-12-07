package jsonrpc;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

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
        value = new StructuredMember(obj);
        type = Types.OBJ;
    }
    public Member(JSONArray array) throws JSONRPCException {
        if (array == null) {throw new JSONRPCException("Member value is null");}
        value = new StructuredMember(array);
        type = Types.ARRAY;
    }
    public Member(StructuredMember m) throws JSONRPCException {
        if (m == null) {throw new JSONRPCException("Member value is null");}
        value = m;
        type = m.isArray() ? Types.ARRAY : Types.OBJ;
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

    public StructuredMember getStructuredMember() throws ClassCastException {
        if (type != Types.OBJ && type != Types.ARRAY) {throw new ClassCastException("Not a structured member");}
        return (StructuredMember)value;
    }
    public ArrayList<Member> getList() throws ClassCastException {
        if (type != Types.ARRAY) {throw new ClassCastException("Not a json array");}
        return ((StructuredMember)value).getList();
    }
    public HashMap<String, Member> getMap() throws ClassCastException {
        if (type != Types.OBJ) {throw new ClassCastException("Not a json object");}
        return ((StructuredMember)value).getMap();
    }

    JSONObject getJSONObj() throws ClassCastException {
        if (type != Types.OBJ) {throw new ClassCastException("Not a json object");}
        return ((StructuredMember)value).getJSONObject();
    }
    JSONArray getJSONArray() throws ClassCastException {
        if (type != Types.ARRAY) {throw new ClassCastException("Not a json array");}
        return ((StructuredMember)value).getJSONArray();
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

    static Member toMember(Object obj) throws JSONRPCException {
        return parse(obj);
    }
}
