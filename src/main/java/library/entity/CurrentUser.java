package library.entity;

import java.time.LocalDate;

public final class CurrentUser {
    private static Account account;

    static {
        account = new Account(0, "", "", false, "", "", LocalDate.MIN);
    }

    public static int getId() {
        return account.getId();
    }

    public static void setId(int newId) {
        account.setId(newId);
    }

    public static String getUsername() {
        return account.getUsername();
    }

    public static void setUsername(String newUsername) {
       account.setUsername(newUsername);
    }

    public static String getPassword() {
        return account.getPassword();
    }

    public static void setPassword(String newPassword) {
        account.setPassword(newPassword);
    }

    public static String getUserFullName() {
        return account.getUserFullName();
    }

    public static void setUserFullName(String newFullName) {
        account.setUserFullName(newFullName);
    }

    public static boolean isAdmin() {
        return account.getIsAdmin();
    }

    public static void setIsAdmin(boolean isAdmin) {
        account.setIsAdmin(isAdmin);
    }

    public static String getEmail() {
        return account.getEmail();
    }

    public static void setEmail(String newEmail) {
        account.setEmail(newEmail);
    }

    public static String getPhoneNumber() {
        return account.getPhoneNumber();
    }

    public static void setPhoneNumber(String newPhoneNumber) {
        account.setPhoneNumber(newPhoneNumber);
    }

    public static LocalDate getDateOfBirth() {
        return account.getDateOfBirth();
    }

    public static void setDateOfBirth(LocalDate newDateOfBirth) {
        account.setDateOfBirth(newDateOfBirth);
    }

    public static Account getAccount() {
        return account;
    }

    public static void setAccount(Account newAccount) {
        account = newAccount;
    }
}
