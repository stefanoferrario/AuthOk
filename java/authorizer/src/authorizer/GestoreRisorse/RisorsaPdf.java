package authorizer.GestoreRisorse;
import java.util.Date;

class RisorsaPdf implements IRisorsa {
	//campi:
	private int livello = 0;
	private String id = null;
	
	//metodi 
	public RisorsaPdf() { System.out.println("RisorsaPdf()"); }	
	
	public void setLivello(int _livello) { livello = _livello; }
	
	public int getLivello() { return livello; }
	
	public String getId() { return id; }
	
	@SuppressWarnings("unused")
	private Date verificaToken() {
		System.out.println("RisorsaPdf.verificaToken() - viene ritornata semplicemente la data attuale per adesso");
		return new Date();
	}
}