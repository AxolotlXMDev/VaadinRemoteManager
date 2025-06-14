package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;


/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/14 15:48
 */
@Layout
public class MainView extends AppLayout {

    public MainView() {
//

        DrawerToggle toggle = new DrawerToggle();
        H3 title = new H3("VaadinRemoteManager");
//        title.getStyle().set("font-size", "var(--lumo-font-size-s)")
//                .set("margin", "0");

        SideNav nav = createSideNav();

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

//        VerticalLayout verticalLayout = new VerticalLayout();
//        verticalLayout.add(new H3("VaadinRemoteManager"));
//        setContent(verticalLayout);

        addToDrawer(scroller);
//        addToNavbar(toggle, title);

        this.addToDrawer();


    }

    private static SideNav createSideNav() {
        SideNav nav = new SideNav();

        SideNavItem dashboardLink = new SideNavItem("Dashboard",
                DashboardView.class, VaadinIcon.DASHBOARD.create());
        SideNavItem vaadinLink = new SideNavItem("Vaadin website",
                "https://vaadin.com", VaadinIcon.VAADIN_H.create());


        nav.addItem(dashboardLink,
//                inboxLink,
//                calendarLink,
//                settingsLink,
                vaadinLink);
        return nav;
    }


}
