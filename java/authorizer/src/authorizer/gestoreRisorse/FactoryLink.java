package authorizer.gestoreRisorse;

public class FactoryLink implements FactoryRisorsa {
    @Override
    public Risorsa creaRisorsa(int _livello) {
        return new RisorsaLink(_livello);
    }
}
