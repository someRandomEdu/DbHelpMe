package library.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import library.AccountService;
import library.Helpers;
import library.entity.CurrentUser;

@Route(value = "app/profile", layout = MainView.class)
public final class ProfileView extends VerticalLayout {
    public ProfileView(AccountService accountService) {
        var usernameTextField = new TextField("Username");
        usernameTextField.setValue(CurrentUser.getUsername());
        var fullNameTextField = new TextField("Full Name");
        fullNameTextField.setValue(CurrentUser.getUserFullName());
        var passwordTextField = new PasswordField("Password");
        passwordTextField.setValue(CurrentUser.getPassword());
        var emailTextField = new TextField("Email");
        emailTextField.setValue(CurrentUser.getEmail());
        var roleLabel = new NativeLabel("Role");
        var roleValueLabel = new NativeLabel(CurrentUser.isAdmin() ? "Admin" : "User");
        var phoneNumberTextField = new TextField("Phone Number");
        phoneNumberTextField.setValue(CurrentUser.getPhoneNumber());
        var dateOfBirthPicker = new DatePicker("Date of Birth");
        dateOfBirthPicker.setValue(CurrentUser.getDateOfBirth());
        var saveChangesButton = new Button("Save Changes");

        saveChangesButton.addClickListener(e -> {
            var acc = accountService.findById(CurrentUser.getId());

            if (acc.isPresent()) {
                var account = acc.get();

                if (!isValidPhoneNumber(phoneNumberTextField.getValue())) {
                    Helpers.showNotification("Invalid phone number!", NotificationVariant.LUMO_ERROR);
                } else {
                    account.setUsername(usernameTextField.getValue());
                    account.setPassword(passwordTextField.getValue());
                    account.setUserFullName(fullNameTextField.getValue());
                    account.setEmail(emailTextField.getValue());
                    account.setPhoneNumber(phoneNumberTextField.getValue());
                    account.setDateOfBirth(dateOfBirthPicker.getValue());
                    accountService.save(account);
                    Helpers.showNotification("Successfully saved changes!", NotificationVariant.LUMO_SUCCESS);
                }
            }
        });

        var leftSideLayout = new VerticalLayout(usernameTextField, fullNameTextField, passwordTextField, emailTextField);
        var rightSideLayout = new VerticalLayout(roleLabel, roleValueLabel, phoneNumberTextField, dateOfBirthPicker);
        add(new HorizontalLayout(leftSideLayout, rightSideLayout), new HorizontalLayout(new VerticalLayout(saveChangesButton)));
    }

    public static String getRoute() {
        return "/app/profile";
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}
