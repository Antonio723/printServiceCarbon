package cars.carbon.printService.production.cutting.enums;


public enum KitType {

    KIT_COMUM("Kit comum"),
    AVULSA("Peças Avulsas"),
    REBLINDAGEM("KIT de reblindagem"),
    DESENVOLIVMENTO("DESENVOLVIMENTO"),
    CORPO_DE_PROVA("TESTE BALISTICO");


    private final String description;

    KitType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

}
