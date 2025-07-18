package github.axolotl.vaadinremotemanager.service;

import dczx.axolotl.util.FileUtil;
import github.axolotl.vaadinremotemanager.entity.SettingEntity;
import github.axolotl.vaadinremotemanager.util.data.SettingDataUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/18 13:32
 */
public class SettingService {
    @Getter
    @Setter
    private static SettingEntity setting;
    private static final SettingDataUtil settingUtil = new SettingDataUtil(FileUtil.keepFileExists("./data/setting.json"));

    static {
        settingUtil.loadEntity();
        setting = settingUtil.getSetting();
    }
    public static void save() {
        settingUtil.saveEntity(setting);
    }


}
