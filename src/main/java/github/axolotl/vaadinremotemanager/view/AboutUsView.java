package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/21 17:16
 */
@Route("/about-us")
public class AboutUsView extends VerticalLayout {
    public AboutUsView() {
        setPadding(true);
        setSpacing(true);
        setSizeFull();


        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(
                new com.vaadin.flow.component.html.H1("关于我们"),
                new com.vaadin.flow.component.html.H3("一个基于Vaadin的远程管理系统"),
                new com.vaadin.flow.component.html.H3("原作者: AxolotlXM")
        );
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);


        add(verticalLayout);
    }
}
