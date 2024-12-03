package library.views;

import library.Account;
import library.AppController;
import library.entity.CurrentUser;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import library.helper.DatabaseHelper;
import org.springframework.http.HttpStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Route("app/login")
public class LoginView extends Composite<VerticalLayout> {
    public static String user_name;
    TextField textField = new TextField("Username");
    PasswordField passwordField = new PasswordField("Password");
    Button logInButton = new Button("Login");
    NativeLabel errorLabel = new NativeLabel();
    Button signUpButton = new Button("Sign up");
    VerticalLayout layout = new VerticalLayout(textField, passwordField, logInButton, errorLabel, signUpButton);
    public LoginView(AppController appController) {
        layout.setSpacing(true);

        signUpButton.addClickListener(event -> {
            UI.getCurrent().navigate("/app/signup");
        });

        logInButton.addClickListener((event) -> {
            var username = textField.getValue();
            this.user_name = username;
            var password = passwordField.getValue();
            var loginResult = appController.login(new Account(null, username, password, null));
            var code = loginResult.getStatusCode();

            if (login(username, password)) {
//                UI.getCurrent().navigate("/app/account/" + username);
                UI.getCurrent().navigate("main");
                loadUserData(user_name);
            }  if (Objects.equals(code, HttpStatus.UNAUTHORIZED)) {
                errorLabel.setText("Invalid password!");
            } else if (Objects.equals(code, HttpStatus.NOT_FOUND)) {
                errorLabel.setText("User not found!");
            } else {
                errorLabel.setText("Unknown login error!");
            }
        });

        getContent().add(layout);
    }

    public static String getUserName() {
        return user_name;
    }

    private static void loadUserData(String user_name) {
        try {
            String query = "SELECT * FROM accounts WHERE username = ?";
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user_name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CurrentUser.setId(rs.getInt("id"));
                CurrentUser.setUsername(rs.getString("username"));
                CurrentUser.setPassword(rs.getString("password"));
                CurrentUser.setUserFullName(rs.getString("userFullName"));
                CurrentUser.setIsAdmin(rs.getBoolean("is_admin"));
                CurrentUser.setEmail(rs.getString("email"));
                CurrentUser.setPhoneNumber(rs.getString("phone_number"));
                CurrentUser.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean login(String user_name, String password) {
        DatabaseHelper.connectToDatabase();
        String query = "SELECT * FROM accounts WHERE username = ?";
        try (Connection conn = DatabaseHelper.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user_name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String acc_password = rs.getString("password");
                if (acc_password.equals(password)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}
