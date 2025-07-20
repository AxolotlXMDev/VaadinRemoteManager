package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
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
import github.axolotl.vaadinremotemanager.entity.ProcessEntity;
import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import github.axolotl.vaadinremotemanager.service.TerminalInstanceService;
import github.axolotl.vaadinremotemanager.service.TerminalTemplateService;

import java.util.List;

import static github.axolotl.vaadinremotemanager.util.ViewUtil.reloadPages;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/13 18:32
 */
@Route("/template")
public class TemplateView extends VerticalLayout {

    public TemplateView() {
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.focus();


        Button createTemplateButton = new Button("新建", VaadinIcon.PLUS.create());
        createTemplateButton.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle("新建模板");

            // 创建关闭按钮
            Button closeButton = new Button(new Icon("lumo", "cross"), e -> dialog.close());

            // 创建用于编辑的对象字段
            TextField nameField = new TextField("名称");
            nameField.setValue("默认模板");
            nameField.setWidthFull();

            TextField descriptionField = new TextField("描述");
            descriptionField.setValue("默认描述");
            descriptionField.setWidthFull();

            TextField startCommandField = new TextField("启动命令");
            startCommandField.setValue(TerminalTemplateService.getDefaultStartCommand());
            startCommandField.setWidthFull();

            TextField workingDirectoryField = new TextField("工作目录");
            workingDirectoryField.setValue(System.getProperty("user.dir"));
            workingDirectoryField.setWidthFull();

            TextField commandsField = new TextField("命令");
            commandsField.setPlaceholder("多条命令使用 '<n>' 分隔");
            commandsField.setWidthFull();

            // 保存按钮
            Button saveButton = new Button("保存", e -> {
                TerminalTemplate template = new TerminalTemplate();
                // 更新对象属性
                template.setName(nameField.getValue());
                template.setDescription(descriptionField.getValue());
                template.setStartCommand(startCommandField.getValue());
                template.setWorkingDirectory(workingDirectoryField.getValue());
                template.setCommands(List.of(commandsField.getValue().split("<n>")));

                Notification notification = new Notification("模板新建成功", 3000);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.open();
                dialog.close();

                TerminalTemplateService.addTemplate(template);
                TerminalTemplateService.save();

                reloadPages();
            });

            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            // 使用 VerticalLayout 布局表单内容
            VerticalLayout formLayout = new VerticalLayout(
                    nameField,
                    descriptionField,
                    workingDirectoryField,
                    startCommandField,
                    commandsField,
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


        List<TerminalTemplate> templateList = TerminalTemplateService.getTemplateList();
        Grid<TerminalTemplate> templateGrid = new Grid<>();
        templateGrid.addColumn(TerminalTemplate::getName).setHeader("名称").setSortable(true).setWidth("7%");


        templateGrid.addColumn(TerminalTemplate::getDescription).setHeader("描述").setSortable(true).setWidth("15%");
        templateGrid.addColumn(TerminalTemplate::getCommands).setHeader("命令").setSortable(true).setWidth("22%");
        templateGrid.addColumn(TerminalTemplate::getWorkingDirectory).setHeader("工作目录").setSortable(true).setWidth("22%");

        GridListDataView<TerminalTemplate> terminalTemplateGridListDataView = templateGrid.setItems(templateList);

        // Add kill process button column with red background
        templateGrid.addComponentColumn(template -> {

            Button infoButton = new Button("编辑", VaadinIcon.COG_O.create());
            infoButton.addClickListener(event -> {
                Dialog dialog = new Dialog();
                dialog.setHeaderTitle("模板信息");

                // 创建关闭按钮
                Button closeButton = new Button(new Icon("lumo", "cross"), e -> dialog.close());

                // 创建用于编辑的对象字段
                TextField nameField = new TextField("名称");
                nameField.setValue(template.getName());
                nameField.setWidthFull();

                TextField descriptionField = new TextField("描述");
                descriptionField.setValue(template.getDescription());
                descriptionField.setWidthFull();

                TextField startCommandField = new TextField("启动命令");
                startCommandField.setValue(template.getStartCommand());
                startCommandField.setWidthFull();

                TextField workingDirectoryField = new TextField("工作目录");
                workingDirectoryField.setValue(template.getWorkingDirectory());
                workingDirectoryField.setWidthFull();

                TextField commandsField = new TextField("命令");
                commandsField.setValue(String.join("<n>", template.getCommands()));
                commandsField.setWidthFull();

                // 保存按钮
                Button saveButton = new Button("保存", e -> {
                    // 更新对象属性
                    template.setName(nameField.getValue());
                    template.setDescription(descriptionField.getValue());
                    template.setStartCommand(startCommandField.getValue());
                    template.setWorkingDirectory(workingDirectoryField.getValue());
                    template.setCommands(List.of(commandsField.getValue().split("<n>")));

                    Notification notification = new Notification("模板已更新", 3000);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.open();
                    dialog.close();

                    TerminalTemplateService.save();
                    reloadPages();
                });

                saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                // 使用 VerticalLayout 布局表单内容
                VerticalLayout formLayout = new VerticalLayout(
                        nameField,
                        descriptionField,
                        workingDirectoryField,
                        startCommandField,
                        commandsField,
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


            Button copyButton = new Button("复制", VaadinIcon.COPY.create());
            copyButton.addClickListener(event -> {
                TerminalTemplate newTemplate = template.copy();
                newTemplate.setName(newTemplate.getName() + " - 副本");
                TerminalTemplateService.addTemplate(newTemplate);
                TerminalTemplateService.save();
                Notification notification = new Notification("模板复制成功", 3000);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.open();
                reloadPages();
            });


            Button removeButton = new Button("删除", VaadinIcon.FILE_REMOVE.create());
            removeButton.addClickListener(event -> {
                TerminalTemplateService.removeTemplate(template);
                TerminalTemplateService.save();

                Notification notification = new Notification("模板删除成功", 3000);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.open();
                reloadPages();
            });
            removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);


            //从模板启动和模板修改启动
            Button luanchButton = new Button("启动", VaadinIcon.PLAY.create());
            luanchButton.addClickListener(event -> {
                TerminalInstanceService.startTerminalInstance(new TerminalInstance(template));
            });

            infoButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            copyButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            removeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            luanchButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

            return new HorizontalLayout(infoButton, copyButton, removeButton, luanchButton);
        }).setHeader("操作").setWidth("20%");

        templateGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        templateGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);


        HorizontalLayout horizontalLayout = new HorizontalLayout(searchField, createTemplateButton);
        horizontalLayout.setWidthFull();
        addFilter(searchField, terminalTemplateGridListDataView);
        add(horizontalLayout);
        add(templateGrid);
    }

    private void addFilter(TextField searchField, GridListDataView<TerminalTemplate> terminalTemplateGridListDataView) {
        searchField.addValueChangeListener(e -> terminalTemplateGridListDataView.refreshAll());
        terminalTemplateGridListDataView.addFilter(template -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesName = template.getName().toLowerCase().contains(searchTerm.toLowerCase());
            boolean matchesDescription = template.getDescription().toLowerCase().contains(searchTerm.toLowerCase());
            boolean matchesCommand = template.getCommands().stream()
                    .anyMatch(command -> command.toLowerCase().contains(searchTerm.toLowerCase()));
            return matchesName || matchesDescription || matchesCommand;
        });
    }

}
