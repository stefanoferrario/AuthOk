package authorizer.GestoreRisorse;

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

    // crea una nuova risorsa e la aggiunga alla mappa. lancia eccezione se è già presente una risorsa con lo stesso ID
    public void addRisorsa(int idRisorsa, int livello, ResourceTypes type) throws ResourceException {
        if (contieneRisorsa(idRisorsa)) {throw new ResourceException("ID risorsa già esistente");}

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
            default: return;
        }
        dataBaseRisorse.put(idRisorsa, fact.creaRisorsa(idRisorsa, livello));
    }

    // modifica il livello di una risorsa. lancia eccezione se non esiste una risorsa con quell'ID
    public void modificaLivRisorsa(int idRisorsa, int livello) throws ResourceException {
        if (!contieneRisorsa(idRisorsa)) {throw new ResourceException("Risorsa inesistente");}

        Risorsa r = dataBaseRisorse.get(idRisorsa);
        r.setLivello(livello);
    }

    // modifica l'id di una risorsa. lancia eccezione se non esiste una risorsa con il vecchio ID
    public void modificaIDRisorsa(int oldID, int newID) throws ResourceException {
        if (!contieneRisorsa(oldID)) {throw new ResourceException("Risorsa inesistente");}

        Risorsa r = dataBaseRisorse.get(oldID);
        cancellaRisorsa(oldID);
        r.setId(newID);
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
        for (Risorsa r : dataBaseRisorse.values()) {
            HashMap<String, Member> resValues = new HashMap<>();
            resValues.put("ID", new Member(r.getId()));
            resValues.put("Livello", new Member(r.getLivello()));
            resources.add(new Member(new StructuredMember(resValues)));
        }
        if (resources.size()==0)
            return new Member();
        else
            return new Member(new StructuredMember(resources));
    }

    public static void main(String[] args) {
        FactoryRisorsa factFibo = new FactoryFibonacci();
        FactoryRisorsa factDado = new FactoryLanciaDado();
        Risorsa rDado = factDado.creaRisorsa(0, 5);
        Risorsa rFibo = factFibo.creaRisorsa(1, 7);
        System.out.println("ID RISORSA: " + rDado.getId() + ", LIVELLO RISORSA: " + rDado.getLivello());
        System.out.println("ID RISORSA: " + rFibo.getId() + ", LIVELLO RISORSA: " + rFibo.getLivello());
        RisorsaFibonacci rF = (RisorsaFibonacci) rFibo;
        System.out.println("I primi 10 termini della serie di Fibonacci sono: " + rF.serieDiFibonacci(10).toString());
        RisorsaLanciaDado rD = (RisorsaLanciaDado) rDado;
        System.out.println("La faccia del dado vale: " + rD.getFacciaDado());
        rD.lanciaDado();
        System.out.println("Lancio il dado...la nuova faccia �: " + rD.getFacciaDado());
    }
}