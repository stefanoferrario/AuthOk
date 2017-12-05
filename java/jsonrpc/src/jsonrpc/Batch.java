package jsonrpc;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;

class Batch {
    private HashMap<Request, Response> batch;

    Batch(ArrayList<Request> requests) {
        batch = new HashMap<>();
        for (Request r : requests) {
            batch.put(r,null);
        }
    }
    Batch(JSONArray requestArray) throws JSONException, JSONRPCException {
        batch = new HashMap<>();
        for (int i=0; i<requestArray.length(); i++) {
            batch.put(new Request(requestArray.getJSONObject(i).toString()),null);
        }
    }
    private void put(Request keyReq, Response valueResp) {
        batch.put(keyReq,valueResp);
    }
    void put(ArrayList<Response> responses) {
        ArrayList<Request> requests = new ArrayList<>(batch.keySet());
        int notifyCount = 0;
        for (int i = 0; i < requests.size(); i++) {
            Request req = requests.get(i);
            if (!req.isNotify()) {
                this.put(req, responses.get(i-notifyCount));
                //la risposta ad una richiesta non notifica deve esserci, altrimenti indexoutofbounds
            } else {
                notifyCount++;
            }
        }
    }
    void put(JSONArray responses) {
        ArrayList<Request> requests = new ArrayList<>(batch.keySet());
        for (int i = 0; i < responses.length(); i++) {
            Request req = requests.get(i);
            if (!req.isNotify()) {
                try {
                    this.put(req, new Response(responses.getString(i)));
                } catch (JSONRPCException | JSONException e) {

                }
            }
        }
    }

    ArrayList<Request> getRequests() {
        return new ArrayList<>(batch.keySet());
    }
    ArrayList<Response> getResponses() {
        return new ArrayList<>(batch.values());
    }
    String getResponseJSON() {
        JSONArray arr = new JSONArray();
        for (Response r : batch.values()) {
            if (r != null) { //le risposte alle notifiche non vengono inviate
                arr.put(r.getObj());
            }
        }
        return arr.toString();
    }

    String getRequestJSON() {
        JSONArray arr = new JSONArray();
        for (Request r : batch.keySet()) {
            arr.put(r.getObj());
        }
        return arr.toString();
    }
}
