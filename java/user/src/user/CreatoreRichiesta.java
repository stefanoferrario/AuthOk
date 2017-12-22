package user;

import authorizer.GestoreRisorse.ResourceTypes;
import authorizer.Methods;
import jsonrpc.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreatoreRichiesta implements IntUtente, IntAdmin {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static IClient clientUtente;
    private ArrayList<Member> members = new ArrayList<>();
    private static int contatoreID = 0;
    private static int getId() {return contatoreID++;}

    CreatoreRichiesta(int port) {
        clientUtente = new Client(port);
    }
    // ritorna la stringa di autorizzazione
    public String creaAutorizzazione(String idUtente, int livello, Date scadenza) throws AuthorizerException {
        members.clear();
        members.add(new Member(idUtente));
        members.add(new Member(livello));
        members.add(new Member(DATE_FORMAT.format(scadenza)));

        Request req = new Request(Methods.CREA_AUTORIZZAZIONE.getName(), new StructuredMember(members), new Id(getId()));
        try {
            Response rep = clientUtente.sendRequest(req);
            if (!rep.hasError()) {
                return rep.getResult().getString();
            } else {
                throw new AuthorizerException(rep.getError());
            }
        } catch (JSONRPCException e) {
            throw new AuthorizerException(e.getMessage());
        }
    }

    // aspettare che � pronto il metodo sul server e poi modificare il getNome
    public boolean revocaAutorizzazione(String key) throws AuthorizerException {
        members.clear();
        members.add(new Member(key));
        Request req = new Request(Methods.REVOCA_AUTORIZZAZIONE.getName(), new StructuredMember(members), new Id(getId()));
        try {
            Response rep = clientUtente.sendRequest(req);
            if (!rep.hasError()) {
                return rep.getResult().getBool();
            } else {
                throw new AuthorizerException(rep.getError());
            }
        } catch (JSONRPCException e) {
            throw new AuthorizerException(e.getMessage());
        }
    }

    public String verificaEsistenzaAutorizzazione(String idUtente) throws AuthorizerException {
        members.clear();
        members.add(new Member(idUtente));
        Request req = new Request(Methods.VERIFICA_ESISTENZA_AUTORIZZAZIONE.getName(), new StructuredMember(members), new Id(getId()));
        try {
            Response rep = clientUtente.sendRequest(req);
            if (!rep.hasError()) {
                ArrayList<Member> result = rep.getResult().getStructuredMember().getList();
                if (!result.get(0).getBool())
                    return null;
                else
                    return result.get(1).getString();
            } else {
                throw new AuthorizerException(rep.getError());
            }
        } catch (JSONRPCException e) {
            throw new AuthorizerException(e.getMessage());
        }
    }

    public String creaToken(String chiave, int idRisorsa) throws AuthorizerException {
        members.clear();
        members.add(new Member(chiave));
        members.add(new Member(idRisorsa));
        Request req = new Request(Methods.CREA_TOKEN.getName(), new StructuredMember(members), new Id(getId()));

        try {
            Response rep = clientUtente.sendRequest(req);
            if (!rep.hasError()) {
                return rep.getResult().getString();
            } else {
                throw new AuthorizerException(rep.getError());
            }
        } catch (JSONRPCException e) {
            throw new AuthorizerException(e.getMessage());
        }
    }

    public HashMap<Integer, Boolean> checkToken(HashMap<Integer, String> tokens) throws AuthorizerException {
        ArrayList<Request> reqs = new ArrayList<>();
        HashMap<Id, Integer> idToResource = new HashMap<>(); //mappa l'id di una richiesta con l'id della risorsa che identifica il token
        for (HashMap.Entry<Integer, String> token : tokens.entrySet()) {
            members.clear();
            members.add(new Member(token.getValue()));
            members.add(new Member(token.getKey()));
            Request req = new Request(Methods.VERIFICA_TOKEN.getName(), new StructuredMember(members), new Id(getId()));
            reqs.add(req);
            idToResource.put(req.getId(), token.getKey());
        }

        ArrayList<Response> resps = clientUtente.sendBatch(reqs);
        HashMap<Integer, Boolean> checked = new HashMap<>();
        for (Response resp : resps) {
            if (resp.hasError()) {throw new AuthorizerException(resp.getError());}
            //i token sono identificati tramite l'id della risorsa a cui si riferiscono
            //id risorsa ricavato dalla mappa che lo associa all'id richiesta
            //salva la validità del token (tempo restante > 0) associandola al token (identificato tramite id risorsa)
            checked.put(idToResource.get(resp.getId()), resp.getResult().getInt() > 0);
        }
        return checked;
    }

    public String checkServer() {
        Request req = new Request(Methods.SERVER_STATE.getName(), null, new Id(getId()));
        try {
            ArrayList<Member> result = clientUtente.sendRequest(req).getResult().getStructuredMember().getList();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                if (i==0)
                    sb.append("Risorse: ");
                else if (i==1)
                    sb.append("Autorizzazioni: ");
                else if (i==2)
                    sb.append("Token: ");
                sb.append(System.lineSeparator());

                if (result.get(i).isNull()) {
                    sb.append("Insieme vuoto");
                } else {
                    for (Member m : result.get(i).getStructuredMember().getList()) {
                        for (HashMap.Entry<String, Member> e : m.getStructuredMember().getMap().entrySet()) {
                            sb.append(e.getKey());
                            sb.append(": ");
                            if (e.getValue().getType() == Member.Types.STRING) {
                                sb.append(e.getValue().getString());
                            } else {
                                sb.append(e.getValue().getInt());
                            }
                            sb.append(" - ");
                        }
                        sb.append(System.lineSeparator());
                    }
                }
                sb.append(System.lineSeparator());
            }
            return sb.toString();

        } catch (JSONRPCException e) {
            return e.getMessage();
        }
    }


    @Override
    public void creaRisorsa(int id, int livello, ResourceTypes type) throws AuthorizerException {
        members.clear();
        members.add(new Member(id));
        members.add(new Member(livello));
        members.add(new Member(type.getName()));
        Request req = new Request(Methods.CREA_RISORSA.getName(), new StructuredMember(members), new Id(getId()));

        try {
            Response rep = clientUtente.sendRequest(req);
            if (rep.hasError()) {
                throw new AuthorizerException(rep.getError());
            }
        } catch (JSONRPCException e) {
            throw new AuthorizerException(e.getMessage());
        }
    }

    @Override
    public void modificaLivRisorsa(int id, int livello) throws AuthorizerException {
        members.clear();
        members.add(new Member(id));
        members.add(new Member(livello));
        Request req = new Request(Methods.MODIFICA_LIV_RISORSA.getName(), new StructuredMember(members), new Id(getId()));

        try {
            Response rep = clientUtente.sendRequest(req);
            if (rep.hasError()) {
                throw new AuthorizerException(rep.getError());
            }
        } catch (JSONRPCException e) {
            throw new AuthorizerException(e.getMessage());
        }
    }

    @Override
    public void modificaIdRisorsa(int currID, int newID) throws AuthorizerException {
        members.clear();
        members.add(new Member(currID));
        members.add(new Member(newID));
        Request req = new Request(Methods.MODIFICA_ID_RISORSA.getName(), new StructuredMember(members), new Id(getId()));

        try {
            Response rep = clientUtente.sendRequest(req);
            if (rep.hasError()) {
                throw new AuthorizerException(rep.getError());
            }
        } catch (JSONRPCException e) {
            throw new AuthorizerException(e.getMessage());
        }
    }

    @Override
    public boolean cancellaRisorsa(int id) throws AuthorizerException {
        members.clear();
        members.add(new Member(id));
        Request req = new Request(Methods.CANCELLA_RISORSA.getName(), new StructuredMember(members), new Id(getId()));

        try {
            Response rep = clientUtente.sendRequest(req);
            if (!rep.hasError()) {
                return rep.getResult().getBool();
            } else {
                throw new AuthorizerException(rep.getError());
            }
        } catch (JSONRPCException e) {
            throw new AuthorizerException(e.getMessage());
        }
    }

    public static void main(String[] args) throws JSONRPCException, IOException {
        clientUtente.sendNotify(new Request("prova", null));
    }
}