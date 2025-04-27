package cars.carbon.printService.service;

import cars.carbon.printService.dto.WorkOrderRequestDTO;
import cars.carbon.printService.model.WorkOrder;
import cars.carbon.printService.model.WorkOrders.Plates;
import cars.carbon.printService.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    public WorkOrder createWorkOrder(WorkOrderRequestDTO dto) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setCreationDate(LocalDateTime.now());
        workOrder.setChangeDate(LocalDateTime.now());
        workOrder.setLote(dto.getLote());
        workOrder.setEnfestoType(dto.getEnfestoType());
        workOrder.setFilmeTye(dto.getFilmeTye());
        workOrder.setPlatesQuantity(dto.getPlatesQuantity());

        // Primeiro salva o WorkOrder para garantir que o ID é gerado
        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        List<Plates> platesList = new ArrayList<>();
        for (long i = 1; i <= dto.getPlatesQuantity(); i++) {
            Plates plate = new Plates();
            plate.setPlate_sequence(i);
            plate.setWorkorderid(savedWorkOrder); // agora o ID existe
            platesList.add(plate);
        }

        savedWorkOrder.setPlates(platesList);

        return workOrderRepository.save(savedWorkOrder); // salva novamente, agora com as plates
    }

    public List<WorkOrder> listAll(){
        return workOrderRepository.findAll();
    }

    public String deleteAllById(Long id){
        if (workOrderRepository.existsById(id)) {
            workOrderRepository.deleteById(id);
            return "Ordem de trabalho deletada com sucesso.";
        } else {
            return "Ordem de trabalho com ID " + id + " não encontrada.";
        }
    }

}
