package library;

import java.time.LocalDate;

public final class CurrentUser {
    private static Account loggedInAccount = null;

    private CurrentUser() {

    }

    public static Long getId() {
        return loggedInAccount.getId();
    }

    public static void setId(Long id) {
        loggedInAccount.setId(id);
    }

    public static String getUsername() {
        return loggedInAccount.getUsername();
    }

    public static void setUsername(String username) {
        loggedInAccount.setUsername(username);
    }

    public static String getPassword() {
        return loggedInAccount.getPassword();
    }

    public static void setPassword(String password) {
        loggedInAccount.setPassword(password);
    }

    public static String getUserFullName() {
        return loggedInAccount.getUserFullName();
    }

    public static void setUserFullName(String userFullName) {
        loggedInAccount.setUserFullName(userFullName);
    }

    public static String getEmail() {
        return loggedInAccount.getEmail();
    }

    public static void setEmail(String email) {
        loggedInAccount.setEmail(email);
    }

    public static String getPhoneNumber() {
        return loggedInAccount.getPhoneNumber();
    }

    public static void setPhoneNumber(String phoneNumber) {
        loggedInAccount.setPhoneNumber(phoneNumber);
    }

    public static LocalDate getDateOfBirth() {
        return loggedInAccount.getDateOfBirth();
    }

    public static void setDateOfBirth(LocalDate dateOfBirth) {
        loggedInAccount.setDateOfBirth(dateOfBirth);
    }

    public static Account getLoggedInAccount() {
        return loggedInAccount;
    }

    public static boolean setLoggedInAccount(Account account) {
        if (loggedInAccount == null) {
            loggedInAccount = account;
            return true;
        } else {
            return false;
        }
    }
}
