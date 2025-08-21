package cars.carbon.printService.dto.autoclave;

import cars.carbon.printService.enums.PackageStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class AutoclavePackageDTO {
    private Long id;
    private int totalPlates;
    private String packageObservations;
    private List<PlateDTO> plates;
    private Long autoclaveCycleId;
    private LocalDateTime creationTime;
    private PackageStatus packageStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTotalPlates() {
        return totalPlates;
    }

    public void setTotalPlates(int totalPlates) {
        this.totalPlates = totalPlates;
    }

    public String getPackageObservations() {
        return packageObservations;
    }

    public void setPackageObservations(String packageObservations) {
        this.packageObservations = packageObservations;
    }

    public List<PlateDTO> getPlates() {
        return plates;
    }

    public void setPlates(List<PlateDTO> plates) {
        this.plates = plates;
    }

    public Long getAutoclaveCycleId() {
        return autoclaveCycleId;
    }

    public void setAutoclaveCycleId(Long autoclaveCycleId) {
        this.autoclaveCycleId = autoclaveCycleId;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public PackageStatus getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(PackageStatus packageStatus) {
        this.packageStatus = packageStatus;
    }
}
