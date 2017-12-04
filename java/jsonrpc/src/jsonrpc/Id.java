package jsonrpc;

import org.json.JSONException;
import org.json.JSONObject;

public class Id {
    //id = string o int o null
    //da specifica id può essere un numero, ma non dovrebbe contenere parti decimali. per questo si usa Integer e non Number

    public enum Types{NULL, STRING, INT}
    private Object value;
    private Types type;
    public Id(int id) {
        value = id;
        type = Types.INT;
    }
    public Id(String id) {
        value = id;
        type = Types.STRING;
    }
    public Id() {
        value = null;//JSONObject.NULL;
        type = Types.STRING;
    }

    public int getInt() throws ClassCastException {
        if (type != Types.INT) {throw new ClassCastException("Not an integer");}
        return (Integer)value;
    }

    public String getString() throws ClassCastException {
        if (type != Types.STRING) {throw new ClassCastException("Not a string");}
        return (String)value;
    }

    public boolean isNull() {
        return type==Types.NULL;
    }
    public Types getType() {
        return type;
    }

    public static Id toId(Object id) throws JSONException{
        if (id.equals(JSONObject.NULL)) {
            return new Id();
        } else if (id instanceof Integer) {
            return new Id((int)id);
        } else if (id instanceof String) {
            return new Id((String) id);
        } else {
            throw new JSONException("Invalid id");
        }
    }
}
