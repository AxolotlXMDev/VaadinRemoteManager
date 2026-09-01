package github.axolotl.vaadinremotemanager.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import github.axolotl.vaadinremotemanager.view.LoginView;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/20 11:48
 */
@EnableWebSecurity
@Configuration
class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    public UserDetailsManager userDetailsManager() {
        LoggerFactory.getLogger(SecurityConfig.class)
                .warn("NOT FOR PRODUCITON: Using in-memory user details manager!");
        var user = User.withUsername("user")
                .password("{noop}AxolotlXM")
                .roles("USER")
                .build();
        var admin = User.withUsername("admin")
                .password("{noop}AXM-e2beaf10-8ef0-4bc2-88a3-cad6489a0286-2daf98c4")
                //这是一段临时生成的uuid，不要尝试用这个来登录我的其他账户(那是徒劳的)
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
