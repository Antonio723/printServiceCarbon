package cars.carbon.printService.dto.autoclave;

import cars.carbon.printService.enums.CycleStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompletCycle {
    private Long cycleId;
    private CycleStatus newStatus;

}

