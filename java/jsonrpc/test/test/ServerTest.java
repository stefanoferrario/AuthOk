package test;

import jsonrpc.*;
import jsonrpc.Error;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import zeromq.IZmqClient;
import zeromq.ZmqClient;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ServerTest {
    private static int port = 5101;
    private Server server;
    private IZmqClient client;
    private Request req = new Request("method", null, new Id(1));
    private Request not = new Request("method", null);
    private Response resp = new Response(new Id(1), new Member());
    private static final Error PARSE_ERROR = new Error(Error.Errors.PARSE);

    @Before
    public void setUp() {
        server = new Server(port);
        client = new ZmqClient(port);
        port++;
    }

    @Test
    public void receive() {
        //receive single request (JSONObject)
        client.send(req.getJsonString());
        ArrayList<Request> r = server.receive();
        assertEquals(1, r.size());
        assertEquals(req, r.get(0));
        //receive batch of one request
        ArrayList<Request> reqs = new ArrayList<>();
        reqs.add(not);
        Batch b = new Batch(reqs);
        client.send(b.getRequestJSON());
        r = server.receive();
        assertEquals(1, r.size());
        assertEquals(not, r.get(0));
        //receive multiple request (batch) (JSONArray)
        reqs = new ArrayList<>();
        reqs.add(req);
        reqs.add(not);
        b = new Batch(reqs);
        client.send(b.getRequestJSON());
        r = server.receive();
        assertEquals(2, r.size());
        assertEquals(req, r.get(0));
        assertEquals(not, r.get(1));

    }

    private String thread_received;
    class Requester implements Runnable {
        Requester(String req) {this.req = req;}
        private String req;
        public void run() {
            thread_received = client.request(req);
        }
    }
    @Test
    public void receiveInvalid() throws InterruptedException {
        Thread c = new Thread(new Requester("not a json object or json array"));
        c.start();
        ArrayList<Request> receivedReqs = server.receive();
        c.join();

        Response resp = new Response(thread_received);
        assertEquals(0, receivedReqs.size());
        assertTrue(resp.hasError());
        assertEquals(PARSE_ERROR, resp.getError());
    }

    @Test (expected = UnsupportedOperationException.class)
    public void replySingleEx() throws JSONRPCException {
        server.reply(new Response(new Id(), new Member()));
        fail("Expected Unsupported Operation exception");
    }

    @Test (expected = JSONRPCException.class)
    public void singleReplyToMultiReqs() throws JSONRPCException {
        ArrayList<Request> multiReqs = new ArrayList<>();
        multiReqs.add(req);
        multiReqs.add(req);
        Batch b = new Batch(multiReqs);
        Thread c = new Thread(new Requester(b.getRequestJSON()));
        c.start();
        ArrayList<Request> receivedReqs = server.receive();

        assertEquals(multiReqs, receivedReqs);
        server.reply(resp);

        fail("Expected JSONRPC exception");
    }

    @Test (expected = JSONRPCException.class)
    public void multiReplyToSingleReq() throws JSONRPCException {
        Thread c = new Thread(new Requester(req.getJsonString()));
        c.start();
        ArrayList<Request> receivedReq = server.receive();
        assertEquals(1, receivedReq.size());
        assertEquals(req, receivedReq.get(0));
        ArrayList<Response> resps = new ArrayList<>();
        resps.add(resp);
        resps.add(resp);
        server.reply(resps);
        fail("Expected JSONRPC exception");
    }

    @Test (expected = JSONRPCException.class)
    public void invalidReplyToBatch() throws JSONRPCException, JSONException {
        ArrayList<Request> reqs = new ArrayList<>();
        reqs.add(req);
        Batch b = new Batch(reqs);
        Thread c = new Thread(new Requester(b.getRequestJSON()));

        c.start();
        ArrayList<Request> receivedReqs = server.receive();
        assertEquals(reqs, receivedReqs);
        server.reply(resp);
        fail("Expected JSONRPC exception");
    }
    @Test
    public void replyToUnidimensionalBatch() throws JSONRPCException, InterruptedException, JSONException {
        ArrayList<Request> reqs = new ArrayList<>();
        reqs.add(req);
        Batch b = new Batch(reqs);
        Thread c = new Thread(new Requester(b.getRequestJSON()));


        //risposta con array di dim 1
        c.start();
        ArrayList<Request> receivedReqs = server.receive();
        assertEquals(reqs, receivedReqs);
        ArrayList<Response> resps = new ArrayList<>();
        resps.add(resp);
        server.reply(resps);
        c.join();
        JSONArray receivedResps = new JSONArray(thread_received);
        b.put(receivedResps); //no exception thrown
    }

    @Test
    public void onlyNotifiesBatch() throws JSONRPCException, InterruptedException {
        ArrayList<Request> allNotifies = new ArrayList<>();
        allNotifies.add(not);
        allNotifies.add(not);
        Batch b = new Batch(allNotifies);

        (new Thread(new Requester(b.getRequestJSON()))).start();
        ArrayList<Request> receivedReqs = server.receive();
        Batch temp = new Batch(receivedReqs);
        assertTrue(temp.isOnlyNotifies());
        server.reply(new ArrayList<>());
    }

    @Test (expected = JSONRPCException.class)
    public void emptyReplyToReq() throws JSONRPCException {
        (new Thread(new Requester(req.getJsonString()))).start();
        server.receive();
        server.reply(new ArrayList<>());
        fail("Expected JSONRPC exception");
    }

    @Test (expected = JSONRPCException.class)
    public void emptyReplyToReqBatch() throws JSONRPCException {
        ArrayList<Request> reqs = new ArrayList<>();
        reqs.add(req);
        reqs.add(req);
        (new Thread(new Requester((new Batch(reqs)).getRequestJSON()))).start();
        server.receive();
        server.reply(new ArrayList<>());
        fail("Expected JSONRPC exception");
    }

    @Test (expected = NullPointerException.class)
    public void replyNullList() throws JSONRPCException {
        (new Thread(new Requester(req.getJsonString()))).start();
        server.receive();
        server.reply((ArrayList<Response>)null);
        fail("Expected null ptr exception");
    }

    @Test (expected = NullPointerException.class)
    public void replyNullRequest() throws JSONRPCException {
        (new Thread(new Requester(req.getJsonString()))).start();
        server.receive();
        server.reply((Response) null);
        fail("Expected null ptr exception");
    }

    /*
    testare invii normali*/

    @Test
    public void replySingleResponse() {
    }

    @Test
    public void replyMultiResponses() {

    }
}