package github.axolotl.vaadinremotemanager;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import github.axolotl.vaadinremotemanager.util.ViewUtil;
import github.axolotl.vaadinremotemanager.view.*;


/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/14 15:48
 */
//用于定义应用程序的布局
@Layout
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

        nav.addItem(
                new SideNavItem("仪表盘", DashboardView.class, VaadinIcon.DASHBOARD.create()),
                new SideNavItem("所有进程管理", ProcessView.class, VaadinIcon.OFFICE.create()),
                new SideNavItem("终端管理", TerminalManagerView.class, VaadinIcon.TERMINAL.create()),
                new SideNavItem("模板管理", TemplateView.class, VaadinIcon.PAPERCLIP.create()),
                new SideNavItem("设置", SettingView.class, VaadinIcon.HEADSET.create()),
                new SideNavItem("关于我们", AboutUsView.class, VaadinIcon.ABACUS.create())
        );
        return nav;
    }


}
