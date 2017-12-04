package jsonrpc;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public abstract class JsonRpcObj {
    JSONObject obj;
    //private boolean valid;
    String jsonRpcString;

    String getJsonString() {
        return jsonRpcString;
    }

    /*public boolean isValid() {
        return valid;
    }*/

    abstract protected JSONObject toJsonObj() throws JSONException; //crea oggetto json rpc utilizzando attributi. implementata in maniera differente in richiesta, risposta e errore

    static boolean checkMembersSubset(Enum<?> members[], JSONObject obj) {
        //verifica l'oggetto abbia solo i parametri contenuti nell'array dei membri
        ArrayList<String> memNames = new ArrayList<>();
        for (Enum<?> mem : members) {
            memNames.add(mem.toString());
        }
        for (String m : JSONObject.getNames(obj)) {
            if (!memNames.contains(m)) {
                return false;
            }
        }
        return true;
    }

    static JSONObject putMember(JSONObject obj, String key, Member value) throws JSONException {
        switch (value.getType()) {
            case ARRAY:
                return obj.put(key, value.getList());
            case OBJ:
                return obj.put(key, value.getMap());
            case BOOL:
                return obj.put(key, value.getBool());
            case NUMBER:
                return obj.put(key, value.getNumber());
            case STRING:
                return obj.put(key, value.getString());
            case NULL:
                return obj.put(key, JSONObject.NULL);
            default: throw new JSONException("Invalid member type");
        }
    }

    static JSONObject putStructuredMember(JSONObject obj, String key, StructuredMember member) throws JSONException {
        if (member.isArray()) {
            return obj.put(key, member.getList());
        } else {
            return obj.put(key, member.getMap());
        }
    }
}
