package com.somerandomdev.dbhelpme;

import jakarta.persistence.*;

import java.text.MessageFormat;
import java.util.Objects;

// TODO: Add Long copyCount?
@Entity
@Table(name = "books")
public final class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String publisher;
    private String description;

    @Column(name = "copy_count")
    private Long copyCount;

    public Book() {

    }

    public Book(Long id, String title, String author, String publisher, String description, Long copyCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.copyCount = copyCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCopyCount() {
        return copyCount;
    }

    public void setCopyCount(Long copyCount) {
        this.copyCount = copyCount;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Book other && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MessageFormat.format("Book[id = {0}, title = {1}, author = {2}, publisher = {3}, description = {4}]",
            id, title, author, publisher, description);
    }
}
