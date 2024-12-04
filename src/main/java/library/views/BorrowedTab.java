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
import library.RentDataRepository;
import library.ReturnDataRepository;

@Route(value = "borrowedTab", layout = MainView.class)
public class BorrowedTab extends VerticalLayout {
    private final RentDataRepository rentDataRepository;
    private ReturnDataRepository returnDataRepository;

    public BorrowedTab(RentDataRepository rentDataRepository, ReturnDataRepository returnDataRepository) {
        this.rentDataRepository = rentDataRepository;
        this.returnDataRepository = returnDataRepository;
        Tab borrowedTab = new Tab("Borrowed");
        Tab returnedTab = new Tab("Returned");

        Tabs tabSheet = new Tabs(borrowedTab, returnedTab);

        tabSheet.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);

        LazyComponent borrowedTabContent = new LazyComponent(() -> new Borrowed(rentDataRepository));
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
