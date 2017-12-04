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

    public StructuredMember(JSONObject obj) throws JSONException{
        map = toMap(obj);
        list = null;
        isArray = false;
    }
    public StructuredMember(JSONArray array) throws JSONException{
        list = toList(array);
        map = null;
        isArray = true;
    }

    public StructuredMember(HashMap<String, Member> members) throws JSONException {
        if (members.size() == 0) {throw new JSONException("Members map is empty");}
        map = members;
        list = null;
        isArray = false;
    }

    public StructuredMember(ArrayList<Member> members) throws JSONException{
        if (members.size() == 0) {throw new JSONException("Members list is empty");}
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

    private static HashMap<String, Member> toMap(JSONObject object) throws JSONException{
        if (object == null) {throw new JSONException("Json object is null");}
        if (object.length()==0) {throw new JSONException("Json object is empty");}
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
        if (array == null) {throw new JSONException("Json array is null");}
        if (array.length()==0) {throw new JSONException("Json array is empty");}
        ArrayList<Member > list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            list.add(parse(value));
        }
        //lanciare eccezione se vuoto
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

    public static StructuredMember toStructuredMember(Object obj) throws JSONException{
        if (obj == null) {throw new NullPointerException("Null structured member");}

        if (obj instanceof JSONArray) {
            return new StructuredMember((JSONArray)obj);
        } else if (obj instanceof  JSONObject) {
            return new StructuredMember((JSONObject)obj);
        } else {
            throw new JSONException("Not a structured member");
        }
    }


}
