package library.views;

import library.Account;
import library.AppController;
import library.entity.Book;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Route(value = "/app/account", layout = MainView.class)
public class AccountView extends VerticalLayout implements HasUrlParameter<String> {
    private final AppController appController;
    private final Grid<Book> rentedBookGrid;
    private final Grid<Book> availableBookGrid;

    public AccountView(AppController appController) {
        this.appController = appController;
        rentedBookGrid = new Grid<>(Book.class);
        availableBookGrid = new Grid<>(Book.class);
    }

    // TODO: Deduplicate code?
    @Override
    public void setParameter(BeforeEvent beforeEvent, String accountName) {
        Account account = appController.findAccountByUsername(accountName).orElseThrow(() ->
            new IllegalArgumentException("Account not found!"));

        var topLayout = new VerticalLayout(new NativeLabel("Hi, " + accountName + "!"));
        topLayout.setDefaultHorizontalComponentAlignment(Alignment.END);
        rentedBookGrid.setItems(appController.findRentedBooks(account).getBody());
        var commandLayout = new HorizontalLayout();
        var rentButton = new Button("Rent");
        var returnButton = new Button("Return");
        var addBookButton = new Button("Add Book");
        var updateBookButton = new Button("Update Book");
        var logoutButton = new Button("Log out");
        var borrowList = new Button("Borrowed List");

        logoutButton.addClickListener(event -> {
            UI.getCurrent().navigate("/app/login");
        });

        rentButton.addClickListener(event -> {
            var popup = new Dialog("Rent book");
            var titleField = new TextField("Title");
            var authorField = new TextField("Author");
            var returnDateField = new DatePicker("Return Date");
            popup.add(new VerticalLayout(titleField, authorField), returnDateField);

            popup.getFooter().add(createButton("Cancel", cancelEvent -> popup.close(),
                    ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR),
                createButton("Proceed", proceedEvent -> {
                    LocalDate returnDate = returnDateField.getValue();
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String returnDateString = returnDate.format(format);
                    String status = "borrowed";
                    var operationResult = appController.rentBook(Map.ofEntries(
                        Map.entry("username", account.getUsername()),
                        Map.entry("password", account.getPassword()),
                        Map.entry("title", titleField.getValue()),
                        Map.entry("author", authorField.getValue()),
                        Map.entry("status", status),
                        Map.entry("returnDate", returnDateString)
                    ));

                    var notification = new Notification();

                    var notificationLayout = new HorizontalLayout(new NativeLabel(operationResult.getBody()), new Button(
                        new Icon("lumo", "cross"), evt -> {
                        notification.close();
                    }));

                    notificationLayout.setAlignItems(Alignment.CENTER);
                    notification.add(notificationLayout);

                    switch (operationResult.getStatusCode()) {
                        case HttpStatus.OK -> notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        case HttpStatus.ALREADY_REPORTED -> notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                        default -> notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }

                    notification.open();
                    rentedBookGrid.setItems(appController.findRentedBooks(account).getBody());
                    popup.close();
                }, ButtonVariant.LUMO_PRIMARY));

            popup.open();
        });

        returnButton.addClickListener(event -> {
            var popup = new Dialog("Return book");
            var titleField = new TextField("Title");
            var authorField = new TextField("Author");
            popup.add(new VerticalLayout(titleField, authorField));

            popup.getFooter().add(new Button("Cancel", cancelEvent -> popup.close()),
                new Button("Proceed", proceedEvent -> {
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

                    notificationLayout.setAlignItems(Alignment.CENTER);
                    notification.add(notificationLayout);

                    switch (operationResult.getStatusCode()) {
                        case HttpStatus.OK -> notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        case HttpStatus.ALREADY_REPORTED -> notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                        default -> notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }

                    notification.open();
                    rentedBookGrid.setItems(appController.findRentedBooks(account).getBody());
                    popup.close();
                }));

            popup.open();
        });

        addBookButton.addClickListener(event -> {
            var popup = new Dialog("Add Book");
            var titleField = new TextField("Title");
            var authorField = new TextField("Author");
            var publisherField = new TextField("Publisher");
            var descriptionField = new TextField("Description");
            var categoryIdField = new TextField("category_Id");
            categoryIdField.setPattern("[0-9]*");  // Only digits are allowed

            popup.add(new VerticalLayout(titleField, authorField, publisherField, descriptionField));

            popup.getFooter().add(
                createButton("Cancel", cancelEvent -> popup.close(),
                    ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR),

                createButton("Proceed", proceedEvent -> {
                    Integer categoryId = null;
                    try {
                        categoryId = Integer.parseInt(categoryIdField.getValue());
                    } catch (NumberFormatException e) {
                        // Handle invalid categoryId input (not a valid number)
                        Notification.show("Invalid Category ID. Please enter a valid number.", 3000, Notification.Position.MIDDLE);
                        return;
                    }

                    var operationResult = appController.addBook(new Book(null, titleField.getValue(),
                            authorField.getValue(), publisherField.getValue(), descriptionField.getValue(), categoryId)); // Assuming 1 is the category_id


                    var notification = new Notification();
                    var label = new NativeLabel();

                    switch (operationResult.getStatusCode()) {
                        case HttpStatus.OK -> {
                            notification.setText("Successfully added book!");
                            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY, NotificationVariant.LUMO_SUCCESS);
                        } case HttpStatus.ALREADY_REPORTED -> {
                            notification.setText("Book already exists!");
                            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                        }
                        default -> {
                            notification.setText("Unexpected error!");
                            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY, NotificationVariant.LUMO_ERROR);
                        }
                    }

                    notification.add(new HorizontalLayout(label,
                        new Button(new Icon("lumo", "cross"), evt -> {
                        notification.close();
                    })));

                    notification.open();
                }, ButtonVariant.LUMO_PRIMARY));
        });

        // TODO:
        updateBookButton.addClickListener(event -> {
            var popup = new Dialog("Update Book");
            var titleField = new TextField("Title");
            var authorField = new TextField("Author");
            var publisherField = new TextField("Publisher");
            var descriptionField = new TextField("Description");
            popup.add(new VerticalLayout(titleField, authorField, publisherField, descriptionField));
            popup.getFooter().add(new Button(), new Button());
            popup.open();
        });

        borrowList.addClickListener(event -> {
            Dialog dialog = new Dialog();

            Button BorrowedList = new Button("BorrowedList", e -> {UI.getCurrent().navigate("/app/borrowed"); dialog.close();});
            Button ReturnedList = new Button("ReturnedList", e -> {UI.getCurrent().navigate("/app/returned"); dialog.close();});

            dialog.add(BorrowedList, ReturnedList);

            dialog.open();
        });

        commandLayout.add(rentButton, returnButton, borrowList, logoutButton);
        availableBookGrid.setItems(appController.findAllBooks());

        add(topLayout, commandLayout, new VerticalLayout(new NativeLabel("Rented books:"), rentedBookGrid),
            new VerticalLayout(new NativeLabel("Available books:"), availableBookGrid));
    }

    private static Button createButton(String text, ComponentEventListener<ClickEvent<Button>> event,
                                       ButtonVariant... variants) {
        Button result = new Button(text, event);
        result.addThemeVariants(variants);
        return result;
    }

    private static <T> Dialog createPopup(Supplier<T> supplier, Consumer<T> consumer, TextField... textFields) {
        return new Dialog();
    }
}
