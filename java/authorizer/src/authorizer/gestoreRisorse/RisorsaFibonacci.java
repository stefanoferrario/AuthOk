package authorizer.gestoreRisorse;

import authorizer.gestoreToken.GestoreToken;
import authorizer.gestoreToken.Token;

import java.util.ArrayList;

public class RisorsaFibonacci extends Risorsa {

	private static ArrayList<Integer> fibonacci = new ArrayList<>(0);

	public RisorsaFibonacci(int _livello) {
		super(_livello);
	}
	
	//serve come supporto alla classe serieDiFibonacci
	private static int fibo(int n) {
		int f = 1;
		int prec = 1, succ = 1;
		if (n > 1)
			for (int i = 2; i <= n; i++) {
				f = prec + succ;
				prec = succ;
				succ = f;
			}
		return f;
	}

	public ArrayList<Integer> serieDiFibonacci(int numeroTermini) {
		fibonacci.clear();
		if (numeroTermini > 0) {
			for (int i = 0; i < numeroTermini; i++)
				fibonacci.add(fibo(i));
			return fibonacci;
		} else return null;
	}

	@SuppressWarnings("unused")
	private long verificaToken(Token token) {
		long d = GestoreToken.getInstance().verificaToken(token.getChiave(), token.getIdRisorsa());
		return d;
	}
}