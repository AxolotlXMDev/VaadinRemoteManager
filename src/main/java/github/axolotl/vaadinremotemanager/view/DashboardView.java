package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import github.axolotl.vaadinremotemanager.util.ViewUtil;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/14 22:59
 */
@Route("/")
public class DashboardView extends VerticalLayout {
    public DashboardView() {
        HorizontalLayout horizontalLayout = ViewUtil.getDrawerToggle();
        add(horizontalLayout);

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassName("dashboard-view");

        add(new H1("Dashboard View"));
    }


}
