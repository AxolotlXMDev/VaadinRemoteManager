package github.axolotl.vaadinremotemanager.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/18 14:09
 */
class SettingServiceTest {
    @Test
    public void testLoad() {
        System.out.println("SettingService.getSetting() = " + SettingService.getSetting());
        SettingService.getSetting().setDefaultTerminalName("ABC");
        SettingService.save();
        System.out.println("Updated SettingService.getSetting() = " + SettingService.getSetting());
    }

}