package github.axolotl.vaadinremotemanager;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

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
    }

}
