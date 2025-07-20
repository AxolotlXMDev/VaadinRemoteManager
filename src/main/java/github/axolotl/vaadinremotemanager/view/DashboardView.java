package github.axolotl.vaadinremotemanager.view;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import dczx.axolotl.terminal.ProcessTerminal;
import dczx.axolotl.util.DateUtil;
import github.axolotl.vaadinremotemanager.entity.SettingEntity;
import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import github.axolotl.vaadinremotemanager.service.TerminalInstanceService;
import github.axolotl.vaadinremotemanager.util.ElementUtil;
import github.axolotl.vaadinremotemanager.util.SystemStatusService;
import github.axolotl.vaadinremotemanager.util.ViewUtil;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/")
@PageTitle("仪表盘")
@RolesAllowed("ADMIN")
public class DashboardView extends VerticalLayout {
    @Autowired
    private SystemStatusService systemStatusService;

    public DashboardView() {
        setPadding(true);
        setSpacing(true);
        setSizeFull();

        DashboardViewTest(
                SystemStatusService.getSystemStatus(),
                this
        );
    }

    public void DashboardViewTest(github.axolotl.vaadinremotemanager.entity.SystemStatus systemStatus, VerticalLayout content) {
        // 1. 创建顶部标题和刷新按钮
        Button refreshButton = new Button("刷新", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(event -> {
            systemStatusService.updateSystemStatus(400, 400);
            UI.getCurrent().accessSynchronously(() -> {
                try {
                    Thread.sleep(450);
                    ViewUtil.reloadPages();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        H4 lastUpdated = new H4("最后更新: " + DateUtil.formatDate(systemStatus.getDate(), "HH:mm:ss"));
        HorizontalLayout header = new HorizontalLayout(
                new H2("系统概览"),
                refreshButton,
                lastUpdated
        );
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);


        // 在DashboardViewTest方法中修改statsRow的创建
        // 2. 创建状态卡片行
        HorizontalLayout statsRow = new HorizontalLayout(
                createStatCard("CPU 使用率", String.format("%.2f%%", systemStatus.getCpuLoad()), VaadinIcon.TIMER, systemStatus.getCpuLoad()),
                createStatCard("内存使用", String.format("%.2f GB / %.2f GB", systemStatus.getUsedMemory(), systemStatus.getTotalMemory()), VaadinIcon.DATABASE, systemStatus.getMemoryLoad()),
                createStatCard("磁盘使用", String.format("%.2f GB / %.2f GB", systemStatus.getUsedDisk(), systemStatus.getTotalDisk()), VaadinIcon.FILE_TEXT, systemStatus.getDiskUsagePercent()),
                createStatCard("网络流量", String.format("%.2f KB/s ↑\n%.2f KB/s ↓", systemStatus.getNetworkUplink(), systemStatus.getNetworkDownlink()), VaadinIcon.SIGNAL, null));
        statsRow.setWidthFull();
        statsRow.setSpacing(true); // 添加卡片间距
        statsRow.addClassName(LumoUtility.FlexWrap.WRAP); // 允许在小屏幕上换行


        statsRow.setWidthFull();

        // 在整体布局中添加间距
        HorizontalLayout operationLayout = new HorizontalLayout();//存储一些操作

        Button stopSelfButton = new Button("关闭自身", VaadinIcon.WARNING.create(), event -> {
            Notification.show("关闭成功！", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            System.exit(0);
        });
        stopSelfButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        stopSelfButton.addThemeVariants(ButtonVariant.LUMO_ERROR);


        Button restartSelfButton = new Button("重启自身", VaadinIcon.REFRESH.create(), event -> {
            Notification.show("正在重启应用，请稍候...", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            if (SettingEntity.isWin()) {
                simpleExecuteCommand("cmd.exe", "start Run.bat");
            } else {
                simpleExecuteCommand("bash", "nohup ./Run.sh &");
            }
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // 等待一段时间以确保命令执行
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.exit(0);
            }).start();
        });
        restartSelfButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        restartSelfButton.addThemeVariants(ButtonVariant.LUMO_ERROR);


        Button restartSystemButton = new Button("重启系统", VaadinIcon.WARNING.create(), event -> {
            Notification.show("正在重启系统，请稍候...", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            if (SettingEntity.isWin()) {
                simpleExecuteCommand("cmd.exe", "shutdown -r -t 0");
            } else {
                simpleExecuteCommand("bash", "reboot");
            }
        });
        restartSystemButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        restartSystemButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button stopAllTernimalButton = new Button("关闭全部终端实例", VaadinIcon.WARNING.create(), event -> {
            TerminalInstanceService.getInstancesList()
                    .parallelStream()
                    .map(TerminalInstance::getTerminal)
                    .filter(ProcessTerminal::isRunning)
                    .forEach(ProcessTerminal::stopForcibly);//全部关闭
            Notification.show("全部终端实例已关闭", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        stopAllTernimalButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        stopAllTernimalButton.addThemeVariants(ButtonVariant.LUMO_ERROR);


        operationLayout.add(stopSelfButton);
        operationLayout.add(restartSelfButton);
        operationLayout.add(restartSystemButton);
        operationLayout.add(stopAllTernimalButton);


        operationLayout.setVisible(false);
        ToggleButton toggleButton = new ToggleButton("我接受风险", event -> {
            if (event.getValue()) {
                operationLayout.setVisible(true);
            } else {
                operationLayout.setVisible(false);
            }
        });

        VerticalLayout mainContent = new VerticalLayout(header, statsRow, new Hr(), toggleButton,new Hr(),operationLayout);
        mainContent.setSpacing(true);
        mainContent.setPadding(true); // 添加内边距
        mainContent.setSizeFull();

        content.add(mainContent);
    }

    /**
     * 执行简单的命令行命令
     *
     * @param command 命令
     */
    @SneakyThrows
    private static void simpleExecuteCommand(String startCommand, String command) {
        ProcessTerminal terminal = new ProcessTerminal(startCommand, System.getProperty("user.dir"));
        terminal.execute(command);
    }

    private Card createStatCard(String title, String value, VaadinIcon iconType, Double percentage) {
        // 创建Card组件
        Card card = new Card();
        card.setWidthFull();

//        // 标题行
        Div header = new Div();
        header.getStyle()
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center");
        Span titleSpan = new Span(title);
        Icon icon = iconType.create();
        header.add(titleSpan, icon);

        card.setTitle(header);

        // 数值显示
        Div subTitleDiv = new Div();
        subTitleDiv.getStyle()
                .set("margin", "var(--lumo-space-s) 0")
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "600");
        subTitleDiv.add(ElementUtil.allowNewLine(new Span(value)));
        // 进度条（如果有）
        if (percentage != null) {
            ProgressBar progressBar = new ProgressBar();
            progressBar.setValue(percentage / 100.0);
            progressBar.getStyle()
                    .set("margin-top", "var(--lumo-space-xs)")
                    .set("height", "6px")
                    .set("border-radius", "3px");
            subTitleDiv.add(progressBar);
        }
        card.setSubtitle(subTitleDiv);

        return card;
    }

    @Data
    @AllArgsConstructor
    public static class SystemStatus {
        private double cpuUsage;
        private double memoryUsed;
        private double memoryTotal;
        private double memoryPercentage;
        private double diskUsed;
        private double diskTotal;
        private double diskPercentage;
        private double networkIn;
        private double networkOut;
        private String lastUpdated;
    }

    @Data
    @AllArgsConstructor
    public static class ProcessInfo {
        private String name;
        private int pid;
        private double cpu;
        private double memory;
    }

    @Data
    @AllArgsConstructor
    public static class Activity {
        private String description;
        private String time;
    }
}