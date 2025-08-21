package cars.carbon.printService.dto.autoclave;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AutoclaveCycleWithDetailsDTO {
    private Long id;
    private LocalDateTime startTime;
    private String status;
    private String cycleObservation;

    private int totalPackages;
    private int totalPlates;

    private List<AutoclavePackageDTO> packages;
    private Map<Long, Long> platesPerLayer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCycleObservation() {
        return cycleObservation;
    }

    public void setCycleObservation(String cycleObservation) {
        this.cycleObservation = cycleObservation;
    }

    public int getTotalPackages() {
        return totalPackages;
    }

    public void setTotalPackages(int totalPackages) {
        this.totalPackages = totalPackages;
    }

    public int getTotalPlates() {
        return totalPlates;
    }

    public void setTotalPlates(int totalPlates) {
        this.totalPlates = totalPlates;
    }

    public List<AutoclavePackageDTO> getPackages() {
        return packages;
    }

    public void setPackages(List<AutoclavePackageDTO> packages) {
        this.packages = packages;
    }

    public Map<Long, Long> getPlatesPerLayer() {
        return platesPerLayer;
    }

    public void setPlatesPerLayer(Map<Long, Long> platesPerLayer) {
        this.platesPerLayer = platesPerLayer;
    }
}
