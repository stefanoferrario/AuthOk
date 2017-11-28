package autorizzatore;
import java.util.Date;

public class Autorizzazione {
	private String nomeUtente;
	private int livello;
	private Date scadenza;
	
	public Autorizzazione(){
		nomeUtente = "NoNome";
		livello = 0;
		scadenza = new Date();
	}
	public Autorizzazione(String _nomeUtente, int _livello, Date _scadenza){
		nomeUtente = _nomeUtente;
		livello = _livello;
		scadenza = _scadenza;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
