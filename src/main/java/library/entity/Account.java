package library.entity;

import jakarta.persistence.*;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "accounts")
public final class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // NOTE: Changing from Long to Integer is a breaking change!

    private String username;
    private String password;

    @Column(name = "userFullName")
    private String userFullName;

    @Column(name = "is_admin")
    private Boolean isAdmin;  // is_admin là Boolean

    @Column(name = "email")
    private String email;  // Thêm email

    @Column(name = "phone_number")
    private String phoneNumber;  // Thêm phone_number

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;  // Thêm date_of_birth, có thể thay bằng kiểu dữ liệu Date nếu cần

    public Account() {
        // Constructor không tham số
    }

    public Account(Integer id, String username, String password, Boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public Account(Integer id, String username, String password, Boolean isAdmin, String email, String phoneNumber, LocalDate dateOfBirth) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public Account(Integer id, String userFullName, String username, String password, Boolean isAdmin, String email, String phoneNumber, LocalDate dateOfBirth) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.userFullName = userFullName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof Account other && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MessageFormat.format("Account[id = {0}, username = {1}, isAdmin = {2}, email = {3}, phone_number = {4}, date_of_birth = {5}]",
                id, username, isAdmin, email, phoneNumber, dateOfBirth);
    }
}
