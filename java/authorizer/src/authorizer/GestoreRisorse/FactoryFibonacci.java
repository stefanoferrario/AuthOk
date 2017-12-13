package authorizer.GestoreRisorse;

class FactoryFibonacci implements FactoryRisorsa {

	@Override
	public Risorsa creaRisorsa(int _id, int _livello) {
		return new RisorsaFibonacci(_id, _livello);
	}

	@Override
	public Risorsa creaRisorsa(int _id) {
		return new RisorsaFibonacci(_id, 1);
	}
}