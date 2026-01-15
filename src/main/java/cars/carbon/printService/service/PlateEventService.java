package cars.carbon.printService.service;

import cars.carbon.printService.model.plate.PlateEvent;
import cars.carbon.printService.repository.PlateEventRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlateEventService {

    private final PlateEventRepository plateEventRepository;

    public PlateEventService(PlateEventRepository plateEventRepository) {
        this.plateEventRepository = plateEventRepository;
    }
}
