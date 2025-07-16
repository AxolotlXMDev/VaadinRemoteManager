package github.axolotl.vaadinremotemanager.view;

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
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import github.axolotl.vaadinremotemanager.service.TerminalTemplateService;
import github.axolotl.vaadinremotemanager.util.ProcessVOService;
import github.axolotl.vaadinremotemanager.util.ViewUtil;
import oshi.software.os.OSProcess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


        List<TerminalTemplate> templateList = TerminalTemplateService.getTemplateList();
        Grid<TerminalTemplate> templateGrid = new Grid<>();
        templateGrid.addColumn(TerminalTemplate::getName).setHeader("名称").setSortable(true).setWidth("7%");


        templateGrid.addColumn(TerminalTemplate::getDescription).setHeader("描述").setSortable(true).setWidth("15%");
        templateGrid.addColumn(TerminalTemplate::getCommands).setHeader("命令").setSortable(true).setWidth("30%");
        templateGrid.addColumn(TerminalTemplate::getWorkingDirectory).setHeader("工作目录").setSortable(true).setWidth("25%");

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
                });

                saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                // 使用 VerticalLayout 布局表单内容
                VerticalLayout formLayout = new VerticalLayout(
                        nameField,
                        descriptionField,
                        workingDirectoryField,
                        commandsField,
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

            //从模板启动和模板修改启动
            Button luanchButton = new Button("启动", VaadinIcon.PLAY.create());
            luanchButton.addClickListener(event -> {

            });

            infoButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            luanchButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            return new HorizontalLayout(infoButton, luanchButton);
        }).setHeader("操作").setWidth("9%");

        templateGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        templateGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);


        GridListDataView<TerminalTemplate> terminalTemplateGridListDataView = templateGrid.setItems(templateList);


        addFilter(searchField, terminalTemplateGridListDataView);
        add(searchField);
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
