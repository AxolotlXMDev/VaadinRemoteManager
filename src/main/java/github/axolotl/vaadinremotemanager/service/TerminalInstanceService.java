package github.axolotl.vaadinremotemanager.service;

import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import lombok.Getter;

import java.util.HashMap;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 10:48
 */
public class TerminalInstanceService {
    @Getter
    private static final HashMap<String,TerminalInstance> instanceMap = new HashMap<>();

    static {
        TerminalInstance terminalInstance = new TerminalInstance(TerminalTemplateService.getTemplateList().get(0));
        startTerminalInstance(terminalInstance);
    }

    public static void startTerminalInstance(TerminalInstance terminalInstance) {
        terminalInstance.start();
        instanceMap.put(terminalInstance.getId(), terminalInstance);
    }

    public static TerminalInstance getTerminalInstance(String id) {
        return instanceMap.get(id);
    }
}
