package gestorerisorse;

class FactoryExcel implements FactoryRisorsa {
	@Override
	public IRisorsa creaRisorsa() {
		return new RisorsaExcel();
	}
}
