package library.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import library.AppController;
import library.BookRepository;
import library.NotificationsRepository;
import library.entity.CurrentUser;
import com.vaadin.flow.component.button.Button;


import java.awt.*;

import static library.views.LoginView.getUserName;

@Route("main")
public class MainView extends AppLayout implements RouterLayout {
    private final BookRepository bookRepository;
    private NotificationPanel notificationPanel;
    private NotificationsRepository notificationsRepository;
    public MainView(AppController appController, NotificationPanel notificationPanel, NotificationsRepository notificationsRepository, BookRepository bookRepository) {
        this.notificationsRepository = notificationsRepository;
        this.notificationPanel = notificationPanel;
        this.bookRepository = bookRepository;
        DrawerToggle toggle = new DrawerToggle();

        H1 appTitle = new H1("Menu");

        appTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("line-height", "var(--lumo-size-l)")
                .set("margin", "0 var(--lumo-space-m)");

        SideNav nav;

        if (CurrentUser.isAdmin()) {
            nav = getAdminNavigation();
        } else {
            nav = getPrimaryNavigation();
        }

        nav.addItem(new SideNavItem("Log out", "",
                VaadinIcon.SIGN_OUT_ALT.create()));

        H2 viewTitle = new H2("My Library");
        viewTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Button notiButton = new Button(LumoIcon.BELL.create());
        notiButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        notiButton.setAriaLabel("Notifications");

        Popover popover = notificationPanel.getPopover(notiButton, CurrentUser.getId(), notificationsRepository, appController, bookRepository);
        notiButton.addClickListener(event -> {
            popover.open();
        });

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        HorizontalLayout wrapper = new HorizontalLayout(toggle, viewTitle, notiButton);
        wrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        notiButton.getStyle().set("margin-right", "var(--lumo-space-m)");
        wrapper.setSpacing(false);

        VerticalLayout viewHeader = new VerticalLayout(wrapper);
        viewHeader.setPadding(false);
        viewHeader.setSpacing(false);

        addToDrawer(appTitle, scroller);
        addToNavbar(viewHeader);

        setPrimarySection(Section.DRAWER);
    }

    private SideNav getPrimaryNavigation() {
        // Tạo một SideNav mới và thêm các phần tử vào đây
        SideNav sideNav = new SideNav();
        String username = getUserName();
        UI.getCurrent().navigate("app/account/" + username);

        sideNav.addItem(new SideNavItem("Dashboard", AccountView.getRoute(username),
                VaadinIcon.DASHBOARD.create()));

        sideNav.addItem(new SideNavItem("Books", BookListView.getRoute(),
                VaadinIcon.OPEN_BOOK.create()));

//        sideNav.addItem(new SideNavItem("Rent books", "/rentbook",
//                VaadinIcon.BOOK.create()));
//        sideNav.addItem(new SideNavItem("Return books", "/returnbook",
//                VaadinIcon.ARROW_FORWARD.create()));

        sideNav.addItem(new SideNavItem("Borrowed List", BorrowedTab.getRoute(),
                VaadinIcon.LIST_OL.create()));

        sideNav.addItem(new SideNavItem("WishList", WishListView.getRoute(),
                VaadinIcon.BOOKMARK_O.create()));

        sideNav.addItem(new SideNavItem("Account", ProfileView.getRoute(),
                VaadinIcon.USER.create()));

        sideNav.addItem(new SideNavItem("Feedback", FeedBackView.getRoute(),
                VaadinIcon.SHARE.create()));

        return sideNav;
    }

    private SideNav getAdminNavigation() {
        SideNav sideNav = getPrimaryNavigation();

        sideNav.addItem(new SideNavItem("Show Feedback", "show_feedback",
                VaadinIcon.BROWSER.create()));

        return sideNav;
    }
}
