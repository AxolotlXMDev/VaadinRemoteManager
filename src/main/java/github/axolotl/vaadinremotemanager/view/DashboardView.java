package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import github.axolotl.vaadinremotemanager.util.ElementUtil;
import github.axolotl.vaadinremotemanager.util.SystemStatusService;
import lombok.AllArgsConstructor;
import lombok.Data;

@Route("/")
public class DashboardView extends VerticalLayout {
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
        HorizontalLayout header = new HorizontalLayout(
                new H2("系统概览"),
                new Button("刷新", VaadinIcon.REFRESH.create())
        );
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        Span lastUpdated = new Span("最后更新: " + systemStatus.getDate());
        header.add(lastUpdated);

        // 在DashboardViewTest方法中修改statsRow的创建
        // 2. 创建状态卡片行
        HorizontalLayout statsRow = new HorizontalLayout(
                createStatCard("CPU 使用率", String.format("%.2f%%", systemStatus.getCpuLoad()), VaadinIcon.COG, systemStatus.getCpuLoad()),
                createStatCard("内存使用", String.format("%.2f GB / %.2f GB", systemStatus.getUsedMemory(), systemStatus.getTotalMemory()), VaadinIcon.DATABASE, systemStatus.getMemoryLoad()),
                createStatCard("磁盘使用", String.format("%.2f GB / %.2f GB", systemStatus.getUsedDisk(), systemStatus.getTotalDisk()), VaadinIcon.CALC, systemStatus.getDiskUsagePercent()),
                createStatCard("网络流量", String.format("%.2f KB/s ↑\n%.2f KB/s ↓", systemStatus.getNetworkUplink(), systemStatus.getNetworkDownlink()), VaadinIcon.GLOBE, null));
        statsRow.setWidthFull();
        statsRow.setSpacing(true); // 添加卡片间距
        statsRow.addClassName(LumoUtility.FlexWrap.WRAP); // 允许在小屏幕上换行


        statsRow.setWidthFull();


        // 在整体布局中添加间距
        VerticalLayout mainContent = new VerticalLayout(
                header,
                statsRow
        );
        mainContent.setSpacing(true);
        mainContent.setPadding(true); // 添加内边距

        mainContent.setSizeFull();

        content.add(mainContent);
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