package jsonrpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class StructuredMember {
    private HashMap<String, Member> map;
    private ArrayList<Member> list;
    private boolean isArray;

    public StructuredMember(JSONObject obj) throws JSONRPCException{
        map = toMap(obj);
        list = null;
        isArray = false;
    }
    public StructuredMember(JSONArray array) throws JSONRPCException{
        list = toList(array);
        map = null;
        isArray = true;
    }

    public StructuredMember(HashMap<String, Member> members) throws JSONRPCException {
        if (members.size() == 0) {throw new JSONRPCException("Members map is empty");}
        map = members;
        list = null;
        isArray = false;
    }

    public StructuredMember(ArrayList<Member> members) throws JSONRPCException{
        if (members.size() == 0) {throw new JSONRPCException("Members list is empty");}
        list = members;
        map = null;
        isArray = true;
    }


    public boolean isArray() {
        return isArray;
    }
    public boolean isMap() {
        return !isArray;
    }

    public HashMap<String,Member> getMap() throws ClassCastException {
        if (isArray) {throw new ClassCastException("Not a json object");}
        return map;
    }

    public ArrayList<Member> getList() throws ClassCastException {
        if (!isArray) {throw new ClassCastException("Not a json array");}
        return list;
    }

    private static HashMap<String, Member> toMap(JSONObject object) throws JSONRPCException{
        if (object == null) {throw new JSONRPCException("Json object is null");}
        if (object.length()==0) {throw new JSONRPCException("Json object is empty");}
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
        if (array == null) {throw new JSONRPCException("Json array is null");}
        if (array.length()==0) {throw new JSONRPCException("Json array is empty");}
        ArrayList<Member > list = new ArrayList<>();
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
            throw new JSONRPCException("Not a valid parameter type");
        }
    }

    public static StructuredMember toStructuredMember(Object obj) throws JSONRPCException{
        if (obj == null) {throw new NullPointerException("Null structured member");}

        if (obj instanceof JSONArray) {
            return new StructuredMember((JSONArray)obj);
        } else if (obj instanceof  JSONObject) {
            return new StructuredMember((JSONObject)obj);
        } else {
            throw new JSONRPCException("Not a structured member");
        }
    }


}
