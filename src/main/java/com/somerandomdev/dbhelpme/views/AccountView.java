package com.somerandomdev.dbhelpme.views;

import com.somerandomdev.dbhelpme.Account;
import com.somerandomdev.dbhelpme.AppController;
import com.somerandomdev.dbhelpme.Book;
import com.vaadin.flow.component.button.Button;
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

import java.util.HashMap;
import java.util.Map;

@Route("/app/account")
public class AccountView extends VerticalLayout implements HasUrlParameter<String> {
    private final AppController appController;
    private final Grid<Book> rentedBookGrid;

    public AccountView(AppController appController) {
        this.appController = appController;
        rentedBookGrid = new Grid<>(Book.class);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String accountName) {
        Account account = appController.findAccountByUsername(accountName).orElseThrow(() ->
            new IllegalArgumentException("Account not found!"));

        var topLayout = new VerticalLayout(new NativeLabel("Hi, " + accountName + "!"));
        topLayout.setDefaultHorizontalComponentAlignment(Alignment.END);
        rentedBookGrid.setItems(appController.findRentedBooks(account).getBody());
        var rentLayout = new HorizontalLayout();
        var rentButton = new Button("Rent");

        rentButton.addClickListener(event -> {
            var popup = new Dialog("Rent book");
            var titleField = new TextField("Title");
            var authorField = new TextField("Author");
            popup.add(titleField, authorField);

            popup.getFooter().add(new Button("Cancel", cancelEvent -> popup.close()),
                new Button("Proceed", proceedEvent -> {
                    var operationResult = appController.rentBook(Map.ofEntries(
                        Map.entry("username", account.getUsername()),
                        Map.entry("password", account.getPassword()),
                        Map.entry("title", titleField.getValue()),
                        Map.entry("author", authorField.getValue())
                    ));

                    var notification = new Notification();
                    notification.setText(operationResult.getBody());
                    notification.setDuration(5);

                    switch (operationResult.getStatusCode()) {
                        case HttpStatus.OK -> notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        case HttpStatus.ALREADY_REPORTED -> notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                        default -> notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }

                    notification.open();
                popup.close();
            }));

        });

        add(topLayout, rentedBookGrid);
    }
}
