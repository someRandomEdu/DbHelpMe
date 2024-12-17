package library.views;

import com.vaadin.flow.router.Route;

@Route(value = "booklist", layout = MainView.class)
public class BookListView {
    public static String getRoute() {
        return "/booklist";
    }
}
