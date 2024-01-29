package com.example.application.views.setup;

import com.example.application.Application;
import com.example.application.applicationyamlhandler.ApplicationYamlHandler;
import com.example.application.condition.SetupNotFinishedCondition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@AnonymousAllowed
@PageTitle("SAPL Server CE First-Time Boot Wizard")
@Route(value = "")
@Conditional(SetupNotFinishedCondition.class)
public class SetupView extends VerticalLayout {
    @Autowired
    private ApplicationYamlHandler applicationYamlHandler;
    private RadioButtonGroup<String> dbms;
    private TextField                username, dbmsURL, dbmsUsername;
    private PasswordField            pwd, pwdRepeat, dbmsPwd;
    private Button                   pwdSaveConfig, dbmsTest, dbmsSaveConfig;

    public SetupView() {
        H2 title   = new H2("SAPL Server CE First-Time Boot Wizard");
        H6 welcome = new H6("Welcome to SAPL Server CE. Finish the following steps to setup the SAPL Server CE");

        Accordion      accordion      = new Accordion();
        accordion.add("Setup your DBMS", createDbmsLayout());
        accordion.add("TLS Configuration", createTLSLayout());
        accordion.add("Set username and password for the admin-user",
                createAdminUserLayout());
        accordion.getStyle().setMargin("50px 0");

        Button restart = new Button("Finish Setup-Wizard and restart");
        restart.addClickListener(e -> Application.restart());

        Anchor help = new Anchor("https://github.com/heutelbeck/sapl-server",
                "You need help? Have a look at the documentation.");

        FormLayout formLayout = new FormLayout(title, welcome, accordion, restart, help);
        // Restrict maximum width and center on page
        formLayout.setMaxWidth("800px");
        formLayout.getStyle().set("margin", "100px auto");

        // Allow the form layout to be responsive. On device widths 0-490px we have one
        // column, then we have two. Field labels are always on top of the fields.
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        formLayout.setColspan(title, 2);
        formLayout.setColspan(welcome, 2);
        formLayout.setColspan(accordion, 2);
        formLayout.setColspan(restart, 2);
        formLayout.setColspan(help, 2);

        add(formLayout);
    }

    private Component createAdminUserLayout() {

        username  = new TextField("Username");
        pwd       = new PasswordField("Password");
        pwdRepeat = new PasswordField("Repeat Password");
        pwdSaveConfig = new Button("Save Admin-User");
        pwdSaveConfig.addClickListener(e ->{
            PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
            applicationYamlHandler.setAt("io.sapl.server.accesscontrol.admin-username", username.getValue());
            applicationYamlHandler.setAt("io.sapl.server.accesscontrol.encoded-admin-password", encoder.encode(pwd.getValue()));
            applicationYamlHandler.writeYamlToRessources();

        });
        Span adminUserErrorMessage = new Span();
        adminUserErrorMessage.getStyle().set("color", "var(--lumo-error-text-color)");

        FormLayout adminUserLayout = new FormLayout(username, pwd, pwdRepeat,pwdSaveConfig);
        adminUserLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        adminUserLayout.setColspan(username, 2);
        adminUserLayout.setColspan(adminUserErrorMessage, 2);
        adminUserLayout.setColspan(pwdSaveConfig, 2);

        return adminUserLayout;
    }

    private Component createDbmsLayout() {
        dbms = new RadioButtonGroup<>("DBMS");
        dbms.setItems("H2", "MariaDB");
        dbms.addValueChangeListener(e -> setDbmsConnStringDefault());
        dbmsURL = new TextField("DBMS URL");
        dbmsURL.setRequiredIndicatorVisible(true);
        dbmsURL.setClearButtonVisible(true);
        dbmsURL.setVisible(false);
        dbmsUsername = new TextField("DBMS Username");
        dbmsUsername.setRequiredIndicatorVisible(true);
        dbmsUsername.setClearButtonVisible(true);
        dbmsUsername.setVisible(false);
        dbmsPwd = new PasswordField("DBMS Password");
        dbmsPwd.setRequiredIndicatorVisible(true);
        dbmsPwd.setClearButtonVisible(true);
        dbmsPwd.setVisible(false);
        dbmsTest = new Button("Test DBMS-Connection");
        dbmsTest.setVisible(false);
        dbmsSaveConfig = new Button("Save DBMS-Configuration");
        dbmsSaveConfig.setVisible(false);
        dbmsSaveConfig.addClickListener(e -> {
            writeDbmsConfigToApplicationYml();
        });
        Span dbmsErrorMessage = new Span();
        dbmsErrorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        dbmsErrorMessage.getStyle().set("padding", "var(--lumo-space-tall-l)");

        FormLayout dbmsLayout = new FormLayout(dbms, dbmsURL, dbmsUsername, dbmsPwd, dbmsErrorMessage, dbmsTest, dbmsSaveConfig);
        dbmsLayout.setColspan(dbms, 2);
        dbmsLayout.setColspan(dbmsURL, 2);
        dbmsLayout.setColspan(dbmsSaveConfig, 2);
        dbmsLayout.setColspan(dbmsTest, 2);
        dbmsLayout.setColspan(dbmsErrorMessage, 2);

        return dbmsLayout;
    }

    private Component createTLSLayout() {
        ListBox<String> listBox = new ListBox<>();
        listBox.setItems("TLSv1.3", "TLSv1.3 + TLSv1.2");
        listBox.setValue("TLSv1.3");

        CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("TLS ciphers");
        checkboxGroup.setItems("TLS_AES_128_GCM_SHA256",
                "TLS_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
                "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
                "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256");

        checkboxGroup.select("TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384");
        checkboxGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        add(checkboxGroup);

        Button tlsSaveConfig = new Button("Save TLS Configuration");
        tlsSaveConfig.addClickListener(e -> {
            writeTlsConfigToApplicationYml();
        });

        //TODO keystore configuration (type, path, password, alias)

        FormLayout tlsLayout = new FormLayout(listBox, checkboxGroup, tlsSaveConfig);
        tlsLayout.setColspan(listBox, 2);
        tlsLayout.setColspan(checkboxGroup, 2);
        tlsLayout.setColspan(tlsSaveConfig, 2);

        return tlsLayout;
    }

    private void setDbmsConnStringDefault() {
        switch (dbms.getValue()) {
            case "H2":
                dbmsURL.setValue("file:~/sapl/db");
                break;
            case "MariaDB":
                dbmsURL.setValue("localhost:3306/saplserver");
                break;
            default:
        }
        dbmsURL.setVisible(true);
        dbmsUsername.setVisible(true);
        dbmsPwd.setVisible(true);
        dbmsSaveConfig.setVisible(true);
        dbmsSaveConfig.setEnabled(true);
    }

    private void writeDbmsConfigToApplicationYml() {
        String driverClassName = "";
        switch (dbms.getValue()) {
            case "H2":
                driverClassName = "org.h2.Driver";
                break;
            case "MariaDB":
                break;
            default:
        }

        applicationYamlHandler.setAt("spring.datasource.driverClassName", driverClassName);
        applicationYamlHandler.setAt("spring.datasource.url", "jdbc:h2:" + dbmsURL.getValue());
        applicationYamlHandler.setAt("spring.datasource.username", dbmsUsername.getValue());
        applicationYamlHandler.setAt("spring.datasource.password", dbmsPwd.getValue());
        applicationYamlHandler.writeYamlToRessources();
        System.out.println("Write application yml file");
    }

    private void writeTlsConfigToApplicationYml() {
        System.out.println("Needs to be implemented: Write tls config to application.yml");
    }
}
