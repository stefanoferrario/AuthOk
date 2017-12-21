package user;

public interface IntAdmin {
	void creaRisorsa();

	void modificaRisorsa();

	void cancellaRisorsa();

	String creaAutorizzazione(String nomeUtente, int livello, String scadenza) throws AuthorizerException;
	
	boolean revocaAutorizzazione(String nomeUtente) throws AuthorizerException;
}
