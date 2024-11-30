package com.somerandomdev.dbhelpme;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Objects;

@Entity
@Table(name = "rent_operation")
public final class RentOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;
    private Timestamp rentTime;

    /** false means return, true means rent! */
    private Boolean isRent;

    public RentOperation() {}

    public RentOperation(Long id, Long accountId, Timestamp rentTime, Boolean isRent) {
        this.id = id;
        this.accountId = accountId;
        this.rentTime = rentTime;
        this.isRent = isRent;
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

    public Timestamp getRentTime() {
        return rentTime;
    }

    public void setRentTime(Timestamp rentTime) {
        this.rentTime = rentTime;
    }

    public Boolean isRent() {
        return isRent;
    }

    public void setRent(Boolean isRent) {
        this.isRent = isRent;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RentOperation other && Objects.equals(accountId, other.accountId) &&
            Objects.equals(rentTime, other.rentTime) && Objects.equals(isRent, other.isRent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, rentTime, isRent);
    }

    @Override
    public String toString() {
        return MessageFormat.format("RentHistory[accountId = {0}, rentTime = {1}, isRentOperation = {2}]",
            accountId, rentTime, isRent);
    }
}
