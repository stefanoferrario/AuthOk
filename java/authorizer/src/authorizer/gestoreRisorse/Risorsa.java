package authorizer.gestoreRisorse;

import java.security.InvalidParameterException;

public abstract class Risorsa {
	
	private int livello = 0;

	public Risorsa(int _livello){
	    if (_livello < 1 || _livello > 9) {throw new InvalidParameterException("Invalid level (level 1-9)");}
		livello = _livello;
	}
	
	public int getLivello() {return livello;}

	public void setLivello(int _livello) {
        if (_livello < 1 || _livello > 9) {throw new InvalidParameterException("Invalid level (level 1-9)");}
	    livello = _livello;
	}
}