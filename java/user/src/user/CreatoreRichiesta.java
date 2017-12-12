package user;

import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import jsonrpc.Client;
import jsonrpc.IClient;
import jsonrpc.Id;
import jsonrpc.JSONRPCException;
import jsonrpc.Member;
import jsonrpc.Request;
import jsonrpc.Response;
import jsonrpc.StructuredMember;

import java.io.*;
import java.util.ArrayList;

//1)gestione errori: li facciamo gestire all' utente della classe
//2)singleton anche qui? no
//3)come impostare la disposizione dei package del progetto intero : ricalcare struttura package del progetto
//4)fare un metodo(private) per gestire da un unico punto l' errore che potrebbe ritornare dal metodo JSON

public class CreatoreRichiesta implements IntUtente, IntAdmin {
	// campi:
	private IClient clientUtente = new Client(5001); // meglio mettere una costante per le porte da usare
	private ArrayList<Member> members = new ArrayList<>();

	// metodi:
	public void creaRisorsa() {
		System.out.println("la risorsa è stata creata");
	}

	public void modificaRisorsa() {
		System.out.println("la risorsa è stata modificata");
	}

	public void cancellaRisorsa() {
		System.out.println("la risorsa è stata cancellata");
	}

	// questo metodo è da fare qui o la creazione dell' autorizzazione è tutto
	// gestito dall' utente della classe?
	// è ottimizzato il fatto di chiamare i metodi della classe che a loro volta
	// aprono e chiudo connessiono json?

	public String creaAutorizzazione(String nomeUtente, int livello, Date scadenza) throws JSONRPCException, IOException {
		Scanner stdin = new Scanner(System.in);
		Pattern pattern = Pattern.compile("s|S|n|N");
		if (verificaEsistenzaAutorizzazione(nomeUtente)) {
			System.out.println("Autorizzazione già esistente: sovrascrivere l' autorizzazione attuale? (s/n)");
			if (stdin.next(pattern) == "s" || stdin.next(pattern) == "S") {
				System.out.println("...SOVRASCRITTURA IN CORSO...");
				revocaAutorizzazione(nomeUtente);
			} else {
				System.out.println("PROCEDURA DI AUTORIZZAZIONE INTERROTTA");
				stdin.close();
				return null;
			}
		}
		stdin.close();
		// il caso particolare di un' autorizzazione già presente è stato gestito,
		// quindi adesso autorizzo e basta
		members.clear();
		members.add(new Member(nomeUtente));
		members.add(new Member(livello));
		members.add(new Member(scadenza.toString())); // questo è giusto???

		Request req = new Request("creaautorizzazione", new StructuredMember(members), new Id(0));
		Response rep = clientUtente.sendRequest(req);
		return rep.getError().toString();

	}

	public boolean revocaAutorizzazione(String nomeUtente) throws JSONRPCException {
		members.clear();
		members.add(new Member(nomeUtente));
		Request req = new Request("revocaautorizzazione", new StructuredMember(members), new Id(0));
		Response rep = clientUtente.sendRequest(req);
		return rep.getResult().getBool();
	}

	public boolean verificaEsistenzaAutorizzazione(String nomeUtente) throws JSONRPCException {
		members.clear();
		members.add(new Member(nomeUtente));
		Request req = new Request("verificaesistenzaautorizzazione", new StructuredMember(members), new Id(0));
		Response rep = clientUtente.sendRequest(req);
		System.out.println(req.getMethod());
		System.out.println(req.getId());
		System.out.println(req.toString());
		// l' errore bisognerebbe gestirlo in modo diverso da un' autorizzazione non
		// esistente:
		// secondo me bisognerebbe delegare ogni tipo di gestione dell' errore all'
		// utente della classe
		return rep.getResult().getBool();
	}

	public String creaToken(String chiave, String idRisorsa) throws JSONRPCException {
		members.clear();
		members.add(new Member(chiave));
		members.add(new Member(idRisorsa));
		Request req = new Request("creatoken", new StructuredMember(members), new Id(0));
		Response rep = clientUtente.sendRequest(req);
		return rep.getResult().getString();
	}

	public static void main(String[] args) throws JSONRPCException, IOException {
		CreatoreRichiesta client = new CreatoreRichiesta();
		client.verificaEsistenzaAutorizzazione("paolo");
	}
}