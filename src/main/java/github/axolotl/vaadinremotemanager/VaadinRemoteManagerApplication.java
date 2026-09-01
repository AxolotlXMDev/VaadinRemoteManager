package github.axolotl.vaadinremotemanager;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import github.axolotl.vaadinremotemanager.entity.SettingEntity;
import github.axolotl.vaadinremotemanager.entity.TerminalInstance;
import github.axolotl.vaadinremotemanager.service.SettingService;
import github.axolotl.vaadinremotemanager.service.TerminalInstanceService;
import github.axolotl.vaadinremotemanager.service.TerminalTemplateService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@Push
@StyleSheet("context://themes/my-theme/styles.css")
public class VaadinRemoteManagerApplication implements AppShellConfigurator, SpringApplicationRunListener {

    @Getter
    @Setter
    private static Long lastAccessTime = System.currentTimeMillis() * 2;

    public static void main(String[] args) {
        SettingService.save();

        SettingEntity setting = SettingService.getSetting();

        if (!setting.isSkipWebLaunch()){
            SpringApplication.run(VaadinRemoteManagerApplication.class, args);
        }

        List<String> selfStartList = setting.getSelfStartList();
        int autoCloseTime = setting.getAutoCloseTime();
        if (autoCloseTime <= 0) {
            System.out.printf("自动关闭: 未启用\n", autoCloseTime);
        } else {
            System.out.printf("自动关闭: %d 分钟后\n", autoCloseTime);
            new Thread(() -> {
                try {
                    Thread.sleep(autoCloseTime * 1000);
                    System.out.println("自动关闭！");
                    System.exit(0);
                } catch (InterruptedException e) {
                }
            }).start();
        }
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


    //        @Override
    public void starting() {
        System.out.println("应用启动中...");
    }

    //    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        System.out.println("环境准备完成");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("上下文准备完成");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("上下文加载完成，Bean 已注册，但未刷新");
    }

    //    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("应用已启动（Tomcat即将启动）");
    }

    //    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println("应用正在运行（Tomcat已启动）");
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("启动失败");
    }

}
