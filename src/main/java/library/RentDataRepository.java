package library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentDataRepository extends JpaRepository<RentData, Integer> {
    Optional<RentData> findByBookIdAndAccountId(Integer bookId, Integer accountId);
    List<RentData> findAllByBookIdAndAccountId(Integer bookId, Integer accountId);
    boolean existsByAccountIdAndBookId(Integer accountId, Integer bookId);
    List<RentData> findAllByAccountId(Integer accountId);
}
