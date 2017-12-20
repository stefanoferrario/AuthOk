package test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import authorizer.GestoreAutorizzazioni.AuthorizationException;
import authorizer.GestoreRisorse.*;
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class GestoreAutorizzazioniTest {

    private GestoreAutorizzazioni auth = null;
    private ArrayList<String> arrayKeys = new ArrayList<String>();

    @Before
    public void getInstance(){

        try{
            auth =  GestoreAutorizzazioni.getInstance();

            SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String strdate = "05-03-2020 11:35:42"; // Esempio di scadenza

            //Autorizzazioni di esempio
            arrayKeys.add(auth.creaAutorizzazione("Anna",2, dateformat.parse(strdate))); //Indice ArrayList -> 0
            arrayKeys.add(auth.creaAutorizzazione("Marco",4,new Date()));
            arrayKeys.add(auth.creaAutorizzazione("Luigi",5,dateformat.parse(strdate)));
            arrayKeys.add(auth.creaAutorizzazione("Pippo",9,dateformat.parse(strdate)));

            GestoreRisorse resource = GestoreRisorse.getInstance();
            resource.addRisorsa(1,9, ResourceTypes.DICE);
            resource.addRisorsa(2,6, ResourceTypes.FIBO);
            resource.addRisorsa(3,4, ResourceTypes.LINK);


        }catch(AuthorizationException e){
            fail("Utenti di test replicati");
        }catch(ParseException e){
            fail("Formato data non valido");
        }catch(ResourceException r){
            fail("ID risorsa già esistente");
        }
    }

    @Test
    public void testVerificaEsistenzaAutorizzazione(){

        //Caso autorizzazione non esistente
        String key = auth.verificaEsistenzaAutorizzazione("Giulia");
        assertEquals(null,key);

        //Caso autorizzazione esistente
        key = auth.verificaEsistenzaAutorizzazione("Marco");
        assertEquals(arrayKeys.get(1),key);

    }

    @Test
    public void testVerificaValiditaAutorizzazione(){

        // Utente-> Marco; LivelloAuth->4
        // idRisorsa-> 3 LivelloRisorsa->4
        boolean valid = auth.verificaValiditaAutorizzazione(arrayKeys.get(1),3);
        assertEquals(false,valid); // Livello OK ma data data autorizzazione scaduta

        // Utente-> Luigi; LivelloAuth->5
        // idRisorsa-> 3 LivelloRisorsa->4
        valid = auth.verificaValiditaAutorizzazione(arrayKeys.get(2),3);
        assertEquals(true,valid); // Livello OK, data OK

        // Utente-> Pippo; LivelloAuth->9
        // idRisorsa-> 2 LivelloRisorsa->6
        valid = auth.verificaValiditaAutorizzazione(arrayKeys.get(3),2);
        assertEquals(true,valid); // Livello OK, data OK

        // Utente-> Anna; LivelloAuth->2
        // idRisorsa->1  LivelloRisorsa->9
        valid = auth.verificaValiditaAutorizzazione(arrayKeys.get(0),1);
        assertEquals(false,valid); // Livello NO, data OK

        // Utente-> Marco; LivelloAuth->4
        // idRisorsa->1  LivelloRisorsa->9
        valid = auth.verificaValiditaAutorizzazione(arrayKeys.get(1),1);
        assertEquals(false,valid); // Livello NO, data NO

    }

    @Test
    public void testRevocaAutorizzazione(){
        //Revoca effettuata
        boolean cond = auth.revocaAutorizzazione(arrayKeys.get(1)); // Utente-> Marco;
        assertTrue(cond);

        //Revoca fallita
        cond = auth.revocaAutorizzazione(arrayKeys.get(1)); //Revoca doppia
        assertEquals(false,cond);

        String key = auth.verificaEsistenzaAutorizzazione("Marco");
        assertEquals(null,key);
    }

}