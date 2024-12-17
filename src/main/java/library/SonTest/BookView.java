package library.SonTest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("bookview")
public class BookView extends VerticalLayout {

    private final OpenLibraryService bookService = new OpenLibraryService();
    private final Grid<Books> bookGrid = new Grid<>(Books.class, false);

    public BookView() {
        // Tiêu đề trang
        add(new com.vaadin.flow.component.html.H2("Search Books - OpenLibrary"));

        // Thanh tìm kiếm
        TextField searchField = new TextField("Search by Title");
        searchField.setPlaceholder("Enter book title...");
        Button searchButton = new Button("Search");

        // Lưới hiển thị sách
        bookGrid.addColumn(Books::getTitle).setHeader("Title");
        bookGrid.addColumn(Books::getAuthor).setHeader("Author");
        bookGrid.addComponentColumn(book -> {
            Image coverImage = new Image(book.getCoverUrl(), "Book Cover");
            coverImage.setWidth("100px");
            return coverImage;
        }).setHeader("Cover");

        // Xử lý sự kiện tìm kiếm
        searchButton.addClickListener(e -> {
            String title = searchField.getValue();
            List<Books> books = bookService.searchBooksByTitle(title);
            bookGrid.setItems(books);
        });

        // Thêm các component vào layout
        add(searchField, searchButton, bookGrid);
    }
}