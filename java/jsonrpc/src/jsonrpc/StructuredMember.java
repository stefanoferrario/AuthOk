package jsonrpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class StructuredMember {
    private JSONArray list;
    private JSONObject map;

    private boolean isArray;

    StructuredMember(JSONObject obj) throws JSONRPCException {
        if (obj.length() == 0) {throw new JSONRPCException("Json object is empty");}
        map = obj;
        list = null;
        isArray = false;
    }
    StructuredMember(JSONArray array) throws JSONRPCException{
        if (array.length() == 0) {throw new JSONRPCException("Json array is empty");}
        list = array;
        map = null;
        isArray = true;
    }

    public StructuredMember(HashMap<String, Member> members) throws JSONRPCException {
        if (members.size() == 0) {throw new JSONRPCException("Members map is empty");}
        map = toMap(members);
        list = null;
        isArray = false;
    }

    public StructuredMember(ArrayList<Member> members) throws JSONRPCException{
        if (members.size() == 0) {throw new JSONRPCException("Members list is empty");}
        list = toList(members);
        map = null;
        isArray = true;
    }


    public boolean isArray() {
        return isArray;
    }

    public HashMap<String, Member> getMap() throws ClassCastException {
        if (isArray) {throw new ClassCastException("Not a json object");}

        HashMap<String, Member> hashmap = new HashMap<>();
        Iterator<?> keysItr = map.keys();
        while(keysItr.hasNext()) {
            String key = (String)keysItr.next();
            try {
                Object value = map.get(key);
                hashmap.put(key, Member.toMember(value));
            } catch (JSONRPCException | JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return hashmap;
    }

    public ArrayList<Member> getList() throws ClassCastException {
        if (!isArray) {throw new ClassCastException("Not a json array");}

        ArrayList<Member > arraylist = new ArrayList<>();
        for(int i = 0; i < list.length(); i++) {
            try {
                Object value = list.get(i);
                arraylist.add(Member.toMember(value));
            } catch (JSONException | JSONRPCException e) {
                System.out.println(e.getMessage());
            }
        }
        return arraylist;
    }

    JSONObject getJSONObject() throws ClassCastException {
        if (isArray) {throw new ClassCastException("Not a json object");}
        return map;
    }

    JSONArray getJSONArray() throws ClassCastException {
        if (!isArray) {throw new ClassCastException("Not a json array");}
        return list;
    }

    private JSONObject toMap(HashMap<String, Member> members) {
        JSONObject o = new JSONObject();
        for (HashMap.Entry<String, Member> entry : members.entrySet()) {
            try {
                switch (entry.getValue().getType()) {
                    case ARRAY: o.put(entry.getKey(), StructuredMember.toStructuredMember(entry.getValue().getJSONArray()).getJSONArray()); break;
                    case OBJ: o.put(entry.getKey(), StructuredMember.toStructuredMember(entry.getValue().getJSONObj()).getJSONObject()); break;
                    case BOOL: o.put(entry.getKey(), entry.getValue().getBool()); break;
                    case NUMBER: o.put(entry.getKey(), entry.getValue().getNumber()); break;
                    case STRING: o.put(entry.getKey(), entry.getValue().getString()); break;
                    case NULL: o.put(entry.getKey(), JSONObject.NULL); break;
                }
            } catch (JSONException | JSONRPCException e) {
                System.out.println(e.getMessage());
            }
        }
        return o;
    }

    private JSONArray toList(ArrayList<Member> array) {
        JSONArray a = new JSONArray();
        for (Member value : array) {
            try {
                switch (value.getType()) {
                    case ARRAY: a.put(StructuredMember.toStructuredMember(value.getJSONArray()).getJSONArray()); break;
                    case OBJ: a.put(StructuredMember.toStructuredMember(value.getJSONObj()).getJSONObject()); break;
                    case BOOL: a.put(value.getBool()); break;
                    case NUMBER: a.put(value.getNumber()); break;
                    case STRING: a.put(value.getString()); break;
                    case NULL: a.put(JSONObject.NULL); break;
                }
            } catch (JSONRPCException e) {
                System.out.println(e.getMessage());
            }
        }
        return a;
    }

    static StructuredMember toStructuredMember(Object obj) throws JSONRPCException{
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