package jsonrpc;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class Response extends AbstractResponse {
    public Response(int id, String result) throws org.json.JSONException{
        super(id, result);
    }

    public Response(int id, String message, int errorCode) throws org.json.JSONException{
        super(id, message, errorCode);
    }


    public Response(int id, String message, int errorCode, Object errorData) throws org.json.JSONException{
        super(id, message, errorCode, errorData);
    }

    Response(String jsonRpcString) throws Exception{
        obj = new JSONObject(jsonRpcString);

        /*definire eccezioni più specifiche*/
        if (!obj.getString(Members.JSONRPC.toString()).equals("2.0")) {throw new Exception();}

        if (obj.has(Members.RESULT.toString())) {
            result = obj.get(Members.RESULT.toString());
            errObj = null;
            errCode = null;
            errMessage = null;
            errData = null;
        } else if (obj.has(Members.ERROR.toString())) {
            JSONObject error = (JSONObject) obj.get(Members.ERROR.toString());
            readErrObj(error);
            result = null;
        } else {
            throw new Exception();
        }

        //obbligatorio nelle risposte
        if (obj.has(Members.ID.toString())) {
            if (obj.isNull(Members.ID.toString())) {
                id = null;
            } else {
                id = obj.get(Members.ID.toString());
                if (!(id instanceof Integer) && !(id instanceof String)) {throw new Exception();}
            }
        } else {
            throw new Exception();
        }

        //verifica che non ci siano altri parametri
        if (!checkMembersSubset(Members.values(), obj)) {throw new Exception();}

        this.jsonRpcString = obj.toString();
    }

    Response(int id, JSONObject error) throws Exception{
        this.id = id;
        this.result = null;
        readErrObj(error);
        this.obj = toJsonRpc();
        this.jsonRpcString = obj.toString();
    }

    @Override
    JSONObject toErrJsonRpc() throws org.json.JSONException{
        JSONObject object = new JSONObject();
        object.put(ErrMembers.CODE.toString(), errCode);
        object.put(ErrMembers.MESSAGE.toString(), errMessage);

        if (errData != null) { //opzionale
            putUndefinedValue(object, ErrMembers.DATA.toString(), errData);
        }

        return object;
    }

    private void readErrObj(JSONObject error) throws Exception{
        this.errObj = error;
        if (error.has(ErrMembers.CODE.toString()) && error.has(ErrMembers.MESSAGE.toString())) {
            errCode = error.getInt(ErrMembers.CODE.toString());
            errMessage = error.getString(ErrMembers.MESSAGE.toString());
        } else {
            throw new Exception();
        }

        if (error.has(ErrMembers.DATA.toString())) {
            Object tmpData = obj.get(ErrMembers.DATA.toString());
            if (tmpData instanceof JSONObject) {
                errData = toMap((JSONObject)tmpData);
            } else if (tmpData instanceof JSONArray) {
                errData = toList((JSONArray)tmpData);
            } else {
                errData = tmpData;
            }
        } else {
            errData = null;
        }

        //verifica che non ci siano altri parametri
        if (!checkMembersSubset(ErrMembers.values(), errObj)) {throw new Exception();}
    }

    @Override
    protected JSONObject toJsonRpc() throws org.json.JSONException{
        JSONObject object = new JSONObject();
        object.put(Members.JSONRPC.toString(), "2.0");

        if (errObj == null) {
            putUndefinedValue(object, Members.RESULT.toString(), result);
        } else {
            object.put(Members.ERROR.toString(), errObj.toString());
        }

        object.put(Members.ID.toString(), id);

        return object;
    }

    private void putUndefinedValue(JSONObject obj, String key, Object value) throws org.json.JSONException{
        //da specifica, possibili tipi
        if (value.getClass().isArray()) {
            obj.put(key, new JSONArray(value));
        } else if (value instanceof Collection) {
            obj.put(key, new JSONArray((Collection)value));
        } else if (value instanceof Map) {
            obj.put(key, new JSONObject((Map)value));
        } else {
            obj.put(key, value);
        }
    }

}
