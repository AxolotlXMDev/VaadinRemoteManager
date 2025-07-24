package github.axolotl.vaadinremotemanager.util;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Collections;
import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/15 1:14
 */
public class ViewUtil {

    /**
     * 刷新当前页面
     */
    public static void reloadPages() {
        UI.getCurrent().getPage().reload();
    }

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

    /**
     * 创建一个数据网格
     *
     * @param data 数据列表，每个元素是一个 List<Object>
     * @return Grid<List < Object>> 对象
     */
    public static Grid<List<Object>> createDataGrid(List<List<Object>> data, List<String> flexRatios) {
        Grid<List<Object>> grid = new Grid<>();
        grid.setItems(data);

        int size = data.get(0).size();
        for (int i = 0; i < size; i++) {
            int finalI = i;
            grid.addColumn(new ComponentRenderer<>(list -> {
                Span label = new Span(String.valueOf(list.get(finalI)));
                label.getStyle().set("white-space", "normal");
                label.getStyle().set("word-wrap", "break-word");
                return label;
            })).setWidth(String.valueOf(flexRatios.get(finalI)));
        }
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        return grid;
    }

    public static Grid<List<Object>> createDataGrid(List<List<Object>> data) {
        return createDataGrid(data, Collections.nCopies(data.get(0).size(), "10%"));
    }
}
