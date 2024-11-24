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

@Route("/app/login")
public class LoginView extends Composite<VerticalLayout> {
    public LoginView(AppController appController) {
        TextField textField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        Button logInButton = new Button("Login");
        NativeLabel errorLabel = new NativeLabel();
        VerticalLayout layout = new VerticalLayout(textField, passwordField, logInButton, errorLabel);
        layout.setSpacing(true);

        logInButton.addClickListener((event) -> {
            var username = textField.getValue();
            var password = passwordField.getValue();
            var loginResult = appController.login(new Account(null, username, password, null));

            if (loginResult.hasBody()) {
                UI.getCurrent().navigate("/app/account/" + username);
            } else {
                var code = loginResult.getStatusCode();

                if (Objects.equals(code, HttpStatus.UNAUTHORIZED)) {
                    errorLabel.setText("Invalid password!");
                } else if (Objects.equals(code, HttpStatus.NOT_FOUND)) {
                    errorLabel.setText("User not found!");
                } else {
                    errorLabel.setText("Unknown login error!");
                }
            }
        });

        getContent().add(layout);
    }
}
