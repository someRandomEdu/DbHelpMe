package library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentDataRepository extends JpaRepository<RentData, Long> {
    Optional<RentData> findByBookIdAndAccountId(Long bookId, Long accountId);
    List<RentData> findAllByBookIdAndAccountId(Long bookId, Long accountId);
    boolean existsByAccountIdAndBookId(Long accountId, Long bookId);
}
