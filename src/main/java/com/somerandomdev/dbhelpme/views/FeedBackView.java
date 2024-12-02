package com.somerandomdev.dbhelpme.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Text;

@Route(value = "feedback", layout = MainView.class)
@CssImport("./styles/feedback-layout.css")
public class FeedBackView extends VerticalLayout {
    private TextField titleField;
    private TextArea contentField;
    private Button sendButton;

    public FeedBackView() {
        // Tiêu đề
        Div titleLabel = new Div(new Text("Send Feedback"));
        titleLabel.addClassName("title-container");
        titleLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "blue")
                .set("text-align", "center")
                .set("margin-bottom", "20px");

        // Layout chính cho các trường
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("100%");
        formLayout.setMaxWidth("600px");
        formLayout.setSpacing(true); // Thêm khoảng cách giữa các thành phần
        formLayout.setPadding(false); // Không thêm padding mặc định

        // Trường tiêu đề
        titleField = new TextField("Title");
        titleField.setWidth("100%");
        titleField.setPlaceholder("Enter the title...");
        titleField.addClassName("title-field");

        // Trường nội dung
        contentField = new TextArea("Content");
        contentField.setWidth("100%");
        contentField.setHeight("200px");
        contentField.setPlaceholder("Enter your feedback...");
        contentField.addClassName("content-container");

        // Thêm các trường vào layout
        formLayout.add(titleField, contentField);

        // Nút gửi
        sendButton = new Button("Send", event -> {
            System.out.println("Feedback Sent: " + titleField.getValue() + " - " + contentField.getValue());
        });
        sendButton.addClassName("send-button");
        sendButton.getStyle()
                .set("background-color", "blue")
                .set("color", "white")
                .set("border-radius", "5px");

        // Layout cho nút
        HorizontalLayout buttonLayout = new HorizontalLayout(sendButton);
        buttonLayout.addClassName("send-button-container");
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Thêm vào giao diện chính
        setAlignItems(Alignment.CENTER);
        add(titleLabel, formLayout, buttonLayout);

        // Đặt kích thước toàn màn hình
        setSizeFull();
    }

    private void updateFeedBacktoDatabase() {

    }
}
