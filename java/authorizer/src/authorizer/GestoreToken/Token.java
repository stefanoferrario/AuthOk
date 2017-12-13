package authorizer.GestoreToken;

import java.util.Date;

public class Token {

	private String chiave;
	private int idRisorsa;
	private Date dataOraConcessione;
	
	Token(String _chiave, int _idRisorsa, long _millisecondiConcessione){
		chiave = _chiave;
		idRisorsa = _idRisorsa;
		dataOraConcessione = new Date(_millisecondiConcessione);
	}

	public String getChiave() {return chiave;}
	public int getIdRisorsa() {return idRisorsa;}
	public Date getData() {return dataOraConcessione;}
	public String toString() {return chiave + "-" + idRisorsa + "-" + dataOraConcessione;}

}
