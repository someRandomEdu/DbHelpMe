package library.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import library.*;
import library.entity.Account;
import library.entity.Book;

import com.vaadin.flow.component.button.Button;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static library.entity.CurrentUser.getPassword;
import static library.entity.CurrentUser.getUsername;
import static library.views.LoginView.getUserName;

@Route(value = "booklist", layout = MainView.class)
public class UserBookListView extends VerticalLayout {
    private final AppController appController;
    private final BookRepository bookRepository;
    private final RentDataRepository rentDataRepository;
    private final AccountRepository accountRepository;
    private static final int PAGE_SIZE = 10;
    private Grid<Book> availableBookGrid;
    private List<Book> books;

    public UserBookListView(AppController appController, AccountRepository accountRepository, BookRepository bookRepository, RentDataRepository rentDataRepository) {
        this.appController = appController;
        this.bookRepository = bookRepository;
        this.rentDataRepository = rentDataRepository;
        this.accountRepository = accountRepository;
        this.books = bookRepository.findAll();
        availableBookGrid = new Grid<>(Book.class);

        setParameter();
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    private void setParameter() {
        availableBookGrid.setAllRowsVisible(false);
        Div titleLabel = new Div(new Text("Book List"));
        titleLabel.addClassName("title-container");
        titleLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "blue")
                .set("text-align", "center")
                .set("margin-bottom", "20px");

        availableBookGrid.setColumns("id", "title", "author", "publisher", "categoryId");

        availableBookGrid.addColumn(
                new ComponentRenderer<>(Button::new, (button, books) -> {
                    if(rentCheck(getUserName(), books.getTitle(), books.getAuthor())) {
                        Long expiredDays = getExpiredDays(getUserName(), books);

                        if(expiredDays >= 0) {
                            button.setIcon(new Icon(VaadinIcon.CLIPBOARD_TEXT));
                            button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                        } else {
                            button.setIcon(new Icon(VaadinIcon.WARNING));
                            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
                        }

                        button.addClickListener(event -> {
                            Dialog dialog = new Dialog();
                            dialog.setHeaderTitle("Borrow detail");

                            VerticalLayout detailLayout = new VerticalLayout();
                            if(expiredDays >= 0) {
                                 detailLayout = createDetailLayout(getUserName(), books, expiredDays);
                            } else {
                                detailLayout = createExpiredLayout(getUserName(), books, expiredDays);
                            }
                            dialog.add(detailLayout);

                            dialog.open();
                        });
                    } else {
                        button.setIcon(new Icon(VaadinIcon.STOCK));
                        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

                        button.addClickListener(event -> {
                            Integer cur = books.getCurrent();
                            if(cur == 0) {
//                                Dialog dialog = createWhisListDialog(books.getTitle(), books.getAuthor());
//
//                                dialog.open();
                            } else {
                                Dialog dialog = createBorrowDialog(books.getTitle(), books.getAuthor());

                                dialog.open();
                            }
                        });
                    }
                })).setHeader("Status");

        availableBookGrid.setItems(appController.findAllBooks());
        Pagination<Book> pagination = new Pagination<>(availableBookGrid, books, PAGE_SIZE);

        GridListDataView<Book> dataView = availableBookGrid.setItems(pagination.getCurrentPageItems());
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        searchField.addValueChangeListener(e -> {
            String searchRes = searchField.getValue().trim();

            // Lọc sách trên toàn bộ danh sách
            List<Book> filteredBooks = books.stream()
                    .filter(book -> {
                        if (searchRes.isEmpty()) {
                            return true;
                        }

                        boolean matchesTitle = matchesTerm(book.getTitle(), searchRes);
                        boolean matchesAuthor = matchesTerm(book.getAuthor(), searchRes);

                        return matchesTitle || matchesAuthor;
                    })
                    .collect(Collectors.toList());

            pagination.setItems(filteredBooks);
            availableBookGrid.setItems(pagination.getCurrentPageItems());

            availableBookGrid.getDataProvider().refreshAll();
        });
        add(titleLabel, searchField, pagination.getLayout(), availableBookGrid);

    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void showBookDetails(Book book) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Borrow details");

        add(dialog);
    }


    private Dialog createBorrowDialog(String bookTitle, String bookAuthor) {
        TextField titleField = new TextField("Book Title");
        titleField.setValue(bookTitle);
        titleField.setReadOnly(true);

        TextField authorField = new TextField("Author");
        authorField.setValue(bookAuthor);
        authorField.setReadOnly(true);

        DatePicker returnDateField = new DatePicker("Return Date");
        Button submitButton = new Button("Submit");
        Button closeButton = new Button("Close");

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

            var notification = new Notification();

            var notificationLayout = new HorizontalLayout(new NativeLabel(operationResult.getBody()), new Button(
                    new Icon("lumo", "cross"), evt -> {
                notification.close();
            }));

            notificationLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            notification.add(notificationLayout);

            switch (operationResult.getStatusCode()) {
                case HttpStatus.OK -> notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                case HttpStatus.ALREADY_REPORTED -> notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                default -> notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            notification.open();

            UI.getCurrent().access(() -> {
                UI.getCurrent().getPage().executeJs("setTimeout(() => { window.location.href = '/booklist'; }, 1000);");
            });
        });


        closeButton.addClickListener(event -> {
            event.getSource().getUI().ifPresent(ui -> {
                ui.getChildren().filter(child -> child instanceof Dialog)
                        .findFirst().ifPresent(dialog -> {
                            ((Dialog) dialog).close();
                        });
            });
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout(submitButton, closeButton);
        buttonsLayout.setSpacing(true);

        VerticalLayout dialogLayout = new VerticalLayout(titleField, returnDateField, buttonsLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        Dialog dialog = new Dialog(dialogLayout);
        dialog.setHeaderTitle("Borrow Detail");

        return dialog;
    }



    private VerticalLayout createDetailLayout(String username, Book book, Long expiredDay) {
        VerticalLayout dialogLayout = new VerticalLayout();
        Account account = accountRepository.findAccountByUsername(username);

        Grid<RentData> rentDataGrid = new Grid<>(RentData.class, false);

        rentDataGrid.addColumn(rentData -> rentData.getBorrowFrom())
                .setHeader("Borrow From")
                .setAutoWidth(true);

        rentDataGrid.addColumn(rentData -> rentData.getBorrowTo())
                .setHeader("Borrow To")
                .setAutoWidth(true);

        rentDataGrid.addColumn(rentData -> expiredDay)
                .setHeader("Expired after (Days)")
                .setAutoWidth(true);

        List<RentData> rentDataList = rentDataRepository.findAllByBookIdAndAccountId(book.getId(), account.getId());
        rentDataGrid.setItems(rentDataList);

        Button closeButton = new Button("Close", event -> {
            Dialog dialog = (Dialog) dialogLayout.getParent().orElse(null);
            if (dialog != null) {
                dialog.close();
            }
        });
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        dialogLayout.add(rentDataGrid, closeButton);
        return dialogLayout;
    }

    private VerticalLayout createExpiredLayout(String username, Book book, Long day) {
        VerticalLayout dialogLayout = new VerticalLayout();
        Account account = accountRepository.findAccountByUsername(username);

        Grid<RentData> rentDataGrid = new Grid<>(RentData.class, false);

        rentDataGrid.addColumn(rentData -> rentData.getBorrowFrom())
                .setHeader("Borrow From")
                .setAutoWidth(true);

        rentDataGrid.addColumn(rentData -> rentData.getBorrowTo())
                .setHeader("Borrow To")
                .setAutoWidth(true);

        rentDataGrid.addColumn(rentData -> (-1)*day)
                .setHeader("Late (Days)")
                .setAutoWidth(true);

        List<RentData> rentDataList = rentDataRepository.findAllByBookIdAndAccountId(book.getId(), account.getId());
        rentDataGrid.setItems(rentDataList);

        Button closeButton = new Button("Close", event -> {
            Dialog dialog = (Dialog) dialogLayout.getParent().orElse(null); // Lấy đối tượng dialog từ layout
            if (dialog != null) {
                dialog.close();
            }
        });

        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        dialogLayout.add(rentDataGrid, closeButton);
        return dialogLayout;
    }

    private boolean rentCheck(String username, String title, String author) {
        Account account = accountRepository.findAccountByUsername(username);

        if(account == null) return false;

        Book book = bookRepository.findByTitleAndAuthor(title, author);

        return rentDataRepository.existsByAccountIdAndBookId(account.getId(), book.getId());
    }


    public Long getExpiredDays(String userName, Book book) {
        Account account = accountRepository.findAccountByUsername(userName);
        RentData rentData = rentDataRepository.findByBookIdAndAccountId(book.getId(), account.getId())
                .orElseThrow(() -> new RuntimeException("RentData not found"));

        LocalDate currentDate = LocalDate.now();
        LocalDate expiredDate = rentData.getBorrowTo();

        long days = java.time.temporal.ChronoUnit.DAYS.between(currentDate, expiredDate);

        return days;
    }

}
