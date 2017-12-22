package test;

import authorizer.gestoreAutorizzazioni.AuthorizationException;
import authorizer.gestoreAutorizzazioni.GestoreAutorizzazioni;
import authorizer.gestoreRisorse.GestoreRisorse;
import authorizer.gestoreToken.GestoreToken;
import authorizer.gestoreRisorse.ResourceException;
import authorizer.gestoreRisorse.ResourceTypes;
import authorizer.gestoreToken.TokenException;

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
        MARCO("Marco",4,Scadenza.DATA_UNO.getData()),
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

        R1("R1",9, ResourceTypes.DICE),
        R2("R2",6,ResourceTypes.FIBO),
        R3("R3",4,ResourceTypes.LINK);

        private final String nomeRisorsa;
        private final int livello;
        private final ResourceTypes tipo;
        Risorsa(String nome, int livello, ResourceTypes tipo){
            this.nomeRisorsa = nome;
            this.livello = livello;
            this.tipo = tipo;
        }
        public String getNomeRisorsa(){return this.nomeRisorsa;}
        public int getLivello(){return this.livello;}
        public ResourceTypes getTipo() {return this.tipo;}
    }

    static GestoreAutorizzazioni auth = null;
    static GestoreRisorse resource = null;
    static GestoreToken token = null;

    //HashMap da usare per i test
    static HashMap<String,String> nome_chiave = null;
    static HashMap<String,String> nome_token = null;
    static HashMap<String,Integer> nome_idRisorsa = null;

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
            nome_idRisorsa = new HashMap<>();

            //Risorse di esempio
            nome_idRisorsa.put(Risorsa.R1.getNomeRisorsa(),resource.addRisorsa(Risorsa.R1.getLivello(),Risorsa.R1.getTipo()));
            nome_idRisorsa.put(Risorsa.R2.getNomeRisorsa(),resource.addRisorsa(Risorsa.R2.getLivello(),Risorsa.R2.getTipo()));
            nome_idRisorsa.put(Risorsa.R3.getNomeRisorsa(),resource.addRisorsa(Risorsa.R3.getLivello(),Risorsa.R3.getTipo()));

            token = GestoreToken.getInstance();
            nome_token = new HashMap<>();

            //Tokens di esempio
            nome_token.put(Utente.LUIGI.getNome(),token.creaToken(nome_chiave.get(Utente.LUIGI.getNome()), nome_idRisorsa.get(Risorsa.R3.getNomeRisorsa())));
            nome_token.put(Utente.PIPPO.getNome(),token.creaToken(nome_chiave.get(Utente.PIPPO.getNome()), nome_idRisorsa.get(Risorsa.R1.getNomeRisorsa())));

        }catch(AuthorizationException e){
            fail("Utenti di test replicati");
        }catch(TokenException e){
            fail(e.getMessage());
        }catch(ResourceException r){
            fail(r.getMessage());
        }
    }
}
