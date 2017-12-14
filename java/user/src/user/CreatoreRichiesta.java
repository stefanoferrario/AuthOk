package user;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

//bisogna capire cosa fare per l' ID della connessione

public class CreatoreRichiesta implements IntUtente, IntAdmin {

	private IClient clientUtente = new Client(5001); // meglio mettere una costante per le porte da usare
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
	
	// ritorna la stringa di autorizzazione, viene ritornato null se si sceglie di
	// non sovrascrivere un' autorizzazione gi� esistente
	public String creaAutorizzazione(String idUtente, int livello, Date scadenza) throws JSONRPCException, IOException {
		boolean inputCorretto = false;
		if (verificaEsistenzaAutorizzazione(idUtente)) {
			Scanner scanner = new Scanner(System.in);
			Pattern pattern = Pattern.compile("s|n");
			String temp = null;
			Matcher matcher =  null;
			while (!inputCorretto) {
				System.out.println("Autorizzazione gi� esistente: sovrascrivere l' autorizzazione attuale? (s/n)");
				temp = scanner.nextLine();
				matcher = pattern.matcher(temp);
				// � fondamentale usare nextLine() e non next() perch� il pattern deve verificare l' intero input.
				// per esempio: con next() un input tipo "s s " sarebbe accettato, con nextLine() no
				if (matcher.matches()) {
					// input senza errori
					if (temp.equals("s")) {
						revocaAutorizzazione(idUtente);
					} else {
						scanner.close();
						return null;
					}
					inputCorretto = true;
				} else { // input con errori
					System.out.println("FORMATO INPUT NON CORRETTO: eseguire nuovamente l' operazione");
				}
			}
			scanner.close();
		}
		// il caso particolare di un' autorizzazione gi� presente � stato gestito, eseguire semplice autorizzazione
		members.clear();
		members.add(new Member(idUtente));
		members.add(new Member(livello));
		members.add(new Member(scadenza.toString())); // questo � giusto???

		Request req = new Request("creaautorizzazione", new StructuredMember(members), new Id(getId()));
		Response rep = clientUtente.sendRequest(req);
		if (!rep.hasError()) {
			return rep.getResult().getString();
		} else {
			throw new JSONRPCException(rep.getError().toString());
		}
	}

	// aspettare che � pronto il metodo sul server e poi modificare il getNome
	public boolean revocaAutorizzazione(String idUtente) throws JSONRPCException {
		members.clear();
		members.add(new Member(idUtente));
		Request req = new Request("revocaautorizzazione", new StructuredMember(members), new Id(getId()));
		Response rep = clientUtente.sendRequest(req);
		if (!rep.hasError()) {
			return rep.getResult().getBool();
		} else {
			throw new JSONRPCException(rep.getError().toString());
		}
	}

	public boolean verificaEsistenzaAutorizzazione(String idUtente) throws JSONRPCException {
		members.clear();
		members.add(new Member(idUtente));
		Request req = new Request(Methods.VERIFICA_ESISTENZA_AUTORIZZAZIONE.getName(), new StructuredMember(members), new Id(getId()));
		Response rep = clientUtente.sendRequest(req);
		if (!rep.hasError()) {
			return rep.getResult().getBool();
		} else {
			throw new JSONRPCException(rep.getError().toString());
		}
	}

	public String creaToken(String chiave, String idRisorsa) throws JSONRPCException {
		members.clear();
		members.add(new Member(chiave));
		members.add(new Member(idRisorsa));
		Request req = new Request(Methods.CREA_TOKEN.getName(), new StructuredMember(members), new Id(getId()));
		Response rep = clientUtente.sendRequest(req);
		if (!rep.hasError()) {
			return rep.getResult().getString();
		} else {
			throw new JSONRPCException(rep.getError().toString());
		}
	}

	public static void main(String[] args) throws JSONRPCException, IOException {

	}
}