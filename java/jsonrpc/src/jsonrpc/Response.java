package jsonrpc;

import org.json.JSONException;
import org.json.JSONObject;

public class Response extends AbstractResponse {
    public Response(Id id, Member result) throws JSONRPCException {
        super(id, result);
    }

    public Response(Id id, Error error) throws JSONRPCException {
        super(id, error);
    }

    Response(String jsonRpcString) throws JSONException, JSONRPCException {
        obj = new JSONObject(jsonRpcString);

        if (!obj.getString(Members.JSONRPC.toString()).equals(VER)) {
            throw new JSONRPCException("Not jsonrpc 2.0");
        }

        if (obj.has(Members.RESULT.toString())) {
            result = Member.toMember(obj.get(Members.RESULT.toString()));
            error = null;
        } else if (obj.has(Members.ERROR.toString())) {
            error = new Error((JSONObject) obj.get(Members.ERROR.toString()));
            result = null;
        } else {
            throw new JSONRPCException("Method member not defined");
        }

        //obbligatorio nelle risposte
        if (obj.has(Members.ID.toString())) {
            id = Id.toId(obj.get(Members.ID.toString()));
        } else {
            throw new JSONRPCException("ID member not defined");
        }

        //verifica che non ci siano altri parametri
        if (!checkMembersSubset(Members.values(), obj)) {
            throw new JSONRPCException("Unexpected member");
        }

        this.jsonRpcString = obj.toString();
    }


    @Override
    protected JSONObject toJsonObj() throws JSONRPCException {
        JSONObject object = new JSONObject();

        try {
            object.put(Members.JSONRPC.toString(), VER);
            if (error == null) {
                putMember(object, Members.RESULT.toString(), result);
            } else {
                object.put(Members.ERROR.toString(), error.getJsonObj());
            }
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        putId(object, Members.ID.toString(), id);

        return object;
    }
}