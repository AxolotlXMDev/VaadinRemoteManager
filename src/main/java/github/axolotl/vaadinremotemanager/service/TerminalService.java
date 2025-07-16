package github.axolotl.vaadinremotemanager.service;

import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 10:48
 */
public class TerminalService {
    @Getter
    private static final List<TerminalInstance> terminalList = new ArrayList<>();

    static {
        TerminalInstance terminalInstance = new TerminalInstance(TerminalTemplateService.getTemplateList().get(0));
        terminalInstance.start();
        terminalList.add(terminalInstance);
    }
}
