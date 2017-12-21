package user;

import java.util.Date;

public interface IntAdmin {
	void creaRisorsa();

	void modificaRisorsa();

	void cancellaRisorsa();

	String creaAutorizzazione(String nomeUtente, int livello, Date scadenza) throws AuthorizerException;
	
	boolean revocaAutorizzazione(String nomeUtente) throws AuthorizerException;
}
