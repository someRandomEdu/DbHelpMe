package com.somerandomdev.dbhelpme.views;

import com.somerandomdev.dbhelpme.Account;
import com.somerandomdev.dbhelpme.AccountController;
import com.somerandomdev.dbhelpme.AppController;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("/api/log-in")
public class LogInView extends Composite<VerticalLayout> {
    public LogInView(AppController appController) {
        TextField textField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        Button logInButton = new Button("Log in");
        NativeLabel errorLabel = new NativeLabel();
        VerticalLayout layout = new VerticalLayout(textField, passwordField, logInButton, errorLabel);
        layout.setSpacing(true);

        logInButton.addClickListener((event) -> {
            String username = textField.getValue();
            String password = passwordField.getValue();
        });

        getContent().add(layout);
    }
}
