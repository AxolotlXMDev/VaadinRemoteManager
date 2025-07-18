package github.axolotl.vaadinremotemanager.util.data;

import dczx.axolotl.util.FileUtil;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/18 14:03
 */
class SettingDataUtilTest {
    static File file = FileUtil.keepFileExists("./test/setting.json");

    @Test
    public void testLoad() {
        SettingDataUtil settingUtil = new SettingDataUtil(file);
        settingUtil.loadEntity();
        System.out.println("settingUtil.getSetting() = " + settingUtil.getSetting());
        settingUtil.getSetting().setDefaultTerminalName("ABC");

        settingUtil.saveEntity();
    }

}