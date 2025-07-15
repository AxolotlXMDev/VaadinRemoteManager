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
                Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());

                List<List<Object>> data = new ArrayList<>();
                data.add(List.of("名称", template.getName()));
                data.add(List.of("描述", template.getDescription()));
                data.add(List.of("工作目录", template.getWorkingDirectory()));
                data.add(List.of("命令", template.getCommands()));

                Grid<List<Object>> grid = ViewUtil.createDataGrid(data,List.of("10%", "90%"));

                dialog.add(grid);
                dialog.setWidth("70%"); // 视口宽度的50%
                dialog.getHeader().add(closeButton);
                dialog.open();
            });


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
