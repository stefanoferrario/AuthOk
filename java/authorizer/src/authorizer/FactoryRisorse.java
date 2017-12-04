package authorizer;

class FactoryRisorsaPdf implements FactoryRisorsa{
	public RisorsaPdf creaRisorsa() { return new RisorsaPdf();}
}

class FactoryRisorsaExcel implements FactoryRisorsa{
	public RisorsaExcel creaRisorsa() {
		return new RisorsaExcel();
	}
}

public class FactoryRisorse {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
