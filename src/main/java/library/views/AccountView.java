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

        availableBookGrid.setItems(appController.findAllBooks());

        add(topLayout, new VerticalLayout(new NativeLabel("Rented books:"), rentedBookGrid),
            new VerticalLayout(new NativeLabel("Available books:"), availableBookGrid));
    }
}