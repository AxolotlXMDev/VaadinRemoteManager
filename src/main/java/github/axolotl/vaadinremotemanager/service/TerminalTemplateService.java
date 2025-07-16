package github.axolotl.vaadinremotemanager.service;

import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 0:43
 */
public class TerminalTemplateService {
    @Getter
    private static final List<TerminalTemplate> templateList = new ArrayList<>();

    static {
        templateList.add(new TerminalTemplate(
                "默认模板", "默认模板，可用于创建一个空白的终端", System.getProperty("user.dir"),
                isWin()?"cmd.exe":"bash","echo Hello World!"
        ));
    }

    private static boolean isWin(){
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }
}
