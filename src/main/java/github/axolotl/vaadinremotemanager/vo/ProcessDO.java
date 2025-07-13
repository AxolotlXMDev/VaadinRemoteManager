package github.axolotl.vaadinremotemanager.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import oshi.software.os.OSProcess;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/21 18:07
 */
@Data
@AllArgsConstructor
public class ProcessDO {
    private String name; // 进程名称
    private int pid; // 进程ID
    private double cpuUsage; // CPU使用率
    private double memoryUsage; // 内存占用
    private OSProcess.State status; // 进程状态
    private OSProcess osProcess; // 进程

}
