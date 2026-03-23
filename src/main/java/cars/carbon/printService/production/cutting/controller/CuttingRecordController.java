package cars.carbon.printService.production.cutting.controller;

import cars.carbon.printService.production.cutting.dto.CuttingRecordRequestDTO;
import cars.carbon.printService.production.cutting.dto.CuttingRecordResponseDTO;
import cars.carbon.printService.production.cutting.dto.MetadataResponseDTO;
import cars.carbon.printService.production.cutting.dto.OperaPlateResponseDTO;
import cars.carbon.printService.production.cutting.enums.KitType;
import cars.carbon.printService.production.cutting.enums.MaterialType;
import cars.carbon.printService.production.cutting.enums.SupplierType;
import cars.carbon.printService.production.cutting.enums.TensylonTypes;
import cars.carbon.printService.production.cutting.service.CuttingRecordService;
import cars.carbon.printService.production.cutting.service.PlateQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cutting")
@RequiredArgsConstructor
public class CuttingRecordController {

    private final CuttingRecordService cuttingRecordService;
    private final PlateQueryService plateQueryService;

    @PostMapping
    public ResponseEntity<CuttingRecordResponseDTO> createCuttingRecord(
            @Validated @RequestBody CuttingRecordRequestDTO request) {
        CuttingRecordResponseDTO response = cuttingRecordService.createCuttingRecord(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CuttingRecordResponseDTO>> getAllCuttingRecords() {
        List<CuttingRecordResponseDTO> records = cuttingRecordService.getAllCuttingRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuttingRecordResponseDTO> getCuttingRecordById(@PathVariable Long id) {
        CuttingRecordResponseDTO record = cuttingRecordService.getCuttingRecordById(id);
        return ResponseEntity.ok(record);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuttingRecord(
            @PathVariable Long id){

        cuttingRecordService.deleteCuttingRecord(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuttingRecordResponseDTO> updateCuttingRecord(
            @PathVariable Long id,
            @Validated @RequestBody CuttingRecordRequestDTO request){

        CuttingRecordResponseDTO response =
                cuttingRecordService.updateCuttingRecord(id, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/metadata")
    public ResponseEntity<MetadataResponseDTO> getMetadata() {
        MetadataResponseDTO metadata = new MetadataResponseDTO();

        // Suppliers
        metadata.setSuppliers(Arrays.stream(SupplierType.values())
                .map(Enum::name)
                .collect(Collectors.toList()));

        // Layers
        metadata.setLayers(Arrays.asList("8", "9", "11"));
        //Material
        metadata.setMaterial(Arrays.stream(MaterialType.values()).map(Enum::name).collect(Collectors.toList()));
        //kit type
        metadata.setKitType(Arrays.stream(KitType.values()).map(Enum::name).collect(Collectors.toList()));

        //Tensylon
        metadata.setTensylonTypes(
                Arrays.stream(TensylonTypes.values())
                        .map(TensylonTypes::getDescription)
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(metadata);
    }

    /*@GetMapping("/plates/opera")
    public ResponseEntity<List<OperaPlateResponseDTO>> getAvailableOperaPlates() {
        List<OperaPlateResponseDTO> plates = plateQueryService.getAvailableOperaPlates();
        return ResponseEntity.ok(plates);
    }*/
}