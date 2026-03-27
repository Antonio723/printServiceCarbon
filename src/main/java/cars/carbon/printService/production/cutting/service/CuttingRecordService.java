package cars.carbon.printService.production.cutting.service;

import cars.carbon.printService.enums.PlateEventType;
import cars.carbon.printService.enums.PlateStatus;
import cars.carbon.printService.model.plate.PlateEvent;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.production.cutting.dto.CuttingRecordRequestDTO;
import cars.carbon.printService.production.cutting.dto.CuttingRecordResponseDTO;
import cars.carbon.printService.production.cutting.dto.PlateConsumptionRequestDTO;
import cars.carbon.printService.production.cutting.dto.PlateConsumptionResponseDTO;
import cars.carbon.printService.production.cutting.enums.MaterialType;
import cars.carbon.printService.production.cutting.enums.SupplierType;
import cars.carbon.printService.production.cutting.exceptions.BusinessException;
import cars.carbon.printService.production.cutting.exceptions.ResourceNotFoundException;
import cars.carbon.printService.production.cutting.model.CuttingRecord;
import cars.carbon.printService.production.cutting.model.PlateConsumption;
import cars.carbon.printService.production.cutting.repository.CuttingRecordRepository;
import cars.carbon.printService.production.cutting.repository.PlateConsumptionRepository;
import cars.carbon.printService.repository.PlateEventRepository;
import cars.carbon.printService.repository.PlateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuttingRecordService {

    private final CuttingRecordRepository cuttingRecordRepository;
    private final PlateConsumptionRepository plateConsumptionRepository;
    private final PlateRepository plateRepository;
    private final PlateEventRepository plateEventRepository;

    @Transactional
    public CuttingRecordResponseDTO createCuttingRecord(
            CuttingRecordRequestDTO request) {

        CuttingRecord cuttingRecord = new CuttingRecord();

        cuttingRecord.setProductionDate(
                request.getProductionDate() != null ?
                        LocalDateTime.of(
                                request.getProductionDate().toLocalDate(),
                                LocalDateTime.now().toLocalTime()
                        )
                        :
                        LocalDateTime.now()
        );

        cuttingRecord.setOrderNumber(request.getOrderNumber());
        cuttingRecord.setOrderDescription(request.getOrderDescription());
        cuttingRecord.setMaterial(request.getMaterial());
        cuttingRecord.setKitType(request.getKitType());
        cuttingRecord.setSeal(request.getSeal());

        CuttingRecord savedRecord =
                cuttingRecordRepository.save(cuttingRecord);

        boolean isAramida =
                request.getMaterial() == MaterialType.ARAMIDA;

        for (PlateConsumptionRequestDTO consumptionRequest :
                request.getConsumptions()) {

            validateConsumption(
                    consumptionRequest,
                    request.getMaterial()
            );

            PlateConsumption consumption =
                    createPlateConsumption(
                            consumptionRequest,
                            savedRecord
                    );

            savedRecord.getConsumptions()
                    .add(consumption);

            boolean usesOperaPlate =
                    isAramida &&
                            consumptionRequest.getSupplier()
                                    == SupplierType.OPERA &&
                            consumptionRequest.getPlateId() != null;

            if (usesOperaPlate) {
                createPlateEvent(
                        consumptionRequest,
                        consumption
                );
            }
        }

        return mapToResponseDTO(savedRecord);
    }

    private void validateConsumption(
            PlateConsumptionRequestDTO request,
            MaterialType material
    ) {

        boolean isAramida =
                material == MaterialType.ARAMIDA;

        if (isAramida &&
                request.getSupplier() == SupplierType.OPERA &&
                request.getPlateId() != null){

            Plates plate =
                    plateRepository.findById(
                                    request.getPlateId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Plate not found with id: "
                                                    + request.getPlateId()
                                    )
                            );

            request.setBatchNumber(
                    plate.getWorkorderid().getLote()
            );

            request.setLayerQuantity(
                    String.valueOf(plate.getLayers())
            );

        } else {

            request.setPlateId(null);

            if(request.getUsedMetrage()
                    .compareTo(BigDecimal.ZERO) <= 0){

                throw new BusinessException(
                        "Used metrage must be greater than zero"
                );
            }
        }
    }

    private PlateConsumption createPlateConsumption(
            PlateConsumptionRequestDTO request,
            CuttingRecord cuttingRecord
    ){

        PlateConsumption consumption = new PlateConsumption();
        consumption.setInvoiceNumber(
                request.getInvoiceNumber()
        );
        consumption.setBatchNumber(
                request.getBatchNumber()
        );
        consumption.setUsedMetrage(
                request.getUsedMetrage()
        );
        consumption.setSupplier(request.getSupplier());
        consumption.setLayerQuantity(
                request.getLayerQuantity()
        );

        consumption.setManualBatch(

                request.getSupplier() != SupplierType.OPERA ||

                        cuttingRecord.getMaterial()
                                != MaterialType.ARAMIDA
        );

        consumption.setCuttingRecord(
                cuttingRecord
        );

        if(request.getSupplier() == SupplierType.OPERA &&
                cuttingRecord.getMaterial() == MaterialType.ARAMIDA &&
                request.getPlateId() != null){

            Plates plate =
                    plateRepository.getReferenceById(
                            request.getPlateId()
                    );

            consumption.setPlate(plate);
        }

        return plateConsumptionRepository.save(consumption);
    }

    private void createPlateEvent(
            PlateConsumptionRequestDTO request,
            PlateConsumption consumption){

        Plates plate =
                plateRepository.findById(
                                request.getPlateId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Plate not found with id: "
                                                + request.getPlateId()
                                )
                        );

        PlateEvent event = new PlateEvent();
        event.setPlate(plate);
        event.setEventType(
                PlateEventType.USO_CORTE
        );

        event.setEventDate(
                LocalDateTime.now()
        );

        event.setConsumedArea(
                request.getUsedMetrage().doubleValue()
        );

        event.setDescription(
                "Consumo em corte - Apontamento: "
                        + consumption.getCuttingRecord().getId()
                        + ", Consumo: "
                        + consumption.getId()
        );

        event.setConsumptionReferenceId(
                consumption.getId()
        );

        plateEventRepository.save(event);

        Double newMetrage =
                plate.getActualSize()
                        - request.getUsedMetrage().doubleValue();

        plate.setActualSize(newMetrage);

        plateRepository.save(plate);
    }

    public List<CuttingRecordResponseDTO> getAllCuttingRecords(){

        return cuttingRecordRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public CuttingRecordResponseDTO getCuttingRecordById(Long id){

        CuttingRecord record =
                cuttingRecordRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cutting record not found with id: "
                                                + id
                                )
                        );

        return mapToResponseDTO(record);
    }

    private CuttingRecordResponseDTO mapToResponseDTO(
            CuttingRecord record){

        CuttingRecordResponseDTO dto =
                new CuttingRecordResponseDTO();

        dto.setId(record.getId());
        dto.setProductionDate(record.getProductionDate());
        dto.setOrderNumber(record.getOrderNumber());
        dto.setOrderDescription(record.getOrderDescription());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setMaterial(record.getMaterial());
        dto.setKitType(record.getKitType());
        dto.setSeal(record.getSeal());
        List<PlateConsumptionResponseDTO>
                consumptionDTOs =

                record.getConsumptions()
                        .stream()
                        .map(this::mapToResponseDTO)
                        .collect(Collectors.toList());

        dto.setConsumptions(consumptionDTOs);
        return dto;
    }

    private PlateConsumptionResponseDTO mapToResponseDTO(
            PlateConsumption consumption){

        PlateConsumptionResponseDTO dto =
                new PlateConsumptionResponseDTO();

        dto.setId(consumption.getId());

        dto.setInvoiceNumber(
                consumption.getInvoiceNumber()
        );

        dto.setBatchNumber(
                consumption.getBatchNumber()
        );

        dto.setUsedMetrage(
                consumption.getUsedMetrage()
        );

        dto.setSupplier(
                consumption.getSupplier().name()
        );

        dto.setLayerQuantity(
                consumption.getLayerQuantity()
        );

        dto.setManualBatch(
                consumption.getManualBatch()
        );

        if(consumption.getPlate() != null){

            dto.setPlateId(
                    consumption.getPlate().getId()
            );

            dto.setPlateBatchNumber(

                    consumption.getPlate()
                            .getWorkorderid()
                            .getLote()
            );
        }

        return dto;
    }

    @Transactional
    public void deleteCuttingRecord(Long id){

        CuttingRecord record =
                cuttingRecordRepository.findById(id)
                        .orElseThrow(()->
                                new ResourceNotFoundException(
                                        "Cutting record not found with id: " + id
                                )
                        );

        for(PlateConsumption consumption : record.getConsumptions()){

            if(consumption.getPlate() != null){

                Plates plate = consumption.getPlate();

                Double restoredMetrage =
                        plate.getActualSize() +
                                consumption.getUsedMetrage().doubleValue();

                plate.setActualSize(restoredMetrage);

                plateRepository.save(plate);

                plateEventRepository
                        .deleteByConsumptionReferenceId(
                                consumption.getId()
                        );
            }
        }

        plateConsumptionRepository.deleteAll(
                record.getConsumptions()
        );

        cuttingRecordRepository.delete(record);
    }

    @Transactional
    public CuttingRecordResponseDTO updateCuttingRecord(
            Long id,
            CuttingRecordRequestDTO request){

        CuttingRecord record =
                cuttingRecordRepository.findById(id)
                        .orElseThrow(()->
                                new ResourceNotFoundException(
                                        "Cutting record not found with id: " + id
                                )
                        );

        // 1 Reverter consumos antigos
        for(PlateConsumption consumption :
                record.getConsumptions()){

            if(consumption.getPlate() != null){

                Plates plate = consumption.getPlate();

                Double restoredSize =
                        plate.getActualSize() +
                                consumption.getUsedMetrage().doubleValue();

                plate.setActualSize(restoredSize);

                plateRepository.save(plate);

                plateEventRepository
                        .deleteByConsumptionReferenceId(
                                consumption.getId()
                        );
            }
        }

        // 2 remover consumos antigos
        plateConsumptionRepository.deleteAll(
                record.getConsumptions()
        );

        record.getConsumptions().clear();

        // 3 atualizar dados base
        record.setProductionDate(
                request.getProductionDate()
        );

        record.setOrderNumber(
                request.getOrderNumber()
        );

        record.setOrderDescription(
                request.getOrderDescription()
        );

        record.setMaterial(request.getMaterial());

        record.setKitType(request.getKitType());
        record.setSeal(request.getSeal());

        boolean isAramida =
                request.getMaterial() == MaterialType.ARAMIDA;

        // 4 recriar consumptions
        for(PlateConsumptionRequestDTO consumptionRequest :
                request.getConsumptions()){

            validateConsumption(
                    consumptionRequest,
                    request.getMaterial()
            );

            PlateConsumption consumption =
                    createPlateConsumption(
                            consumptionRequest,
                            record
                    );

            record.getConsumptions().add(consumption);

            boolean usesOperaPlate =
                    isAramida &&
                            consumptionRequest.getSupplier()
                                    == SupplierType.OPERA &&
                            consumptionRequest.getPlateId() != null;

            if(usesOperaPlate){

                createPlateEvent(
                        consumptionRequest,
                        consumption
                );
            }
        }

        CuttingRecord saved =
                cuttingRecordRepository.save(record);

        return mapToResponseDTO(saved);
    }
}