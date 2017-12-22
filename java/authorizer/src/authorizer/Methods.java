package authorizer;

public enum Methods {
    CREA_TOKEN("CREA_TOKEN", 2),
    VERIFICA_TOKEN("VERIFICA_TOKEN", 2),
    CREA_AUTORIZZAZIONE("CREA_AUTORIZZAZIONE", 3),
    VERIFICA_ESISTENZA_AUTORIZZAZIONE("VERIFICA_ESISTENZA_AUTORIZZAZIONE", 1),
    CREA_RISORSA("CREA_RISORSA", 2),
    MODIFICA_LIV_RISORSA("MODIFICA_LIV_RISORSA", 2),
    MODIFICA_ID_RISORSA("MODIFICA_ID_RISORSA", 2),
    CANCELLA_RISORSA("CANCELLA_RISORSA", 1),
    REVOCA_AUTORIZZAZIONE("REVOCA_AUTORIZZAZIONE", 1),
    SERVER_STATE("SERVER_STATE", 0);

    private final String name;
    private final int params_num;

    Methods(String method_name, int params_num) {
        this.name = method_name;
        this.params_num = params_num;
    }

    public String getName() {
        return this.name;
    }

    public int getParamsNum() {
        return this.params_num;
    }

}
