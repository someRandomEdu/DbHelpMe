package com.somerandomdev.dbhelpme.views;

import com.somerandomdev.dbhelpme.Account;
import com.somerandomdev.dbhelpme.AppController;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Route("app/login")
public class LoginView extends Composite<VerticalLayout> {
    public static String user_name;
    public LoginView(AppController appController) {
        TextField textField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        Button logInButton = new Button("Login");
        NativeLabel errorLabel = new NativeLabel();
        Button signUpButton = new Button("Sign up");
        VerticalLayout layout = new VerticalLayout(textField, passwordField, logInButton, errorLabel, signUpButton);
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

            if (Objects.equals(code, HttpStatus.OK)) {
//                UI.getCurrent().navigate("/app/account/" + username);
                UI.getCurrent().navigate("main");
            } if (Objects.equals(code, HttpStatus.UNAUTHORIZED)) {
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
}
