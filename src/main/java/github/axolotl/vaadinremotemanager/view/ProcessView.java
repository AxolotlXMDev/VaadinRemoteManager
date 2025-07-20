package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import dczx.axolotl.terminal.ProcessTerminal;
import dczx.axolotl.util.DateUtil;
import github.axolotl.vaadinremotemanager.VaadinRemoteManagerApplication;
import github.axolotl.vaadinremotemanager.util.ProcessVOService;
import github.axolotl.vaadinremotemanager.util.ViewUtil;
import github.axolotl.vaadinremotemanager.entity.ProcessEntity;
import org.springframework.beans.factory.annotation.Autowired;
import oshi.software.os.OSProcess;

import java.util.*;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/29 20:58
 */
@Route("/process")
public class ProcessView extends VerticalLayout {

    @Autowired
    private ProcessVOService processVOService;

    public ProcessView() {
        VaadinRemoteManagerApplication.setLastAccessTime(System.currentTimeMillis());
        setSizeFull();


        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.focus();

        Button refreshButton = new Button("刷新", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(event -> {
            processVOService.updateProcessList();
            UI.getCurrent().accessSynchronously(() -> {
                try {
                    Thread.sleep(150);
                    ViewUtil.reloadPages();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        List<ProcessEntity> processList = ProcessVOService.getProcessList();
        processList.sort((p1, p2) -> Math.toIntExact(p2.getPid() - p1.getPid()));

        Grid<ProcessEntity> processGrid = new Grid<>();
        processGrid.addColumn(ProcessEntity::getPid).setHeader("pid").setSortable(true).setWidth("2%");

        processGrid.addColumn(ProcessEntity::getName).setHeader("名称").setSortable(true).setWidth("30%");
        processGrid.addColumn(p -> "%.2f".formatted(p.getCpuUsage() * 100)).setHeader("CPU占用").setSortable(true).setWidth("5%");
        processGrid.addColumn(p -> "%.2f".formatted(p.getMemoryUsage())).setHeader("内存占用").setSortable(true).setWidth("5%");


        processGrid.addComponentColumn(process -> {

            Span span;
            switch (process.getStatus()) {
                case OSProcess.State.RUNNING -> {
                    span = new Span("运行");
                    span.getElement().getThemeList().add("badge success");
                }
                case OSProcess.State.SLEEPING -> {
                    span = new Span("休眠");
                    span.getElement().getThemeList().add("badge warning");
                }
                case OSProcess.State.STOPPED -> {
                    span = new Span("停止");
                    span.getElement().getThemeList().add("badge error");
                }
                case OSProcess.State.ZOMBIE -> {
                    span = new Span("僵尸进程");
                    span.getElement().getThemeList().add("badge error");
                }
                case OSProcess.State.WAITING -> {
                    span = new Span("等待中");
                    span.getElement().getThemeList().add("badge info");
                }
                case OSProcess.State.SUSPENDED -> {
                    span = new Span("已挂起");
                    span.getElement().getThemeList().add("badge secondary");
                }
                case OSProcess.State.NEW -> {
                    span = new Span("新建");
                    span.getElement().getThemeList().add("badge primary");
                }
                case OSProcess.State.INVALID -> {
                    span = new Span("无效状态");
                    span.getElement().getThemeList().add("badge error");
                }
                case OSProcess.State.OTHER -> {
                    span = new Span("其他状态");
                    span.getElement().getThemeList().add("badge secondary");
                }
                default -> {
                    span = new Span("未知状态");
                    span.getElement().getThemeList().add("badge secondary");
                }
            }
            return span;
        }).setHeader("运行状态").setSortable(true).setWidth("4%");

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

                Grid<List<Object>> grid = ViewUtil.createDataGrid(data, List.of("10%", "90%"));

                dialog.add(grid);
                dialog.setWidth("70%"); // 视口宽度的50%
                dialog.getHeader().add(closeButton);
                dialog.open();
            });
            //TODO [A] 增加深浅杀死
            Button shallowKillButton = new Button("浅杀死", VaadinIcon.WARNING.create());
            shallowKillButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            shallowKillButton.addClickListener(event -> {
                ProcessTerminal.killProcess(process.getPid(), false);
                processList.remove(process);
                Notification.show("进程已终止: " + process.getName(), 1200, Notification.Position.MIDDLE);
                reloadGrid(processGrid, searchField);

            });
            Button deepKillButton = new Button("深杀死", VaadinIcon.WARNING.create());
            deepKillButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deepKillButton.addClickListener(event -> {
                ProcessTerminal.killProcess(process.getPid(), true);
                processList.remove(process);
                Notification.show("进程已终止: " + process.getName(), 1200, Notification.Position.MIDDLE);
                reloadGrid(processGrid, searchField);

            });
            infoButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            shallowKillButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            deepKillButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            return new HorizontalLayout(infoButton, shallowKillButton,deepKillButton);
        }).setHeader("操作").setWidth("7%");

        processGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        processGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);


        GridListDataView<ProcessEntity> processDOGridListDataView = processGrid.setItems(processList);


        addFilter(searchField, processDOGridListDataView);
        H4 lastUpdated = new H4("最后更新: " + DateUtil.formatDate(ProcessVOService.getDate(), "HH:mm:ss"));
        HorizontalLayout horizontalLayout = new HorizontalLayout(searchField, refreshButton, lastUpdated);
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.setWidthFull();
        add(horizontalLayout);
        add(processGrid);
    }

    private void reloadGrid(Grid<ProcessEntity> processGrid, TextField searchField) {
        GridListDataView<ProcessEntity> processDOGridListDataView = processGrid.setItems(ProcessVOService.getProcessList());
        addFilter(searchField, processDOGridListDataView);
        searchField.focus();
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

}
