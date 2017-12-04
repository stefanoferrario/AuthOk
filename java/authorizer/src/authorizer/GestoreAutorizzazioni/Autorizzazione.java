package authorizer.GestoreAutorizzazioni;

import java.util.Date;

public class Autorizzazione {
    private String nomeUtente;
    private int livello;
    private Date data;

    //Costruttore visibilità
    Autorizzazione(String nome,int liv,Date scad){
        nomeUtente = nome;
        livello = liv;
        data = scad;
    }
    String getUtente(){return nomeUtente;}
    int getLivello(){return livello;}
    Date getScadenza(){return data;}
}
