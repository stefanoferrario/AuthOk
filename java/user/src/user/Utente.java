package user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import jsonrpc.JSONRPCException;

public class Utente {

	private String nome = "default";
	private String chiave = "0";
	private ArrayList<String> tokens; //lista dei token posseduti dall' utente
	
	//si ASSUME che l' identificativo dell' utente sia UNIVOCO
	public Utente(String idUtente) {nome = idUtente;}

	public String getChiave() {return chiave;}

	public String getNome() {return nome;}

	public void setChiave(String s) {chiave = s;}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		IntUtente clientUtente = new CreatoreRichiesta();
		//creo un utente
		Utente utente = new Utente("Paolo");
		//autorizzo l' utente
		Date data = new Date();
		data.setYear(2018);
		data.setMonth(0);
		data.setDate(31);
		try {
			utente.setChiave(clientUtente.creaAutorizzazione(utente.getNome(), 7 , data));
		} catch (JSONRPCException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}