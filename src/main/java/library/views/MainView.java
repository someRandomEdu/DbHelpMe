package library.views;

import com.sun.jna.platform.win32.Advapi32Util;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import library.AppController;
import library.Account;
import library.entity.CurrentUser;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import library.entity.CurrentUser;

import static library.views.LoginView.getUserName;

@Route("main")
public class MainView extends AppLayout implements RouterLayout {
    public MainView(AppController appController) {
        DrawerToggle toggle = new DrawerToggle();

        H1 appTitle = new H1("Menu");
        appTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("line-height", "var(--lumo-size-l)")
                .set("margin", "0 var(--lumo-space-m)");

        SideNav nav = getPrimaryNavigation();

        H2 viewTitle = new H2("My Library");
        viewTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        HorizontalLayout wrapper = new HorizontalLayout(toggle, viewTitle);
        wrapper.setAlignItems(FlexComponent.Alignment.CENTER);
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

        sideNav.addItem(new SideNavItem("Dashboard", "/app/account/" + username,
                VaadinIcon.DASHBOARD.create()));
        sideNav.addItem(new SideNavItem("Books", "/booklist",
                VaadinIcon.OPEN_BOOK.create()));
//        sideNav.addItem(new SideNavItem("Rent books", "/rentbook",
//                VaadinIcon.BOOK.create()));
//        sideNav.addItem(new SideNavItem("Return books", "/returnbook",
//                VaadinIcon.ARROW_FORWARD.create()));
        sideNav.addItem(new SideNavItem("Borrowed List", "/borrowedTab",
                VaadinIcon.LIST_OL.create()));
        sideNav.addItem(new SideNavItem("Account", "",
                VaadinIcon.USER.create()));
        sideNav.addItem(new SideNavItem("Feedback", "/feedback",
                VaadinIcon.SHARE.create()));
        sideNav.addItem(new SideNavItem("Log out", "",
                VaadinIcon.SIGN_OUT_ALT.create()));

        return sideNav;
    }
}
