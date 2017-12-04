package jsonrpc;

import org.json.JSONException;
import org.json.JSONObject;

public class Request extends AbstractRequest{
    public Request(String method, StructuredMember params)  throws JSONException {
        super(method, params, null);
    }
    public Request(String method, StructuredMember params, Id id) throws JSONException {
        super(method, params, id);
    }

    Request(String jsonRpcString) throws JSONException{
        //https://stleary.github.io/JSON-java/
        //https://stackoverflow.com/questions/21720759/convert-a-json-string-to-a-hashmap
        obj = new JSONObject(jsonRpcString);

        /*definire eccezioni più specifiche*/
        if (!obj.getString(Members.JSONRPC.toString()).equals(VER)) {throw new JSONException("Not jsonrpc 2.0");}
        if (!obj.has(Members.METHOD.toString())) {throw new JSONException("Method member not defined");}
        method = obj.getString(Members.METHOD.toString());

        //i parametri possono essere omessi, ma se presenti devono essere o un json array o un json object (non può essere una primitiva (es. "params" : "foo"))
        if (obj.has(Members.PARAMS.toString())) {
            params = StructuredMember.toStructuredMember(obj.get(Members.PARAMS.toString()));
        } else {
            params = null;
        }

        notify = !obj.has(Members.ID.toString());
        id = notify ? null: Id.toId(obj.get(Members.ID.toString()));

        //verifica che non ci siano altri parametri
        if (!checkMembersSubset(Members.values(), obj)) {throw new JSONException("Unexpected member");}

        this.jsonRpcString = obj.toString();
    }

    @Override
    protected JSONObject toJsonObj() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(Members.JSONRPC.toString(), VER);
        if (method == null || method.isEmpty()) {throw new JSONException("Method member not defined");}
        object.put(Members.METHOD.toString(), method);
        if (params != null) { putStructuredMember(object, Members.PARAMS.toString(), params);} //opzionale
        if (!notify) {
            putId(object, Members.ID.toString(), id);
        }
        return object;
    }
}
