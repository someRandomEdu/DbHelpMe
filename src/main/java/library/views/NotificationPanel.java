package library.views;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.router.Route;
import org.springframework.stereotype.Component;

@Route(value = "notification", layout = MainView.class)
@Component
public class NotificationPanel extends Div {
    public NotificationPanel() {}

    public Popover getPopover(Button button) {
        Popover popover = new Popover();
        popover.setTarget(button);
        popover.setWidth("300px");
        popover.addThemeVariants(PopoverVariant.ARROW,
                PopoverVariant.LUMO_NO_PADDING);
        popover.setPosition(PopoverPosition.BOTTOM);
        popover.setAriaLabelledBy("notifications-heading");

        MessageList unreadList = new MessageList();

        MessageList allList = new MessageList();

        MessageListItem message1 = new MessageListItem("A");

        MessageListItem message2 = new MessageListItem("B");

        MessageListItem message3 = new MessageListItem("C");

        MessageListItem message4 = new MessageListItem("D");

        unreadList.setItems(Arrays.asList(message1, message2, message3));
        allList.setItems(Arrays.asList(message1, message2, message3, message4));

        TabSheet tabSheet = new TabSheet();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_SMALL, TabSheetVariant.LUMO_NO_PADDING);
        tabSheet.addClassName("notifications");

        Div unreadContent = new Div();
        unreadContent.add(unreadList);

        tabSheet.add("Unread", unreadContent);
        tabSheet.add("All", new Div(allList));

        H4 heading = new H4("Notifications");
        heading.setId("notifications-heading");
        heading.getStyle().set("margin", "0");

        Button markRead = new Button("Mark all read", (e) -> {
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
}
