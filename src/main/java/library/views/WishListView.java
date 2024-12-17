package library.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import library.BookRepository;
import library.WishListRepository;
import library.WishList;
import library.entity.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "wishlist", layout = MainView.class)
public class WishListView extends VerticalLayout {
    private WishListRepository wishListRepository;
    private BookRepository bookRepository;
    private Grid<WishList> grid;

    @Autowired
        public WishListView(WishListRepository wishListRepository, BookRepository bookRepository) {
        this.wishListRepository = wishListRepository;
        this.bookRepository = bookRepository;
        Div titleLabel = new Div(new Text("WishList"));
        titleLabel.addClassName("title-container");
        titleLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "blue")
                .set("text-align", "center")
                .set("margin-bottom", "20px");

        grid = new Grid<>(WishList.class);
        grid.setColumns("bookId", "addedDate");
        List<WishList> data = wishListRepository.findAllByUserId(CurrentUser.getId());

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
    }

    public static String getRoute() {
        return "/wishlist";
    }
}
