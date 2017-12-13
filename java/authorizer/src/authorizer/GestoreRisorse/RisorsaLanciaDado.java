package authorizer.GestoreRisorse;
import java.time.Duration;
import java.util.Random;
import java.util.Date;
import authorizer.GestoreToken.*;

public class RisorsaLanciaDado extends Risorsa {
	private int facciaDado = 0;

	public RisorsaLanciaDado(int _id, int _livello) {
		super(_id,_livello);
		facciaDado = new Random().nextInt((6-1) + 1) + 1;
	}

	public int getFacciaDado() {return facciaDado;}
	
	public void lanciaDado() {facciaDado = new Random().nextInt((6-1) + 1) + 1;}
	
	@SuppressWarnings("unused")
	private static Duration verificaToken(Token token) {
		Duration d = GestoreToken.getInstance().verificaToken(token.getChiave(), token.getIdRisorsa());
		return d;
	}
	
	public static void main(String[] arr) { 
		Risorsa r = new RisorsaLanciaDado(1, 10);
		System.out.println(r.getId());
		System.out.println(r.getLivello());
	}
}