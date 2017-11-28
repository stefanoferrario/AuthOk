package authorizer;
import java.util.Date;

public class Token {

	private String chiave;
	private int idRisorsa;
	private Date dataOraConcessione;
	
	Token(String _chiave, int _idRisorsa, Date _dataOraConcessione){
		chiave = _chiave;
		idRisorsa = _idRisorsa;
		dataOraConcessione = _dataOraConcessione;
	}
	String getChiave() {return chiave;}
	int getIdRisorsa() {return idRisorsa;}
	Date getData() {return dataOraConcessione;}
	public String toString() {return chiave + "-" + idRisorsa + "-" + dataOraConcessione;}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
