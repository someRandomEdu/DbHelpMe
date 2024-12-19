package library.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import jakarta.mail.MessagingException;
import library.SonTest.EmailService;
import library.helper.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Route("forgot-password")
public class ForgotPasswordView extends VerticalLayout {

    private Div error = new Div();
    private EmailField emailField = new EmailField();
    private String verificationCode;

    public ForgotPasswordView() {
        setSizeFull();
        emailField.setPlaceholder("Enter your email");
        Button confirmButton = new Button("Confirm", event -> handleForgotPassword(emailField.getValue()));
        Button cancelButton = new Button("Cancel", event -> UI.getCurrent().navigate(""));

        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonsLayout.setSpacing(true);

        VerticalLayout emailLayout = new VerticalLayout();
        emailLayout.add(emailField, buttonsLayout);
        emailLayout.setPadding(false);
        emailLayout.setSpacing(false);

        add(emailLayout, error);
    }

    private void handleForgotPassword(String email) {
        try {
            String query = "SELECT * FROM accounts WHERE email = ?";
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                verificationCode = sendVerificationCode(email);
                sendEmail(email, verificationCode);
                showVerificationCodeInput(email);
            } else {
                error.setText("Account does not exist");
                error.getElement().getStyle().set("color", "red");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendEmail(String email, String verificationCode) {
        EmailService emailService = new EmailService();
        try {
            emailService.sendEmail(
                    email,
                    "Verification code for SomeRandom Lib account",
                    "Your verification code is: " + verificationCode
            );
            Notification.show("Email send!");
        } catch (MessagingException e) {
            Notification.show("Send failed " + e.getMessage());
        }
    }


    private String sendVerificationCode(String email) {
        String code = String.valueOf((int) (Math.random() * 9000) + 1000);
        System.out.println("Verification code sent to " + email + ": " + code);
        return code;
    }

    private void showVerificationCodeInput(String email) {
        removeAll();
        error.setText("");

        Div verificationDiv = new Div();
        verificationDiv.add(new NativeLabel("Enter the verification code sent to your email:"));

        TextField codeField = new TextField("Verification Code");
        Button verifyButton = new Button("Verify", event -> {
            if (verificationCode.equals(codeField.getValue())) {
                showNewPasswordInput(email);
            } else {
                error.setText("Incorrect verification code");
                error.getElement().getStyle().set("color", "red");
                verificationDiv.add(error); // Thêm lỗi vào layout
            }
        });
        Button cancelButton = new Button("Cancel", event -> UI.getCurrent().navigate(""));

        verificationDiv.add(codeField, verifyButton, cancelButton, error);
        add(verificationDiv);
    }

    private void showNewPasswordInput(String email) {
        removeAll();
        error.setText("");

        Div newPasswordDiv = new Div();
        newPasswordDiv.add(new NativeLabel("Enter your new password:"));

        PasswordField newPasswordField = new PasswordField("New Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password");

        Button resetButton = new Button("Reset Password", event -> {
            String newPassword = newPasswordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();

            if (!newPassword.equals(confirmPassword)) {
                error.setText("Passwords do not match");
                error.getElement().getStyle().set("color", "red");
                newPasswordDiv.add(error); // Thêm lỗi vào layout
                return;
            }

            resetPassword(email, newPassword);
            UI.getCurrent().access(() -> {
                UI.getCurrent().getPage().executeJs("setTimeout(() => { window.location.href = ''; }, 1000);");
            });
        });
        Button cancelButton = new Button("Cancel", event -> UI.getCurrent().navigate(""));

        newPasswordDiv.add(newPasswordField, confirmPasswordField, resetButton, cancelButton);
        add(newPasswordDiv);
    }

    private void resetPassword(String email, String newPassword) {
        try {
            String query = "UPDATE accounts SET password = ? WHERE email = ?";
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newPassword);
            stmt.setString(2, email);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                error.setText("Password reset successfully");
                error.getElement().getStyle().set("color", "green");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
