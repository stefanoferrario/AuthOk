package authorizer.GestoreRisorse;

public abstract class Risorsa {
	
	private int livello = 0;
	private int id = 0;

	public Risorsa(int _id, int _livello){id = _id;	livello = _livello;}
	
	public int getLivello() {return livello;}

	public int getId() {return id;}

	public void setLivello(int _livello) {livello = _livello;}

	public void setId(int _id) {id = _id;}
}