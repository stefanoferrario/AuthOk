package authorizer.gestoreRisorse;

class FactoryFibonacci implements FactoryRisorsa {
	@Override
	public Risorsa creaRisorsa(int _livello) {
		return new RisorsaFibonacci(_livello);
	}
}