package sistemacentrale.gestorerisorse;

class FactoryLanciaDado implements FactoryRisorsa {

	@Override
	public Risorsa creaRisorsa(int _id, int _livello) {
		return new RisorsaLanciaDado(_id, _livello);
	}

	@Override
	public Risorsa creaRisorsa(int _id) {
		return new RisorsaLanciaDado(_id, 1);
	}
}