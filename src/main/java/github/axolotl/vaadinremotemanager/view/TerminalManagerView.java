package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dczx.axolotl.terminal.SimpleTerminal;
import dczx.axolotl.util.DateUtil;
import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import github.axolotl.vaadinremotemanager.service.TerminalInstanceService;
import github.axolotl.vaadinremotemanager.service.TerminalTemplateService;
import github.axolotl.vaadinremotemanager.util.NotificationUtil;
import jakarta.annotation.security.RolesAllowed;

import java.util.Date;

import static github.axolotl.vaadinremotemanager.util.ViewUtil.reloadPages;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 17:38
 */
@PageTitle("终端管理")
@RolesAllowed("ADMIN")
@Route("/terminal-manager")
public class TerminalManagerView extends VerticalLayout {
    public TerminalManagerView() {
        setSizeFull();
        createView();

    }

    private void createView() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.focus();

        Button createButton = new Button("从模板中创建", VaadinIcon.PLUS.create());
        createButton.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle("终端信息");

            // 创建关闭按钮
            Button closeButton = new Button(new Icon("lumo", "cross"), e -> dialog.close());


            TextField nameField = new TextField("名称");
            nameField.setWidthFull();

            Select<TerminalTemplate> templateSelect = new Select<>();
            templateSelect.setLabel("选择模板");
            templateSelect.setWidth("100%");
            templateSelect.setItems(TerminalTemplateService.getTemplateList());
            templateSelect.addValueChangeListener(selectEvent -> {
                nameField.setValue(selectEvent.getValue().getName());
            });


            // 保存按钮
            Button sureCreateButton = new Button("创建", e -> {
                TerminalInstance instance = new TerminalInstance(templateSelect.getValue());
                instance.setName(nameField.getValue());

                TerminalInstanceService.startTerminalInstance(instance);

                NotificationUtil.showNotificationSuccess("已创建新终端实例");

                dialog.close();
                reloadPages();
            });

            sureCreateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            // 使用 VerticalLayout 布局表单内容
            VerticalLayout formLayout = new VerticalLayout(
                    templateSelect,
                    nameField,
                    sureCreateButton
            );
            formLayout.setAlignSelf(FlexComponent.Alignment.END, sureCreateButton); // 右对齐 [[8]]
            formLayout.setSpacing(false);

            // 设置对话框内容
            dialog.add(formLayout);
            dialog.setWidth("70%"); // 设置宽度为视口的70%
            dialog.getHeader().add(closeButton);

            // 打开对话框
            dialog.open();
        });
        horizontalLayout.add(searchField, createButton);
        horizontalLayout.setWidthFull();


        Grid<TerminalInstance> instanceGrid = new Grid<>();
        GridListDataView<TerminalInstance> terminalInstanceGridListDataView = instanceGrid.setItems(TerminalInstanceService.getInstanceMap().values());

        instanceGrid.addColumn(TerminalInstance::getName).setHeader("名称").setSortable(true).setWidth("15%");

        instanceGrid.addColumn(instance -> DateUtil.formatDate(new Date(instance.getStartTime()), "MM-dd hh:mm:ss"))
                .setHeader("创建时间").setKey("time").setSortable(true).setWidth("8%");
        instanceGrid.sort(GridSortOrder.desc(instanceGrid.getColumnByKey("time")).build());

        instanceGrid.addColumn(instance -> instance.getTemplate().getName()).setHeader("父模板").setSortable(true).setWidth("6%");

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
        }).setHeader("运行状态").setSortable(true).setWidth("4%");

        instanceGrid.addComponentColumn(instance -> {
            Button jumpButton = new Button("跳转", VaadinIcon.PLAY_CIRCLE.create());
            jumpButton.addClickListener(event -> {
                TerminalInstanceService.jumpToTerminalById(instance.getId());
            });
            Button renameButton = new Button("改名", VaadinIcon.COG_O.create());
            renameButton.addClickListener(event -> {
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

                    NotificationUtil.showNotificationSuccess("名称已更新");
                    dialog.close();
                    reloadPages();
                });

                saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                // 使用 VerticalLayout 布局表单内容
                VerticalLayout formLayout = new VerticalLayout(
                        nameField,
                        saveButton
                );
                formLayout.setAlignSelf(Alignment.END, saveButton); // 右对齐 [[8]]
                formLayout.setSpacing(false);

                // 设置对话框内容
                dialog.add(formLayout);
                dialog.setWidth("70%"); // 设置宽度为视口的70%
                dialog.getHeader().add(closeButton);

                // 打开对话框
                dialog.open();
            });

            //按照模板执行
            Button shallowCopyButton = new Button("浅复制", VaadinIcon.COPY.create());
            shallowCopyButton.addClickListener(event -> {
                TerminalInstance copyInstance = new TerminalInstance(instance.getTemplate());
                copyInstance.setName(instance.getName() + " - 浅副本");
                copyInstance.start(true);

                TerminalInstanceService.putTerminalInstance(copyInstance);
                NotificationUtil.showNotificationSuccess("已创建新终端实例 ");
                terminalInstanceGridListDataView.addItem(copyInstance);
                terminalInstanceGridListDataView.refreshAll();

            });
            //复制一个终端 按照历史执行
            Button deepCopyButton = new Button("深复制", VaadinIcon.COPY.create());
            deepCopyButton.addClickListener(event -> {
                TerminalInstance copyInstance = new TerminalInstance(instance.getTemplate());
                copyInstance.setName(instance.getName() + " - 深副本");
                copyInstance.start(false);
                instance.getHistory()
                        .stream()
                        .filter(historyEntry -> historyEntry.type == SimpleTerminal.HistoryEntry.Type.INPUT)
                        .forEach(historyEntry -> {
                            try {
                                copyInstance.getTerminal().execute(historyEntry.content);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
//                reloadPages();
                TerminalInstanceService.putTerminalInstance(copyInstance);
                NotificationUtil.showNotificationSuccess("已创建新终端实例");
                terminalInstanceGridListDataView.addItem(copyInstance);
                terminalInstanceGridListDataView.refreshAll();
            });

            Button killButton = new Button("停止", VaadinIcon.WARNING.create());
            killButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            killButton.addClickListener(event -> {
                instance.getTerminal().stopForcibly();
                // 获取数据视图并刷新该项
                reFreshGridData(instance, instanceGrid);
            });

            Button removeButton = new Button("移除", VaadinIcon.MINUS_CIRCLE_O.create());
            removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            removeButton.addClickListener(event -> {
                instance.getTerminal().stopForcibly();
                NotificationUtil.showNotificationSuccess("已移除终端实例");
                TerminalInstanceService.removeTerminalInstance(instance.getId());
                terminalInstanceGridListDataView.removeItem(instance);
            });

            jumpButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            renameButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            shallowCopyButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            deepCopyButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            killButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            removeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            return new HorizontalLayout(jumpButton, killButton, removeButton, shallowCopyButton, deepCopyButton, renameButton);
        }).setHeader("操作").setWidth("40%");

        instanceGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        instanceGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);


        addFilter(searchField, terminalInstanceGridListDataView);
        add(horizontalLayout);
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
