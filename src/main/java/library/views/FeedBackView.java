package library.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Text;
import library.entity.CurrentUser;
import library.helper.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Route(value = "feedback", layout = MainView.class)
public class FeedBackView extends VerticalLayout {
    private TextField titleField;
    private TextArea contentField;
    private Button sendButton;
    private NativeLabel error;

    public FeedBackView() {

        error = new NativeLabel();
        Div titleLabel = new Div(new Text("Send Feedback"));
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
        titleField.addClassName("title-field");


        contentField = new TextArea("Content");
        contentField.setWidth("100%");
        contentField.setHeight("200px");
        contentField.setPlaceholder("Enter your feedback...");
        contentField.addClassName("content-container");


        formLayout.add(titleField, contentField);


        sendButton = new Button("Send", event -> {
            // Kiểm tra xem nội dung có trống không
            if (contentField.getValue().isEmpty()) {
                error.setText("Content cannot be empty!");
                error.getStyle().set("color", "red");
            } else {
                updateFeedBacktoDatabase();
            }
        });
        sendButton.addClassName("send-button");
        sendButton.getStyle()
                .set("background-color", "blue")
                .set("color", "white")
                .set("border-radius", "5px");

        HorizontalLayout buttonLayout = new HorizontalLayout(sendButton);
        buttonLayout.addClassName("send-button-container");
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        setAlignItems(Alignment.CENTER);
        add(titleLabel, formLayout, buttonLayout, error);

        setSizeFull();
    }

    private void updateFeedBacktoDatabase() {
        String query = "insert into feedbacks (user_id, title, content) Values (?,?,?)";
        DatabaseHelper.connectToDatabase();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, CurrentUser.getId());
            stmt.setString(2, titleField.getValue());
            stmt.setString(3, contentField.getValue());

            int roweffected = stmt.executeUpdate();
            if (roweffected >= 1) {
                error.setText("Sent Successfully!");
                error.getStyle().set("color", "green");
                titleField.clear();
                contentField.clear();
            } else {
                error.setText("Please try again!");
                error.getStyle().set("color", "red");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            error.setText("Error occurred while submitting feedback!");
            error.getStyle().set("color", "red");
        }
    }

    public static String getRoute() {
        return "/feedback";
    }
}
