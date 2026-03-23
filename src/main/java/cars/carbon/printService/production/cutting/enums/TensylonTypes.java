package cars.carbon.printService.production.cutting.enums;

public enum TensylonTypes {
    a30("30A"),
    a40("40A");

    private final String description;

    TensylonTypes(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
