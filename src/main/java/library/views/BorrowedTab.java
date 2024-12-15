package library.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.function.SerializableSupplier;
import library.*;
import org.checkerframework.checker.units.qual.N;

@Route(value = "borrowedTab", layout = MainView.class)
public class BorrowedTab extends VerticalLayout {
    private final RentDataRepository rentDataRepository;
    private NotificationsRepository notificationsRepository;
    private ReturnDataRepository returnDataRepository;
    private final AppController appController;
    private final BookRepository bookRepository;
    private final WishListRepository wishListRepository;
    private final NotificationsService notificationsService;

    public BorrowedTab(AppController appController, RentDataRepository rentDataRepository, ReturnDataRepository returnDataRepository, BookRepository bookRepository, WishListRepository wishListRepository, NotificationsRepository notificationsRepository, NotificationsService notificationsService) {
        this.appController = appController;
        this.rentDataRepository = rentDataRepository;
        this.returnDataRepository = returnDataRepository;
        this.bookRepository = bookRepository;
        this.wishListRepository = wishListRepository;
        this.notificationsRepository = notificationsRepository;
        this.notificationsService = notificationsService;
        Tab borrowedTab = new Tab("Borrowed");
        Tab returnedTab = new Tab("Returned");

        Tabs tabSheet = new Tabs(borrowedTab, returnedTab);

        tabSheet.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);

        LazyComponent borrowedTabContent = new LazyComponent(() -> new Borrowed(appController, bookRepository, rentDataRepository, wishListRepository, notificationsRepository, notificationsService));
        LazyComponent returnedTabContent = new LazyComponent(() -> new Returned(returnDataRepository));

        tabSheet.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == borrowedTab) {
                removeAll();
                add(tabSheet, borrowedTabContent);
                setAlignItems(Alignment.CENTER);
                setSizeFull();
            } else if (event.getSelectedTab() == returnedTab) {
                removeAll();
                add(tabSheet, returnedTabContent);
                setAlignItems(Alignment.CENTER);
                setSizeFull();
            }
        });

        add(tabSheet, borrowedTabContent);
        setAlignItems(Alignment.CENTER);
        setSizeFull();

        tabSheet.setWidthFull();
        tabSheet.setHeight("50px");
        borrowedTabContent.setSizeFull();
        returnedTabContent.setSizeFull();
        this.notificationsRepository = notificationsRepository;
    }
}

class LazyComponent extends Div {
    public LazyComponent(SerializableSupplier<? extends Component> supplier) {
        addAttachListener(e -> {
            if (getElement().getChildCount() == 0) {
                add(supplier.get());
            }
        });
    }
}
