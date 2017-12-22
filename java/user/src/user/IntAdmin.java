package user;

import authorizer.GestoreRisorse.ResourceTypes;
import java.util.Date;

public interface IntAdmin {
	int creaRisorsa(int livello, ResourceTypes type) throws AuthorizerException;

	int modificaLivRisorsa(int id, int livello) throws AuthorizerException;

	void modificaIdRisorsa(int currID, int newID) throws AuthorizerException;

	boolean cancellaRisorsa(int id) throws AuthorizerException;

	String creaAutorizzazione(String nomeUtente, int livello, Date scadenza) throws AuthorizerException;
	
	boolean revocaAutorizzazione(String nomeUtente) throws AuthorizerException;
}
