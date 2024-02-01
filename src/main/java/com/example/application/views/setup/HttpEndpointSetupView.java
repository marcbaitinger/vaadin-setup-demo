package com.example.application.views.setup;

import com.example.application.applicationyamlhandler.ApplicationYamlHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public class HttpEndpointSetupView extends VerticalLayout {
    public static final String TITLE = "HTTP Endpoint Configuration";

    private ApplicationYamlHandler applicationYamlHandler;

    private ListBox<String> listBox;
    private CheckboxGroup<String> checkboxGroup;

    private static final String TLS_V1_3_PROTOCOL = "TLSv1.3";
    private static final String TLS_V1_3_AND_V1_2_PROTOCOL = "TLSv1.3 + TLSv1.2";

    public void setApplicationYamlHandler(ApplicationYamlHandler applicationYamlHandler) {
        this.applicationYamlHandler = applicationYamlHandler;
    }

    public Component getLayout() {
        listBox = new ListBox<>();
        listBox.setItems(TLS_V1_3_PROTOCOL, TLS_V1_3_AND_V1_2_PROTOCOL);
        listBox.setValue(TLS_V1_3_PROTOCOL);

        checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("TLS ciphers");
        checkboxGroup.setItems(getCiphers());

        checkboxGroup.select("TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384");
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        add(checkboxGroup);

        Button tlsSaveConfig = new Button("Save HTTP Endpoint Configuration");
        tlsSaveConfig.addClickListener(e -> writeTlsConfigToApplicationYml());

        //TODO keystore configuration (type, path, password, alias)

        FormLayout tlsLayout = new FormLayout(listBox, checkboxGroup, tlsSaveConfig);
        tlsLayout.setColspan(listBox, 2);
        tlsLayout.setColspan(checkboxGroup, 2);
        tlsLayout.setColspan(tlsSaveConfig, 2);

        return tlsLayout;
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

        for (String cipher : checkboxGroup.getSelectedItems())
            applicationYamlHandler.setAt("server/ssl/ciphers", cipher);

        applicationYamlHandler.setAt("server/ssl/protocols", "TLSv1.3");

        if (listBox.getValue().equals(TLS_V1_3_AND_V1_2_PROTOCOL))
            applicationYamlHandler.setAt("server/ssl/protocols", "TLSv1.2");

        applicationYamlHandler.writeYamlToRessources();
        System.out.println("Write tls config to application yml file");
    }
}
