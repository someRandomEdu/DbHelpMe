package library;
import java.util.Objects;

public final class AccountCredential {
    private String username;
    private String password;

    public AccountCredential() {

    }

    public AccountCredential(String username, String password) {
        this.username = username;
        this.password = password;
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AccountCredential other && Objects.equals(getUsername(), other.getUsername()) &&
            Objects.equals(getPassword(), other.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword());
    }

    @Override
    public String toString() {
        return "AccountCredential[username = " + username + ", password = " + password + "]";
    }
}
