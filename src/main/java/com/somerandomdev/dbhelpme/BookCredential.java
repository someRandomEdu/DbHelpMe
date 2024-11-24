package com.somerandomdev.dbhelpme;

import java.util.Objects;

public class BookCredential {
    private String title;
    private String author;

    public BookCredential() {

    }

    public BookCredential(String title, String author) {
        this.title = title;
        this.author = author;
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BookCredential other &&
            Objects.equals(title, other.title) && Objects.equals(author, other.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }

    @Override
    public String toString() {
        return "BookCredential[" + "title = " + title + ", author = " + author + ']';
    }
}
