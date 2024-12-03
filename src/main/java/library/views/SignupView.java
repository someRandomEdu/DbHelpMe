package library.views;

import library.AccountSignupCheck;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.datepicker.DatePicker;
import library.helper.DatabaseHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Route ("app/signup")
public class SignupView extends Composite<VerticalLayout> {
    private TextField fullNameField = new TextField("Full Name");
    private TextField newTextField = new TextField("User name");
    private PasswordField newPasswordField  = new PasswordField("Password");
    private PasswordField newConfirmPW = new PasswordField("Confirm Password");
    private TextField emailField = new TextField("Email");
    private TextField phone_number = new TextField("Phone number");
    private DatePicker dobField = new DatePicker("Date of birth");
    private Button signUp = new Button("Sign up");
    private Button signin = new Button("Sign in");
    private NativeLabel error = new NativeLabel();

    @Autowired
    private AccountSignupCheck accountSignupCheck = new AccountSignupCheck();

    public SignupView() {
        signUp.addClickListener(event -> {
        String username = newTextField.getValue();
        String password = newPasswordField.getValue();
        String cfpassword = newConfirmPW.getValue();
        String email = emailField.getValue();
        String phoneNumber = phone_number.getValue();
        String userFullName = fullNameField.getValue();
        LocalDate localDob = dobField.getValue();
        DateTimeFormatter formatDob = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(!(password.equals(cfpassword))) {
           error.setText("Password do not match!");
           return;
        }

        if (accountSignupCheck.accountCheck(username)) {
            error.setText("Username already exists!");
            return;
        }
        String query = "Insert into accounts ( userFullName, username, password, " +
                "is_admin, email, phone_number, date_of_birth)\n" +
                "Values (?,?,?,?,?,?,?)";
            DatabaseHelper.connectToDatabase();
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userFullName);
                stmt.setString(2, username);
                stmt.setString(3, password);
                stmt.setBoolean(4,false);
                stmt.setString(5, email);
                stmt.setString(6, phoneNumber);
                if (localDob != null) {
                    stmt.setDate(7, Date.valueOf(localDob));
                } else {
                    stmt.setNull(7, java.sql.Types.DATE);
                }
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Sign up succesfully");
                    error.setText("Sign up succesfully, connecting to your account...");
                    UI.getCurrent().access(() -> {
                        UI.getCurrent().getElement().executeJs("setTimeout(function() {" +
                                "window.location = '/main';" +
                                "}, 1000);"
                        );
                    });
                } else {
                    System.out.println("Sign up unsuccesfully");
                }



            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        signin.addClickListener(event -> {
            UI.getCurrent().navigate("/app/login");
        });

        getContent().add(fullNameField, newTextField, newPasswordField, newConfirmPW, dobField, phone_number, emailField, signUp, error, signin);
        getStyle().set("gap", "var(--lumo-space-m)");
    }


}
