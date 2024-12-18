package library.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * TODO: Remove this and go to {@link LoginView} directly?
 * */
//@Route("")
public class HomeView extends VerticalLayout {
    public HomeView() {
        add(new H1("Welcome to your new application"));
        add(new Paragraph("This is the home view"));

        add(new Button("Sign in", (event) -> {
            UI.getCurrent().navigate("/app/login");
        }));

        add(new Button("Sign up", (event) -> {
            UI.getCurrent().navigate("/app/signup");
        }));
    }
}
