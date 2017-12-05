package authorizer.GestoreRisorse;
import java.util.HashMap;

import authorizer.GestoreRisorse.FactoryRisorsa;

public class GestoreRisorse {
	private static GestoreRisorse gestoreRisorse = null;
	private HashMap<Integer, IRisorsa> dataBaseRisorse = null; 
	
	//singleton:
	private GestoreRisorse() {
		dataBaseRisorse = new HashMap<>();
	}
	
	public static GestoreRisorse getIstance() {
        if (gestoreRisorse == null) {
        	gestoreRisorse = new GestoreRisorse();
        }
        return gestoreRisorse;
	}
	
	//in realt� aggiunge una risorsa al "database", infatti non ritorna niente...il nome � ok?
	public void creaRisorsa(int idRisorsa, FactoryRisorsa fact) {
		if(!dataBaseRisorse.containsKey(idRisorsa))
			dataBaseRisorse.put(idRisorsa, fact.creaRisorsa()); 
		else
			System.out.println("Errore: idRisorsa gi� presente nel database");
	}
	
	//la modifica � solo un rimpiazzo di una risorsa con una nuova che la sovrascrive
	public void modificaRisorsa(int idRisorsa, FactoryRisorsa fact) {
		if(dataBaseRisorse.containsKey(idRisorsa))
			dataBaseRisorse.put(idRisorsa, fact.creaRisorsa()); 
		else
			System.out.println("Errore: idRisorsa non presente nel database");
	}
	
	public void cancellaRisorsa(int idRisorsa) { dataBaseRisorse.remove(idRisorsa);}
	
	public int getLivelloRisorsa(int idRisorsa) {
		return (dataBaseRisorse.get(idRisorsa)).getLivello();
	}
		
	public static void main(String[] args) {
		
		//PDF:
		FactoryRisorsa factPdf = new FactoryPdf();
		IRisorsa rPdf =  factPdf.creaRisorsa();
		System.out.println(rPdf.getId());
		System.out.println(rPdf.getLivello());
		rPdf.setLivello(7);
		System.out.println(rPdf.getLivello());
		
		//EXCEL:
		FactoryRisorsa factExcel = new FactoryExcel();
		IRisorsa rExcel =  factExcel.creaRisorsa();
		System.out.println(rExcel.getId());
		System.out.println(rExcel.getLivello());
		rExcel.setLivello(9);
		System.out.println(rExcel.getLivello());
	}
}

//IMPORTANTE: per adesso la factory permette solo la costruzione di risorse che non sono niente in pratica, bisogna capire come gestirle
//1)aggiungere l' overload del metodo creaRisorsa nell' interfaccia (IFactoryRisorsa) e implementarlo (FactoryRisorse specializzati) 
//2)apportare le conseguenti modifiche al metodo di creazione risorse :
//		cosa � una risorsa?
//		quali sono i parametri che devono essere passati per istanziare una risorsa complessa?
//		cosa vuol dire modificare una risorsa?
//		ma soprattutto...tutte queste cose devono essere fatte o devono solo essere presenti i metodi nell' interfaccia e poi non implementati?
//3)I metodi delle risorse concrete quale livello di visibilit� devono avere?
//4)Altri eventuali metodi ausiliari dovranno essere obbligatoriamente privati, ma per adesso non ce ne sono