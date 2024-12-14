package library;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnDataRepository extends JpaRepository<ReturnData, Integer> {
    Optional<ReturnData> findOneByAccountIdAndBookId(Integer accountId, Integer bookId);
    List<ReturnData> findAllReturnByAccountId(Integer accountId);
}
