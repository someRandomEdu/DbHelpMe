package com.somerandomdev.dbhelpme;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.*;
import java.util.List;
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

    /**
     * Only {@link Account#getUsername()} and {@link Account#getPassword()} matters here!
     * */
    @GetMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Optional<Account> tmp = accountService.findOneBy(
                value -> Objects.equals(value.getUsername(), account.getUsername()));

        if (tmp.isPresent()) {
            return Objects.equals(tmp.get().getPassword(), account.getPassword()) ?
                    new ResponseEntity<>(tmp.get(), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("find-book")
    public ResponseEntity<Book> findBook(@RequestBody Map<String, String> map) {
        var bk = bookService.findOneBy(value -> Objects.equals(value.getTitle(), map.get("title")) &&
                Objects.equals(value.getAuthor(), map.get("author")));

        return bk.isPresent() ? ResponseEntity.ok(bk.get()) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("find-rented-books")
    public ResponseEntity<List<Book>> findRentedBooks(@RequestBody Account account) {
        Optional<Account> tmp = accountService.findOneBy(
                value -> Objects.equals(value.getUsername(), account.getUsername()));

        if (tmp.isPresent()) {
            if (Objects.equals(tmp.get().getPassword(), account.getPassword())) {
                var accId = tmp.get().getId();
                var rentedBooks = new ArrayList<Book>();
                var books = bookService.findAll();

                var rentDataList = rentDataService.findAllBy(
                        value -> Objects.equals(value.getAccountId(), accId));

                Predicate<Book> isRented = value -> {
                    for (var rentData : rentDataList) {
                        if (Objects.equals(rentData.getBookId(), value.getId())) {
                            return rentData.getRented();
                        }
                    }

                    return false;
                };

                for (var book : books) {
                    if (isRented.test(book)) {
                        rentedBooks.add(book);
                    }
                }

                return new ResponseEntity<>(rentedBooks, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("find-rented-books-of")
    public ResponseEntity<List<Book>> findRentedBooksOf(@RequestBody String accountName) {
        var acc = accountService.findOneBy(value -> Objects.equals(value.getUsername(), accountName));

        if (acc.isPresent()) {
            return findRentedBooks(acc.get());
        } else {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register-user-account")
    public ResponseEntity<Account> registerUserAccount(@RequestBody Account account) {
        account.setIsAdmin(false);

        Optional<Account> tmp = accountService.findOneBy(value -> Objects.equals(value.getUsername(),
                account.getUsername()));

        if (tmp.isPresent()) {
            return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
        } else {
            return new ResponseEntity<>(accountService.save(account), HttpStatus.CREATED);
        }
    }

    @PostMapping("/add-book")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Optional<Book> tmp = bookService.findOneBy(value -> value.getTitle().equals(book.getTitle()) &&
                value.getAuthor().equals(book.getAuthor()));

        return tmp.isPresent() ? new ResponseEntity<>(tmp.get(), HttpStatus.ALREADY_REPORTED) :
                new ResponseEntity<>(bookService.save(book), HttpStatus.CREATED);
    }

    @PutMapping("/return-book")
    public ResponseEntity<String> returnBook(@RequestBody Map<String, String> map) {
        var acc = accountService.findOneBy(value ->
                Objects.equals(value.getUsername(), map.get("username")));

        if (acc.isPresent()) {
            var bk = bookService.findOneBy(value -> Objects.equals(value.getTitle(), map.get("title")) &&
                    Objects.equals(value.getAuthor(), map.get("author")));

            if (bk.isPresent()) {
                var rd = rentDataService.findOneBy(value ->
                        Objects.equals(value.getAccountId(), acc.get().getId()) &&
                                Objects.equals(value.getBookId(), bk.get().getId()));

                if (rd.isPresent()) {
                    var rentData = rd.get();

                    if (rentData.getRented()) {
                        rentDataService.delete(rd.get());
                        return new ResponseEntity<>("Book successfully returned!", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Book is not rented!", HttpStatus.ALREADY_REPORTED);
                    }
                } else {
                    return new ResponseEntity<>("Book is not rented!", HttpStatus.ALREADY_REPORTED);
                }
            } else {
                return new ResponseEntity<>("Book not found!", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Account not found!", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/rent-book")
    public ResponseEntity<String> rentBook(@RequestBody Map<String, String> map) {
        Optional<Account> acc = accountService.findOneBy(
                value -> Objects.equals(value.getUsername(), map.get("username")));

        if (acc.isPresent()) {
            Account account = acc.get();

            if (Objects.equals(account.getPassword(), map.get("password"))) {
                Optional<Book> bk = bookService.findOneBy(value ->
                        Objects.equals(value.getTitle(), map.get("title")) &&
                                Objects.equals(value.getAuthor(), map.get("author")));

                if (bk.isPresent()) {
                    Book book = bk.get();

                    if (rentDataService.findOneBy(value ->
                            Objects.equals(account.getId(), value.getAccountId()) &&
                                    Objects.equals(book.getId(), value.getBookId())).isPresent()) {
                        return new ResponseEntity<>("Book already rented!", HttpStatus.ALREADY_REPORTED);
                    } else {
                        rentDataService.save(new RentData(null, account.getId(), book.getId(), true));
                        return new ResponseEntity<>("Rented book successfully!", HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>("Requested book not found!", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Wrong password!", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Account not found!", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/rent-book-by-username")
    public ResponseEntity<String> rentBookByUsername(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        System.out.println(username);

        Optional<Account> acc = accountService.findOneBy(
                value -> Objects.equals(value.getUsername(), username));

        if (acc.isPresent()) {
            Account account = acc.get();

            Optional<Book> bk = bookService.findOneBy(value ->
                    Objects.equals(value.getTitle(), map.get("title")) &&
                            Objects.equals(value.getAuthor(), map.get("author")));

            if (bk.isPresent()) {
                Book book = bk.get();

                if (rentDataService.findOneBy(value ->
                        Objects.equals(account.getId(), value.getAccountId()) &&
                                Objects.equals(book.getId(), value.getBookId())).isPresent()) {
                    return new ResponseEntity<>("Book already rented!", HttpStatus.ALREADY_REPORTED);
                } else {
                    rentDataService.save(new RentData(null, account.getId(), book.getId(), true));
                    return new ResponseEntity<>("Rented book successfully!", HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("Requested book not found!", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Account not found!", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-book")
    public ResponseEntity<String> updateBook(@RequestBody Map<String, String> map) {
        var operationResult = bookService.updateBook(map.get("originalTitle"), map.get("originalAuthor"),
                new Book(null, map.get("title"), map.get("author"), map.get("publisher"), map.get("description")));

        if (operationResult) {
            return new ResponseEntity<>("Book successfully updated!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Book not found!", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-account-by-username")
    public ResponseEntity<String> deleteAccountByUsername(@RequestBody String username) {
        Optional<Account> account = findAccountByUsername(username);

        if (account.isPresent()) {
            accountService.delete(account.get());
            return new ResponseEntity<>("Account deleted!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Account not found!", HttpStatus.NOT_FOUND);
        }
    }

    // TODO: Refactor this!
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