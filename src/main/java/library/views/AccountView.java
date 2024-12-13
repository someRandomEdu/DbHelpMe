package library.views;

import library.entity.Account;
import library.AppController;
import library.entity.Book;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

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
