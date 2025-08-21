package cars.carbon.printService.service;

import cars.carbon.printService.dto.PlateStatusUpdateDTO;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.repository.PlateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlateService {
    @Autowired
    private PlateRepository plateRepository;

    @Transactional
    public Plates updatePlateStatus(PlateStatusUpdateDTO dto) {
        Optional<Plates> optionalPlate = plateRepository.findById(dto.getPlateId());
        if (optionalPlate.isEmpty()) {
            throw new RuntimeException("Placa com ID " + dto.getPlateId() + " não encontrada.");
        }

        Plates plate = optionalPlate.get();
        plate.setStatus(dto.getNewStatus());
        return plateRepository.save(plate);
    }


    @Transactional
    public List<Plates> listPlatesInfoById(List<Long> PlatesList){
        return plateRepository.findAllById(PlatesList);
    }
}
