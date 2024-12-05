package library.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import library.entity.Feedback;
import library.helper.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/custom-grid-cell.css")
@Route(value = "show_feedback", layout = MainView.class)
public class ShowReportView extends VerticalLayout {

    private ComboBox<String> statusComboBox;
    private Grid<Feedback> tableView;

    public ShowReportView() {
        statusComboBox = new ComboBox<>("Select Status");
        statusComboBox.setItems("All", "Pending", "Handled");
        statusComboBox.setValue("All");
        statusComboBox.addValueChangeListener(event -> updateFeedbackList(event.getValue()));

        tableView = new Grid<>(Feedback.class);

        updateFeedbackList("All");

        tableView.removeAllColumns();
        tableView.addColumn(Feedback::getFeedback_id)
                .setHeader("Feedback ID")
                .setWidth("110px")
                .setFlexGrow(0);
        tableView.addColumn(Feedback::getUser_id)
                .setHeader("User ID")
                .setWidth("80px")
                .setFlexGrow(0);
        tableView.addColumn(Feedback::getTitle)
                .setHeader("Feedback Title")
                .setWidth("250px")
                .setFlexGrow(0);
        tableView.addColumn(Feedback::getContent)
                .setHeader("Feedback Content");

        tableView.getColumns().forEach(column -> column.setClassNameGenerator(item -> "./styles/custom-grid-cell.css"));

        Button showDetails = new Button("Show Detail", event -> {
            Feedback selectedFeedback = tableView.asSingleSelect().getValue();
            if (selectedFeedback != null) {
                UI.getCurrent().navigate("feedbackdetail/" + selectedFeedback.getFeedback_id());
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.add(showDetails);

        add(statusComboBox, tableView, buttonLayout);
    }

    private void updateFeedbackList(String status) {
        List<Feedback> feedbackList = loadFromDatabase(status);
        tableView.setItems(feedbackList);
    }

    private List<Feedback> loadFromDatabase(String status) {
        List<Feedback> ans = new ArrayList<>();
        String query = "SELECT * FROM feedbacks";

        if (!status.equals("All")) {
            query += " WHERE status = ?";
        }

        DatabaseHelper.connectToDatabase();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (!status.equals("All")) {
                stmt.setString(1, status);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ans.add(new Feedback(
                        rs.getInt("feedback_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("content")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ans;
    }
}
