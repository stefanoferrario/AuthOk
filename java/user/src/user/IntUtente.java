package user;

import java.util.HashMap;

public interface IntUtente {
	String creaAutorizzazione(String nomeUtente, int livello, String scadenza) throws AuthorizerException;

	boolean revocaAutorizzazione(String nomeUtente) throws AuthorizerException;

	String verificaEsistenzaAutorizzazione(String nomeUtente) throws AuthorizerException;

	String creaToken(String chiave, int idRisorsa) throws AuthorizerException;

	HashMap<Integer, Boolean> checkToken(HashMap<Integer, String> tokens) throws AuthorizerException;

    String checkServer();
}
