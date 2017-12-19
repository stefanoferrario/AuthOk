package authorizer.GestoreRisorse;

import java.security.InvalidParameterException;

public abstract class Risorsa {
	
	private int livello = 0;
	private int id = 0;

	public Risorsa(int _id, int _livello){
	    if (_livello < 1 || _livello > 9) {throw new InvalidParameterException("Invalid level (level 1-9)");}
		id = _id;
		livello = _livello;
	}
	
	public int getLivello() {return livello;}

	public int getId() {return id;}

	public void setLivello(int _livello) {
        if (_livello < 1 || _livello > 9) {throw new InvalidParameterException("Invalid level (level 1-9)");}
	    livello = _livello;
	}

	public void setId(int _id) {id = _id;}
}