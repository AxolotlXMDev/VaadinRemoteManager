package github.axolotl.vaadinremotemanager.entity;

import dczx.axolotl.terminal.ProcessTerminal;
import dczx.axolotl.terminal.SimpleTerminal;
import dczx.axolotl.terminal.TerminalStreamRefresh;
import dczx.axolotl.util.DateUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 10:39
 */
@Data
public class TerminalInstance {
    private TerminalTemplate template;
    private ProcessTerminal terminal;

    public boolean isRunning() {
        return terminal.isRunning();
    }

    private String name; // 终端实例名称


    public TerminalInstance(TerminalTemplate template) {
        this.template = template;
        name = template.getName() +" - "+ DateUtil.formatDate(new Date(), "hh:mm:ss");
    }

    public TerminalInstance(String name, TerminalTemplate template) {
        this.name = name;
        this.template = template;
    }

    public void start() {
         terminal = new ProcessTerminal(template.getStartCommand(),template.getWorkingDirectory());
        template.getCommands().forEach(command -> {
            try {
                terminal.execute(command);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        terminal.regRefreshListener(new TerminalStreamRefresh() {
            @Override
            @SneakyThrows
            public void refresh(InputStream outputStream, InputStream errorStream) {
                String output = IOUtils.toString(outputStream, Charset.defaultCharset());
                String error = IOUtils.toString(errorStream, Charset.defaultCharset());
                System.out.println("Load terminal refresh!");
            }
        });
    }

    public List<SimpleTerminal.HistoryEntry> getHistory() {
        if (terminal == null) {
            throw new IllegalStateException("Terminal is not started yet.");
        }
        return terminal.getHistory();
    }

    @Override
    public String toString() {
        return "[%s]%s".formatted(name,template.getDescription());
    }
}
