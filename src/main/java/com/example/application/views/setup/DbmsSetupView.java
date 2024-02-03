package com.example.application.views.setup;

import com.example.application.applicationyamlhandler.ApplicationYamlHandler;
import com.example.application.dbms.H2ConnectionTest;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

public class DbmsSetupView {
    public static final String TITLE = "Setup your DBMS";
    private static final String H2_DRIVER = "org.h2.Driver";

    private ApplicationYamlHandler applicationYamlHandler;

    private RadioButtonGroup<String> dbms;
    private TextField                dbmsURL, dbmsUsername;
    private PasswordField            dbmsPwd;
    private Button                   dbmsTest, dbmsSaveConfig, restart;

    public void setApplicationYamlHandler(ApplicationYamlHandler applicationYamlHandler) {
        this.applicationYamlHandler = applicationYamlHandler;
    }

    public Component getLayout() {
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
        dbmsTest.setVisible(true);
        dbmsTest.addClickListener(e -> H2ConnectionTest.run(dbmsURL.getValue(), H2_DRIVER));
        dbmsSaveConfig = new Button("Save DBMS-Configuration");
        dbmsSaveConfig.setVisible(false);
        dbmsSaveConfig.addClickListener(e -> {
            writeDbmsConfigToApplicationYml();
            if(!applicationYamlHandler.getAt("spring/datasource/url").toString().isEmpty()){
                restart.setEnabled(true);
            }
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

    private void setDbmsConnStringDefault() {
        switch (dbms.getValue()) {
            case "H2":
                dbmsURL.setValue("jdbc:h2:file:~/sapl/db");
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
                driverClassName = H2_DRIVER;
                break;
            case "MariaDB":
                break;
            default:
        }

        applicationYamlHandler.setAt("spring/datasource/driverClassName", driverClassName);
        applicationYamlHandler.setAt("spring/datasource/url", dbmsURL.getValue());
        applicationYamlHandler.setAt("spring/datasource/username", dbmsUsername.getValue());
        applicationYamlHandler.setAt("spring/datasource/password", dbmsPwd.getValue());
        applicationYamlHandler.writeYamlToRessources();

        System.out.println("Write application yml file");
    }

    public void setButtonRestart(Button restart) {
        this.restart = restart;
    }
}
