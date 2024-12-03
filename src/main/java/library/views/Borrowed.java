package library.views;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.List;

import library.*;

@Route("/app/borrowed")
public class Borrowed extends VerticalLayout {
    private final RentDataRepository rentDataRepository;
    private Grid<RentData> grid;

    public Borrowed(RentDataRepository rentDataRepository) {
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

        List<RentData> data = rentDataRepository.findAll();

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
