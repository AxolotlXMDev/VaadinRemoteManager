package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import github.axolotl.vaadinremotemanager.VaadinRemoteManagerApplication;
import github.axolotl.vaadinremotemanager.util.ProcessVOService;
import github.axolotl.vaadinremotemanager.util.ViewUtil;
import github.axolotl.vaadinremotemanager.entity.ProcessEntity;
import lombok.Getter;
import lombok.Setter;
import oshi.software.os.OSProcess;

import java.util.*;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/29 20:58
 */
@Route("/process")
public class ProcessView extends VerticalLayout {

    public ProcessView() {
        VaadinRemoteManagerApplication.setLastAccessTime(System.currentTimeMillis());
        setSizeFull();


        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.focus();

        List<ProcessEntity> processList = ProcessVOService.getProcessList();
        Grid<ProcessEntity> processGrid = new Grid<>();
        processGrid.addColumn(ProcessEntity::getPid).setHeader("pid").setSortable(true).setWidth("2%");


        processGrid.addColumn(ProcessEntity::getName).setHeader("名称").setSortable(true).setWidth("30%");
        processGrid.addColumn(p -> "%.2f".formatted(p.getCpuUsage() * 100)).setHeader("CPU占用").setSortable(true).setWidth("5%");
        processGrid.addColumn(p -> "%.2f".formatted(p.getMemoryUsage())).setHeader("内存占用").setSortable(true).setWidth("5%");
        processGrid.addColumn(process -> process.getStatus().equals(OSProcess.State.RUNNING) ? "运行中" : "未知").setHeader("运行状态").setSortable(true).setWidth("4%");

        // Add kill process button column with red background
        processGrid.addComponentColumn(process -> {
            Button infoButton = new Button("查看", VaadinIcon.INFO_CIRCLE.create());
            infoButton.addClickListener(event -> {
                Dialog dialog = new Dialog();
                dialog.setHeaderTitle("进程信息");
                Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());

                OSProcess osProcess = process.getOsProcess();
                List<List<Object>> data = new ArrayList<>();
                data.add(List.of("名称", osProcess.getName()));
                data.add(List.of("PID", osProcess.getProcessID()));
                data.add(List.of("命令", osProcess.getCommandLine()));
                data.add(List.of("路径", osProcess.getPath()));
                data.add(List.of("工作目录", osProcess.getCurrentWorkingDirectory()));
                data.add(List.of("启动时间", DateUtil.formatDate(new Date(osProcess.getStartTime()))));

                Grid<List<Object>> grid = ViewUtil.createDataGrid(data,List.of("10%", "90%"));

                dialog.add(grid);
                dialog.setWidth("70%"); // 视口宽度的50%
                dialog.getHeader().add(closeButton);
                dialog.open();
            });

            Button killButton = new Button("杀死进程", VaadinIcon.WARNING.create());
            killButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            killButton.addClickListener(event -> {
                boolean success = killProcess(process.getPid());
                if (success) {
                    processList.remove(process);
                    Notification.show("进程已终止: " + process.getName(), 1200, Notification.Position.MIDDLE);
                    // Refresh the grid
                    GridListDataView<ProcessEntity> processDOGridListDataView = processGrid.setItems(ProcessVOService.getProcessList());
                    addFilter(searchField, processDOGridListDataView);
                    searchField.focus();
                } else {
                    Notification.show("终止进程失败: " + process.getName(), 1000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            infoButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            killButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            return new HorizontalLayout(infoButton,killButton);
        }).setHeader("操作").setWidth("7%");

        processGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        processGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);


        GridListDataView<ProcessEntity> processDOGridListDataView = processGrid.setItems(processList);


        addFilter(searchField, processDOGridListDataView);
        add(searchField);
        add(processGrid);
    }


    private void addFilter(TextField searchField, GridListDataView<ProcessEntity> processDOGridListDataView) {
        searchField.addValueChangeListener(e -> processDOGridListDataView.refreshAll());
        processDOGridListDataView.addFilter(processEntity -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesName = processEntity.getName().toLowerCase().contains(searchTerm.toLowerCase());
            boolean matchesPid = String.valueOf(processEntity.getPid()).contains(searchTerm.toLowerCase());
            return matchesName || matchesPid;
        });
    }

    private void showMapDialog(Map<String, String> data) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("用户详情");

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull();
            row.setJustifyContentMode(JustifyContentMode.BETWEEN);
            row.setSpacing(true);

            TextField keyField = new TextField(entry.getKey());
            keyField.setWidth("30%");
            keyField.setReadOnly(true);

            TextField valueField = new TextField(entry.getValue());
            valueField.setWidth("70%");
            valueField.setReadOnly(true);

            row.add(keyField, valueField);
            layout.add(row);
        }

        dialog.add(layout);

        Button closeButton = new Button("关闭", e -> dialog.close());
        dialog.getFooter().add(closeButton);

        dialog.open();
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
