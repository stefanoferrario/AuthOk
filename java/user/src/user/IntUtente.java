package user;

import java.io.IOException;
import java.util.Date;
import jsonrpc.JSONRPCException;

public interface IntUtente {
	public String creaAutorizzazione(String nomeUtente, int livello, Date scadenza) throws JSONRPCException, IOException;

	public boolean revocaAutorizzazione(String nomeUtente) throws JSONRPCException;

	boolean verificaEsistenzaAutorizzazione(String nomeUtente) throws JSONRPCException;

	String creaToken(String chiave, String idRisorsa) throws JSONRPCException;
}
