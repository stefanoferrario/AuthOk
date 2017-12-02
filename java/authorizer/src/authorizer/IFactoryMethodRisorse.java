package autorizzatore;

interface IRisorsa {
	int getLivello();
	String getId();
	void setLivello(int _livello);
	//private Date verificaToken(); non fa parte dell' interfaccia in quanto privato, ma è presente nella classe RisorseConcrete
}

interface FactoryRisorsa {
	IRisorsa creaRisorsa();
}

public class IFactoryMethodRisorse {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
