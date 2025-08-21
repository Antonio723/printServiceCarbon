package cars.carbon.printService.controller;

import cars.carbon.printService.dto.PlateStatusUpdateDTO;
import cars.carbon.printService.model.plate.Plates;
import cars.carbon.printService.service.PlateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

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

}
