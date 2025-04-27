package cars.carbon.printService.dto;

public class WorkOrderRequestDTO {
    private String enfestoType;
    private String filmeTye;
    private String lote;
    private Long platesQuantity;
    private Long platesLayres;

    public Long getPlatesQuantity() {
        return platesQuantity;
    }

    public void setPlatesQuantity(Long platesQuantity) {
        this.platesQuantity = platesQuantity;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getFilmeTye() {
        return filmeTye;
    }

    public void setFilmeTye(String filmeTye) {
        this.filmeTye = filmeTye;
    }

    public String getEnfestoType() {
        return enfestoType;
    }

    public void setEnfestoType(String enfestoType) {
        this.enfestoType = enfestoType;
    }

    public Long getPlatesLayres() {
        return platesLayres;
    }

    public void setPlatesLayres(Long platesLayres) {
        this.platesLayres = platesLayres;
    }
}
