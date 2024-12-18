package library;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public final class Helpers {
    private Helpers() {

    }

    public static Notification showNotification(String text, NotificationVariant variant) {
        var result = new Notification();
        result.addThemeVariants(variant);
        var closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addClickListener(event -> result.close());
        var layout = new HorizontalLayout(new NativeLabel(text), closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        result.add(layout);
        result.open();
        return result;
    }

    public static Notification showNotification(String text, int duration, NotificationVariant variant) {
        var result = showNotification(text, variant);
        result.setDuration(duration);
        return result;
    }
}
