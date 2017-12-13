package authorizer.GestoreRisorse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import authorizer.GestoreToken.GestoreToken;
import authorizer.GestoreToken.*;

public class RisorsaFibonacci extends Risorsa {

	private static ArrayList<Integer> fibonacci = new ArrayList<>(0);

	public RisorsaFibonacci(int _id, int _livello) {
		super(_id, _livello);
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
	private Duration verificaToken(Token token) {
		Duration d = GestoreToken.getInstance().verificaToken(token.getChiave(), token.getIdRisorsa());
		return d;
	}

	public static void main(String[] a) {
		ArrayList<Integer> f = new RisorsaFibonacci(1,5).serieDiFibonacci(10);
		if (f == null) {
			System.out.println("Input non valido!!!");
		} else {
			System.out.println(f.toString());
		}
	}
}