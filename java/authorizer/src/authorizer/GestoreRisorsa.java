package authorizer;
import java.util.*; //sistemare con il package specifico

//IMPORTANTE: per adesso la factory permette solo la costruzione di risorse che non sono niente in pratica, bisogna capire come gestirle
//1)aggiungere l' overload del metodo creaRisorsa nell' interfaccia (IFactoryMethodRisorse) e implementarlo (FactoryRisorse) 
//2)apportare le conseguenti modifiche al metodo di creazione risorse :
//	(notare il fatto che gestore risorse costruisce solo con la factory, ma potrebbe anche farlo direttamente, questo � un problema?)
//		cosa � una risorsa?
//		quali sono i parametri che devono essere passati per istanziare una risorsa complessa?
//		cosa vuol dire modificare una risorsa?
//		ma soprattutto...tutte queste cose devono essere fatte o devono solo essere presenti i metodi nell' interfaccia e poi non implementati?

public class GestoreRisorsa {
	private static GestoreRisorsa gestoreRisorse = null;
	private Map<Integer, IRisorsa> dataBaseRisorse = null;  //se metto "int" mi da errore
	
	//singleton:
	private GestoreRisorsa() {	
		gestoreRisorse = new GestoreRisorsa();
		dataBaseRisorse = new HashMap<>();
	}
	
	public static GestoreRisorsa getGestoreRisorse() {
        if (gestoreRisorse == null) {
        	gestoreRisorse = new GestoreRisorsa();
        }
        return gestoreRisorse;
	}
	
	//questo metodo ritorna una risorsa il cui tipo viene definito a run-time e stabilito in base al tip di factory passato come parametro
	//private IRisorsa generatoreRisorsa(FactoryRisorsa fact) {return fact.creaRisorsa();}

	//creaRisorsa non ritorna niente, ma aggiunge la risorsa alla mappa  
	public void creaRisorsa(int idRisorsa, FactoryRisorsa fact) {
		if(!dataBaseRisorse.containsKey(idRisorsa))
			//con la factory generica viene istanziata un tipo di risorsa che viene decisa a run-time
			dataBaseRisorse.put(idRisorsa, (IRisorsa) fact.creaRisorsa());
		else
			System.out.println("Errore: idRisorsa gi� presente nel database");
	}
	
	public void modificaRisorsa(int idRisorsa, FactoryRisorsa fact) {
		if(dataBaseRisorse.containsKey(idRisorsa))
			dataBaseRisorse.put(idRisorsa, (IRisorsa) fact.creaRisorsa());
		else
			System.out.println("Errore: idRisorsa non presente nel database");
	}
	
	public void cancellaRisorsa(int idRisorsa) { dataBaseRisorse.remove(idRisorsa);}
	
	public int getLivelloRisorsa(int idRisorsa) {
		return (dataBaseRisorse.get(idRisorsa)).getLivello();
	}
		
	public static void main(String[] args) {
		
	}
}
//fare interfaccia o metodo abstract?
//perch� se si fa abstract il metodo verificaToken(), che � privato, non potrebbe essere ereditato.
//soluzione: includere tutto ci� che � risorsa in un sotto_package e difinire il metodo come protected