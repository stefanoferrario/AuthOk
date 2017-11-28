package authorizer;

interface Risorsa {
	int getLivello();
	boolean verificaToken();
}

interface FactoryRisorsa {
	Risorsa creaRisorsa();
}

class Risorsa1 implements Risorsa {
	private Risorsa1() {}	
	public int getLivello() {
		System.out.print("Risorsa1  getLivello()");
		return 1;
	}
	public boolean verificaToken() {
		System.out.print("Risorsa1 verificaToken");
		return true;
	}
	public static FactoryRisorsa factory =	new FactoryRisorsa() {
		public Risorsa creaRisorsa() {return new Risorsa1();}
	};
}

class Risorsa2 implements Risorsa {
	private Risorsa2() {}	
	public int getLivello() {
		System.out.print("Risorsa2  getLivello()");
		return 1;
	}
	public boolean verificaToken() {
		System.out.print("Risorsa2 verificaToken");
		return true;
	}
	public static FactoryRisorsa factory =	new FactoryRisorsa() {
		public Risorsa creaRisorsa() {return new Risorsa2();}
	};
}