package com.example.application.views.setup;

import com.example.application.applicationyamlhandler.ApplicationYamlHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public class HttpEndpointSetupView extends VerticalLayout {
    public static final String TITLE = "HTTP Endpoint Configuration";

    private ApplicationYamlHandler applicationYamlHandler;
    private ListBox<String> listBox;
    private CheckboxGroup<String> checkboxGroup;
    private Button tlsSaveConfig;

    private static final String TLS_V1_3_PROTOCOL = "TLSv1.3";
    private static final String TLS_V1_3_AND_V1_2_PROTOCOL = "TLSv1.3 + TLSv1.2";

    public void setApplicationYamlHandler(ApplicationYamlHandler applicationYamlHandler) {
        this.applicationYamlHandler = applicationYamlHandler;
    }

    public Component getLayout() {
        listBox = new ListBox<>();
        listBox.setItems(TLS_V1_3_PROTOCOL, TLS_V1_3_AND_V1_2_PROTOCOL);
        listBox.setValue(TLS_V1_3_PROTOCOL);

        tlsSaveConfig = new Button("Save HTTP Endpoint Configuration");
        tlsSaveConfig.addClickListener(e -> writeTlsConfigToApplicationYml());

        checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("TLS ciphers");
        checkboxGroup.setItems(getCiphers());
        checkboxGroup.addSelectionListener(e -> checkIfAtLeastOneCipherOptionSelected());

        checkboxGroup.select("TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384");
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        add(checkboxGroup);

        //TODO keystore configuration (type, path, password, alias)

        FormLayout tlsLayout = new FormLayout(listBox, checkboxGroup, tlsSaveConfig);
        tlsLayout.setColspan(listBox, 2);
        tlsLayout.setColspan(checkboxGroup, 2);
        tlsLayout.setColspan(tlsSaveConfig, 2);

        return tlsLayout;
    }

    private void checkIfAtLeastOneCipherOptionSelected() {
        tlsSaveConfig.setEnabled(!checkboxGroup.getSelectedItems().isEmpty());

        if (checkboxGroup.getSelectedItems().isEmpty()) {
            Notification notification = new Notification();
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

            Div text = new Div(new Text("At least one cipher option must be selected"));

            Button closeButton = new Button(new Icon("lumo", "cross"));
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            closeButton.setAriaLabel("Close");
            closeButton.addClickListener(event -> {
                notification.close();
            });

            HorizontalLayout layout = new HorizontalLayout(text, closeButton);
            layout.setAlignItems(Alignment.CENTER);

            notification.add(layout);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();
        }
    }

    private List<String> getCiphers() {
        List<String> ciphers = new ArrayList<>();
        ciphers.add("TLS_AES_128_GCM_SHA256");
        ciphers.add("TLS_AES_256_GCM_SHA384");
        ciphers.add("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
        ciphers.add("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");
        ciphers.add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
        ciphers.add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        ciphers.add("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384");
        ciphers.add("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256");
        ciphers.add("TLS_DHE_DSS_WITH_AES_256_GCM_SHA384");
        ciphers.add("TLS_DHE_DSS_WITH_AES_128_GCM_SHA256");
        ciphers.add("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384");
        ciphers.add("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256");
        ciphers.add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384");
        ciphers.add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256");
        ciphers.add("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256");
        ciphers.add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256");
        ciphers.add("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256");
        ciphers.add("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256");

        return ciphers;
    }

    private void writeTlsConfigToApplicationYml() {
        applicationYamlHandler.setAt("server/port", "8443");
        applicationYamlHandler.setAt("server/ssl/enabled", "true");
        applicationYamlHandler.setAt("server/ssl/ciphers", checkboxGroup.getSelectedItems());
        applicationYamlHandler.setAt("server/ssl/protocols", "TLSv1.3");

        if (listBox.getValue().equals(TLS_V1_3_AND_V1_2_PROTOCOL))
            applicationYamlHandler.setAt("server/ssl/protocols", "TLSv1.2");

        applicationYamlHandler.writeYamlToRessources();
        System.out.println("Write tls config to application yml file");
    }
}