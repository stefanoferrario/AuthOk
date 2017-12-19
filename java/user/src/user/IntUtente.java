package user;

import java.io.IOException;
import java.util.Date;
import jsonrpc.JSONRPCException;

public interface IntUtente {
	String creaAutorizzazione(String nomeUtente, int livello, Date scadenza) throws AuthorizerException;

	boolean revocaAutorizzazione(String nomeUtente) throws AuthorizerException;

	String verificaEsistenzaAutorizzazione(String nomeUtente) throws AuthorizerException;

	String creaToken(String chiave, int idRisorsa) throws AuthorizerException;
}
