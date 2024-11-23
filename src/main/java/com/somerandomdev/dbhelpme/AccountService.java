package com.somerandomdev.dbhelpme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public final class AccountService extends JpaService<Account, Long> {
    public AccountService(JpaRepository<Account, Long> repository) {
        super(repository);
    }

    public boolean deleteByUsername(String username) {
        for (Account account : findAll()) {
            if (account.getUsername().equals(username)) {
                repository.delete(account);
                return true;
            }
        }

        return false;
    }
}
