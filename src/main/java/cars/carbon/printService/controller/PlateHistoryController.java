package cars.carbon.printService.controller;

import cars.carbon.printService.model.plate.PlateHistory;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.repository.PlateRepository;
import cars.carbon.printService.service.PlateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/autoclave/plate/history")
public class PlateHistoryController {

    private final PlateRepository plateRepository;
    private final PlateHistoryService historyService;

    public PlateHistoryController(PlateRepository plateRepository, PlateHistoryService historyService) {
        this.plateRepository = plateRepository;
        this.historyService = historyService;
    }

    @GetMapping("/{plateId}/history")
    public List<PlateHistory> getHistory(@PathVariable Long plateId) {
        Plates plate = plateRepository.findById(plateId).orElseThrow();
        return historyService.getHistory(plate);
    }


}