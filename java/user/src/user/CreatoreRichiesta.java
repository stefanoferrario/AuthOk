package user;

//import authorizer.Autorizzazione;

import java.util.Date;

public class CreatoreRichiesta implements IntUtente, IntAdmin {
	 public int creaRisorsa() {
		 System.out.println("la risorsa � stata creata");
		 return 1;
	 }
	 public void modificaRisorsa() {
		 System.out.println("la risorsa è stata modificata");
	 }

	 public void cancellaRisorsa() {
		 System.out.println("la risorsa è stata cancellata");
	 }

	 /*public Autorizzazione creaAutorizzazione(String _nomeUtente, int _livello, Date _scadenza) {
		 return new Autorizzazione(_nomeUtente,_livello,_scadenza);
	 }*/

	 public boolean revocaAutorizzazione(String chiave) {
		 System.out.println("Autorizzazione revocata");
		 return true;
	 }

	 public String verificaEsistenzaAutorizzazione() {
		 return "Autorizzazione esistente";
	 }

	 public String creaToken(String chiave, String idRisorsa) {
		 String s = "Token creato : " + chiave + "-" + idRisorsa;
		 System.out.println(s);
		 return s;
	 }

	 public static void main(String[] args) {
	 // TODO Auto-generated method stub
	 }
}