package library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByUsername(String username);

    Account findAccountByUsername(String username);

    @Query("SELECT MAX(a.id) FROM Account a")
    Long findMaxId();
}