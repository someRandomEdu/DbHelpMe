package com.somerandomdev.dbhelpme;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public final class AccountController {
    AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello world!";
    }

    @GetMapping("find-all")
    public List<Account> findAll() {
        return accountService.findAll();
    }

    @PostMapping("/save")
    public Account save(@RequestBody Account account) {
        return accountService.save(account);
    }
}
