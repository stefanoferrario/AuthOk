package test;

import authorizer.GestoreAutorizzazioni.AuthorizationException;
import authorizer.GestoreAutorizzazioni.GestoreAutorizzazioni;
import authorizer.GestoreRisorse.GestoreRisorse;
import authorizer.GestoreToken.GestoreToken;
import authorizer.GestoreRisorse.ResourceException;
import authorizer.GestoreRisorse.ResourceTypes;
import authorizer.GestoreToken.TokenException;

import org.junit.BeforeClass;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class PreTest {

    enum Scadenza{

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

    enum Utente{

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

    enum Risorsa{

        R1(1,9, ResourceTypes.DICE),
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

    static GestoreAutorizzazioni auth = null;
    static GestoreRisorse resource = null;
    static GestoreToken token = null;

    //HashMap da usare per i test
    static HashMap<String,String> nome_chiave = null;
    static HashMap<String,String> nome_token = null;

    @BeforeClass
    public static void inizializzazione(){

        try{
            auth =  GestoreAutorizzazioni.getInstance();
            nome_chiave = new HashMap<>();

            //Autorizzazioni di esempio
            nome_chiave.put(Utente.ANNA.getNome(),auth.creaAutorizzazione(Utente.ANNA.getNome(),Utente.ANNA.getLivello(),Utente.ANNA.getScadenza()));
            nome_chiave.put(Utente.MARCO.getNome(),auth.creaAutorizzazione(Utente.MARCO.getNome(),Utente.MARCO.getLivello(),Utente.MARCO.getScadenza()));
            nome_chiave.put(Utente.LUIGI.getNome(),auth.creaAutorizzazione(Utente.LUIGI.getNome(),Utente.LUIGI.getLivello(),Utente.LUIGI.getScadenza()));
            nome_chiave.put(Utente.PIPPO.getNome(),auth.creaAutorizzazione(Utente.PIPPO.getNome(),Utente.PIPPO.getLivello(),Utente.PIPPO.getScadenza()));

            resource = GestoreRisorse.getInstance();
            //Risorse di esempio
            resource.addRisorsa(Risorsa.R1.getIdRisorsa(),Risorsa.R1.getLivello(),Risorsa.R1.getTipo());
            resource.addRisorsa(Risorsa.R2.getIdRisorsa(),Risorsa.R2.getLivello(),Risorsa.R2.getTipo());
            resource.addRisorsa(Risorsa.R3.getIdRisorsa(),Risorsa.R3.getLivello(),Risorsa.R3.getTipo());

            token = GestoreToken.getInstance();
            nome_token = new HashMap<>();
            //Tokens di esempio -> Verificato
            nome_token.put(Utente.LUIGI.getNome(),token.creaToken(nome_chiave.get(Utente.LUIGI.getNome()), Risorsa.R3.getIdRisorsa()));
            nome_token.put(Utente.PIPPO.getNome(),token.creaToken(nome_chiave.get(Utente.PIPPO.getNome()), Risorsa.R1.getIdRisorsa()));

        }catch(AuthorizationException e){
            fail("Utenti di test replicati");
        }catch(TokenException e){
            fail(e.getMessage());
        }catch(ResourceException r){
            fail("ID risorsa già esistente");
        }
    }

}
