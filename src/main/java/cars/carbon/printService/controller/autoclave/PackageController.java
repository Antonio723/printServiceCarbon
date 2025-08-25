package cars.carbon.printService.controller.autoclave;

import cars.carbon.printService.dto.autoclave.PackageDTO;
import cars.carbon.printService.dto.autoclave.packing.AutoclaveStatusChange;
import cars.carbon.printService.enums.PackageStatus;
import cars.carbon.printService.model.autoclave.AutoclavePackage;
import cars.carbon.printService.service.AutoclavePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/autoclave/package")
public class PackageController {

    @Autowired
    private AutoclavePackageService packageService;

    @PostMapping("/cycle")
    public ResponseEntity<AutoclavePackage> createPackage(@RequestBody PackageDTO dto) {
        return ResponseEntity.ok(packageService.createPackage(dto));
    }

    @PostMapping("/{packid}/addPlates")
    public ResponseEntity<AutoclavePackage> addPlatesToPackage(@PathVariable Long packid,
                                                               @RequestBody List<Long> plateIds) {
        return ResponseEntity.ok(packageService.addPlatesToPackage(packid, plateIds));
    }


    //todo: criar dto para alteração de status
    @PostMapping("/{packid}/updateStatus")
    public ResponseEntity<AutoclavePackage> updatePackageStatus(@PathVariable Long packid,
                                                               @RequestBody AutoclaveStatusChange dto) {
        return ResponseEntity.ok(packageService.updatePackageStatus(packid, dto));
    }
    /*
    @GetMapping("/cycle/{cycleId}")
    public ResponseEntity<List<AutoclavePackage>> getPackagesByCycle(@PathVariable Long cycleId) {
        return ResponseEntity.ok(packageService.getPackagesInCycle(cycleId));
    }*/
}