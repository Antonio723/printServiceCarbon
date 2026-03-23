package cars.carbon.printService.production.cutting.service;

import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.production.cutting.dto.OperaPlateResponseDTO;
import cars.carbon.printService.repository.PlateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PlateQueryService {

    private final PlateRepository plateRepository;


    private OperaPlateResponseDTO mapToDTO(Plates plate) {
        OperaPlateResponseDTO dto = new OperaPlateResponseDTO();
        dto.setId(plate.getId());
        dto.setBatchNumber(plate.getWorkorderid().getLote());
        dto.setLayerQuantity(String.valueOf(plate.getLayers()));
        return dto;
    }
}
