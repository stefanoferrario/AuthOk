package user;

import java.util.Date;
import authorizer.MethodsUtils;
import jsonrpc.Client;
import jsonrpc.IClient;
import jsonrpc.Id;
import jsonrpc.JSONRPCException;
import jsonrpc.Member;
import jsonrpc.Request;
import jsonrpc.Response;
import jsonrpc.StructuredMember;
import authorizer.MethodsUtils.Methods;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CreatoreRichiesta implements IntUtente, IntAdmin {

    private static IClient clientUtente = new Client(MethodsUtils.PORT);
    private ArrayList<Member> members = new ArrayList<>();
    private static int contatoreID = 0;

    public void creaRisorsa() {
        System.out.println("la risorsa � stata creata");
    }

    public void modificaRisorsa() {
        System.out.println("la risorsa � stata modificata");
    }

    public void cancellaRisorsa() {
        System.out.println("la risorsa � stata cancellata");
    }

    private static int getId() {return contatoreID++;}

    // ritorna la stringa di autorizzazione
    public String creaAutorizzazione(String idUtente, int livello, Date scadenza) throws AuthorizerException {
        members.clear();
        members.add(new Member(idUtente));
        members.add(new Member(livello));
        members.add(new Member(MethodsUtils.DATE_FORMAT.format(scadenza)));

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

    public static void main(String[] args) throws JSONRPCException, IOException {
        clientUtente.sendNotify(new Request("prova", null));
    }
}