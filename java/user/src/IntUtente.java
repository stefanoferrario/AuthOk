import authorizer.Autorizzazione;
import java.util.Date;

public interface IntUtente {
	Autorizzazione creaAutorizzazione(String _nomeUtente, int _livello, Date _scadenza);
	boolean revocaAutorizzazione(String chiave);
	String verificaEsistenzaAutorizzazione();
	String creaToken(String chiave, String idRisorsa);
}
