package library.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;

import java.sql.*;

import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.html.Div;

@Route("")
public class LoginView extends Div {
    public static String user_name;
    private NativeLabel error = new NativeLabel();
    private LoginOverlay loginOverlay;

    public LoginView() {
        loginOverlay = new LoginOverlay();
        loginOverlay.setTitle("Chua nghi ra");
        loginOverlay.setDescription("Better than UET-leak");
        loginOverlay.addLoginListener(this::onLogin);

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

        if (checkCredentials(username, password)) {
            System.out.println("Đăng nhập thành công!");
            UI.getCurrent().navigate("main");
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

    private boolean checkCredentials(String username, String password) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mydatabase";
        String dbUsername = "root";
        String dbPassword = "130405";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT * FROM accounts WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}