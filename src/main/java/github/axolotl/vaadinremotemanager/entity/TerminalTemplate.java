package github.axolotl.vaadinremotemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 0:40
 * <p>
 * 可以用创建一个模拟终端的模板
 */
@Data
@AllArgsConstructor
public class TerminalTemplate {
    private String name;
    private String description;

    private String workingDirectory; // 工作目录
    private List<String> commands; // 执行的命令


    public TerminalTemplate(String name, String description, String workingDirectory, String... commands) {
        this.name = name;
        this.description = description;
        this.workingDirectory = workingDirectory;
        this.commands = List.of(commands);
    }
}
