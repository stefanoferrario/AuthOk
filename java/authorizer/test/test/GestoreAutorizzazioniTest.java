package test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import authorizer.GestoreAutorizzazioni.AuthorizationException;
import authorizer.GestoreRisorse.*;

import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class GestoreAutorizzazioniTest {

    private enum Scadenza{

        DATA_OGGI(), DATA_UNO("05-03-2020 11:30:00");

        private Date data_ora;
        Scadenza(){
            this.data_ora = new Date();
        }
        Scadenza(final String data){

            SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            try{
                this.data_ora =  dateformat.parse(data);
            }catch(ParseException e){
                System.out.println("Formato data non valido");
            }
        }
        public Date getData(){return this.data_ora;}
    }

    private enum Utente{

        ANNA("Anna",2,Scadenza.DATA_UNO.getData()),
        MARCO("Marco",4,Scadenza.DATA_OGGI.getData()),
        LUIGI("Luigi",5,Scadenza.DATA_UNO.getData()),
        PIPPO("Pippo",9,Scadenza.DATA_UNO.getData());

        private final String nome;
        private final int livello;
        private final Date scadenza;

        Utente(String nome,int livello,Date scadenza){
            this.nome = nome;
            this.livello = livello;
            this.scadenza = scadenza;
        }
        public String getNome(){return this.nome;}
        public int getLivello(){return this.livello;}
        public Date getScadenza(){return this.scadenza;}
    }

    private enum Risorsa{

        R1(1,9,ResourceTypes.DICE),
        R2(2,6,ResourceTypes.FIBO),
        R3(3,4,ResourceTypes.LINK);

        private final int idRisorsa;
        private final int livello;
        private final ResourceTypes tipo;
        Risorsa(int idRisorsa, int livello, ResourceTypes tipo){
            this.idRisorsa = idRisorsa;
            this.livello = livello;
            this.tipo = tipo;
        }
        public int getIdRisorsa(){return this.idRisorsa;}
        public int getLivello(){return this.livello;}
        public ResourceTypes getTipo() {return this.tipo;}
    }

    private GestoreAutorizzazioni auth = null;
    private HashMap<String,String> nome_chiave = null;

    @Before
    public void getInstance(){

        try{
            auth =  GestoreAutorizzazioni.getInstance();
            nome_chiave = new HashMap<>();

            //Autorizzazioni di esempio
            nome_chiave.put(Utente.ANNA.getNome(),auth.creaAutorizzazione(Utente.ANNA.getNome(),Utente.ANNA.getLivello(),Utente.ANNA.getScadenza()));
            nome_chiave.put(Utente.MARCO.getNome(),auth.creaAutorizzazione(Utente.MARCO.getNome(),Utente.MARCO.getLivello(),Utente.MARCO.getScadenza()));
            nome_chiave.put(Utente.LUIGI.getNome(),auth.creaAutorizzazione(Utente.LUIGI.getNome(),Utente.LUIGI.getLivello(),Utente.LUIGI.getScadenza()));
            nome_chiave.put(Utente.PIPPO.getNome(),auth.creaAutorizzazione(Utente.PIPPO.getNome(),Utente.PIPPO.getLivello(),Utente.PIPPO.getScadenza()));

            GestoreRisorse resource = GestoreRisorse.getInstance();

            resource.addRisorsa(Risorsa.R1.getIdRisorsa(),Risorsa.R1.getLivello(),Risorsa.R1.getTipo());
            resource.addRisorsa(Risorsa.R2.getIdRisorsa(),Risorsa.R2.getLivello(),Risorsa.R2.getTipo());
            resource.addRisorsa(Risorsa.R3.getIdRisorsa(),Risorsa.R3.getLivello(),Risorsa.R3.getTipo());

        }catch(AuthorizationException e){
            fail("Utenti di test replicati");
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
        assertEquals(nome_chiave.get(Utente.MARCO.getNome()),key);
    }

    @Test
    public void testVerificaValiditaAutorizzazione(){

        // Utente-> Luigi; LivelloAuth-> 5
        // idRisorsa-> 3 LivelloRisorsa-> 4
        GestoreAutorizzazioni.Validity valid = auth.verificaValiditaAutorizzazione(nome_chiave.get(Utente.LUIGI.getNome()),Risorsa.R3.getIdRisorsa());
        assertEquals(GestoreAutorizzazioni.Validity.VALID,valid); // Livello OK, data OK, chiave OK, idRisorsa OK

        // Utente-> Marco; LivelloAuth-> 4
        // idRisorsa-> 3 LivelloRisorsa-> 4
        valid = auth.verificaValiditaAutorizzazione(nome_chiave.get(Utente.MARCO.getNome()),Risorsa.R3.getIdRisorsa());
        assertEquals(GestoreAutorizzazioni.Validity.EXPIRED,valid); // data NO, Livello OK, chiave OK, idRisorsa OK

        // Utente-> Anna; LivelloAuth-> 2
        // idRisorsa-> 1  LivelloRisorsa-> 9
        valid = auth.verificaValiditaAutorizzazione(nome_chiave.get(Utente.ANNA.getNome()),Risorsa.R1.getIdRisorsa());
        assertEquals(GestoreAutorizzazioni.Validity.INSUFFICIENT_LEVEL,valid); // Livello NO, data OK, chiave OK, idRisorsa OK

        // Utente-> Pippo; LivelloAuth-> 9
        // idRisorsa-> 50 (NON ESISTENTE)
        valid = auth.verificaValiditaAutorizzazione(nome_chiave.get(Utente.PIPPO.getNome()),50);
        assertEquals(GestoreAutorizzazioni.Validity.RESOURCE_NON_EXISTENT,valid); // idRisorsa NO, Livello OK, data OK , chiave OK

        // Utente-> NON ESISTE  -> chiave null
        // Utente-> Esiste      -> chiave null
        // idRisorsa->2  LivelloRisorsa->6
        valid = auth.verificaValiditaAutorizzazione(null,1);
        assertEquals(GestoreAutorizzazioni.Validity.KEY_NON_EXISTENT,valid); // chiave NO
    }

    @Test
    public void testRevocaAutorizzazione(){
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