package github.axolotl.vaadinremotemanager.util.data;

import com.alibaba.fastjson2.JSONObject;
import dczx.axolotl.util.data.JsonFileDataOperator;
import dczx.axolotl.util.data.JsonFileEntityDataOperator;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/18 11:25
 */
public class TerminalTemplateDataUtil extends JsonFileDataOperator {
    private static final String JSON_KEY = "TerminalTemplate";
    @Setter
    @Getter
    private List<TerminalTemplate> terminalTemplateList;


    public TerminalTemplateDataUtil(File file) {
        super(file);
    }

    public List<TerminalTemplate> loadEntity() {
        load();
        terminalTemplateList = jsonObject.getJSONArray(JSON_KEY).toJavaList(TerminalTemplate.class);
        return terminalTemplateList;
    }


    public void saveEntity() {
        jsonObject = JSONObject.of(JSON_KEY,terminalTemplateList);
        save();
    }

    @Override
    public void reset() {
        jsonObject = JSONObject.of(JSON_KEY,List.of());
        save();
        loadEntity();
    }
}
