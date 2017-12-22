package authorizer.gestoreRisorse;

public enum ResourceTypes {
    FIBO("FIBO"), DICE("DICE"), LINK("LINK");

    private String name;
    ResourceTypes(String name) {this.name = name;}
    public String getName() {return name;}
}
