package library.views;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.router.Route;
import library.*;
import library.entity.Book;
import library.helper.DatabaseHelper;
import org.bouncycastle.crypto.prng.drbg.DualECPoints;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;

import static library.entity.CurrentUser.getPassword;
import static library.entity.CurrentUser.getUsername;

@Route(value = "notification", layout = MainView.class)
@Component
public class NotificationPanel extends Div {
    private final StringHttpMessageConverter stringHttpMessageConverter;
    private BookRepository bookRepository;
    private NotificationsRepository notificationsRepository;
    private WishListRepository wishListRepository;
    private NotificationsService notificationsService;
    public NotificationPanel(StringHttpMessageConverter stringHttpMessageConverter) {
        this.stringHttpMessageConverter = stringHttpMessageConverter;
    }
    private AppController appController;

    public Popover getPopover(Button button, Integer userId, NotificationsRepository notificationsRepository, AppController appController, BookRepository bookRepository, WishListRepository wishListRepository, NotificationsService notificationsService) {
        this.notificationsRepository = notificationsRepository;
        this.bookRepository = bookRepository;
        this.appController = appController;
        this.notificationsService = notificationsService;
        this.wishListRepository = wishListRepository;
        Popover popover = new Popover();
        popover.setTarget(button);
        popover.setWidth("300px");
        popover.setHeight("300px");
        popover.addThemeVariants(PopoverVariant.ARROW,
                PopoverVariant.LUMO_NO_PADDING);
        popover.setPosition(PopoverPosition.BOTTOM);
        popover.setAriaLabelledBy("notifications-heading");

        List<Notifications> unreadList = notificationsRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "unread");
        List<Notifications> allList = notificationsRepository.findByUserIdOrderByCreatedAtDesc(userId);

        Div unreadContent = createClickableNotificationList(unreadList, "No unread notifications", wishListRepository, notificationsService);
        Div allContent = createClickableNotificationList(allList, "No notifications", wishListRepository, notificationsService);

        TabSheet tabSheet = new TabSheet();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_SMALL, TabSheetVariant.LUMO_NO_PADDING);
        tabSheet.addClassName("notifications");

        tabSheet.add("Unread", unreadContent);
        tabSheet.add("All", allContent);

        H4 heading = new H4("Notifications");
        heading.setId("notifications-heading");
        heading.getStyle().set("margin", "0");

        Button markRead = new Button("Mark all read", (e) -> {
           try (Connection connection = DatabaseHelper.getConnection()) {
                String sql = "UPDATE notifications SET status = 'read' WHERE status = 'unread'";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("All notifications marked as read.");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            unreadContent.removeAll();
            unreadContent.add(new Div("No new notifications"){{
                this.addClassName("no-notifications-msg");
            }});
        });
        markRead.getStyle().set("margin", "0 0 0 auto");
        markRead.addThemeVariants(ButtonVariant.LUMO_SMALL);

        HorizontalLayout layout = new HorizontalLayout(heading, markRead);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.getStyle().set("padding",
                "var(--lumo-space-m) var(--lumo-space-m) var(--lumo-space-xs)");

        popover.add(layout, tabSheet);

        return popover;
    }

    private Instant minutesAgo(int minutes) {
        return LocalDateTime.now(ZoneOffset.UTC).minusMinutes(minutes)
                .toInstant(ZoneOffset.UTC);
    }

    private Div createClickableNotificationList(List<Notifications> notifications, String emptyMessage, WishListRepository wishListRepository, NotificationsService notificationsService) {
        Div container = new Div();

        if (notifications.isEmpty()) {
            container.add(new Div(emptyMessage) {{
                this.addClassName("no-notifications-msg");
            }});
        } else {
            notifications.forEach(notification -> {
                Div notificationItem = new Div();
                notificationItem.setText(notification.getMessage());
                notificationItem.addClassName("notification-item");

                notificationItem.addClickListener(event -> {
                    if (notification.getType().equals("wishlist")) {
                        showBorrowDialog(notification, bookRepository, wishListRepository, notificationsService);
//                    System.out.println(notification.getType());
                    } else {
                        showNotificationDialog(notification);
                    }
                    try (Connection connection = DatabaseHelper.getConnection()) {
                        String sql = "UPDATE notifications SET status = 'read' WHERE status = 'unread'";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                            int rowsAffected = preparedStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("All notifications marked as read.");
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                container.add(notificationItem);
            });
        }

        return container;
    }

    private void showNotificationDialog(Notifications notification) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Notification details");

        Div content = new Div();
        content.setText(notification.getMessage());

        dialog.add(content);

        Button closeButton = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(closeButton);

        dialog.open();
    }

    private void showBorrowDialog(Notifications notification, BookRepository bookRepository, WishListRepository wishListRepository, NotificationsService notificationsService) {
        String message = notification.getMessage();
        String st = "The book ";
        String en = " is now available, do you want to borrow?";
        int stM = message.indexOf(st) + st.length();
        int enM = message.indexOf(en);

        String bookTitle = message.substring(stM, enM);

        Book book = bookRepository.findByTitle(bookTitle);

        NativeLabel label = new NativeLabel("Do you want to borrow this book?");

        TextField titleField = new TextField("Book Title");
        titleField.setValue(bookTitle);
        titleField.setReadOnly(true);

        TextField authorField = new TextField("Author");
        authorField.setValue(book.getAllAuthors());
        authorField.setReadOnly(true);

        DatePicker returnDateField = new DatePicker("Return Date");
        Button submitButton = new Button("Submit");
        Button closeButton = new Button("Do not borrow");

        submitButton.addClickListener(e -> {
            LocalDate returnDate = returnDateField.getValue();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String returnDateString = returnDate.format(format);
            String status = "borrowed";
            var operationResult = appController.rentBook(Map.ofEntries(
                    Map.entry("username", getUsername()),
                    Map.entry("password", getPassword()),
                    Map.entry("title", titleField.getValue()),
                    Map.entry("author", authorField.getValue()),
                    Map.entry("status", status),
                    Map.entry("returnDate", returnDateString)
            ));

            var noti = new Notification();

            var notificationLayout = new HorizontalLayout(new NativeLabel(operationResult.getBody()), new Button(
                    new Icon("lumo", "cross"), evt -> {
                noti.close();
            }));

            notificationLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            noti.add(notificationLayout);

            switch (operationResult.getStatusCode()) {
                case HttpStatus.OK -> noti.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                case HttpStatus.ALREADY_REPORTED -> noti.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                default -> noti.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            noti.open();

            UI.getCurrent().access(() -> {
                UI.getCurrent().getPage().executeJs("setTimeout(() => { window.location.href = '/borrowedTab'; }, 1000);");
            });
        });


        closeButton.addClickListener(event -> {
            List<WishList> wishlist = wishListRepository.findAllWishByBookId(book.getId());
            System.out.println("11111" + wishlist);

            if(!wishlist.isEmpty()) {
                WishList firstUser = wishlist.get(0);
                Integer userId = firstUser.getUserId();
                String mes = "The book " + bookTitle + " is now available, do you want to borrow?";
                notificationsService.saveNotification(userId, "wishlist", mes);

                wishListRepository.delete(firstUser);
            }

            event.getSource().getUI().ifPresent(ui -> {
                ui.getChildren().filter(child -> child instanceof Dialog)
                        .findFirst().ifPresent(dialog -> {
                            ((Dialog) dialog).close();
                        });
            });
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout(submitButton, closeButton);
        buttonsLayout.setSpacing(true);

        VerticalLayout dialogLayout = new VerticalLayout(label, titleField, authorField, returnDateField, buttonsLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        Dialog dialog = new Dialog(dialogLayout);
        dialog.setHeaderTitle("Notification details");

        dialog.open();
    }
}
