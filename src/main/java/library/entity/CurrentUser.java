package library.entity;

import java.time.LocalDate;

public class CurrentUser {
    private static Account account;
    private static int id;
    private static String username;
    private static String password;
    private static String userFullName;
    private static boolean isAdmin;
    private static String email;
    private static String phoneNumber;
    private static LocalDate dateOfBirth;

    public static int getId() {
        return id;
    }

    public static void setId(int newId) {
        id = newId;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String newUsername) {
        username = newUsername;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String newPassword) {
        password = newPassword;
    }

    public static String getUserFullName() {
        return userFullName;
    }

    public static void setUserFullName(String newFullName) {
        userFullName = newFullName;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean isAdmin) {
        CurrentUser.isAdmin = isAdmin;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String newEmail) {
        email = newEmail;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public static void setPhoneNumber(String newPhoneNumber) {
        phoneNumber = newPhoneNumber;
    }

    public static LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public static void setDateOfBirth(LocalDate newDateOfBirth) {
        dateOfBirth = newDateOfBirth;
    }
}
