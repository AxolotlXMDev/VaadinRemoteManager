package github.axolotl.vaadinremotemanager.service;

import dczx.axolotl.util.file.FilesUtil;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import github.axolotl.vaadinremotemanager.util.data.TerminalTemplateDataUtil;
import lombok.Getter;

import java.io.IOException;
import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 0:43
 */
public class TerminalTemplateService {
        @Getter
        private static List<TerminalTemplate> templateList;
        private static final TerminalTemplateDataUtil terminalTemplateDataUtil;

        static {
                try {
                        terminalTemplateDataUtil = new TerminalTemplateDataUtil(FilesUtil.keepFileExists("./data/terminal_template.json"));
                } catch (IOException e) {
                        throw new RuntimeException(e);
                }
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
