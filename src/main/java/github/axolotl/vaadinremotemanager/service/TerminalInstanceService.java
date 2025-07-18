package github.axolotl.vaadinremotemanager.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteParameters;
import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import github.axolotl.vaadinremotemanager.view.TerminalInstanceView;
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
//        TerminalInstance terminalInstance = new TerminalInstance(TerminalTemplateService.getTemplateList().get(0));
//        startTerminalInstance(terminalInstance);
    }

    public static void startTerminalInstance(TerminalInstance terminalInstance) {
        terminalInstance.start();
        instanceMap.put(terminalInstance.getId(), terminalInstance);
    }

    public static TerminalInstance getTerminalInstance(String id) {
        return instanceMap.get(id);
    }
    public static void jumpToTerminalById(String terminalId) {
        UI.getCurrent().navigate(TerminalInstanceView.class, new RouteParameters("terminalId",terminalId));
    }
}
