package test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import static org.junit.Assert.*;
import authorizer.gestoreAutorizzazioni.GestoreAutorizzazioni;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GestoreAutorizzazioniTest extends PreTest {

    @Test //Verifica esistenza autorizzazione
    public void test1(){

        //Caso autorizzazione non esistente
        String key = auth.verificaEsistenzaAutorizzazione("Giulia");
        assertEquals(null,key);

        //Caso autorizzazione esistente
        key = auth.verificaEsistenzaAutorizzazione("Marco");
        assertEquals(nome_chiave.get(Utente.MARCO.getNome()),key);
    }

    @Test //Verifica validita autorizzazione
    public void test2(){

        // Utente-> Luigi; LivelloAuth-> 5
        // idRisorsa-> 3 LivelloRisorsa-> 4
        GestoreAutorizzazioni.Validity valid = auth.verificaValiditaAutorizzazione(nome_chiave.get(Utente.LUIGI.getNome()),nome_idRisorsa.get(Risorsa.R3.getNomeRisorsa()));
        assertEquals(GestoreAutorizzazioni.Validity.VALID,valid); // Livello OK, data OK, chiave OK, idRisorsa OK

        // Utente-> Marco; LivelloAuth-> 4
        // idRisorsa-> 3 LivelloRisorsa-> 4
        valid = auth.verificaValiditaAutorizzazione(nome_chiave.get(Utente.MARCO.getNome()),nome_idRisorsa.get(Risorsa.R3.getNomeRisorsa()));
        assertFalse(GestoreAutorizzazioni.Validity.EXPIRED == valid);// data NO, Livello OK, chiave OK, idRisorsa OK

        // Utente-> Anna; LivelloAuth-> 2
        // idRisorsa-> 1  LivelloRisorsa-> 9
        valid = auth.verificaValiditaAutorizzazione(nome_chiave.get(Utente.ANNA.getNome()),nome_idRisorsa.get(Risorsa.R1.getNomeRisorsa()));
        assertEquals(GestoreAutorizzazioni.Validity.INSUFFICIENT_LEVEL,valid); // Livello NO, data OK, chiave OK, idRisorsa OK

        // Utente-> Pippo; LivelloAuth-> 9
        // idRisorsa-> 50 (NON ESISTENTE)
        valid = auth.verificaValiditaAutorizzazione(nome_chiave.get(Utente.PIPPO.getNome()),50);
        assertEquals(GestoreAutorizzazioni.Validity.RESOURCE_NON_EXISTENT,valid); // idRisorsa NO, Livello OK, data OK , chiave OK

        // Utente-> NON ESISTE  -> chiave null
        // Utente-> Esiste      -> chiave null
        // idRisorsa->2  LivelloRisorsa->6
        valid = auth.verificaValiditaAutorizzazione(null,nome_idRisorsa.get(Risorsa.R2.getNomeRisorsa()));
        assertEquals(GestoreAutorizzazioni.Validity.KEY_NON_EXISTENT,valid); // chiave NO
    }

    @Test //Revoca autorizzazione
    public void test3(){
        //Revoca effettuata
        boolean cond = auth.revocaAutorizzazione(nome_chiave.get(Utente.MARCO.getNome()));
        assertTrue(cond);

        //Revoca fallita
        cond = auth.revocaAutorizzazione(nome_chiave.get(Utente.MARCO.getNome())); //Revoca doppia
        assertEquals(false,cond);

        //Verifica ulteriore
        String key = auth.verificaEsistenzaAutorizzazione(nome_chiave.get(Utente.MARCO.getNome()));
        assertEquals(null,key);
    }
}