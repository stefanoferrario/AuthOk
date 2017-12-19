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

	public static void main(String[] args) throws JSONRPCException, IOException {
		clientUtente.sendNotify(new Request("prova", null));
	}
}