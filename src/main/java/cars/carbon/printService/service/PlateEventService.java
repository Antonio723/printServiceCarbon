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

    // Retorna todas as OS agrupadas
    public List<Map<String, Object>> getEventsGroupedByOs() {
        List<PlateEvent> events = plateEventRepository.findAll();

        // Agrupar por OS e converter em lista
        return events.stream()
                .collect(Collectors.groupingBy(PlateEvent::getOs))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> group = new LinkedHashMap<>();
                    group.put("os", entry.getKey());
                    group.put("events", entry.getValue().stream().map(event -> {
                        Map<String, Object> dto = new LinkedHashMap<>();
                        dto.put("id", event.getId());
                        dto.put("type", event.getType().toString());
                        dto.put("value", event.getValue());
                        dto.put("details", event.getDetails());
                        dto.put("timestamp", event.getTimestamp());
                        dto.put("plateId", event.getPlate().getId());
                        return dto;
                    }).toList());
                    return group;
                })
                .toList();
    }

    // Retorna apenas uma OS específica agrupada
    public List<Map<String, Object>> getEventsGroupedBySingleOs(String os) {
        List<PlateEvent> events = plateEventRepository.findByOs(os);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> group = new LinkedHashMap<>();
        group.put("os", os);
        group.put("events", events.stream().map(event -> {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", event.getId());
            dto.put("type", event.getType().toString());
            dto.put("value", event.getValue());
            dto.put("details", event.getDetails());
            dto.put("timestamp", event.getTimestamp());
            dto.put("plateSequence", event.getPlate().getPlateSequence());
            return dto;
        }).toList());

        return List.of(group);
    }
}
