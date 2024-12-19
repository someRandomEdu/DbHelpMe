package library.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;

import java.awt.*;
import java.sql.*;

import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.html.Div;
import library.entity.CurrentUser;
import library.helper.DatabaseHelper;


@Route("")
public class LoginView extends Div {
    public static String user_name;
    private NativeLabel error = new NativeLabel();
    private LoginOverlay loginOverlay;

    public LoginView() {
        loginOverlay = new LoginOverlay();
        loginOverlay.setTitle("SomeRandom Lib");
        loginOverlay.setDescription("Better than UET-leak");
        loginOverlay.addLoginListener(this::onLogin);
        loginOverlay.addForgotPasswordListener(this::onforgotPassword);

        Button signupButton = new Button("Sign up");
        signupButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        signupButton.addClickListener(event -> {
            UI.getCurrent().navigate("/app/signup");
        });

        loginOverlay.getFooter().add(signupButton);
        loginOverlay.getFooter().add(error);

        add(loginOverlay);
        loginOverlay.setOpened(true);
        loginOverlay.getElement().setAttribute("no-autofocus", "");
    }

    private void onLogin(AbstractLogin.LoginEvent event) {
        String username = event.getUsername();
        user_name = username;
        String password = event.getPassword();

        if (login(username, password)) {
            System.out.println("Đăng nhập thành công!");
            loadUserData(user_name);
            UI.getCurrent().navigate("/book-chart");
        } else {
            error.setText("Invalid user name or password");
            System.out.println("Thông tin đăng nhập không đúng!");
            error.getElement().getStyle().set("color", "red");
//            resetForm(event.getSource());
            resetForm(); // Reset form
        }
    }

    private void resetForm() {
        loginOverlay.setOpened(false);
        loginOverlay.setOpened(true);
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
            e.printStackTrace(System.err);
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

    private void onforgotPassword(AbstractLogin.ForgotPasswordEvent event) {
        UI.getCurrent().navigate("/forgot-password");
    }
}