package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import dczx.axolotl.util.DateUtil;
import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import github.axolotl.vaadinremotemanager.service.TerminalInstanceService;

import java.util.Date;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 17:38
 */
@Route("/terminal-manager")
public class TerminalManagerView extends VerticalLayout {
    public TerminalManagerView() {
        setSizeFull();
        createView();

    }

    private void createView() {
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.focus();


        Grid<TerminalInstance> instanceGrid = new Grid<>();
        instanceGrid.addColumn(TerminalInstance::getName).setHeader("名称").setSortable(true).setWidth("20%");

        instanceGrid.addColumn(instance -> DateUtil.formatDate(new Date(instance.getStartTime())))
                .setHeader("创建时间").setKey("time").setSortable(true).setWidth("20%");
        instanceGrid.sort(GridSortOrder.desc(instanceGrid.getColumnByKey("time")).build());

        instanceGrid.addColumn(instance -> instance.getTemplate().getName()).setHeader("父模板").setSortable(true).setWidth("20%");

        instanceGrid.addComponentColumn(instance -> {
            Span span;
            if (instance.isRunning()) {
                span = new Span("运行中");
                span.getElement().getThemeList().add("badge success");
            } else {
                span = new Span("停止");
                span.getElement().getThemeList().add("badge error");
            }
            return span;
        }).setHeader("运行状态").setSortable(true).setWidth("12%");

        instanceGrid.addComponentColumn(instance -> {
            Button jumpButton = new Button("跳转", VaadinIcon.PLAY_CIRCLE.create());
            jumpButton.addClickListener(event -> {
                TerminalInstanceService.jumpToTerminalById(instance.getId());
            });
            Button settingButton = new Button("编辑", VaadinIcon.COG_O.create());
            settingButton.addClickListener(event -> {
                Dialog dialog = new Dialog();
                dialog.setHeaderTitle("终端信息");

                // 创建关闭按钮
                Button closeButton = new Button(new Icon("lumo", "cross"), e -> dialog.close());

                // 创建用于编辑的对象字段
                TextField nameField = new TextField("名称");
                nameField.setValue(instance.getName());
                nameField.setWidthFull();

                // 保存按钮
                Button saveButton = new Button("保存", e -> {
                    // 更新对象属性
                    instance.setName(nameField.getValue());

                    Notification notification = new Notification("名称已更新", 3000);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.open();

                    dialog.close();
                });

                saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                // 使用 VerticalLayout 布局表单内容
                VerticalLayout formLayout = new VerticalLayout(
                        nameField,
                        saveButton
                );
                formLayout.setAlignSelf(FlexComponent.Alignment.END, saveButton); // 右对齐 [[8]]
                formLayout.setSpacing(false);

                // 设置对话框内容
                dialog.add(formLayout);
                dialog.setWidth("70%"); // 设置宽度为视口的70%
                dialog.getHeader().add(closeButton);

                // 打开对话框
                dialog.open();
            });

            //复制一个终端 按照历史执行
            Button copyButton = new Button("复制", VaadinIcon.COPY.create());
            copyButton.addClickListener(event -> {

            });

            //从模板启动和模板修改启动
            Button killButton = new Button("停止", VaadinIcon.WARNING.create());
            killButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            killButton.addClickListener(event -> {
                instance.getTerminal().stop();
                // 获取数据视图并刷新该项
                reFreshGridData(instance, instanceGrid);
            });

            jumpButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            settingButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            copyButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            killButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            return new HorizontalLayout(jumpButton, settingButton, copyButton, killButton);
        }).setHeader("操作").setWidth("150%");

        instanceGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        instanceGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);


        GridListDataView<TerminalInstance> terminalInstanceGridListDataView = instanceGrid.setItems(TerminalInstanceService.getInstanceMap().values());


        addFilter(searchField, terminalInstanceGridListDataView);
        add(searchField);
        add(instanceGrid);
    }



    private static void reFreshGridData(TerminalInstance instance, Grid<TerminalInstance> instanceGrid) {
        GridListDataView<TerminalInstance> dataView = instanceGrid.getListDataView();
        if (dataView != null) {
            dataView.refreshItem(instance);
        }
    }


    private void addFilter(TextField searchField, GridListDataView<TerminalInstance> terminalInstanceGridListDataView) {
        searchField.addValueChangeListener(e -> terminalInstanceGridListDataView.refreshAll());
        terminalInstanceGridListDataView.addFilter(instance -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            TerminalTemplate template = instance.getTemplate();

            boolean matchesInstanceName = instance.getName().toLowerCase().contains(searchTerm.toLowerCase());
            boolean matchesTemplateName = template.getName().toLowerCase().contains(searchTerm.toLowerCase());
            boolean matchesTemplateDescription = template.getDescription().toLowerCase().contains(searchTerm.toLowerCase());
            boolean matchesTemplateCommand = template.getCommands().stream()
                    .anyMatch(command -> command.toLowerCase().contains(searchTerm.toLowerCase()));
            return matchesInstanceName || matchesTemplateName || matchesTemplateDescription || matchesTemplateCommand;
        });
    }
}
