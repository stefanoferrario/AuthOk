package authorizer.gestoreAutorizzazioni;

import java.security.InvalidParameterException;
import java.util.Date;

class Autorizzazione {
    private String nomeUtente;
    private int livello;
    private Date scadenza;

    //Costruttore visibilità
    Autorizzazione(String nome,int livello,Date scadenza){
        if (nome == null) {throw new InvalidParameterException("Name is null");}
        if (scadenza == null) {throw new InvalidParameterException("Date is null");}
        if (livello<1 || livello >10) {throw new InvalidParameterException("Invalid level");}
        this.nomeUtente = nome;
        this.livello = livello;
        this.scadenza = scadenza;
    }
    String getUtente(){return nomeUtente;}
    int getLivello(){return livello;}
    Date getScadenza(){return scadenza;}
}
