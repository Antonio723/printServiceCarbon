package cars.carbon.printService.dto.workorder;

import java.time.LocalDate;
import java.util.List;

public class EnfestoGroupDTO {
    private LocalDate enfestoDate;
    private Long totalPlacas;
    private List<WorkOrderDTO> workOrders;

    public List<WorkOrderDTO> getWorkOrders() {
        return workOrders;
    }

    public void setWorkOrders(List<WorkOrderDTO> workOrders) {
        this.workOrders = workOrders;
    }

    public Long getTotalPlacas() {
        return totalPlacas;
    }

    public void setTotalPlacas(Long totalPlacas) {
        this.totalPlacas = totalPlacas;
    }

    public LocalDate getEnfestoDate() {
        return enfestoDate;
    }

    public void setEnfestoDate(LocalDate enfestoDate) {
        this.enfestoDate = enfestoDate;
    }
}
