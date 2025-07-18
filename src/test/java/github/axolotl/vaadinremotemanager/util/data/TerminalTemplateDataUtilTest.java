package github.axolotl.vaadinremotemanager.util.data;

import com.alibaba.fastjson2.JSONObject;
import dczx.axolotl.util.FileUtil;
import dczx.axolotl.util.data.JsonFileDataOperator;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/18 11:37
 */
class TerminalTemplateDataUtilTest {

    static File file = FileUtil.keepFileExists("./test/test.json");

    @Test
    public void testLoad() {
        TerminalTemplateDataUtil terminalTemplateDataUtil = new TerminalTemplateDataUtil(file);
        System.out.println("terminalTemplateDataUtil.getTerminalTemplateList() = " + terminalTemplateDataUtil.getTerminalTemplateList());
        terminalTemplateDataUtil.loadEntity();

        terminalTemplateDataUtil.getTerminalTemplateList().add(new TerminalTemplate("","","","",""));

        terminalTemplateDataUtil.saveEntity();
    }

}