package library;

import library.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public final class AccountService extends JpaService<Account, Integer> {
    public AccountService(JpaRepository<Account, Integer> repository) {
        super(repository);
    }

    public Optional<Account> findByUsername(String username) {
        for (var account : findAll()) {
            if (Objects.equals(account.getUsername(), username)) {
                return Optional.of(account);
            }
        }

        return Optional.empty();
    }

    public Result<Account, String> checkCredentials(String username, String password) {
        for (var account : findAll()) {
            if (Objects.equals(account.getUsername(), username)) {
                return Objects.equals(account.getPassword(), password) ? Result.successReified(account) :
                    Result.errorReified("Wrong password!");
            }
        }

        return Result.errorReified("Account not found!");
    }

    public Result<Void, String> deleteByUsernameAndPassword(String username, String password) {
        for (Account account : findAll()) {
            if (Objects.equals(account.getUsername(), username)) {
                if (Objects.equals(account.getPassword(), password)) {
                    repository.delete(account);
                    return Result.successReified(null);
                } else {
                    return Result.errorReified("Wrong password!");
                }
            }
        }

        return Result.errorReified("Account not found!");
    }


}
