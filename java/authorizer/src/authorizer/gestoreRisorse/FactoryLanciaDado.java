package authorizer.GestoreRisorse;

class FactoryLanciaDado implements FactoryRisorsa {
	@Override
	public Risorsa creaRisorsa(int _livello) {
		return new RisorsaLanciaDado(_livello);
	}
}