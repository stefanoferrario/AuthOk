package test;

import authorizer.gestoreToken.TokenException;
import org.junit.Test;

import static org.junit.Assert.*;

public class GestoreTokenTest extends PreTest {

    private enum Messaggio{
        EXPIRED("Chiave scaduta"),
        KEY_NON_EXISTENT("Chiave inesistente"),
        INSUFFICIENT_LEVEL("Livello di autorizzazione non sufficiente"),
        RESOURCE_NON_EXISTENT("Risorsa non trovata");

        private final String msg;
        Messaggio(String msg){
           this.msg =  msg;
        }
        public String getMsg() {return msg;}
    }

    //Chiave non esistente quindi TokenException
    @Test
    public void testCreaTokenChiaveNonEsistente(){
        try {
            token.creaToken(null, nome_idRisorsa.get(Risorsa.R3.getNomeRisorsa()));
            fail("Expected Token exception");
        }catch(TokenException e){
            System.out.println(e.getMessage());
            assertTrue(e.getMessage().equals(Messaggio.KEY_NON_EXISTENT.getMsg()));
        }
    }

    //Livello insufficiente quindi TokenException
    @Test
    public void testCreaTokenLivelloIns(){
        try {
            token.creaToken(nome_chiave.get(Utente.LUIGI.getNome()), nome_idRisorsa.get(Risorsa.R1.getNomeRisorsa()));
            fail("Expected Token exception");
        }catch(TokenException e){
            System.out.println(e.getMessage());
            assertTrue(e.getMessage().equals(Messaggio.INSUFFICIENT_LEVEL.getMsg()));
        }
    }

    //Risorsa non trovata quindi TokenException
    @Test
    public void testCreaTokenRisorsaNonEsistente(){
        try {
            token.creaToken(nome_chiave.get(Utente.LUIGI.getNome()), 4);
            fail("Expected Token exception");
        }catch(TokenException e){
            System.out.println(e.getMessage());
            assertTrue(e.getMessage().equals(Messaggio.RESOURCE_NON_EXISTENT.getMsg()));
        }
    }

    //Tutto funzionante
    @Test
    public void testCreaToken(){
        try{
            String key = token.creaToken(nome_chiave.get(Utente.LUIGI.getNome()), nome_idRisorsa.get(Risorsa.R3.getNomeRisorsa()));
            assertFalse(key == null);
        }catch(TokenException e){
            fail(e.getMessage());
        }
    }

   @Test
    public void testVerificaToken(){
        //Token validi
        long durata = token.verificaToken(nome_token.get(Utente.LUIGI.getNome()), nome_idRisorsa.get(Risorsa.R3.getNomeRisorsa()));
        assertFalse(durata == 0);

        durata = token.verificaToken(nome_token.get(Utente.PIPPO.getNome()), nome_idRisorsa.get(Risorsa.R1.getNomeRisorsa()));
        assertFalse(durata == 0);

        //Token non esiste
        durata = token.verificaToken("",nome_idRisorsa.get(Risorsa.R1.getNomeRisorsa()));
        assertTrue(durata == 0);
    }

    /*@Test
    public void testVerificaCancellaTokenChiave(){

        token.cancellaTokenChiave(nome_token.get(Utente.LUIGI.getNome()));

        long exist = token.verificaToken(nome_token.get(Utente.LUIGI.getNome()), nome_idRisorsa.get(Risorsa.R3.getNomeRisorsa()));
        assertTrue(exist == 0);
    }*/
}