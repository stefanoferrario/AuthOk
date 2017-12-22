package authorizer.gestoreRisorse;

import jsonrpc.Member;
import jsonrpc.StructuredMember;
import java.util.ArrayList;
import java.util.HashMap;

public class GestoreRisorse {
    private static GestoreRisorse gestoreRisorse = null;
    private HashMap<Integer, Risorsa> dataBaseRisorse = null;

    // singleton:
    private GestoreRisorse() {
        dataBaseRisorse = new HashMap<>();
    }

    public static GestoreRisorse getInstance() {
        if (gestoreRisorse == null)
            gestoreRisorse = new GestoreRisorse();
        return gestoreRisorse;
    }

    // verifica esistenza risorsa
    public boolean contieneRisorsa(int idRisorsa) {
        return dataBaseRisorse.containsKey(idRisorsa);
    }

    // crea una nuova risorsa e la aggiunga alla mappa. restituisce id assegnato alla risorsa
    public int addRisorsa(int livello, ResourceTypes type) throws ResourceException {
        FactoryRisorsa fact;
        switch (type) {
            case DICE:
                fact = new FactoryLanciaDado();
                break;
            case FIBO:
                fact = new FactoryFibonacci();
                break;
            case LINK:
                fact = new FactoryLink();
                break;
            default: throw new ResourceException("Invalid resource type");
        }
        int id = 0;
        while (dataBaseRisorse.containsKey(id)) {
            id++;
        }

        dataBaseRisorse.put(id, fact.creaRisorsa(livello));
        return id;
    }

    // modifica il livello di una risorsa. lancia eccezione se non esiste una risorsa con quell'ID
    //restituisce il livello precedente
    public int modificaLivRisorsa(int idRisorsa, int livello) throws ResourceException {
        if (!contieneRisorsa(idRisorsa)) {throw new ResourceException("Risorsa inesistente");}

        Risorsa r = dataBaseRisorse.get(idRisorsa);
        int oldLiv = r.getLivello();
        r.setLivello(livello);
        return oldLiv;
    }

    // modifica l'id di una risorsa. lancia eccezione se non esiste una risorsa con il vecchio ID
    public void modificaIDRisorsa(int oldID, int newID) throws ResourceException {
        if (!contieneRisorsa(oldID)) {throw new ResourceException("Risorsa " + String.valueOf(oldID) + " inesistente");}
        if (contieneRisorsa(newID)) {throw new ResourceException("ID " + String.valueOf(newID) + " già esistente");}
        Risorsa r = dataBaseRisorse.get(oldID);
        cancellaRisorsa(oldID);
        dataBaseRisorse.put(newID, r);
    }

    // restituisce true se era presente una risorsa con quell'ID
    public boolean cancellaRisorsa(int idRisorsa) {
        return dataBaseRisorse.remove(idRisorsa) != null;
    }

    public int getLivelloRisorsa(int idRisorsa) throws ResourceException {
        if (!contieneRisorsa(idRisorsa)) {throw new ResourceException("Risorsa inesistente");}
        return dataBaseRisorse.get(idRisorsa).getLivello();
    }

    public Member getState() {
        ArrayList<Member> resources = new ArrayList<>();
        for (HashMap.Entry<Integer, Risorsa> entry : dataBaseRisorse.entrySet()) {
            HashMap<String, Member> resValues = new HashMap<>();
            resValues.put("ID", new Member(entry.getKey()));
            resValues.put("Livello", new Member(entry.getValue().getLivello()));
            resources.add(new Member(new StructuredMember(resValues)));
        }
        if (resources.size()==0)
            return new Member();
        else
            return new Member(new StructuredMember(resources));
    }
}