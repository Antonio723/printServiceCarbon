package cars.carbon.printService.service;

import cars.carbon.printService.dto.PlateStatusUpdateDTO;
import cars.carbon.printService.dto.plate.PlateConsumptionDTO;
import cars.carbon.printService.enums.PlateEventType;
import cars.carbon.printService.enums.PlateStatus;
import cars.carbon.printService.model.plate.PlateEvent;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.repository.PlateEventRepository;
import cars.carbon.printService.repository.PlateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlateService {
    @Autowired
    private PlateRepository plateRepository;
    private PlateEventRepository plateEventRepository;

    @Autowired
    public PlateService(PlateRepository plateRepository, PlateEventRepository plateEventRepository) {
        this.plateRepository = plateRepository;
        this.plateEventRepository = plateEventRepository;
    }

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

    @Transactional
    public List<Plates> findByInStock(){
        List<PlateStatus> stausSelecionados = EnumSet.of(
                PlateStatus.CONSUMO_PARCIAL,
                PlateStatus.EM_ESTOQUE
        ).stream().collect(Collectors.toList());
        return plateRepository.findByStatusIn(stausSelecionados);
    }

    @Transactional
    public Optional<Plates> addConsumption(PlateConsumptionDTO dto){
        Optional<Plates> optionalPlate = plateRepository.findById(dto.getPlateId());

        if (optionalPlate.isEmpty()) {
            throw new RuntimeException("Placa com ID " + dto.getPlateId() + " não encontrada.");
        }

        Plates plate = optionalPlate.get();
        plate.setActualSize(plate.getActualSize()-dto.getLength());
        //Se a placa tiver saldo menor de 8Cm o estoque é eliminado
        if(plate.getActualSize() >= 0.08){
            plate.setStatus(PlateStatus.CONSUMO_PARCIAL);
        }else if(dto.length >= plate.getActualSize()){
            plate.setStatus(PlateStatus.CONSUMO_TOTAL);

        }
        Plates savedPlate = plateRepository.save(plate);

        PlateEvent plateEvent = new PlateEvent();
        plateEvent.setPlate(plate);
        plateEvent.setType(PlateEventType.CONSUMO);
        plateEvent.setValue(dto.length);
        plateEvent.setTimestamp(LocalDateTime.now());
        plateEvent.setDetails("Consumo feito pela OS:"+dto.getOS());
        plateEvent.setOs(dto.getOS());

        plateEventRepository.save(plateEvent);

        return Optional.of(savedPlate);
    }

}
