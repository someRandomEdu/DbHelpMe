package library;

import library.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    boolean existsByUsername(String username);

    Account findAccountByUsername(String username);

    @Query("SELECT MAX(a.id) FROM Account a")
    Integer findMaxId();
}
