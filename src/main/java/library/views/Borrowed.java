package library.views;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import library.*;
import library.entity.Book;
import library.entity.CurrentUser;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpStatus;

import static library.entity.CurrentUser.*;
import static library.views.LoginView.getUserName;

@Route(value = "/app/borrowed", layout = MainView.class)
public class Borrowed extends VerticalLayout {
    private final RentDataRepository rentDataRepository;
    private final AppController appController;
    private final BookRepository bookRepository;
    private final WishListRepository wishListRepository;
    private final NotificationsRepository notificationsRepository;
    private final NotificationsService notificationsService;
    private Grid<RentData> grid;

    public Borrowed(AppController appController, BookRepository bookRepository, RentDataRepository rentDataRepository, WishListRepository wishListRepository, NotificationsRepository notificationsRepository, NotificationsService notificationsService) {
        this.appController = appController;
        this.bookRepository = bookRepository;
        this.wishListRepository = wishListRepository;
        this.notificationsRepository = notificationsRepository;
        this.notificationsService = notificationsService;

        Div titleLabel = new Div(new Text("Borrowed List"));
        titleLabel.addClassName("title-container");
        titleLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "blue")
                .set("text-align", "center")
                .set("margin-bottom", "20px");

        grid = new Grid<>(RentData.class);
        grid.setColumns("id", "bookId", "borrowFrom", "borrowTo");

        grid.addColumn(rentData -> {
            // Kiểm tra xem sách có đang mượn hay không và ngày trả có quá hạn hay không
            if (rentData.getStatus().equals("borrowed")) {
                LocalDate currentDate = LocalDate.now();  // Lấy ngày hiện tại
                LocalDate returnDate = rentData.getBorrowTo();  // Lấy ngày trả sách từ đối tượng RentData

                // Kiểm tra ngày trả sách có vượt quá ngày hiện tại không
                if (returnDate != null && returnDate.isBefore(currentDate)) {
                    return "Expired";  // Nếu quá hạn
                } else {
                    return "Unexpired";  // Nếu chưa quá hạn
                }
            }
            return "Not Borrowed";  // Nếu sách không còn đang mượn
        }).setHeader("Status");

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, rentData) -> {
                    button.setIcon(new Icon(VaadinIcon.REPLY));
                    button.addThemeVariants(ButtonVariant.LUMO_ERROR);

                    final Integer bookId = rentData.getBookId();
                    Optional<Book> bookOpt = bookRepository.findById(bookId);
                    final String bookTitle = bookOpt.map(Book::getTitle).orElse("Book not found");
                    final String bookAuthor = bookOpt.map(Book::getAuthor).orElse("Author not found");
                    TextField titleField = new TextField();
                    titleField.setValue(bookTitle);
                    titleField.setReadOnly(true);

                    TextField authorField = new TextField();
                    authorField.setValue(bookAuthor);
                    authorField.setReadOnly(true);

                    button.addClickListener(e -> {
                        var operationResult = appController.returnBook(Map.ofEntries(
                                Map.entry("username", getUsername()),
                                Map.entry("password", getPassword()),
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

                        List<WishList> wishlist = wishListRepository.findAllWishByBookId(bookId);

                        if(!wishlist.isEmpty()) {
                            WishList firstUser = wishlist.get(0);
                            Integer userId = firstUser.getUserId();
                            String message = "The book " + bookTitle + " is now available, do you want to borrow?";
                            notificationsService.saveNotification(userId, "wishlist", message);

                            wishListRepository.delete(firstUser);
                        }

                        UI.getCurrent().access(() -> {
                            UI.getCurrent().getPage().executeJs("setTimeout(() => { window.location.href = '/borrowedTab'; }, 1000);");
                        });
                    });

                })).setHeader("");


        List<RentData> data = rentDataRepository.findAllByAccountId(CurrentUser.getId());

        grid.setItems(data);

        setAlignItems(Alignment.CENTER);
        add(titleLabel, grid);
        setSizeFull();
        grid.getElement().getStyle().set("border-collapse", "collapse");
        grid.getElement().getStyle().set("width", "100%");

        // Áp dụng style cho các cột và hàng
        grid.getElement().getStyle().set("border", "1px solid black");  // Kẻ viền cho grid
        grid.getElement().getStyle().set("border-spacing", "0");

        // Áp dụng style cho các cột và hàng bên trong grid
        grid.getElement().getChildren().forEach(child -> {
            if (child.getTag().equals("vaadin-grid-cell-content")) {
                child.getStyle().set("border", "1px solid black");
            }
        });

        this.rentDataRepository = rentDataRepository;
    }
}
