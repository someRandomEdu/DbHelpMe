package com.somerandomdev.dbhelpme;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class AccountSignupCheck {
    @Autowired
    private AccountRepository accountRepository;

    public boolean accountCheck(String username) {
        return accountRepository.existsByUsername(username);
    }

    public Long updateId() {
        Long maxId = accountRepository.findMaxId();
        return maxId != null ? maxId + 1 : 1L;
    }

    public void createAccount(String username, String password, String email, String phoneNumber, String dob) {
        Account newAccount = new Account(updateId(), username, password, false, email, phoneNumber, dob);
        System.out.println("Creating new account for: " + username);
        accountRepository.save(newAccount);
        System.out.println("Account created successfully for: " + username);
    }
}
