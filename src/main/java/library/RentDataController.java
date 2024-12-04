package library;

import library.entity.Book;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping
public final class RentDataController {
    private final RentDataService service;
    private final BookService bookService;
    private final AccountService accountService;

    public RentDataController(RentDataService service, BookService bookService, AccountService accountService) {
        this.service = service;
        this.bookService = bookService;
        this.accountService = accountService;
    }



    @GetMapping("/find-all-rented-books")
    public List<Book> findAllRentedBooks(@RequestBody Account account) {
        return findAllRentedBooksByAccountId(account.getId());
    }

    @GetMapping("/find-all-rented-books-by-account-id")
    public List<Book> findAllRentedBooksByAccountId(Long accountId) {
        List<Book> books = bookService.findAll();
        var result = new ArrayList<Book>();

        List<RentData> rented = service.findAllBy((book) ->
            book.getAccountId().equals(accountId));

        for (RentData r : rented) {
            for (Book b : books) {
                if (Objects.equals(b.getId(), r.getBookId())) {
                    if (r.getRented()) {
                        result.add(b);
                    }

                    break;
                }
            }
        }

        return result;
    }

    @GetMapping("/is-rented")
    public boolean isRented(Long accountId, Long bookId) {
        List<RentData> list = service.findAllBy((rentData) ->
            rentData.getAccountId().equals(accountId) && rentData.getBookId().equals(bookId));

        return (!list.isEmpty()) && list.getFirst().getRented();
    }

    @GetMapping("/is-rented-by-names")
    public boolean isRentedByNames(String accountUsername, String bookTitle, String bookAuthor) {
        var accounts = accountService.findAllBy((account) -> account.getUsername().equals(accountUsername));
        var books = bookService.findAllBy((book) -> book.getTitle().equals(bookTitle) && book.getAuthor().equals(bookAuthor));
        return !accounts.isEmpty() && !books.isEmpty() && isRented(accounts.getFirst().getId(), books.getFirst().getId());
    }

    @GetMapping("/borrow-to")
    public LocalDate getBorrowTo(@RequestParam Long bookId, @RequestParam Long accountId) {
        return service.getBorrowTo(bookId, accountId);
    }
}
