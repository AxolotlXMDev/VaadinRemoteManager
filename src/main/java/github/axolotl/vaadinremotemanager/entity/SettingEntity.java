package github.axolotl.vaadinremotemanager.entity;

import github.axolotl.vaadinremotemanager.service.TerminalTemplateService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/18 13:32
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingEntity {
    private String defaultWorkingDirectory =System.getProperty("user.dir");
    private String defaultStartCommand = TerminalTemplateService.getDefaultStartCommand();
    private String defaultTerminalName="%TemplateName%-%%";//TODO 支持占位符
}
