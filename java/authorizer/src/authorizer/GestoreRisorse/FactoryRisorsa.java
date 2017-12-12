package sistemacentrale.gestorerisorse;

public interface FactoryRisorsa {
	
	Risorsa creaRisorsa(int idRisorsa, int _livello);

	Risorsa creaRisorsa(int _id);
}