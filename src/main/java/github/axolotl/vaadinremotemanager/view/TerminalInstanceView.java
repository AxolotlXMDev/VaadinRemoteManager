package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import dczx.axolotl.terminal.ProcessTerminal;
import dczx.axolotl.terminal.SimpleTerminal;
import dczx.axolotl.terminal.TerminalStringRefresh;
import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import github.axolotl.vaadinremotemanager.service.SettingService;
import github.axolotl.vaadinremotemanager.service.TerminalInstanceService;
import jakarta.annotation.security.RolesAllowed;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/5 11:07
 */

@PageTitle("终端实例")
@RolesAllowed("ADMIN")
@Route("/terminal-instance/:terminalId?")
public class TerminalInstanceView extends VerticalLayout implements BeforeEnterObserver {

    private static final String QUERY_PARAM_ID = "terminalId";

    private String terminalId;

    private TerminalInstance instance;
    private TerminalTemplate template;
    private ProcessTerminal terminal;
    private String name;

    private long lastAccessTime = System.currentTimeMillis();

    private static final boolean useRegToUpload = false;//是否使用注册的刷新器来刷新信息

    private Div historyDisplay;
    private static final int getHistoryDelay = 1;
    private static int refreshDelay = SettingService.getSetting().getDefaultRefreshDelay();
    private TextField commandInput;
    private Select<String> historyComboBox;


    public TerminalInstanceView() {

//        initView();
    }

    private void initView() {
        this.instance = TerminalInstanceService.getTerminalInstance(terminalId);
        this.terminal = instance.getTerminal();
        this.template = instance.getTemplate();
        this.name = instance.getName();

        setSizeFull();
        createView();
        loadInitialHistory();
        if (useRegToUpload)
            registerRefreshListener();
    }

    /**
     * 当组件被附加到UI时调用 用于推送更新
     *
     * @param attachEvent the attach event
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        var task = scheduler.scheduleAtFixedRate(() -> attachEvent.getUI().accessSynchronously(() -> {
                    if (refreshDelay >= 0) {
                        if (System.currentTimeMillis() - lastAccessTime > refreshDelay) {
                            loadInitialHistory();
                            lastAccessTime = System.currentTimeMillis(); // 更新最后访问时间
                        }
                    }
                }), 0, 120, TimeUnit.MILLISECONDS
        );
        addDetachListener(detachEvent -> {
            detachEvent.unregisterListener();
            task.cancel(true);
        });
    }


    private void createView() {


        // 顶部栏：终端名称 + 终端选择器
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.CENTER);

        Span terminalName = new Span(name);
        terminalName.getStyle().set("font-weight", "bold");

        Button returnButton = new Button("返回", VaadinIcon.REPLY.create(), e -> {
            returnToManager();
        });
        returnButton.addThemeVariants(ButtonVariant.LUMO_LARGE);

        IntegerField refreshDelayField = new IntegerField();
        refreshDelayField.setLabel("刷新延迟");
        refreshDelayField.setValue(refreshDelay);

        refreshDelayField.addValueChangeListener(event -> {
            refreshDelay = event.getValue();
        });

        Select<TerminalInstance> terminalSelector = new Select<>();
        terminalSelector.setLabel("切换终端");
        terminalSelector.setPlaceholder("Select terminal");
        terminalSelector.setWidth("60%");
        terminalSelector.setItems(TerminalInstanceService.getInstancesList());
        terminalSelector.setValue(instance);
        terminalSelector.addValueChangeListener(event -> {
            String id = event.getValue().getId();
            UI.getCurrent().navigate(AboutUsView.class);//必须先切换走 否则会被认为是同一个页面不重新路由
            TerminalInstanceService.jumpToTerminalById(id);
        });

        topBar.add(terminalName);
        topBar.addAndExpand(new Div()); // 占位空间
        topBar.add(returnButton);
        topBar.add(refreshDelayField);
        topBar.add(terminalSelector);
        add(topBar);

        // 历史显示区域
        historyDisplay = new Div();
        historyDisplay.getStyle()
                .set("overflow-y", "auto")
                .set("height", "70%")
                .set("border", "1px solid #ccc")
                .set("padding", "5px")
                .set("white-space", "pre-wrap"); // 保留换行和空格
        historyDisplay.setWidthFull();
        add(historyDisplay);
        setFlexGrow(1, historyDisplay); // 占据剩余空间

        // 底部命令输入区域
        HorizontalLayout bottomBar = new HorizontalLayout();
        bottomBar.setWidthFull();
        bottomBar.setAlignItems(Alignment.CENTER);

        commandInput = new TextField();
        commandInput.setWidthFull();
        commandInput.setPlaceholder("Enter command...");

        Button sendButton = new Button("Send", e -> sendCommand());

        historyComboBox = new Select<>();
        historyComboBox.setPlaceholder("History commands");
        historyComboBox.setWidth("200px");
        historyComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                commandInput.setValue(e.getValue());
            }
        });

        bottomBar.add(commandInput, sendButton, historyComboBox);
        bottomBar.setFlexGrow(1, commandInput); // 输入框占据剩余空间
        add(bottomBar);

        // 设置回车键发送
        commandInput.addKeyPressListener(Key.ENTER, e -> sendCommand());
    }

    private static void returnToManager() {
        UI.getCurrent().navigate(TerminalManagerView.class);
    }


    /**
     * 初始化历史记录
     * 这里会在加载时获取当前终端的历史记录并显示
     */
    private void loadInitialHistory() {
        // 等待一段时间后更新历史记录
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.accessSynchronously(() -> {
                updateHistoryComboBox();
                try {
                    Thread.sleep(getHistoryDelay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                historyDisplay.removeAll();
                List<SimpleTerminal.HistoryEntry> history = terminal.getHistory();
                history.forEach(this::renderHistoryEntry);
                scrollToBottom();
            });
        }
    }

    private void registerRefreshListener() {
        terminal.regRefreshListener((TerminalStringRefresh) (output, error) -> {
            UI ui = UI.getCurrent();
            if (ui != null) {
                ui.accessSynchronously(() -> {
                    renderOutput(output, false);
                    renderOutput(error, true);
                    scrollToBottom();
                });
            }
        });
    }

    private void renderHistoryEntry(SimpleTerminal.HistoryEntry entry) {
        String content = entry.content;
        boolean isError = entry.type == SimpleTerminal.HistoryEntry.Type.ERROR;
        boolean isInput = entry.type == SimpleTerminal.HistoryEntry.Type.INPUT;

        renderOutput(content, isError, isInput);
    }

    private void renderOutput(String content, boolean isError) {
        renderOutput(content, isError, false);
    }

    private void renderOutput(String content, boolean isError, boolean isInput) {
        String color;
        if (isError) {
            color = "red";
        } else if (isInput) {
            color = "green";
        } else {
            color = "inherit"; // 默认颜色
        }

        // 分割多行内容
        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            if (!line.isEmpty()) {
                Span lineSpan = new Span(line);
                lineSpan.getStyle().set("color", color);
                lineSpan.getStyle().set("display", "block");
                lineSpan.getStyle().set("font-family", "monospace");
                historyDisplay.add(lineSpan);
            }
        }
    }

    @SneakyThrows
    private void sendCommand() {
        String command = commandInput.getValue().trim();
        if (!command.isEmpty()) {
            // 发送命令
            terminal.execute(command);

            // 在界面显示用户输入（绿色）
            renderOutput(command, false, true);
            scrollToBottom();

            // 清空输入框
            commandInput.clear();

            updateHistoryComboBox();

            if (!useRegToUpload)
                loadInitialHistory();

        }
    }

    private void updateHistoryComboBox() {
        List<String> inputHistory = terminal.getHistory().stream()
                .filter(entry -> entry.type == SimpleTerminal.HistoryEntry.Type.INPUT)
                .map(entry -> entry.content)
                .distinct()
                .collect(Collectors.toList());

        historyComboBox.setItems(inputHistory);
    }

    private void scrollToBottom() {
        historyDisplay.getElement().executeJs("this.scrollTop = this.scrollHeight;");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getRouteParameters().get(QUERY_PARAM_ID).ifPresentOrElse(id -> {
            if (!TerminalInstanceService.getInstanceMap().containsKey(id)) {//没有这个终端
//                Notification.show("终端实例不存在或已被删除", 3000, Notification.Position.MIDDLE);
                returnToManager();
                return;
            }
            terminalId = id;
            initView();
        }, TerminalInstanceView::returnToManager);
    }

}