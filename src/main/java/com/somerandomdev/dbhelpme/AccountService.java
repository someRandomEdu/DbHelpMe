package com.somerandomdev.dbhelpme;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public final class AccountService {
    AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public <S extends Account> S save(S account) {
        return accountRepository.save(account);
    }


    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public void delete(Account account) {
        accountRepository.delete(account);
    }
}
