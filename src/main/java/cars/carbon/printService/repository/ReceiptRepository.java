package cars.carbon.printService.repository;

import cars.carbon.printService.model.receipt.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long>{

}
