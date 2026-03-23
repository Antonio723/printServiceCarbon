package cars.carbon.printService.production.cutting.enums;

public enum MaterialType {

    ARAMIDA("ARAMIDA"),
    TENSYLON_30A("TENSYLON_30A"),
    TENSYLON_40A("TENSYLON_40A");

    private final String description;

    MaterialType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

}
