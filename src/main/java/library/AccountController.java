package library;

import library.entity.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public final class AccountController {
    AccountService accountService;
    RentDataService rentDataService;
    BookService bookService;

    public AccountController(AccountService accountService, RentDataService rentDataService, BookService bookService) {
        this.accountService = accountService;
        this.rentDataService = rentDataService;
        this.bookService = bookService;
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

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody Account account) {
        accountService.delete(account);
        return ResponseEntity.ok("Account deleted!");
    }

    @PostMapping("/log-in")
    public Optional<Account> tryLoggingIn(String username, String password) {
        for (Account account : accountService.findAll()) {
            if (account.getUsername().equals(username)) {
                return account.getPassword().equals(password) ? Optional.of(account) : Optional.empty();
            }
        }

        return Optional.empty();
    }

    @PostMapping("/rent")
    public void rentBook(Account account, Book book) {
        var rentData = rentDataService.findAllBy((value) ->
            value.getAccountId().equals(account.getId()) && value.getBookId().equals(book.getId()));

        var books = bookService.findAllBy(value -> value.equals(book));
    }

    @PostMapping
    public void rentBook(Account account, String bookName, String bookAuthor) {

    }
}
