package user;

import java.io.IOException;
import java.util.Date;
import jsonrpc.JSONRPCException;

public interface IntAdmin {
	void creaRisorsa();

	void modificaRisorsa();

	void cancellaRisorsa();

	public String creaAutorizzazione(String nomeUtente, int livello, Date scadenza) throws AuthorizerException;
	
	boolean revocaAutorizzazione(String nomeUtente) throws AuthorizerException;
}
