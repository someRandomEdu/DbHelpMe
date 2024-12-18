package library.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.*;
import library.AccountSignupCheck;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.datepicker.DatePicker;
import library.entity.Account;
import library.entity.CurrentUser;
import library.helper.DatabaseHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;


@Route ("app/signup")
public class SignupView extends Div {
    @Autowired
    private AccountSignupCheck accountSignupCheck = new AccountSignupCheck();

    private TextField fullnameField = new TextField("Full name");
    private TextField usernameField = new TextField("Username");
    private PasswordField passwordField = new PasswordField("Password");
    private PasswordField confirmPasswordField = new PasswordField("Confirm password");
    private EmailField validEmailField = new EmailField();
    private TextField phone_number = new TextField("Phone number");
    private DatePicker dobField = new DatePicker("Date of birth");
    private Button signUp = new Button("Sign up");
    private Button signin = new Button("Sign in");
    private NativeLabel error = new NativeLabel();

    public SignupView() {
        validEmailField.setLabel("Email address");
        validEmailField.getElement().setAttribute("name", "email");
        validEmailField.setValue("julia.scheider@email.com");
        validEmailField.setErrorMessage("Enter a valid email address");
        validEmailField.setClearButtonVisible(true);

        FormLayout formLayout = new FormLayout();
        formLayout.add(fullnameField, usernameField, passwordField,
                confirmPasswordField, dobField, phone_number,
                validEmailField, signUp, error, signin);
        formLayout.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("500px", 2));
        formLayout.setColspan(fullnameField, 2);
        formLayout.setColspan(usernameField, 2);
        formLayout.setColspan(passwordField, 1);
        formLayout.setColspan(confirmPasswordField, 1);
        formLayout.setColspan(dobField, 1);
        formLayout.setColspan(phone_number, 1);
        formLayout.setColspan(validEmailField, 2);

        formLayout.setColspan(signUp, 2);
        formLayout.setColspan(error, 2);
        formLayout.setColspan(signin, 2);

        formLayout.setWidth("35%");
        formLayout.getStyle().set("margin", "0 auto");
        add(formLayout);

        signUp.addClickListener(event -> {
            String fullname = fullnameField.getValue();
            String username = usernameField.getValue();
            String password = passwordField.getValue();
            String cfpassword = confirmPasswordField.getValue();
            String email = validEmailField.getValue();
            String phoneNumber = phone_number.getValue();
            LocalDate localDob = dobField.getValue();

            if (fullname.isEmpty()) {
                error.setText("Please fill full name");
                return;
            }

            if (username.isEmpty()) {
                error.setText("Please fill user name");
                return;
            }

            if (password.isEmpty()) {
                error.setText("Please fill password");
                return;
            }

            if (cfpassword.isEmpty()) {
                error.setText("Please fill confirm password");
                return;
            }

            if (email.isEmpty()) {
                error.setText("Please fill email");
                return;
            }

            if (phoneNumber.isEmpty()) {
                error.setText("Please fill phone number");
                return;
            }

            if(username.length() < 8 || username.length() > 32) {
                error.setText("User name must between 8 and 32 characters!");
                return;
            }

            if(!(password.equals(cfpassword))) {
                error.setText("Password do not match!");
                return;
            }

            if (accountSignupCheck.accountCheck(username)) {
                error.setText("Username already exists!");
                return;
            }
            String query = "INSERT INTO accounts (userFullName, username, password, is_admin, email, phone_number, date_of_birth) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            DatabaseHelper.connectToDatabase();
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, fullname);
                stmt.setString(2, username);
                stmt.setString(3, password);
                stmt.setBoolean(4, false);
                stmt.setString(5, email);
                stmt.setString(6, phoneNumber);

                if (localDob != null) {
                    stmt.setDate(7, Date.valueOf(localDob));
                } else {
                    stmt.setNull(7, java.sql.Types.DATE);
                }

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    error.setText("Sign up successfully, connecting to your account...");

                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            CurrentUser.setId(generatedId);
                        } else {
                            throw new SQLException("Failed to retrieve the generated ID.");
                        }
                    }

                    CurrentUser.setUserFullName(fullname);
                    CurrentUser.setUsername(username);
                    CurrentUser.setPassword(password);
                    CurrentUser.setIsAdmin(false);
                    CurrentUser.setEmail(email);
                    CurrentUser.setPhoneNumber(phoneNumber);
                    CurrentUser.setDateOfBirth(localDob);


                    UI.getCurrent().access(() -> {
                        UI.getCurrent().getElement().executeJs("setTimeout(function() {" +
                                "window.location = '/main';" +
                                "}, 1000);"
                        );
                    });
                } else {
                    error.setText("Sign up failed. Please try again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        signin.addClickListener(event -> {
            UI.getCurrent().navigate("/app"); // Login screen was moved to /app smh
        });
    }
}


//    private TextField fullNameField = new TextField("Full Name");
//    private TextField newTextField = new TextField("User name");
//    private PasswordField newPasswordField  = new PasswordField("Password");
//    private PasswordField newConfirmPW = new PasswordField("Confirm Password");
//    private TextField emailField = new TextField("Email");
//    private TextField phone_number = new TextField("Phone number");
//    private DatePicker dobField = new DatePicker("Date of birth");
//    private Button signUp = new Button("Sign up");
//    private Button signin = new Button("Sign in");
//    private NativeLabel error = new NativeLabel();
//
//    @Autowired
//    private AccountSignupCheck accountSignupCheck = new AccountSignupCheck();
//
//    public SignupView() {
//        signUp.addClickListener(event -> {
//        String username = newTextField.getValue();
//        String password = newPasswordField.getValue();
//        String cfpassword = newConfirmPW.getValue();
//        String email = emailField.getValue();
//        String phoneNumber = phone_number.getValue();
//        String userFullName = fullNameField.getValue();
//        LocalDate localDob = dobField.getValue();
//        DateTimeFormatter formatDob = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        if(!(password.equals(cfpassword))) {
//           error.setText("Password do not match!");
//           return;
//        }
//
//        if (accountSignupCheck.accountCheck(username)) {
//            error.setText("Username already exists!");
//            return;
//        }
//        String query = "Insert into accounts ( userFullName, username, password, " +
//                "is_admin, email, phone_number, date_of_birth)\n" +
//                "Values (?,?,?,?,?,?,?)";
//            DatabaseHelper.connectToDatabase();
//            try (Connection conn = DatabaseHelper.getConnection();
//                 PreparedStatement stmt = conn.prepareStatement(query)) {
//                stmt.setString(1, userFullName);
//                stmt.setString(2, username);
//                stmt.setString(3, password);
//                stmt.setBoolean(4,false);
//                stmt.setString(5, email);
//                stmt.setString(6, phoneNumber);
//                if (localDob != null) {
//                    stmt.setDate(7, Date.valueOf(localDob));
//                } else {
//                    stmt.setNull(7, java.sql.Types.DATE);
//                }
//                int rowsInserted = stmt.executeUpdate();
//                if (rowsInserted > 0) {
//                    System.out.println("Sign up succesfully");
//                    error.setText("Sign up succesfully, connecting to your account...");
//                    UI.getCurrent().access(() -> {
//                        UI.getCurrent().getElement().executeJs("setTimeout(function() {" +
//                                "window.location = '/main';" +
//                                "}, 1000);"
//                        );
//                    });
//                } else {
//                    System.out.println("Sign up unsuccesfully");
//                }
//
//
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//
//        signin.addClickListener(event -> {
//            UI.getCurrent().navigate("/app/login");
//        });
//
//        getContent().add(fullNameField, newTextField, newPasswordField, newConfirmPW, dobField, phone_number, emailField, signUp, error, signin);
//        getStyle().set("gap", "var(--lumo-space-m)");
//    }
//

