package com.somerandomdev.dbhelpme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public final class BookService extends JpaService<Book, Long> {


    public BookService(JpaRepository<Book, Long> repository) {
        super(repository);
    }

    /**
     * NOTE: {@link Book#getId()} is ignored!
     * */
    public boolean updateBook(String originalTitle, String originalAuthor, Book newInfo) {
        var bk = findOneBy(value -> Objects.equals(value.getTitle(), originalTitle) &&
            Objects.equals(value.getAuthor(), originalAuthor));

        if (bk.isPresent()) {
            var book = bk.get();
            book.setId(null);
            book.setTitle(newInfo.getTitle());
            book.setAuthor(newInfo.getAuthor());
            book.setPublisher(newInfo.getPublisher());
            book.setDescription(newInfo.getDescription());
            save(book);
            return true;
        } else {
            return false;
        }
    }
}
