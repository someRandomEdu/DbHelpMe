package com.somerandomdev.dbhelpme.views;

import com.somerandomdev.dbhelpme.RentData;
import com.somerandomdev.dbhelpme.ReturnData;
import com.somerandomdev.dbhelpme.ReturnDataRepository;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("/app/returned")
public class Returned extends VerticalLayout {
    private ReturnDataRepository returnDataRepository;
    private Grid<ReturnData> grid;

    public Returned (ReturnDataRepository returnDataRepository) {
        Div titleLabel = new Div(new Text("Returned List"));
        titleLabel.addClassName("title-container");
        titleLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "blue")
                .set("text-align", "center")
                .set("margin-bottom", "20px");

        grid = new Grid<>(ReturnData.class);
        grid.setColumns("bookId", "borrowDate", "returnDate");
        List<ReturnData> data = returnDataRepository.findAll();

        grid.setItems(data);
        setAlignItems(Alignment.CENTER);
        add(titleLabel, grid);
        setSizeFull();
        grid.getElement().getStyle().set("border-collapse", "collapse");
        grid.getElement().getStyle().set("width", "100%");

        grid.getElement().getStyle().set("border", "1px solid black");  // Kẻ viền cho grid
        grid.getElement().getStyle().set("border-spacing", "0");

        grid.getElement().getChildren().forEach(child -> {
            if (child.getTag().equals("vaadin-grid-cell-content")) {
                child.getStyle().set("border", "1px solid black");
            }
        });

        this.returnDataRepository = returnDataRepository;
    }
}
