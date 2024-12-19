package library.views;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import library.entity.Account;
import library.AppController;
import library.entity.Book;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import library.entity.CurrentUser;

@Route(value = "/app/account", layout = MainView.class)
public class AccountView extends VerticalLayout {
    // private final AppController appController;
    // private final Grid<Book> rentedBookGrid;
    // private final Grid<Book> availableBookGrid;

    public AccountView(AppController appController) {
        var account = CurrentUser.getAccount();
        var topLayout = new VerticalLayout(new NativeLabel("Hi, " + account.getUsername() + "!"));
        topLayout.setDefaultHorizontalComponentAlignment(Alignment.END);
        var rentedBookGrid = new Grid<>(Book.class);
        var availableBookGrid = new Grid<>(Book.class);
        rentedBookGrid.setItems(appController.findAllRentedBooksFromAccountId(CurrentUser.getId()));
        availableBookGrid.setItems(appController.findAllBooks());

        add(topLayout, new NativeLabel("Rented Books"), rentedBookGrid,
            new NativeLabel("Available Books"),availableBookGrid);
    }

    // // TODO: Deduplicate code?
    // @Override
    // public void setParameter(BeforeEvent beforeEvent, String accountName) {
    //     Account account = appController.findAccountByUsername(accountName).orElseThrow(() ->
    //         new IllegalArgumentException("Account not found!"));
    //
    //     var topLayout = new VerticalLayout(new NativeLabel("Hi, " + accountName + "!"));
    //     topLayout.setDefaultHorizontalComponentAlignment(Alignment.END);
    //     rentedBookGrid.setItems(appController.findRentedBooks(account).getBody());
    //
    //     availableBookGrid.setItems(appController.findAllBooks());
    //
    //     add(topLayout, new VerticalLayout(new NativeLabel("Rented books:"), rentedBookGrid),
    //         new VerticalLayout(new NativeLabel("Available books:"), availableBookGrid));
    // }

    public static String getRoute() {
        return "/app/account";
    }
}
