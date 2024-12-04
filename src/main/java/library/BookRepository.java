package library;

import library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAll();
    Book findByTitleAndAuthor(String title, String author);

    Optional<Book> findById(Long bookId);
}
