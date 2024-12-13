package library;

import library.entity.Account;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Component
public class AccountSignupCheck {
    @Autowired
    private AccountRepository accountRepository;

    public boolean accountCheck(String username) {
        return accountRepository.existsByUsername(username);
    }

    public Integer updateId() {
        Integer maxId = accountRepository.findMaxId();
        return maxId != null ? maxId + 1 : 1;
    }

    public void createAccount(String username, String userFullName, String password, String email, String phoneNumber, LocalDate dob) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        if (userFullName == null || userFullName.isEmpty()) {
            throw new IllegalArgumentException("Full Name cannot be null or empty");
        }

        Account newAccount = new Account(updateId(), userFullName, username, password, false, email, phoneNumber, dob);
        System.out.println("Creating new account for: " + username);
        accountRepository.save(newAccount);
        System.out.println("Account created successfully for: " + username);
    }
}
