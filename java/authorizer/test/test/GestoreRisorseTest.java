package test;

import authorizer.GestoreRisorse.ResourceException;
import authorizer.GestoreRisorse.ResourceTypes;
import org.junit.Test;


import static org.junit.Assert.*;

public class GestoreRisorseTest extends PreTest {

    @Test //Aggiunta risorsa funzionante
    public void testAddRisorsa(){
        try{
            int id = resource.addRisorsa(2, ResourceTypes.LINK);
            assertTrue(id>=0);
        }catch(ResourceException e){
            fail(e.getMessage());
        }
    }

    @Test
    public void testModificaLivRisorsa(){
        try{
            int lev = 5;
            int idRisorsa = nome_idRisorsa.get(Risorsa.R1.getNomeRisorsa());
            resource.modificaLivRisorsa(idRisorsa,lev); //Modifica livello da 9 a 5

            assertTrue(lev == resource.getLivelloRisorsa(idRisorsa));

        }catch(ResourceException e){
            fail(e.getMessage());
        }

    }

    @Test
    public void testContieneRisorsa(){
        //Risorsa esistente
        boolean exist = resource.contieneRisorsa(nome_idRisorsa.get(Risorsa.R1.getNomeRisorsa()));
        assertTrue(exist);
    }

    @Test
    public void testcancellaRisorsa(){
        boolean exist = resource.cancellaRisorsa(nome_idRisorsa.get(Risorsa.R1.getNomeRisorsa()));
        assertTrue(exist); //Torna true se esisteva una risorsa con quell'ID
    }

}