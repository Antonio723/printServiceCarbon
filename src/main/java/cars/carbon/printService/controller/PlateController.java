package cars.carbon.printService.controller;

import cars.carbon.printService.dto.PlateStatusUpdateDTO;
import cars.carbon.printService.dto.plate.PlateAddEventDTO;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.service.PlateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/plate")
@RequiredArgsConstructor
public class PlateController {
    @Autowired
    private PlateService plateService;

    @PostMapping("")
    public ResponseEntity<List<Plates>> findAllById(@RequestBody List<Long> platesId){
        return ResponseEntity.ok(plateService.listPlatesInfoById(platesId));
    }

    @PostMapping("/update-status")
    public ResponseEntity<Plates> updatePlateStatus(@RequestBody PlateStatusUpdateDTO dto) {
        Plates updatedPlate = plateService.updatePlateStatus(dto);
        return ResponseEntity.ok(updatedPlate);
    }

    @GetMapping("/getEstoque")
    public ResponseEntity<List<Plates>> findByInStock(){
        return ResponseEntity.ok(plateService.findByInStock());
    }

}
