package github.axolotl.vaadinremotemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 0:40
 * <p>
 * 可以用创建一个模拟终端的模板
 */
@Data
public class TerminalTemplate {
    private String name;
    private String description;

    private String id;//由创建的时候生成

    private String workingDirectory; // 工作目录
    private String startCommand; // 启动命令 如cmd.exe
    private List<String> commands; // 执行的命令

    public TerminalTemplate() {
        id = UUID.randomUUID().toString();
    }

    public TerminalTemplate(String name, String description, String workingDirectory, String startCommand, String... commands) {
        this.name = name;
        this.description = description;
        this.workingDirectory = workingDirectory;
        this.startCommand = startCommand;
        this.commands = List.of(commands);
        if (id==null||id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }
    }
    public TerminalTemplate copy() {
        return new TerminalTemplate(name, description, workingDirectory, startCommand, commands.toArray(new String[0]));
    }

    @Override
    public String toString() {
        if (description.isEmpty()){
            return "%s: %s (%s)".formatted(name,commands , workingDirectory);
        }
        return "%s(%s): %s (%s)".formatted(name,description,commands , workingDirectory);
    }
}
