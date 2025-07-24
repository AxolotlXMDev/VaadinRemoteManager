package github.axolotl.vaadinremotemanager.util;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/21 17:12
 */
public class NotificationUtil {

    /**
     * 统一格式显示通知
     *
     * @param text           通知文本
     * @param duration       持续时间（毫秒）
     * @param position       通知位置
     * @param variants       通知主题变体
     * @param <TVariantEnum> 通知变体类型
     */
    @SafeVarargs
    public static <TVariantEnum extends NotificationVariant> void showNotification(String text, int duration, Notification.Position position, TVariantEnum... variants) {
        Notification.show(text, duration, position).addThemeVariants(variants);
    }

    @SafeVarargs
    public static <TVariantEnum extends NotificationVariant> void showNotification(String text, TVariantEnum... variants) {
        Notification.show(text, 2000, Notification.Position.TOP_END).addThemeVariants(variants);
    }

    public static void showNotificationSuccess(String text) {
        Notification.show(text, 2000, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static void showNotificationError(String text) {
        Notification.show(text, 2000, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    public static void showNotificationWarning(String text) {
        Notification.show(text, 2000, Notification.Position.TOP_END).addThemeVariants(NotificationVariant.LUMO_WARNING);
    }

}
