package library.views;

import com.beust.ah.A;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import library.*;
import library.entity.Account;
import library.entity.Book;

import com.vaadin.flow.component.button.Button;
import library.entity.CurrentUser;
import library.helper.DatabaseHelper;
import org.springframework.http.HttpStatus;

import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static library.entity.Book.getCategoriesList;
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

        availableBookGrid.removeAllColumns();

        availableBookGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        availableBookGrid.addColumn(Book::getId)
                .setHeader("ID")
                .setWidth("80px")
                .setFlexGrow(0);

        availableBookGrid.addColumn(Book::getTitle)
                .setHeader("Title")
                .setFlexGrow(1);

        availableBookGrid.addColumn(Book::getAllAuthors)
                .setHeader("Authors")
                .setFlexGrow(1);

        availableBookGrid.addColumn(Book::getPublisher)
                .setHeader("Publisher")
                .setFlexGrow(1);

        availableBookGrid.addColumn(Book::getCategoriesString)
                .setHeader("Category ID")
                .setFlexGrow(1);

        availableBookGrid.addColumn(
                new ComponentRenderer<>(Button::new, (button, books) -> {
                    if(rentCheck(getUserName(), books.getTitle(), books.getAllAuthors())) {
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
                            Integer cur = books.getQuantity();
                            if(cur == 0) {
                                Dialog dialog = createWishListDialog(CurrentUser.getId(), books.getId(), books.getTitle(), books.getAllAuthors());

                                dialog.open();
                            } else {
                                Dialog dialog = createBorrowDialog(books.getTitle(), books.getAllAuthors());

                                dialog.open();
                            }
                        });
                    }
                })).setHeader("Status")
                .setFlexGrow(0);

        availableBookGrid.setItems(appController.findAllBooks());
        Pagination<Book> pagination = new Pagination<>(availableBookGrid, books, PAGE_SIZE);

        GridListDataView<Book> dataView = availableBookGrid.setItems(pagination.getCurrentPageItems());
        TextField titleSearchField = new TextField();
        titleSearchField.setWidth("30%");
        titleSearchField.setPlaceholder("Search by Title");
        titleSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        titleSearchField.setValueChangeMode(ValueChangeMode.EAGER);

        TextField authorSearchField = new TextField();
        authorSearchField.setWidth("30%");
        authorSearchField.setPlaceholder("Search by Author");
        authorSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        authorSearchField.setValueChangeMode(ValueChangeMode.EAGER);

        MultiSelectComboBox<String> categoriesComboBox = new MultiSelectComboBox<>();
        categoriesComboBox.setPlaceholder("Select categories");
        categoriesComboBox.setItems(getCategoriesList());
        categoriesComboBox.setWidth("30%");

        titleSearchField.addValueChangeListener(e -> filterBooks(pagination, titleSearchField, authorSearchField, categoriesComboBox));
        authorSearchField.addValueChangeListener(e -> filterBooks(pagination, titleSearchField, authorSearchField, categoriesComboBox));
        categoriesComboBox.addValueChangeListener(e -> filterBooks(pagination, titleSearchField, authorSearchField, categoriesComboBox));

        Button addBookButton = new Button("Add book");
        addBookButton.addClickListener(event-> {
           addBookdialog();
        });

        HorizontalLayout searchLayout = new HorizontalLayout(titleSearchField, authorSearchField, categoriesComboBox);
        searchLayout.setWidthFull();
        searchLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        searchLayout.setSpacing(true);

        add(titleLabel, searchLayout);
        HorizontalLayout buttonLayout = new HorizontalLayout(pagination.getLayout());
        if(CurrentUser.isAdmin()) buttonLayout.add(addBookButton);
        add(buttonLayout, availableBookGrid);

    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void showBookDetails(Book book) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Borrow details");

        add(dialog);
    }

    private Dialog  createWishListDialog(Integer userId, Integer BookId, String bookTitle, String bookAuthor) {
        TextField titleField = new TextField("Book Title");
        titleField.setValue(bookTitle);
        titleField.setReadOnly(true);

        TextField authorField = new TextField("Author");
        authorField.setValue(bookAuthor);
        authorField.setReadOnly(true);


        Button AddButton = new Button("Add to WishList");
        Button closeButton = new Button("Close");

        NativeLabel error = new NativeLabel("Do you want to add to WishList?");

        AddButton.addClickListener(event -> {
            boolean success = addToWishlist(userId, BookId);

            if (success) {
                Notification.show("The book has been added to your wishlist!", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("Failed to add the book to the wishlist. Please try again.", 3000, Notification.Position.MIDDLE);
            }

            event.getSource().getUI().ifPresent(ui -> {
                ui.getChildren().filter(child -> child instanceof Dialog)
                        .findFirst().ifPresent(dialog -> {
                            ((Dialog) dialog).close();
                        });
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

        HorizontalLayout buttonsLayout = new HorizontalLayout(AddButton, closeButton);
        buttonsLayout.setSpacing(true);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(error, titleField, authorField, buttonsLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        Dialog dialog = new Dialog(dialogLayout);
        dialog.setHeaderTitle("Book is currently unavailable!");

        return dialog;
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

        VerticalLayout dialogLayout = new VerticalLayout(titleField, authorField, returnDateField, buttonsLayout);
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

        Book book = bookRepository.findByTitle(title);

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

    public boolean addToWishlist(Integer userId, Integer bookId) {

        String query = "INSERT INTO wishlist (user_id, book_id) VALUES (?, ?)";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setInt(2, bookId);

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addBookdialog() {
        TextField titleField = new TextField("Book Title");
        TextField authorField = new TextField("Author");
        TextField publisherField = new TextField("Publisher");
        TextArea descriptionField = new TextArea("description");

        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>("Categories");
        List<String> categories = getCategoriesList();
        comboBox.setItems(categories);

        NumberField quantityField = new NumberField("Quantity");
        Button AddButton = new Button("Add book");
        Button closeButton = new Button("Cancel");

        AddButton.addClickListener(event-> {
            String title = titleField.getValue();
            String author = authorField.getValue();
            String publisher = publisherField.getValue();
            String description = descriptionField.getValue();
            Integer quantity = quantityField.getValue().intValue();
            Set<String> selectedCategories = comboBox.getSelectedItems();
            addNewBookToDatabase(title, author, publisher, description, selectedCategories, quantity);
        });

        closeButton.addClickListener(event -> {
            event.getSource().getUI().ifPresent(ui -> {
                ui.getChildren().filter(child -> child instanceof Dialog)
                        .findFirst().ifPresent(dialog -> {
                            ((Dialog) dialog).close();
                        });
            });
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout(AddButton, closeButton);
        buttonsLayout.setSpacing(true);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(titleField, authorField, publisherField, descriptionField, comboBox, quantityField, buttonsLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        Dialog dialog = new Dialog(dialogLayout);
        dialog.setHeaderTitle("ADD BOOK");

        dialog.open();
    }

    private void addNewBookToDatabase(String title, String author, String publisher, String description, Set<String> selectedCategories, Integer quantity) {
        try (Connection connection = DatabaseHelper.getConnection()) {
            connection.setAutoCommit(false);

            String checkBookSql = "SELECT COUNT(*) FROM books WHERE title = ?";
            try (PreparedStatement checkBookStmt = connection.prepareStatement(checkBookSql)) {
                checkBookStmt.setString(1, title);
                ResultSet bookCheckResult = checkBookStmt.executeQuery();
                bookCheckResult.next();
                if (bookCheckResult.getInt(1) > 0) {
                    Notification.show("Book title already exists!", 5000, Notification.Position.MIDDLE);
                    return;
                }
            }

            String insertBookSql = "INSERT INTO books (title, publisher, description, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement bookStmt = connection.prepareStatement(insertBookSql, Statement.RETURN_GENERATED_KEYS)) {
                bookStmt.setString(1, title);
                bookStmt.setString(2, publisher);
                bookStmt.setString(3, description);
                bookStmt.setInt(4, quantity);
                bookStmt.executeUpdate();

                ResultSet generatedKeys = bookStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int bookId = generatedKeys.getInt(1);

                    int authorId;
                    String getAuthorIdSql = "SELECT author_id FROM authors WHERE author_name = ?";
                    try (PreparedStatement getAuthorIdStmt = connection.prepareStatement(getAuthorIdSql)) {
                        getAuthorIdStmt.setString(1, author);
                        ResultSet authorResult = getAuthorIdStmt.executeQuery();
                        if (authorResult.next()) {
                            authorId = authorResult.getInt("author_id");
                        } else {
                            String insertAuthorSql = "INSERT INTO authors (author_name) VALUES (?)";
                            try (PreparedStatement authorStmt = connection.prepareStatement(insertAuthorSql, Statement.RETURN_GENERATED_KEYS)) {
                                authorStmt.setString(1, author);
                                authorStmt.executeUpdate();
                                ResultSet authorKeys = authorStmt.getGeneratedKeys();
                                if (authorKeys.next()) {
                                    authorId = authorKeys.getInt(1);
                                } else {
                                    throw new SQLException("Failed to insert new author and retrieve authorId.");
                                }
                            }
                        }
                    }

                    String insertBookAuthorSql = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
                    try (PreparedStatement bookAuthorStmt = connection.prepareStatement(insertBookAuthorSql)) {
                        bookAuthorStmt.setInt(1, bookId);
                        bookAuthorStmt.setInt(2, authorId);
                        bookAuthorStmt.executeUpdate();
                    }

                    for (String category : selectedCategories) {
                        String insertCategorySql = "INSERT IGNORE INTO categories (name) VALUES (?)";
                        try (PreparedStatement categoryStmt = connection.prepareStatement(insertCategorySql, Statement.RETURN_GENERATED_KEYS)) {
                            categoryStmt.setString(1, category);
                            categoryStmt.executeUpdate();

                            int categoryId;
                            ResultSet categoryKeys = categoryStmt.getGeneratedKeys();
                            if (categoryKeys.next()) {
                                categoryId = categoryKeys.getInt(1);
                            } else {
                                String getCategoryIdSql = "SELECT id FROM categories WHERE name = ?";
                                try (PreparedStatement getCategoryIdStmt = connection.prepareStatement(getCategoryIdSql)) {
                                    getCategoryIdStmt.setString(1, category);
                                    ResultSet categoryResult = getCategoryIdStmt.executeQuery();
                                    categoryResult.next();
                                    categoryId = categoryResult.getInt("id");
                                }
                            }

                            String insertBookCategorySql = "INSERT INTO book_category (book_id, category_id) VALUES (?, ?)";
                            try (PreparedStatement bookCategoryStmt = connection.prepareStatement(insertBookCategorySql)) {
                                bookCategoryStmt.setInt(1, bookId);
                                bookCategoryStmt.setInt(2, categoryId);
                                bookCategoryStmt.executeUpdate();
                            }
                        }
                    }
                }

                connection.commit();
                Notification.show("Book added successfully!");
            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
                Notification.show("Failed to add book!", 5000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterBooks(Pagination<Book> pagination, TextField titleSearchField, TextField authorSearchField, MultiSelectComboBox<String> categoriesComboBox) {
        String titleSearch = titleSearchField.getValue().trim().toLowerCase();
        String authorSearch = authorSearchField.getValue().trim().toLowerCase();
        Set<String> selectedCategories = categoriesComboBox.getValue();

        List<Book> filteredBooks = books.stream()
                .filter(book -> (titleSearch.isEmpty() || book.getTitle().toLowerCase().contains(titleSearch))
                        && (authorSearch.isEmpty() || book.getAllAuthors().toLowerCase().contains(authorSearch))
                        && (selectedCategories.isEmpty() || selectedCategories.stream().anyMatch(book.getCategoriesString()::contains)))
                .collect(Collectors.toList());

        pagination.setItems(filteredBooks);
        availableBookGrid.setItems(pagination.getCurrentPageItems());
        availableBookGrid.getDataProvider().refreshAll();
    }
}
