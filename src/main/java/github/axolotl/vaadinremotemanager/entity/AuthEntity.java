package github.axolotl.vaadinremotemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthEntity {
        String username;
        String password;
        String role;
}
