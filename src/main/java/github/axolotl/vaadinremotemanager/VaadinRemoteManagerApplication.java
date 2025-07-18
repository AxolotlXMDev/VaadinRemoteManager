package github.axolotl.vaadinremotemanager;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import github.axolotl.vaadinremotemanager.service.SettingService;
import github.axolotl.vaadinremotemanager.service.TerminalInstanceService;
import github.axolotl.vaadinremotemanager.service.TerminalTemplateService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@Push
@Theme("my-theme")
public class VaadinRemoteManagerApplication implements AppShellConfigurator {

    @Getter
    @Setter
    private static Long lastAccessTime = System.currentTimeMillis() * 2;

    public static void main(String[] args) {
        SpringApplication.run(VaadinRemoteManagerApplication.class, args);

        List<String> selfStartList = SettingService.getSetting().getSelfStartList();
        System.out.printf("自启动项(%d):\n", selfStartList.size());
        TerminalTemplateService.getTemplateList()
                .stream()
                .filter(template -> selfStartList.contains(template.getId()))
                .forEach(template -> {
                    try {
                        System.out.printf("自启动: %s(%s)\n", template.getName(), template.getId());
                        TerminalInstanceService.startTerminalInstance(new TerminalInstance(template));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

}
