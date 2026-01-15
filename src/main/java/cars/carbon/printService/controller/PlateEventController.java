package cars.carbon.printService.controller;

import cars.carbon.printService.service.PlateEventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plate-events")
public class PlateEventController {

    private final PlateEventService plateEventService;

    public PlateEventController(PlateEventService plateEventService) {
        this.plateEventService = plateEventService;
    }

}
