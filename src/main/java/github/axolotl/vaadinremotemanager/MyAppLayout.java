package github.axolotl.vaadinremotemanager;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import github.axolotl.vaadinremotemanager.util.ElementUtil;
import github.axolotl.vaadinremotemanager.util.ViewUtil;
import github.axolotl.vaadinremotemanager.view.AboutUsView;
import github.axolotl.vaadinremotemanager.view.DashboardView;
import github.axolotl.vaadinremotemanager.view.ProcessView;


/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/14 15:48
 */
//用于定义应用程序的布局
@Layout
@CssImport(value = "./styles/main-styles.css")
public class MyAppLayout extends AppLayout {

    public MyAppLayout() {
        VaadinRemoteManagerApplication.setLastAccessTime(System.currentTimeMillis());

        SideNav nav = createSideNav();

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);


        addToNavbar(ViewUtil.getDrawerToggle());
        addToDrawer(scroller);


    }

    /**
     * 创建侧边导航栏
     */
    private static SideNav createSideNav() {
        SideNav nav = new SideNav();

        SideNavItem dashboardLink = new SideNavItem("仪表盘", DashboardView.class, VaadinIcon.DASHBOARD.create());
        SideNavItem processLink = new SideNavItem("进程管理", ProcessView.class, VaadinIcon.DASHBOARD.create());
        SideNavItem vaadinLink = new SideNavItem("关于我们", AboutUsView.class, VaadinIcon.VAADIN_H.create());


        nav.addItem(
                dashboardLink,
                processLink,
//                calendarLink,
//                settingsLink,
                vaadinLink);
        return nav;
    }


}
