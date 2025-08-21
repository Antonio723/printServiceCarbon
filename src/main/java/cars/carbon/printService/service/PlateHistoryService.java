package cars.carbon.printService.service;

import cars.carbon.printService.model.autoclave.AutoclaveCycle;
import cars.carbon.printService.model.autoclave.AutoclavePackage;
import cars.carbon.printService.model.plate.PlateHistory;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.repository.PlateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlateHistoryService {

    private final PlateHistoryRepository repository;

    public void log(Plates plate, String status, String description,
                    AutoclaveCycle cycle, AutoclavePackage packageEntity) {
        PlateHistory history = PlateHistory.builder()
                .plate(plate)
                .status(status)
                .description(description)
                .timestamp(LocalDateTime.now())
                .cycle(cycle)
                .packageEntity(packageEntity)
                .build();
        repository.save(history);
    }

    public List<PlateHistory> getHistory(Plates plate) {
        return repository.findByPlateOrderByTimestampAsc(plate);
    }
}
