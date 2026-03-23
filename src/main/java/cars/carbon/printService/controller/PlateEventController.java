package cars.carbon.printService.controller;

import cars.carbon.printService.model.plate.PlateEvent;
import cars.carbon.printService.production.cutting.enums.KitType;
import cars.carbon.printService.production.cutting.enums.MaterialType;
import cars.carbon.printService.service.PlateEventService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plate-events")
public class PlateEventController {

    private final PlateEventService plateEventService;

    public PlateEventController(PlateEventService plateEventService) {
        this.plateEventService = plateEventService;
    }

    @GetMapping("/plate/{plateId}")
    public List<PlateEvent> history(@PathVariable Long plateId){
        return plateEventService.findByPlate(plateId);
    }

    @GetMapping("/metadata")
    public Map<String,Object> metadata(){

        Map<String,Object> map = new HashMap<>();

        map.put("materials", MaterialType.values());
        map.put("kitType", KitType.values());

        return map;
    }

}
