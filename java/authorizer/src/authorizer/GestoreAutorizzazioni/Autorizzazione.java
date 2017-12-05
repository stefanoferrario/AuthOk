package authorizer.GestoreAutorizzazioni;

import java.util.Date;

class Autorizzazione {
    private String nomeUtente;
    private int livello;
    private Date scadenza;

    //Costruttore visibilità
    Autorizzazione(String nome,int livello,Date scadenza){
        this.nomeUtente = nome;
        this.livello = livello;
        this.scadenza = scadenza;
    }
    String getUtente(){return nomeUtente;}
    int getLivello(){return livello;}
    Date getScadenza(){return scadenza;}
}
