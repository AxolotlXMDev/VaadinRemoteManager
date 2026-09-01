package github.axolotl.vaadinremotemanager.service;

import dczx.axolotl.util.file.FilesUtil;
import github.axolotl.vaadinremotemanager.entity.SettingEntity;
import github.axolotl.vaadinremotemanager.util.data.SettingDataUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/18 13:32
 */
public class SettingService {
    @Getter
    @Setter
    private static SettingEntity setting;
    private static final SettingDataUtil settingUtil;

    static {
            try {
                    settingUtil = new SettingDataUtil(FilesUtil.keepFileExists("./data/setting.json"));
            } catch (IOException e) {
                    throw new RuntimeException(e);
            }
            settingUtil.loadEntity();
        setting = settingUtil.getSetting();
    }
    public static void save() {
        settingUtil.saveEntity(setting);
    }


}
