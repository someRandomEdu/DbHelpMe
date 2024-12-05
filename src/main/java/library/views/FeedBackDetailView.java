package library.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import library.entity.Feedback;
import library.helper.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Route(value = "feedbackdetail/:feedbackId", layout = MainView.class)
public class FeedBackDetailView extends VerticalLayout implements BeforeEnterObserver {
    private TextField titleField;
    private TextArea contentField;
    private Button handleButton;
    private NativeLabel errorLabel;

    private int feedbackId;
    private String currentStatus;

    public FeedBackDetailView() {
        errorLabel = new NativeLabel();
        Div titleLabel = new Div(new Text("Feedback Detail"));
        titleLabel.addClassName("title-container");
        titleLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "blue")
                .set("text-align", "center")
                .set("margin-bottom", "20px");

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("100%");
        formLayout.setMaxWidth("600px");
        formLayout.setSpacing(true);
        formLayout.setPadding(false);

        titleField = new TextField("Title");
        titleField.setWidth("100%");
        titleField.setPlaceholder("Enter the title...");
        titleField.setReadOnly(true);

        contentField = new TextArea("Content");
        contentField.setWidth("100%");
        contentField.setHeight("200px");
        contentField.setPlaceholder("Enter your feedback...");
        contentField.setReadOnly(true);

        formLayout.add(titleField, contentField);

        handleButton = new Button("Handle", event -> handleFeedback());
        handleButton.addClassName("handle-button");
        handleButton.getStyle().set("background-color", "green").set("color", "white");

        HorizontalLayout buttonLayout = new HorizontalLayout(handleButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        setAlignItems(Alignment.CENTER);
        add(titleLabel, formLayout, buttonLayout, errorLabel);
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        String feedbackIdStr = parameters.get("feedbackId").orElse(null);

        if (feedbackIdStr != null) {
            feedbackId = Integer.parseInt(feedbackIdStr);
            loadFeedbackDetails(feedbackId);
        } else {
            errorLabel.setText("Invalid feedback ID.");
            errorLabel.getStyle().set("color", "red");
        }
    }

    private void loadFeedbackDetails(int feedbackId) {
        String query = "SELECT * FROM feedbacks WHERE feedback_id = ?";
        DatabaseHelper.connectToDatabase();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, feedbackId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                titleField.setValue(rs.getString("title"));
                contentField.setValue(rs.getString("content"));
                currentStatus = rs.getString("status");
                updateButtonText();
            } else {
                errorLabel.setText("Feedback not found.");
                errorLabel.getStyle().set("color", "red");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleFeedback() {
        String newStatus = currentStatus.equals("Pending") ? "Handled" : "Pending";
        String query = "UPDATE feedbacks SET status = ? WHERE feedback_id = ?";

        DatabaseHelper.connectToDatabase();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, feedbackId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                currentStatus = newStatus;
                updateButtonText();
            } else {
                errorLabel.setText("Error updating feedback status.");
                errorLabel.getStyle().set("color", "red");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonText() {
        if (currentStatus.equals("Pending")) {
            handleButton.setText("Mark as Handled");
            handleButton.getStyle().set("background-color", "green");
        } else {
            handleButton.setText("Mark as Pending");
            handleButton.getStyle().set("background-color", "orange");
        }
    }
}
