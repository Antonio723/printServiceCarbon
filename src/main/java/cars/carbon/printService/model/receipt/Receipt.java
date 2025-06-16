package cars.carbon.printService.model.receipt;

import jakarta.persistence.*;

@Entity
@Table(name = "Receipt")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receipt_seq")
    @SequenceGenerator(
            name = "receipt_seq",
            sequenceName = "receipt_sequence",
            initialValue = 250,
            allocationSize = 1
    )
    private Long id;

    private String nf;
    private String internBatch;
    private String situation;
    private String quantity;
    private String responsible;
    private String observation;
    private String receiveDate;

    @Deprecated
    public Receipt() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNf() {
        return nf;
    }

    public void setNf(String nf) {
        this.nf = nf;
    }

    public String getInternBatch() {
        return internBatch;
    }

    public void setInternBatch(String internBatch) {
        this.internBatch = internBatch;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }
}
