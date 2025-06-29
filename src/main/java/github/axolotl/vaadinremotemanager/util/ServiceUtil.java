package github.axolotl.vaadinremotemanager.util;


import github.axolotl.vaadinremotemanager.VaadinRemoteManagerApplication;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/6/21 18:17
 */
public class ServiceUtil {

    /*
     * 检查是否最近有访问仪表盘
     */
    public static boolean isRecentlyAccessed(){
        return VaadinRemoteManagerApplication.getLastAccessTime() > System.currentTimeMillis() - 10 * 1000;
    }

}
