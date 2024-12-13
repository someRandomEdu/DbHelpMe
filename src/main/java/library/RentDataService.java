package library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public final class RentDataService extends JpaService<RentData, Integer> {
    private RentDataRepository rentDataRepository;

    public RentDataService(JpaRepository<RentData, Integer> repository) {
        super(repository);
    }
        public void returnBook(Integer bookId, Integer accountId) {
            var rentData = rentDataRepository.findByBookIdAndAccountId(bookId, accountId);
            var data = rentData.get();
            rentDataRepository.delete(data);
        }

    public LocalDate getBorrowTo(Integer bookId, Integer accountId) {
        return rentDataRepository.findByBookIdAndAccountId(bookId, accountId)
                .map(RentData::getBorrowTo)
                .orElse(null);
    }
}
