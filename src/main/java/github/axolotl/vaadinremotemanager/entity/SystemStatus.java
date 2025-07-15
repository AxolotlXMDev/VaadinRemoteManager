package github.axolotl.vaadinremotemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/1 18:59
 */
@Data
@AllArgsConstructor
public class SystemStatus {
    private Date date;

    private double cpuLoad;
    private String cpuName;
    //内存GB
    private double memoryLoad;
    private double usedMemory;
    private double totalMemory;
    //磁盘Byte
    private double diskUsagePercent;
    private double usedDisk;
    private double totalDisk;
    //网络KB
    private double networkUplink;
    private double networkDownlink;
}
