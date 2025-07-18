package github.axolotl.vaadinremotemanager.util.data;

import com.alibaba.fastjson2.JSONObject;
import dczx.axolotl.util.data.JsonFileDataOperator;
import dczx.axolotl.util.data.JsonFileEntityDataOperator;
import github.axolotl.vaadinremotemanager.entity.SettingEntity;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import github.axolotl.vaadinremotemanager.service.TerminalTemplateService;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/18 11:25
 */
public class SettingDataUtil extends JsonFileEntityDataOperator<SettingEntity> {
    private static final String JSON_KEY = "SettingData";
    @Setter
    @Getter
    private SettingEntity setting;

    public SettingDataUtil(File file) {
        super(file);
    }

    public SettingEntity loadEntity() {
        setting = super.loadEntity(SettingEntity.class);
        return setting;
    }

    @Override
    public void reset() {
        jsonObject = JSONObject.of(JSON_KEY, getDefaultSettingEntity());
        save();
    }

    public static SettingEntity getDefaultSettingEntity() {
        return new SettingEntity();
    }

    public void saveEntity() {
        super.saveEntity(setting);
    }
}
