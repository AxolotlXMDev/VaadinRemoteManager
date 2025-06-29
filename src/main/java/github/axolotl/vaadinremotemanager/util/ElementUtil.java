package github.axolotl.vaadinremotemanager.util;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/21 17:13
 */
public class ElementUtil {
    /**
     * 设置Span组件的文本允许换行
     * @param span Span组件
     */
    public static Span allowNewLine(Span span){
        span.setWhiteSpace(HasText.WhiteSpace.PRE); // 设置文本允许换行
        return span;
    }
    public static void addDrawerToggle(HasComponents hasComponents){
        HorizontalLayout horizontalLayout = ViewUtil.getDrawerToggle();
        hasComponents.add(horizontalLayout);
    }
}
