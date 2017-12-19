package authorizer.GestoreRisorse;

public class FactoryLink implements FactoryRisorsa {
    @Override
    public Risorsa creaRisorsa(int idRisorsa, int _livello) {
        return new RisorsaLink(idRisorsa, _livello);
    }
}
