package cars.carbon.printService.dto.autoclave;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class CycleReportUploadDTO {
    private Long cycleId;
    private MultipartFile reportFile;
    private String description;
    private String analystName;
    private LocalDateTime uploadTimestamp = LocalDateTime.now();
    public CycleReportUploadDTO() {

    }
}
