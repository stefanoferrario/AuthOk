package jsonrpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static jsonrpc.Server.getIdFromRequest;

class Batch {
    private ArrayList<Request> reqs;
    private ArrayList<Response> resps;


    Batch(ArrayList<Request> requests) {
        reqs = requests;
        resps = new ArrayList<>();
        for (Request r : reqs) {
            resps.add(null);
        }
    }
    Batch(JSONArray requestArray) throws JSONException, JSONRPCException {
        reqs = new ArrayList<>();
        resps = new ArrayList<>();

        for (int i=0; i<requestArray.length(); i++) {
            JSONObject o = requestArray.getJSONObject(i);

            String stringReq = o.toString();
            Request req = null;
            Response resp = null;
            try {
                req = new Request(stringReq);
                //resp = null;
            } catch (JSONRPCException e) {
                Id id = getIdFromRequest(stringReq); //tenta di recuperarne l'id, altrimenti id null
                Error err = new Error(Error.Errors.INVALID_REQUEST);
                //req = null;
                resp = new Response(id, err);
            } finally {
                reqs.add(req);
                resps.add(resp);
            }
        }
    }
    private void put(Request req, Response resp) {
        int i = reqs.indexOf(req);
        resps.set(i, resp);
    }
    void put(ArrayList<Response> responses) {
        //gestire eccezioni

        int count = 0; //conta le richieste a cui non va inserita la risposta corrispondente perché non valide o notifiche
        for (int i = 0; i < reqs.size(); i++) {
            Request req = reqs.get(i);
            if (req != null && !req.isNotify()) {
                this.put(req, responses.get(i - count));
                //la risposta ad una richiesta non notifica deve esserci, altrimenti indexoutofbounds
            } else {
                count++;
            }
        }
    }
    void put(JSONArray responses) throws JSONException, JSONRPCException{
        ArrayList<Response> resps = new ArrayList<>();
        for (int i = 0; i<responses.length(); i++) {
            resps.add(new Response(responses.get(i).toString()));
        }
        this.put(resps);
    }

    ArrayList<Request> getAllRequests() {
        return reqs;
    }

    ArrayList<Request> getValidRequests() {
        ArrayList<Request> rq = new ArrayList<>();
        for (Request r : reqs) {
            if (r!=null) {
                rq.add(r);
            }
        }
        return rq;
    }

    ArrayList<Response> getAllResponses() {
        return resps;
    }

    ArrayList<Response> getValidResponses() {
        ArrayList<Response> rp = new ArrayList<>();
        for (Response r : resps) {
            if (r!=null) {
                rp.add(r);
            }
        }
        return rp;
    }

    String getResponseJSON() {
        JSONArray arr = new JSONArray();
        for (Response r : resps) {
            if (r != null) { //le risposte alle notifiche non vengono inviate
                arr.put(r.getObj());
            }
        }
        return arr.toString();
    }

    String getRequestJSON() {
        JSONArray arr = new JSONArray();
        for (Request r : reqs) {
            arr.put(r.getObj());
        }
        return arr.toString();
    }
}
