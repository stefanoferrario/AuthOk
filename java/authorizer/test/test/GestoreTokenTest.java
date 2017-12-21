package test;

import authorizer.GestoreToken.TokenException;
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

    //Chiave scaduta quindi TokenException
    @Test
    public void testCreaTokenChiaveScaduta(){
        try {
            token.creaToken(nome_chiave.get(Utente.MARCO.getNome()), Risorsa.R3.getIdRisorsa());
            fail("Expected Token exception");
        }catch(TokenException e){
            System.out.println(e.getMessage());
            assertTrue(e.getMessage().equals(Messaggio.EXPIRED.getMsg()));
        }
    }

    //Chiave non esistente quindi TokenException
    @Test
    public void testCreaTokenChiaveNonEsistente(){
        try {
            token.creaToken(null, Risorsa.R3.getIdRisorsa());
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
            token.creaToken(nome_chiave.get(Utente.LUIGI.getNome()), Risorsa.R1.getIdRisorsa());
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
            token.creaToken(nome_chiave.get(Utente.LUIGI.getNome()), 0);
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
            String key = token.creaToken(nome_chiave.get(Utente.LUIGI.getNome()), Risorsa.R3.getIdRisorsa());
            assertFalse(key == null);
        }catch(TokenException e){
            fail(e.getMessage());
        }
    }

    @Test //Da rivedere
    public void testVerificaToken(){
        //Token validi
        long durata = token.verificaToken(nome_token.get(Utente.LUIGI.getNome()), Risorsa.R3.getIdRisorsa());
        assertFalse(durata == 0);

        durata = token.verificaToken(nome_token.get(Utente.PIPPO.getNome()), Risorsa.R1.getIdRisorsa());
        assertFalse(durata == 0);

        //Token non esiste
        durata = token.verificaToken("",Risorsa.R1.getIdRisorsa());
        assertTrue(durata == 0);
    }

    @Test //Da rivedere
    public void testCancellaTokenChiave(){

        token.cancellaTokenChiave(nome_token.get(Utente.LUIGI.getNome()));

        long exist = token.verificaToken(nome_token.get(Utente.LUIGI.getNome()), Risorsa.R3.getIdRisorsa());
        assertTrue(exist == 0);
    }
}