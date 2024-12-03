package library;

import library.entity.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public final class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping("/find-all")
    public List<Book> findAll() {
        return service.findAll();
    }

    @GetMapping("/find-by-title")
    public List<Book> findByTitle(@RequestBody String title) {
        return service.findAllBy((book) -> book.getTitle().equals(title));
    }

    @GetMapping("/find-by-title-and-author")
    public Optional<Book> findByTitleAndAuthor(String title, String author) {
        for (Book book : service.findAll()) {
            if (book.getTitle().equals(title) && book.getAuthor().equals(author)) {
                return Optional.of(book);
            }
        }

        return Optional.empty();
    }
}
