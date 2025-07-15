package github.axolotl.vaadinremotemanager.util;


import github.axolotl.vaadinremotemanager.entity.SystemStatus;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static github.axolotl.vaadinremotemanager.util.ServiceUtil.isRecentlyAccessed;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/1 19:01
 */
@Component
public class SystemStatusService {
    @Getter
    private static SystemStatus systemStatus;//存放最新的VO对象



    /**
     * 主方法用于运行系统监控程序。
     * 它打印操作系统的硬件详情、1 秒内的 CPU 使用率、内存使用率、磁盘详情、网络接口统计信息、运行进程和传感器信息。
     *
     * @throws InterruptedException 如果在计算 CPU 负载时线程休眠被中断
     */
    private SystemStatus requestSystemStatus(int cpuCheckTime, int networkCheckTime) throws InterruptedException {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        OperatingSystem os = systemInfo.getOperatingSystem();

        // 捕获 CPU 刻度以测量 1 秒内的 CPU 负载 ，休眠以计算两次刻度之间的 CPU 使用率 ，算两组刻度之间的 CPU 使用率百分比
        CentralProcessor processor = hardware.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        TimeUnit.MILLISECONDS.sleep(cpuCheckTime);
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        String cpuName = processor.getProcessorIdentifier().getName();
//        System.out.println("CPU：" + cpuName);
//        System.out.printf("CPU 使用率: %.2f%%\n", cpuLoad);

        // 获取内存使用情况
        GlobalMemory memory = hardware.getMemory();
        double totalMemory = memory.getTotal() / (1024.0 * 1024 * 1024);
        double availableMemory = memory.getAvailable() / (1024.0 * 1024 * 1024);
        double usedMemory = totalMemory - availableMemory;
        double memoryLoad = usedMemory * 100 / totalMemory;
//        System.out.printf("内存使用: %.2f%% 已使用(%.2fGB/%.2fGB)\n",
//                memoryLoad, usedMemory, totalMemory);

        // 获取总的磁盘使用情况
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsList = fileSystem.getFileStores();

        double totalDisk = 0;
        double usedDisk = 0;

        for (OSFileStore fs : fsList) {
            long capacity = fs.getTotalSpace();
            long free = fs.getUsableSpace();
            totalDisk += capacity;
            usedDisk += (capacity - free);
        }
        usedDisk /= 1000.0 * 1000 * 1000;
        totalDisk /= 1000.0 * 1000 * 1000;
        double diskUsagePercent = 100.0 * usedDisk / totalDisk;
//        System.out.printf("总磁盘使用: %.2f%% 已使用(%.2fGB/%.2fGB)\n", diskUsagePercent,
//                usedDisk, totalDisk
//        );

        // 获取网络上下行速度
        long[] beforeNetworkBytes = getNetworkBytes(hardware);
        Util.sleep(networkCheckTime);
        long[] afterNetworkBytes = getNetworkBytes(hardware);

        long bytesReceived = afterNetworkBytes[0] - beforeNetworkBytes[0];
        long bytesSent = afterNetworkBytes[1] - beforeNetworkBytes[1];

        double seconds = 5.0;
        double networkUplink = bytesSent / 1024.0 / seconds;
        double networkDownlink = bytesReceived / 1024.0 / seconds;
//        System.out.printf("网络上行速度: %.2f KB/s%n", networkUplink);
//        System.out.printf("网络下行速度: %.2f KB/s%n", networkDownlink);

        return new SystemStatus(new Date(),
                cpuLoad, cpuName,
                memoryLoad, usedMemory, totalMemory,
                diskUsagePercent, usedDisk, totalDisk,
                networkUplink, networkDownlink);
    }

    private long[] getNetworkBytes(HardwareAbstractionLayer hardware) {
        long received = 0;
        long sent = 0;
        for (var networkIF : hardware.getNetworkIFs()) {
            networkIF.updateAttributes();
            received += networkIF.getBytesRecv();
            sent += networkIF.getBytesSent();
        }
        return new long[]{received, sent};
    }


    // 每隔fixedRate执行一次
    @Scheduled(fixedRate = 7 * 1000)
    @SneakyThrows
    public void FastCheck() {
        //最近有访问就快速更新
        if (isRecentlyAccessed())
            systemStatus = requestSystemStatus(1000, 1000);
    }

    // 每隔fixedRate执行一次 延迟initialDelay执行
    @Scheduled(fixedRate = 60 * 1000, initialDelay = 20 * 1000)
    @SneakyThrows
    public void LongTimeCheck() {
        if (!isRecentlyAccessed())
            systemStatus = requestSystemStatus(3000, 3000);
    }

    @PostConstruct
    public void init() {
        // 在对象创建后执行的初始化逻辑
        systemStatus = new SystemStatus(
                new Date(),
                0, "Unknown",
                0.0, 0.0, 0.0,
                0.0, 0.0, 0.0,
                0.0, 0.0
        );
    }


}
