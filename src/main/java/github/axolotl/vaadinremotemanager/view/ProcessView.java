package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import github.axolotl.vaadinremotemanager.VaadinRemoteManagerApplication;
import github.axolotl.vaadinremotemanager.util.ProcessVOService;
import github.axolotl.vaadinremotemanager.vo.ProcessDO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import oshi.software.os.OSProcess;

import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/29 20:58
 */
@Route("/process")
public class ProcessView extends VerticalLayout {
    @Getter
    @Setter
    private static Long lastAccessTime = System.currentTimeMillis() * 2;

    public ProcessView() {
        VaadinRemoteManagerApplication.setLastAccessTime(System.currentTimeMillis());
        setSizeFull();


        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.focus();

        List<ProcessDO> processList = ProcessVOService.getProcessList();
        Grid<ProcessDO> objectGrid = new Grid<>();
        objectGrid.addColumn(ProcessDO::getPid).setHeader("pid").setSortable(true);
        objectGrid.addColumn(ProcessDO::getName).setHeader("名称").setSortable(true);
        objectGrid.addColumn(p -> "%.2f".formatted(p.getCpuUsage() * 100)).setHeader("CPU占用").setSortable(true);
        objectGrid.addColumn(p -> "%.2f".formatted(p.getMemoryUsage())).setHeader("内存占用").setSortable(true);
        objectGrid.addColumn(process -> process.getStatus().equals(OSProcess.State.RUNNING) ? "运行中" : "未知").setHeader("运行状态").setSortable(true);

        // Add kill process button column with red background
        objectGrid.addComponentColumn(process -> {
            Button killButton = new Button("杀死进程", VaadinIcon.WARNING.create());
            killButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            killButton.addClickListener(event -> {
                boolean success = killProcess(process.getPid());
                if (success) {
                    processList.remove(process);
                    Notification.show("进程已终止: " + process.getName(), 1200, Notification.Position.MIDDLE);
                    // Refresh the grid
                    GridListDataView<ProcessDO> processDOGridListDataView = objectGrid.setItems(ProcessVOService.getProcessList());
                    addFilter(searchField, processDOGridListDataView);
                    searchField.focus();
                } else {
                    Notification.show("终止进程失败: " + process.getName(), 1000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            return killButton;
        }).setHeader("杀死进程");

        objectGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        objectGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);


        GridListDataView<ProcessDO> processDOGridListDataView = objectGrid.setItems(processList);


        addFilter(searchField, processDOGridListDataView);
        add(searchField);
        add(objectGrid);
    }

    private void addFilter(TextField searchField, GridListDataView<ProcessDO> processDOGridListDataView) {
        searchField.addValueChangeListener(e -> processDOGridListDataView.refreshAll());
        processDOGridListDataView.addFilter(processDO -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesName = processDO.getName().toLowerCase().contains(searchTerm.toLowerCase());
            boolean matchesPid = String.valueOf(processDO.getPid()).contains(searchTerm.toLowerCase());
            return matchesName || matchesPid;
        });
    }


    private boolean killProcess(int pid) {
        try {
            String cmd;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                cmd = "taskkill /F /PID " + pid;
            } else {
                cmd = "kill -9 " + pid;
            }
            Process process = Runtime.getRuntime().exec(cmd);
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
