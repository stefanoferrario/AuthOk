package user;


import java.util.HashMap;

class Utente {

    private String nome;
    private String chiave = null;
    private HashMap<Integer, String> tokens = new HashMap<>(); //lista dei token posseduti dall' utente

    //si ASSUME che l' identificativo dell' utente sia UNIVOCO
    Utente(String idUtente) {nome = idUtente;}

    boolean hasKey() {return chiave != null;}
    String getChiave() {return chiave;}

    String getNome() {return nome;}

    void setChiave(String s) {chiave = s;}

    void putToken(int resource, String token) {
        tokens.put(resource, token);
    }

    HashMap<Integer, String> getTokens() {return tokens;}

    boolean hasTokens() {
        return tokens.size()>0;
    }
}