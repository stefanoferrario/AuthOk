package authorizer.GestoreRisorse;

import authorizer.GestoreToken.GestoreToken;
import authorizer.GestoreToken.Token;

import java.util.Random;

public class RisorsaLanciaDado extends Risorsa {
	private int facciaDado = 0;

	public RisorsaLanciaDado(int _livello) {
		super(_livello);
		facciaDado = new Random().nextInt((6-1) + 1) + 1;
	}

	public int getFacciaDado() {return facciaDado;}
	
	public void lanciaDado() {facciaDado = new Random().nextInt((6-1) + 1) + 1;}
	
	@SuppressWarnings("unused")
	private static long verificaToken(Token token) {
		long d = GestoreToken.getInstance().verificaToken(token.getChiave(), token.getIdRisorsa());
		return d;
	}
}