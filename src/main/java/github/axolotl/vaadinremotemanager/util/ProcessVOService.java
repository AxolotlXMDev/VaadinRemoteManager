package github.axolotl.vaadinremotemanager.util;

import github.axolotl.vaadinremotemanager.entity.ProcessEntity;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static github.axolotl.vaadinremotemanager.util.ServiceUtil.isRecentlyAccessed;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/7 23:41
 */
@Component
public class ProcessVOService {
    @Getter
    private static List<ProcessEntity> processList;//存放最新的VO对象
    @Getter
    private static Date date;
    private static List<ProcessEntity> getAllProcesses() {
        // 创建系统信息对象
        SystemInfo systemInfo = new SystemInfo();
        // 获取操作系统接口
        OperatingSystem os = systemInfo.getOperatingSystem();

        // 获取所有进程（指定数量为0表示获取全部）
        List<OSProcess> processes = os.getProcesses();

        double totalCpuUsage = processes.stream()
                .mapToDouble(process -> process.getProcessCpuLoadBetweenTicks(process))
                .sum();

        // 转换为ProcessVO列表并按CPU降序排序
        return processes.stream()
                .filter(process -> process.getResidentSetSize() >= 1024 * 1024) // 过滤小于1M的进程
                .filter(process -> {
                    String name = process.getName();
                    return !name.equals("msedge")
                            && !name.equals("msedgewebview2")
                            && !name.equals("svchost")
                            && !name.equals("百度翻译")
                            && !name.equals("QQ")
                            && !name.equals("WeChatAppEx")
                            ;
                }) // 过滤浏览器进程
                .map(process -> new ProcessEntity(
                        process.getName(),           // 进程名称
                        process.getProcessID(),      // 进程ID
                        process.getProcessCpuLoadBetweenTicks(process) / totalCpuUsage, // CPU使用率
                        process.getResidentSetSize() / (1024.0 * 1024.0), // 内存占用(MB)
                        process.getState(),           // 进程状态
                        process
                ))
                .sorted(Comparator.comparingDouble(ProcessEntity::getMemoryUsage).reversed())
                .collect(Collectors.toList());
    }
    // 每隔fixedRate执行一次
    @Scheduled(fixedRate = 12 * 1000)
    @SneakyThrows
    public void FastCheck() {
        //最近有访问就快速更新
        if (isRecentlyAccessed())
            updateProcessList();
    }

    // 每隔fixedRate执行一次 延迟initialDelay执行
    @Scheduled(fixedRate = 30 * 1000, initialDelay = 20 * 1000)
    @SneakyThrows
    public void LongTimeCheck() {
        if (!isRecentlyAccessed())
            updateProcessList();
    }
    public void updateProcessList() {
        date = new Date();
        processList = getAllProcesses();
    }


    @PostConstruct
    public void init() {
        processList = getAllProcesses();
    }
}
