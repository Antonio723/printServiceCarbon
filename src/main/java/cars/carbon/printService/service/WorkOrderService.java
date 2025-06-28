package cars.carbon.printService.service;

import cars.carbon.printService.dto.WorkOrderRequestDTO;
import cars.carbon.printService.model.WorkOrders.Plates;
import cars.carbon.printService.model.WorkOrders.WorkOrder;
import cars.carbon.printService.repository.WorkOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cars.carbon.printService.dto.workorder.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Transactional
    public WorkOrder createWorkOrder(WorkOrderRequestDTO dto) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setCreationDate(LocalDateTime.now());
        workOrder.setChangeDate(LocalDateTime.now());
        workOrder.setLote(dto.getLote());
        workOrder.setClothType(dto.getClothType());
        workOrder.setPlasticType(dto.getPlasticType());
        workOrder.setPlasticBatch(dto.getPlasticBatch());
        workOrder.setClothBatch(dto.getClothBatch());
        workOrder.setPlatesQuantity(dto.getPlatesQuantity());
        workOrder.setPlatesLayres(dto.getPlatesLayres());
        workOrder.setResinedBatch(dto.getResinedBatch());
        workOrder.setEnfestoDate(dto.getEnfestoDate());

        // Primeiro salva o WorkOrder para garantir que o ID é gerado
        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        List<Plates> platesList = new ArrayList<>();
        for (long i = 1; i <= dto.getPlatesQuantity(); i++) {
            Plates plate = new Plates();
            plate.setPlateSequence(i);
            plate.setWorkorderid(savedWorkOrder); // agora o ID existe
            platesList.add(plate);
        }

        savedWorkOrder.setPlates(platesList);

        return workOrderRepository.save(savedWorkOrder); // salva novamente, agora com as plates
    }

    @Transactional
    public WorkOrder updateWorkOrder(Long id, WorkOrderRequestDTO dto) {
        Optional<WorkOrder> optionalWorkOrder = workOrderRepository.findById(id);

        if (optionalWorkOrder.isEmpty()) {
            throw new RuntimeException("Ordem de trabalho não encontrada para o ID: " + id);
        }

        WorkOrder workOrder = optionalWorkOrder.get();

        workOrder.setChangeDate(LocalDateTime.now());
        workOrder.setLote(dto.getLote());
        workOrder.setClothType(dto.getClothType());
        workOrder.setPlasticType(dto.getPlasticType());
        workOrder.setPlasticBatch(dto.getPlasticBatch());
        workOrder.setClothBatch(dto.getClothBatch());
        workOrder.setPlatesQuantity(dto.getPlatesQuantity());
        workOrder.setPlatesLayres(dto.getPlatesLayres());
        workOrder.setResinedBatch(dto.getResinedBatch());
        workOrder.setEnfestoDate(dto.getEnfestoDate());

        return workOrderRepository.save(workOrder);
    }

    @Transactional
    public List<WorkOrder> listAll(){
        return workOrderRepository.findAll();
    }

    @Transactional
    public String deleteAllById(Long id){
        if (workOrderRepository.existsById(id)) {
            workOrderRepository.deleteById(id);
            return "Ordem de trabalho deletada com sucesso.";
        } else {
            return "Ordem de trabalho com ID " + id + " não encontrada.";
        }
    }


    @Transactional
    public List<EnfestoGroupDTO> listGroupedByEnfestoDate() {
        List<WorkOrder> allOrders = workOrderRepository.findAll();

        // Agrupar por data (ignorando o tempo)
        Map<LocalDate, List<WorkOrder>> groupedByDate = allOrders.stream()
                .collect(Collectors.groupingBy(w -> w.getEnfestoDate().toLocalDate()));

        List<EnfestoGroupDTO> result = new ArrayList<>();

        for (Map.Entry<LocalDate, List<WorkOrder>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<WorkOrder> workOrders = entry.getValue();

            int totalPlacas = Math.toIntExact(workOrders.stream()
                    .mapToLong(WorkOrder::getPlatesQuantity)
                    .sum());

            List<WorkOrderDTO> workOrderDTOs = workOrders.stream().map(w -> {
                WorkOrderDTO dto = new WorkOrderDTO();
                dto.setId(w.getId());
                dto.setLote(w.getLote());
                dto.setPlatesQuantity(w.getPlatesQuantity());
                dto.setPlatesLayres(w.getPlatesLayres());
                dto.setClothType(w.getClothType());
                dto.setClothBatch(w.getClothBatch());
                dto.setPlasticType(w.getPlasticType());
                dto.setPlasticBatch(w.getPlasticBatch());
                dto.setResinedBatch(w.getResinedBatch());
                dto.setPlates(w.getPlates());
                return dto;
            }).collect(Collectors.toList());

            EnfestoGroupDTO groupDTO = new EnfestoGroupDTO();
            groupDTO.setEnfestoDate(date);
            groupDTO.setTotalPlacas((long) totalPlacas);
            groupDTO.setWorkOrders(workOrderDTOs);

            result.add(groupDTO);
        }

        return result;
    }


}
