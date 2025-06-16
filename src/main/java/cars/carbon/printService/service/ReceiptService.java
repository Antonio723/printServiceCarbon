package cars.carbon.printService.service;

import cars.carbon.printService.dto.ReceiptDTO;
import cars.carbon.printService.model.receipt.Receipt;
import cars.carbon.printService.model.workOrders.WorkOrder;
import cars.carbon.printService.repository.ReceiptRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Transactional
    public Receipt createReceipt(ReceiptDTO dto) {
        Receipt receipt = new Receipt();
        receipt.setNf(dto.getNf());
        receipt.setInternBatch(dto.getInternBatch());
        receipt.setSituation(dto.getSituation());
        receipt.setQuantity(dto.getQuantity());
        receipt.setResponsible(dto.getResponsible());
        receipt.setObservation(dto.getObservation());
        receipt.setReceiveDate(dto.getReceiveDate());

        return receiptRepository.save(receipt);
    }

    @Transactional
    public List<Receipt> listAll(){
        return receiptRepository.findAll();
    }

    @Transactional
    public String deletById(Long id){
        if(receiptRepository.existsById(id)){
            receiptRepository.deleteById(id);
            return "Deletar recebimento "+id+" realizado com sucesso";
        }else{
            return "Erro ao deletar recebimento, recebimento não encontrado";
        }
    }

    @Transactional
    public Receipt updateReceipt(Long id, ReceiptDTO dto){
        Optional<Receipt> optionalReceipt = receiptRepository.findById(id);

        if (optionalReceipt.isEmpty()) {
            throw new RuntimeException("Ordem de trabalho não encontrada para o ID: " + id+ " "+optionalReceipt);
        }

        Receipt receipt = optionalReceipt.get();

        receipt.setNf(dto.getNf());
        receipt.setInternBatch(dto.getInternBatch());
        receipt.setSituation(dto.getSituation());
        receipt.setQuantity(dto.getQuantity());
        receipt.setResponsible(dto.getResponsible());
        receipt.setObservation(dto.getObservation());
        receipt.setReceiveDate(dto.getReceiveDate());

        return receiptRepository.save(receipt);
    }

}
