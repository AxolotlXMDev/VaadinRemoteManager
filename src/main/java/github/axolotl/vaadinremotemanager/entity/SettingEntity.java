package github.axolotl.vaadinremotemanager.entity;

import github.axolotl.vaadinremotemanager.service.TerminalTemplateService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/18 13:32
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingEntity {
    private String defaultWorkingDirectory = System.getProperty("user.dir");
    private String defaultStartCommand =  getSystemDefaultStartCommand();
    /**
     * 终端名称
     * 支持占位符{@link github.axolotl.vaadinremotemanager.entity.TerminalInstance#TerminalInstance(TerminalTemplate)}
     */
    private String defaultTerminalName = "%TemplateName%-%hh:mm:ss%";
    private List<String> selfStartList = List.of();//自启动模板Id列表
    //TODO 默认刷新间隔

    private static boolean isWin() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    public static String getSystemDefaultStartCommand() {
        if (isWin()) {
            return "cmd.exe";
        } else {
            return "bash";
        }
    }
}
