package com.somerandomdev.dbhelpme.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.RouterLayout;

@Route("main")
@CssImport("./styles/mainview-layout.css")
public class MainView extends HorizontalLayout implements RouterLayout {

    private final Div contentArea;

    public MainView() {
        // Tạo sidebar
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.getStyle()
                .set("background-color", "#333")
                .set("width", "250px")
                .set("height", "100vh")
                .set("padding", "20px")
                .set("color", "white")
                .set("overflow", "auto")
                .set("position", "fixed")  // Sidebar cố định
                .set("top", "0")
                .set("left", "0")
                .set("z-index", "1000"); // Đảm bảo sidebar nằm trên cùng

        // Thêm các liên kết cho sidebar
        sidebar.add(new RouterLink("Feedback", FeedBackView.class));
        sidebar.add(new RouterLink("Account", AccountView.class, "admin"));

        // Tạo content area
        contentArea = new Div();
        contentArea.getStyle()
                .set("padding", "20px")
                .set("margin-left", "250px") // Đảm bảo nội dung không bị che khuất bởi sidebar
                .set("background-color", "#f9f9f9")
                .set("height", "100vh") // Giữ độ cao của content area bằng viewport height
                .set("overflow", "auto"); // Đảm bảo nội dung cuộn khi cần

        // Cấu hình layout
        setSizeFull();
        add(sidebar, contentArea);
    }
}
