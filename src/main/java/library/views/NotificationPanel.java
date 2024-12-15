package library.views;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.router.Route;
import library.AppController;
import library.Notifications;
import library.NotificationsRepository;
import org.springframework.stereotype.Component;

@Route(value = "notification", layout = MainView.class)
@Component
public class NotificationPanel extends Div {
    private NotificationsRepository notificationsRepository;
    public NotificationPanel() {}
    private AppController appController;

    public Popover getPopover(Button button, Integer userId, NotificationsRepository notificationsRepository, AppController appController) {
        this.notificationsRepository = notificationsRepository;
        this.appController = appController;
        Popover popover = new Popover();
        popover.setTarget(button);
        popover.setWidth("300px");
        popover.addThemeVariants(PopoverVariant.ARROW,
                PopoverVariant.LUMO_NO_PADDING);
        popover.setPosition(PopoverPosition.BOTTOM);
        popover.setAriaLabelledBy("notifications-heading");

        List<Notifications> unreadList = notificationsRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "unread");
        List<Notifications> allList = notificationsRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Tạo danh sách thông báo có thể click
        Div unreadContent = createClickableNotificationList(unreadList);
        Div allContent = createClickableNotificationList(allList);

        TabSheet tabSheet = new TabSheet();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_SMALL, TabSheetVariant.LUMO_NO_PADDING);
        tabSheet.addClassName("notifications");

        tabSheet.add("Unread", unreadContent);
        tabSheet.add("All", allContent);

        H4 heading = new H4("Notifications");
        heading.setId("notifications-heading");
        heading.getStyle().set("margin", "0");

        Button markRead = new Button("Mark all read", (e) -> {
            final String URL = "jdbc:mysql://localhost:3306/mydatabase";
            final String USERNAME = "root";
            final String PASSWORD = "130405";

            try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                String sql = "UPDATE notifications SET status = 'read' WHERE status = 'unread'";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("All notifications marked as read.");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            unreadContent.removeAll();
            unreadContent.add(new Div("No new notifications"){{
                this.addClassName("no-notifications-msg");
            }});
        });
        markRead.getStyle().set("margin", "0 0 0 auto");
        markRead.addThemeVariants(ButtonVariant.LUMO_SMALL);

        HorizontalLayout layout = new HorizontalLayout(heading, markRead);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.getStyle().set("padding",
                "var(--lumo-space-m) var(--lumo-space-m) var(--lumo-space-xs)");

        popover.add(layout, tabSheet);

        return popover;
    }

    private Instant minutesAgo(int minutes) {
        return LocalDateTime.now(ZoneOffset.UTC).minusMinutes(minutes)
                .toInstant(ZoneOffset.UTC);
    }

    private Div createClickableNotificationList(List<Notifications> notifications) {
        Div container = new Div();

        notifications.forEach(notification -> {
            Div notificationItem = new Div();
            notificationItem.setText(notification.getMessage());
            notificationItem.addClassName("notification-item");

            // Thêm sự kiện click
            notificationItem.addClickListener(event -> {
                showNotificationDialog(notification);
            });

            container.add(notificationItem);
        });

        return container;
    }

    private void showNotificationDialog(Notifications notification) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Chi tiết thông báo");

        Div content = new Div();
        content.setText(notification.getMessage());

        dialog.add(content);

        Button closeButton = new Button("Đóng", e -> dialog.close());
        dialog.getFooter().add(closeButton);

        dialog.open();
    }
}
