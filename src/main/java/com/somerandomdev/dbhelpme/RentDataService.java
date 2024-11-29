package com.somerandomdev.dbhelpme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class RentDataService extends JpaService<RentData, Long> {
    private RentDataRepository rentDataRepository;

    public RentDataService(JpaRepository<RentData, Long> repository) {
        super(repository);
    }

        public void returnBook(Long bookId, Long accountId) {
            Optional<RentData> rentData = rentDataRepository.findByBookIdAndAccountId(bookId, accountId);
                RentData data = rentData.get();
                rentDataRepository.delete(data);
        }
}
