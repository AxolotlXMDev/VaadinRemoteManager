package github.axolotl.vaadinremotemanager.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import github.axolotl.vaadinremotemanager.entity.SettingEntity;
import github.axolotl.vaadinremotemanager.entity.TerminalTemplate;
import github.axolotl.vaadinremotemanager.service.SettingService;
import github.axolotl.vaadinremotemanager.service.TerminalTemplateService;
import github.axolotl.vaadinremotemanager.util.NotificationUtil;
import jakarta.annotation.security.RolesAllowed;


/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/7/5 11:07
 */
@Route("/setting")
@PageTitle("设置")
@RolesAllowed("ADMIN")
public class SettingView extends VerticalLayout {
    public SettingView() {
        SettingEntity setting = SettingService.getSetting();

        setSizeFull();

        TextField defaultWorkingDirectoryField = new TextField("默认工作路径");
        defaultWorkingDirectoryField.setValue(setting.getDefaultWorkingDirectory());
        defaultWorkingDirectoryField.setWidthFull();

        TextField defaultStartCommandField = new TextField("默认启动命令");
        defaultStartCommandField.setValue(setting.getDefaultStartCommand());
        defaultStartCommandField.setWidthFull();

        TextField defaultTerminalNameField = new TextField("默认终端名称");
        defaultTerminalNameField.setValue(setting.getDefaultTerminalName());
        defaultTerminalNameField.setWidthFull();

        IntegerField defaultRefreshDelayField = new IntegerField("默认终端刷新间隔");
        defaultRefreshDelayField.setValue(setting.getDefaultRefreshDelay());
        defaultRefreshDelayField.setWidthFull();

        Span selfStartListSpan = new Span(("自启动终端列表"));
        MultiSelectListBox<TerminalTemplate> selfStartList = new MultiSelectListBox<>();
        selfStartList.setItems(TerminalTemplateService.getTemplateList());
        selfStartList.setWidthFull();

        selfStartList.select(
                TerminalTemplateService.getTemplateList()
                        .parallelStream()
                        .filter(template -> setting.getSelfStartList().contains(template.getId()))
                        .toList()
        );
        selfStartList.setRenderer(new ComponentRenderer<>(template -> {
            VerticalLayout verticalLayout = new VerticalLayout();

            Span span = new Span("%s(%s)[%s]".formatted(template.getName(), template.getDescription(), template.getWorkingDirectory()));
            Span subSpan = new Span(String.join(",", template.getCommands()));
            verticalLayout.add(span);
            verticalLayout.add(subSpan);

            verticalLayout.setSpacing(false);
            verticalLayout.setPadding(false);

//            verticalLayout.addClassName("small");
            return verticalLayout;
        }));

        Button saveButton = new Button("保存", VaadinIcon.FILE_CODE.create());
        saveButton.addClickListener(event -> {
            setting.setDefaultWorkingDirectory(defaultWorkingDirectoryField.getValue());
            setting.setDefaultStartCommand(defaultStartCommandField.getValue());
            setting.setDefaultTerminalName(defaultTerminalNameField.getValue());
            setting.setDefaultRefreshDelay(defaultRefreshDelayField.getValue());
            setting.setSelfStartList(selfStartList.getSelectedItems().stream().map(TerminalTemplate::getId).toList());

            SettingService.setSetting(setting);
            SettingService.save();
            NotificationUtil.showNotificationSuccess("设置已保存");
        });


        add(defaultWorkingDirectoryField);
        add(defaultStartCommandField);
        add(defaultTerminalNameField);
        add(defaultRefreshDelayField);
        add(new Hr());
        add(selfStartListSpan);
        add(selfStartList);
        add(new Hr());
        add(saveButton);


    }
}
