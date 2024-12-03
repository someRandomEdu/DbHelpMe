package library;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentDataRepository extends JpaRepository<RentData, Long> {
    Optional<RentData> findByBookIdAndAccountId(Long bookId, Long accountId);
}
