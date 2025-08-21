package cars.carbon.printService.dto.autoclave;

import cars.carbon.printService.enums.CycleStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class AutoclaveCycleDTO {
    private LocalDateTime cycleDate;
}