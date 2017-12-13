package authorizer.GestoreRisorse;

import java.util.HashMap;

public class GestoreRisorse {
	private static GestoreRisorse gestoreRisorse = null;
	private HashMap<Integer, Risorsa> dataBaseRisorse = null;

	// singleton:
	private GestoreRisorse() {
		gestoreRisorse = new GestoreRisorse();
		dataBaseRisorse = new HashMap<>();
	}

	public static GestoreRisorse getInstance() {
		if (gestoreRisorse == null)
			gestoreRisorse = new GestoreRisorse();
		return gestoreRisorse;
	}

	// serve a verificare se un idRisorsa � gi� associato ad una risorsa nella
	// HashMap
	public boolean contieneRisorsa(int idRisorsa) {
		if (dataBaseRisorse.containsKey(idRisorsa))
			return true;
		else
			return false;
	}

	// ho deciso di "ricalcare" il comportamento della classe HashMap per quanto
	// riguarda il tipo di dato ritornato
	public Risorsa addRisorsa(int idRisorsa, int livello, FactoryRisorsa fact) {
		return dataBaseRisorse.put(idRisorsa, fact.creaRisorsa(idRisorsa, livello));
	}

	// overload per aggiungere una risorsa (generica) direttamente alla HashMap
	public Risorsa addRisorsa(int idRisorsa, Risorsa risorsa) {
		return dataBaseRisorse.put(idRisorsa, risorsa);
	}

	// se non � presente una risorsa da modificare torna null, altrimenti torna la
	// versione pre-modifica della risorsa stessa
	public Risorsa modificaRisorsa(int idRisorsa, int livello) {
		Risorsa r = dataBaseRisorse.get(idRisorsa);
		if (r == null) { // non sono presenti risorse con l' idRisorsa passato come parametro
			return null;
		} else {
			r.setId(idRisorsa);
			r.setLivello(livello);
			return addRisorsa(idRisorsa, r);
		}
	}

	// viene ritornata la risorsa cancellata o null se non era presente una risorsa
	// per il parametro idRisorsa
	public Risorsa cancellaRisorsa(int idRisorsa) {
		return dataBaseRisorse.remove(idRisorsa);
	}

	public int getLivelloRisorsa(int idRisorsa) {
		return (dataBaseRisorse.get(idRisorsa)).getLivello();
	}

	public static void main(String[] args) {
		FactoryRisorsa factFibo = new FactoryFibonacci();
		FactoryRisorsa factDado = new FactoryLanciaDado();
		Risorsa rDado = factDado.creaRisorsa(0, 5);
		Risorsa rFibo = factFibo.creaRisorsa(1, 7);
		System.out.println("ID RISORSA: " + rDado.getId() + ", LIVELLO RISORSA: " + rDado.getLivello());
		System.out.println("ID RISORSA: " + rFibo.getId() + ", LIVELLO RISORSA: " + rFibo.getLivello());
		RisorsaFibonacci rF = (RisorsaFibonacci) rFibo;
		System.out.println("I primi 10 termini della serie di Fibonacci sono: " + rF.serieDiFibonacci(10).toString()); 
		RisorsaLanciaDado rD = (RisorsaLanciaDado) rDado;
		System.out.println("La faccia del dado vale: " + rD.getFacciaDado());
		rD.lanciaDado();
		System.out.println("Lancio il dado...la nuova faccia �: " + rD.getFacciaDado());
	}
}