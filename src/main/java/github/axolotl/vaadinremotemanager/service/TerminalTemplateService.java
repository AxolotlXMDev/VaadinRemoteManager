package github.axolotl.vaadinremotemanager.service;

import dczx.axolotl.util.FileUtil;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import github.axolotl.vaadinremotemanager.util.data.TerminalTemplateDataUtil;
import lombok.Getter;

import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 0:43
 */
public class TerminalTemplateService {
    @Getter
    private static List<TerminalTemplate> templateList;
    private static final TerminalTemplateDataUtil terminalTemplateDataUtil = new TerminalTemplateDataUtil(FileUtil.keepFileExists("./data/terminal_template.json"));

    static {
        terminalTemplateDataUtil.loadEntity();
        templateList = terminalTemplateDataUtil.getTerminalTemplateList();
    }


    public static void save() {
        terminalTemplateDataUtil.setTerminalTemplateList(templateList);
        terminalTemplateDataUtil.saveEntity();
    }

    public static void addTemplate(TerminalTemplate template) {
        templateList.add(template);
        save();
    }

    public static void removeTemplate(TerminalTemplate template) {
        templateList.remove(template);
        save();
    }

    public static String getDefaultStartCommand() {
        return SettingService.getSetting().getDefaultStartCommand();
    }

}
