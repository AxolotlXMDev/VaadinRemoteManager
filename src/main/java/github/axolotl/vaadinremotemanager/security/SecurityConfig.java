package github.axolotl.vaadinremotemanager.security;

import com.alibaba.fastjson2.JSONObject;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import dczx.axolotl.util.file.FilesUtil;
import github.axolotl.vaadinremotemanager.entity.AuthEntity;
import github.axolotl.vaadinremotemanager.entity.AuthFileEntity;
import github.axolotl.vaadinremotemanager.view.LoginView;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        @Value("${vrm.auth-filepath}")
        String authFilePath;
        AuthFileEntity authFileEntity;

        @PostConstruct
        public void init() throws IOException {
                FilesUtil.keepFileExists(authFilePath);
                String text = Files.readString(Path.of(authFilePath));
                AuthFileEntity entity;
                if (text.isBlank()) {
                        entity = new AuthFileEntity(new ArrayList<>());
                        //默认账户为USER权限、无法进程管理、设置操作，需要在${vrm.auth-filepath}添加一个ADMIN权限账户
                        entity.getEntities().add(new AuthEntity("user", "{noop}AxolotlXM", "USER"));
                        String jsonString = JSONObject.toJSONString(entity);
                        Files.writeString(Path.of(authFilePath), jsonString);
                } else {
                        entity = JSONObject.parseObject(text, AuthFileEntity.class);
                }
                authFileEntity = entity;
        }

        @Bean
        public UserDetailsManager userDetailsManager() {
                LoggerFactory.getLogger(SecurityConfig.class)
                        .warn("NOT FOR PRODUCITON: Using in-memory user details manager!");
                List<UserDetails> users = authFileEntity.getEntities().stream().map(authEntity ->
                                User.withUsername(authEntity.getUsername())
                                        .password(authEntity.getPassword())
                                        .roles(authEntity.getRole())
                                        .build())
                        .toList();
                return new InMemoryUserDetailsManager(users);
        }
}
