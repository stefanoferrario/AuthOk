package autorizzatore;

public class  RisorsaFactory{
	public static void serviceConsumer(FactoryRisorsa fact) {
		Risorsa r = fact.creaRisorsa();
		r.getLivello();
		r.verificaToken();
	}
	public static void main(String[] args) {
		serviceConsumer(Risorsa1.factory);
		serviceConsumer(Risorsa2.factory);
	}
}