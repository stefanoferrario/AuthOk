package jsonrpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static jsonrpc.JsonRpcObj.putMember;

public class StructuredMember {
    private JSONArray list;
    private JSONObject map;

    private boolean isArray;

    StructuredMember(JSONObject obj) {
        if (obj.length() == 0) {throw new InvalidParameterException("Json object is empty");}
        map = obj;
        list = null;
        isArray = false;
    }
    StructuredMember(JSONArray array) {
        if (array.length() == 0) {throw new InvalidParameterException("Json array is empty");}
        list = array;
        map = null;
        isArray = true;
    }

    public StructuredMember(HashMap<String, Member> members) {
        if (members.size() == 0) {throw new InvalidParameterException("Members map is empty");}
        map = toMap(members);
        list = null;
        isArray = false;
    }

    public StructuredMember(ArrayList<Member> members) {
        if (members.size() == 0) {throw new InvalidParameterException("Members list is empty");}
        list = toList(members);
        map = null;
        isArray = true;
    }


    public boolean isArray() {
        return isArray;
    }

    public HashMap<String, Member> getMap() {
        if (isArray) {throw new ClassCastException("Not a json object");}

        HashMap<String, Member> hashmap = new HashMap<>();
        Iterator<?> keysItr = map.keys();
        while(keysItr.hasNext()) {
            String key = (String)keysItr.next();
            try {
                Object value = map.get(key);
                hashmap.put(key, Member.toMember(value));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return hashmap;
    }

    public ArrayList<Member> getList() {
        if (!isArray) {throw new ClassCastException("Not a json array");}

        ArrayList<Member > arraylist = new ArrayList<>();
        for(int i = 0; i < list.length(); i++) {
            try {
                Object value = list.get(i);
                arraylist.add(Member.toMember(value));
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return arraylist;
    }

    public JSONObject getJSONObject() { //public solo per test
        if (isArray) {throw new ClassCastException("Not a json object");}
        return map;
    }

    public JSONArray getJSONArray() { //public solo per test
        if (!isArray) {throw new ClassCastException("Not a json array");}
        return list;
    }

    private JSONObject toMap(HashMap<String, Member> members) {
        JSONObject o = new JSONObject();
        for (HashMap.Entry<String, Member> entry : members.entrySet()) {
            try {
                putMember(o, entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return o;
    }

    private JSONArray toList(ArrayList<Member> array) {
        JSONArray a = new JSONArray();
        for (Member value : array) {
            putMember(a, value);
        }
        return a;
    }

    public static StructuredMember toStructuredMember(Object obj) { //public solo per test
        if (obj == null) {throw new NullPointerException("Null structured member");}

        if (obj instanceof JSONArray) {
            return new StructuredMember((JSONArray)obj);
        } else if (obj instanceof  JSONObject) {
            return new StructuredMember((JSONObject)obj);
        } else {
            throw new InvalidParameterException("Not a structured member");
        }
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof StructuredMember))return false;
        StructuredMember o = (StructuredMember) other;
        if (this.isArray) {
            return o.isArray && getList().equals(o.getList());
        } else {
            return (!o.isArray) && getMap().equals(o.getMap());
        }
    }
}