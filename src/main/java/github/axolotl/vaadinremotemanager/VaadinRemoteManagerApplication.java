package github.axolotl.vaadinremotemanager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VaadinRemoteManagerApplication {

    @Getter
    @Setter
    private static Long lastAccessTime = System.currentTimeMillis() * 2;
    public static void main(String[] args) {
        SpringApplication.run(VaadinRemoteManagerApplication.class, args);
    }

}
