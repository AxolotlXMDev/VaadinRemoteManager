package github.axolotl.vaadinremotemanager.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteParameters;
import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import github.axolotl.vaadinremotemanager.view.TerminalInstanceView;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/16 10:48
 */
public class TerminalInstanceService {
    @Getter
    private static final HashMap<String, TerminalInstance> instanceMap = new HashMap<>();
    @Getter
    private static ArrayList<TerminalInstance> instancesList = new ArrayList<>();

    /**
     * 在对map进行修改后调用该方法
     */
    public static void reloadList() {
        instancesList = new ArrayList<>(TerminalInstanceService.getInstanceMap().values());
        instancesList.sort((t1, t2) -> Math.toIntExact(t2.getStartTime() - t1.getStartTime()));
    }

    public static void startTerminalInstance(TerminalInstance terminalInstance) {
        terminalInstance.start();
        putTerminalInstance(terminalInstance);
    }


    public static void putTerminalInstance(TerminalInstance terminalInstance) {
        instanceMap.put(terminalInstance.getId(), terminalInstance);
        reloadList();
    }

    public static TerminalInstance getTerminalInstance(String id) {
        return instanceMap.get(id);
    }

    public static void jumpToTerminalById(String terminalId) {
        UI.getCurrent().navigate(TerminalInstanceView.class, new RouteParameters("terminalId", terminalId));
    }

    public static void removeTerminalInstance(String id) {
        TerminalInstance terminalInstance = instanceMap.get(id);
        if (terminalInstance != null) {
            instanceMap.remove(id);
            reloadList();
        }
    }
}
