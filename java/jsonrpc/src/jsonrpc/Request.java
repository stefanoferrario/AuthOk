package jsonrpc;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;

public class Request extends AbstractRequest{
    public Request(String method, HashMap<String,Object> params)  throws org.json.JSONException {
        super(method, params);
    }
    public Request(String method, HashMap<String,Object> params, int id) throws org.json.JSONException {
        super(method, params, id);
    }
    public Request(String method, HashMap<String, Object> params, String id) throws org.json.JSONException {
        super(method, params, id);
    }

    Request(String jsonRpcString) throws Exception{
        //https://stleary.github.io/JSON-java/
        //https://stackoverflow.com/questions/21720759/convert-a-json-string-to-a-hashmap
        obj = new JSONObject(jsonRpcString);

        /*definire eccezioni più specifiche*/
        if (!obj.getString(Members.JSONRPC.toString()).equals("2.0")) {throw new Exception();}
        if (!obj.has(Members.METHOD.toString())) {throw new Exception();}
        method = obj.getString(Members.METHOD.toString());

        //i parametri possono essere omessi, ma se presenti devono essere o un json array o un json object (non può essere una primitiva (es. "params" : "foo"))
        if (obj.has(Members.PARAMS.toString())) {
            Object tmpParams = obj.get(Members.PARAMS.toString());
            if (tmpParams instanceof JSONObject) {
                params = new JSONObject(toMap((JSONObject)tmpParams));
            } else if (tmpParams instanceof JSONArray) {
                params = new JSONArray(toList((JSONArray) tmpParams));
            } else {
                throw  new Exception();
            }
        }

        //id = string o int o null
        //da specifica id può essere un numero, ma non dovrebbe contenere parti decimali. per questo si usa Integer e non Number
        notify = !obj.has(Members.ID.toString());
        if (!notify) {
            if (obj.isNull(Members.ID.toString())) {
                id = null;
            } else {
                id = obj.get(Members.ID.toString());
                if (!(id instanceof Integer) && !(id instanceof String)) {throw new Exception();}
            }
        }

        //verifica che non ci siano altri parametri
        if (!checkMembersSubset(Members.values(), obj)) {throw new Exception();}

        this.jsonRpcString = obj.toString();
    }

    @Override
    protected JSONObject toJsonRpc() throws org.json.JSONException {
        JSONObject object = new JSONObject();
        object.put(Members.JSONRPC.toString(), "2.0");
        object.put(Members.METHOD.toString(), method);
        if (params != null) { object.put(Members.PARAMS.toString(), params);} //opzionale
        if (!notify) {
            if (id==null)
                object.put(Members.ID.toString(),JSONObject.NULL);
            else
                object.put(Members.ID.toString(),id);
        }

        return object;
    }

    JSONObject createErrorObj() {
        //analizza la propria stinga jsonrpc e restituisce l'errore del caso
        //può diventare un getter e far settare un attributo errorcode al costruttore per stringa che intercetta le eccezioni e nei vari catch salva il codice dell'eccezione
        //il costruttore per stringa non lancerebbe più eccezioni
        //se non ci sono errori lancia eccezione
        return null;
    }

}
