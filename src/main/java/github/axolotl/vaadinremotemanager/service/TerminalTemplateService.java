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
                "测试", "测试用", System.getProperty("user.dir"),
                "cmd.exe","chcp 65001", "java -version", "java"
        ));
    }
}
