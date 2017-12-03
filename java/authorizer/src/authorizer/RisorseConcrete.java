package autorizzatore;
import java.util.Date;

class RisorsaPdf implements IRisorsa {
	//campi:
	private int livello = 0;
	private String id = null;
	//metodi 
	protected RisorsaPdf() { System.out.println("RisorsaPdf()"); }	
	public void setLivello(int _livello) { livello = _livello; }
	public int getLivello() { return livello; }
	public String getId() { return id; }
	public Date verificaToken() {
		System.out.println("RisorsaPdf.verificaToken - viene ritornata semplicemente la data attuale per adesso");
		return new Date();
	}
}

class RisorsaExcel implements IRisorsa {
	//campi:
	private int livello = 0;
	private String id = null;
	//metodi:
	protected RisorsaExcel() { System.out.println("RisorsaExcel()"); }	
	public void setLivello(int _livello) { livello = _livello; }
	public int getLivello() { return livello; }
	public String getId() { return id; }
	public Date verificaToken() {
		System.out.println("RisorsaExecel.verificaToken - viene ritornata semplicemente la data attuale per adesso");
		return new Date();
	}
}

public class RisorseConcrete {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
