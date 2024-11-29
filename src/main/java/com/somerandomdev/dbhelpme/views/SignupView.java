package com.somerandomdev.dbhelpme.views;

import com.somerandomdev.dbhelpme.Account;
import com.somerandomdev.dbhelpme.AccountSignupCheck;
import com.somerandomdev.dbhelpme.AppController;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Route ("app/signup")
public class SignupView extends Composite<VerticalLayout> {
    private TextField newTextField = new TextField("User name");
    private PasswordField newPasswordField  = new PasswordField("Password");
    private PasswordField newConfirmPW = new PasswordField("Confirm Password");
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

           if(!(password.equals(cfpassword))) {
               error.setText("Password do not match!");
               return;
           }

            if (accountSignupCheck.accountCheck(username)) {
                error.setText("Username already exists!");
                return;
            }

           accountSignupCheck.createAccount(username, password);
            error.setText("Account created successfully!");
        });

        signin.addClickListener(event -> {
            UI.getCurrent().navigate("/app/login");
        });

        getContent().add(newTextField, newPasswordField, newConfirmPW, signUp, error, signin);
        getStyle().set("gap", "var(--lumo-space-m)");
    }
}
