package github.axolotl.vaadinremotemanager.util;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/15 1:14
 */
public class ViewUtil {
    /**
     * 获取抽屉切换按钮
     */
    public static HorizontalLayout getDrawerToggle() {
        DrawerToggle drawerToggle = new DrawerToggle();
        drawerToggle.getStyle().setHeight("var(--lumo-size-s)")
                .setWidth("var(--lumo-size-s)");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        horizontalLayout.add(drawerToggle);
        horizontalLayout.add(new Text("VaadinRemoteManager"));
        return horizontalLayout;
    }
}
