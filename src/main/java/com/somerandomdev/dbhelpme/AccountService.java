package com.somerandomdev.dbhelpme;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
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

    public Optional<Account> findById(long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public void delete(Account account) {
        accountRepository.delete(account);
    }

    public void deleteById(long id) {
        accountRepository.deleteById(id);
    }

    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    public boolean deleteByUsername(String username) {
        for (Account account : findAll()) {
            if (account.getUsername().equals(username)) {
                accountRepository.delete(account);
                return true;
            }
        }

        return false;
    }
}
