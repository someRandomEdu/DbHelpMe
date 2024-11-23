package com.somerandomdev.dbhelpme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public final class BookService extends JpaService<Book, Long> {
    public BookService(JpaRepository<Book, Long> repository) {
        super(repository);
    }
}
