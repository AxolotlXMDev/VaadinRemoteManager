package github.axolotl.vaadinremotemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthFileEntity {
        List<AuthEntity> entities;
}
