package library.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Route;
import library.Account;
import library.AppController;
import library.entity.CurrentUser;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Route(value = "returnbook", layout = MainView.class)
public class ReturnBookView extends VerticalLayout {
    private final AppController appController;

    private TextField titleField = new TextField("Title");
    private TextField authorField = new TextField("Author");
    private Button submitButton = new Button("Submit");

    public void setParameter(BeforeEvent beforeEvent, String accountName) {
        Account account = appController.findAccountByUsername(accountName).orElseThrow(() ->
                new IllegalArgumentException("Account not found!"));

        submitButton.addClickListener(e -> {
            var operationResult = appController.returnBook(Map.ofEntries(
                    Map.entry("username", account.getUsername()),
                    Map.entry("password", account.getPassword()),
                    Map.entry("title", titleField.getValue()),
                    Map.entry("author", authorField.getValue())
            ));

            var notification = new Notification();

            var notificationLayout = new HorizontalLayout(new NativeLabel(operationResult.getBody()),
                    new Button(new Icon("lumo", "cross"),
                            evt -> notification.close()));

            notificationLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            notification.add(notificationLayout);

            switch (operationResult.getStatusCode()) {
                case HttpStatus.OK -> notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                case HttpStatus.ALREADY_REPORTED -> notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                default -> notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

            notification.open();
        });
    }

    public ReturnBookView(AppController appController) {
        Div titleLabel = new Div(new Text("Rent book"));
        titleLabel.addClassName("title-container");
        titleLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "blue")
                .set("text-align", "center")
                .set("margin-bottom", "20px");

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("100%");
        formLayout.setMaxWidth("600px");
        formLayout.setSpacing(true); // Thêm khoảng cách giữa các thành phần
        formLayout.setPadding(false);

        titleField.setWidth("100%");
        titleField.setPlaceholder("Enter the title...");
        titleField.addClassName("title-field");

        authorField.setWidth("100%");
        authorField.setPlaceholder("Enter the author name...");
        authorField.addClassName("author-field");

        formLayout.add(titleField, authorField);

        this.appController = appController;
        String accountName = CurrentUser.getUsername();
        setParameter(null, accountName);
        add(this.titleField, formLayout, submitButton);
        setSizeFull();
    }
}
