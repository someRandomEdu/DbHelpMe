package com.somerandomdev.dbhelpme.views;

import com.somerandomdev.dbhelpme.AppController;
import com.somerandomdev.dbhelpme.Book;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
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
import java.util.Objects;

@Route("/app/account")
public class AccountView extends VerticalLayout implements HasUrlParameter<String> {
    private final AppController appController;
    private final Grid<Book> rentedBookGrid;
    private String accountName;

    public AccountView(AppController appController) {
        this.appController = appController;
        rentedBookGrid = new Grid<>(Book.class);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String accountName) {
        this.accountName = accountName;
        NativeLabel accountNameLabel = new NativeLabel("Hi, " + accountName + '!');
        var topLabelLayout = new VerticalLayout(accountNameLabel);
        var rentButton = new Button("Rent");
        var returnButton = new Button("Return");
        rentButton.addClickListener(event -> getRentDialog().open());
        returnButton.addClickListener(event -> getReturnDialog().open());
        var commandLayout = new HorizontalLayout(rentButton, returnButton);
        topLabelLayout.setSpacing(true);
        topLabelLayout.setAlignItems(Alignment.END);
        rentedBookGrid.setItems(appController.findRentedBooksOf(accountName).getBody());
        add(topLabelLayout);
        add(commandLayout);
        add(rentedBookGrid);
    }

    private Dialog getRentDialog() {
        var dialog = new Dialog();
        var layout = new HorizontalLayout();
        layout.setSpacing(true);
        TextField titleField = new TextField("Title");
        TextField authorField = new TextField("Author");
        layout.add(titleField);
        layout.add(authorField);
        dialog.add(new VerticalLayout(titleField, authorField));

        dialog.getFooter().add(new Button("Cancel", event -> dialog.close()),
            new Button("Rent", event -> {
                var operationResult = appController.rentBookWithUsername(Map.ofEntries(
                    Map.entry("account", accountName),
                    Map.entry("title", titleField.getValue()),
                    Map.entry("author", authorField.getValue())));

                var statusCode = operationResult.getStatusCode();
                Notification notification = new Notification();

                var notificationLayout = new HorizontalLayout(
                    new Button("x", evt -> notification.close()));

                notificationLayout.setSpacing(true);
                notificationLayout.setAlignItems(Alignment.CENTER);
                notification.add(notificationLayout);

                switch (statusCode) {
                    case HttpStatus.OK -> {
                        notification.setText("Book rented successfully!");
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                    case HttpStatus.ALREADY_REPORTED -> {
                        notification.setText("Book already rented!");
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                    case HttpStatus.NOT_FOUND -> {
                        notification.setText("Book not found!");
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                    default -> {
                        notification.setText("Unknown error!");
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                }

                notification.open();
                dialog.close();
            }));

        return dialog;
    }

    private Dialog getReturnDialog() {
        var dialog = new Dialog();
        var layout = new HorizontalLayout();
        layout.setSpacing(true);
        TextField titleField = new TextField("Title");
        TextField authorField = new TextField("Author");
        layout.add(titleField);
        layout.add(authorField);
        dialog.add(layout);

        dialog.getFooter().add(new Button("Cancel", event -> dialog.close()),
            new Button("Return", event -> {
                var operationResult = appController.returnBook(Map.ofEntries(
                    Map.entry("account", accountName),
                    Map.entry("title", titleField.getValue()),
                    Map.entry("author", authorField.getValue())
                ));

                var notification = new Notification();

                var notificationLayout = new HorizontalLayout(
                    new Button("x", evt -> notification.close())
                );

                notificationLayout.setSpacing(true);
                notificationLayout.setAlignItems(Alignment.CENTER);
                notification.setText(operationResult.getBody());
                notification.add(notificationLayout);

                switch (operationResult.getStatusCode()) {
                    case HttpStatus.OK, HttpStatus.ALREADY_REPORTED -> {
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                    case HttpStatus.NOT_FOUND -> {
                        notification.addThemeVariants();
                    }
                    default -> {
                        notification.setText("Unknown error!");
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                }

                dialog.close();
                notification.open();
            }));

        return dialog;
    }
}
