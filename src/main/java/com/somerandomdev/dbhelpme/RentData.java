package com.somerandomdev.dbhelpme;

import jakarta.persistence.*;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "rent_data")
public final class RentData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "status")
    private String status;

    @Column(name = "borrow_from")
    private LocalDate borrowFrom;

    @Column(name = "borrow_to")
    private LocalDate borrowTo;

    private Boolean rented;

    public RentData() {

    }

    public RentData(Long id, Long accountId, Long bookId, Boolean rented) {
        this.id = id;
        this.accountId = accountId;
        this.bookId = bookId;
        this.rented = rented;
    }

    public RentData(Long id, Long accountId, Long bookId, Boolean rented, String status, LocalDate borrowFrom, LocalDate borrowTo) {
        this.id = id;
        this.accountId = accountId;
        this.bookId = bookId;
        this.rented = rented;
        this.status = status;
        this.borrowFrom = borrowFrom;
        this.borrowTo = borrowTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Boolean getRented() {
        return rented;
    }

    public void setRented(Boolean rented) {
        this.rented = rented;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getBorrowFrom() {
        return borrowFrom;
    }

    public void setBorrowFrom(LocalDate borrowFrom) {
        this.borrowFrom = borrowFrom;
    }

    public LocalDate getBorrowTo() {
        return borrowTo;
    }

    public void setBorrowTo(LocalDate borrowTo) {
        this.borrowTo = borrowTo;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RentData && Objects.equals(id, ((RentData) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return MessageFormat.format("RentData[accountId = {0}, bookId = {1}, rented = {2}]",
            accountId, bookId, rented);
    }
}
