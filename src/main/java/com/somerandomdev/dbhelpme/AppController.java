package com.somerandomdev.dbhelpme;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@RestController
@RequestMapping("/app")
public final class AppController {
    private final AccountService accountService;
    private final BookService bookService;
    private final RentDataService rentDataService;

    public AppController(AccountService accountService, BookService bookService,
                         RentDataService rentDataService) {
        this.accountService = accountService;
        this.bookService = bookService;
        this.rentDataService = rentDataService;
    }

    @GetMapping("/find-all-books")
    public List<Book> findAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/find-all-rented-books-from-account-id")
    public List<Book> findAllRentedBooksFromAccountId(Long accountId) {
        var result = new ArrayList<Book>();

        var rentDataList = rentDataService.findAllBy(rentData ->
            Objects.equals(accountId, rentData.getAccountId()));

        Predicate<Book> predicate = book -> {
            for (var rentData : rentDataList) {
                if (rentData.getBookId().equals(book.getId())) {
                    return rentData.getRented();
                }
            }

            return false;
        };

        for (Book book : findAllBooks()) {
            if (predicate.test(book)) {
                result.add(book);
            }
        }

        return result;
    }

    @GetMapping("find-account-by-username")
    public Optional<Account> findAccountByUsername(String username) {
        for (var account : accountService.findAll()) {
            if (account.getUsername().equals(username)) {
                return Optional.of(account);
            }
        }

        return Optional.empty();
    }

    @GetMapping("find-book-by-title-and-author")
    public Optional<Book> findBookByTitleAndAuthor(String title, String author) {
        for (var book : findAllBooks()) {
            if (book.getTitle().equals(title) && book.getAuthor().equals(author)) {
                return Optional.of(book);
            }
        }

        return Optional.empty();
    }

    @GetMapping("/find-all-accounts")
    public List<Account> findAllAccounts() {
        return accountService.findAll();
    }

    @GetMapping("/login")
    public ResponseEntity<Account> login(@RequestBody AccountCredential credential) {
        Optional<Account> tmp = accountService.findOneBy(
            account -> account != null && Objects.equals(account.getUsername(), credential.getUsername()));

        if (tmp.isPresent()) {
            return Objects.equals(tmp.get().getPassword(), credential.getPassword()) ?
                ResponseEntity.ok(tmp.get()) : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register-user-account")
    public ResponseEntity<Account> registerUserAccount(@RequestBody Account account) {
        account.setIsAdmin(false);
        Account saved = accountService.save(account);
        return saved.equals(account) ? new ResponseEntity<>(saved, HttpStatus.ALREADY_REPORTED) : new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/add-book")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Optional<Book> tmp = bookService.findOneBy(value -> value.getTitle().equals(book.getTitle()) &&
            value.getAuthor().equals(book.getAuthor()));

        return tmp.isPresent() ? new ResponseEntity<>(tmp.get(), HttpStatus.ALREADY_REPORTED) :
            new ResponseEntity<>(bookService.save(book), HttpStatus.CREATED);
    }

    @PutMapping("/return-rented-book")
    public ResponseEntity<String> returnRentedBook(@RequestBody Account account, @RequestBody Book book) {
        Optional<RentData> rentData = rentDataService.findOneBy(
            value -> value.getAccountId().equals(account.getId()) &&
                value.getBookId().equals(book.getId()));

        if (rentData.isPresent()) {
            RentData value = rentData.get();

            if (value.getRented()) {
                value.setRented(false);
                rentDataService.save(value);
                return new ResponseEntity<>("Book returned!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Book is not rented!", HttpStatus.ALREADY_REPORTED);
            }
        } else {
            return new ResponseEntity<>("Book is not rented!", HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("/delete-account-by-username")
    public ResponseEntity<String> deleteAccountByUsername(String username) {
        Optional<Account> account = findAccountByUsername(username);

        if (account.isPresent()) {
            accountService.delete(account.get());
            return new ResponseEntity<>("Account deleted!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Account not found!", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-book-by-title-and-author")
    public ResponseEntity<String> deleteBookByTitleAndAuthor(String title, String author) {
        Optional<Book> book = findBookByTitleAndAuthor(title, author);

        if (book.isPresent()) {
            bookService.delete(book.get());
            return new ResponseEntity<>("Book deleted!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Book not found!", HttpStatus.NOT_FOUND);
        }
    }
}
