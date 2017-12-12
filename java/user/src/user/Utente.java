package user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import jsonrpc.JSONRPCException;

public class Utente {

	public Utente(String _nome) {
		nome = _nome;
	}

	private String nome = "default";
	private String chiave = "0000";
	@SuppressWarnings("unused")
	private ArrayList<String> tokens;

	public String getChiave() {
		return chiave;
	}

	public String getNome() {
		return nome;
	}

	public void setChiave(String s) {
		chiave = s;
	}

	public static void main(String[] args) {
		IntUtente clientUtente = new CreatoreRichiesta();
		Utente utente = new Utente("Paolo");
		Scanner scanner = new Scanner(System.in);
		try {
			if(clientUtente.verificaEsistenzaAutorizzazione("Paolo")) 
				System.out.println("l' utente è autorizzato");
			else 
				System.out.println("l' utente NON è autorizzato");
			System.out.println(utente.getChiave());
			System.out.println("Inserire livello per l' autorizzazione");
			utente.setChiave(clientUtente.creaAutorizzazione(utente.getNome(), scanner.nextInt(), new Date()));
			System.out.println(utente.getChiave());
		} catch (JSONRPCException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}finally {
			scanner.close();
		}
	}
}