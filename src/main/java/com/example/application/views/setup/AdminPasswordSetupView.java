package com.example.application.views.setup;

import com.example.application.applicationyamlhandler.ApplicationYamlHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class AdminPasswordSetupView {
    public static final String TITLE = "Set username and password for the admin-user";

    private ApplicationYamlHandler applicationYamlHandler;
    private TextField username;
    private PasswordField pwd, pwdRepeat;
    private Button pwdSaveConfig;
    private Span adminUserErrorMessage;
    private boolean enablePasswordCheck;

    public Component getLayout() {
        username  = new TextField("Username");
        pwd       = new PasswordField("Password");
        pwdRepeat = new PasswordField("Repeat Password");
        pwdSaveConfig = new Button("Save Admin-User");
        pwdSaveConfig.setEnabled(false);
        pwdSaveConfig.addClickListener(e ->{
            PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
            applicationYamlHandler.setAt("io.sapl/server/accesscontrol/admin-username", username.getValue());
            applicationYamlHandler.setAt("io.sapl/server/accesscontrol/encoded-admin-password", encoder.encode(pwd.getValue()));
            applicationYamlHandler.writeYamlToRessources();

        });

        adminUserErrorMessage = new Span("");
        adminUserErrorMessage.setVisible(false);
        adminUserErrorMessage.getStyle().set("color", "var(--lumo-error-text-color)");

        username.addValueChangeListener(e -> validateAdminUser());
        pwd.addValueChangeListener(e -> validateAdminUser());
        pwdRepeat.addValueChangeListener(e -> {
            enablePasswordCheck = true;
            validateAdminUser();});
        enablePasswordCheck = false;

        FormLayout adminUserLayout = new FormLayout(username, pwd, pwdRepeat,adminUserErrorMessage, pwdSaveConfig);
        adminUserLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        adminUserLayout.setColspan(username, 2);
        adminUserLayout.setColspan(adminUserErrorMessage, 2);
        adminUserLayout.setColspan(pwdSaveConfig, 2);

        return adminUserLayout;
    }

    private void validateAdminUser(){
        List<String> errors = new ArrayList<>();
        if(username.getValue().isBlank()){
            errors.add("Username has to be set");
        }
        if(enablePasswordCheck && !pwd.getValue().isEmpty() && !pwdRepeat.getValue().isEmpty()  && !pwd.getValue().equals(pwdRepeat.getValue())){
            errors.add("Passwords do not match");
        }
        if(!errors.isEmpty() && !pwd.getValue().isEmpty()){
            adminUserErrorMessage.getElement().setProperty("innerHTML", String.join( "<br />",errors));
            adminUserErrorMessage.setVisible(true);
            pwdSaveConfig.setEnabled(false);
        }
        else{
            adminUserErrorMessage.setVisible(false);
            pwdSaveConfig.setEnabled(true);
        }
    }

    public void setApplicationYamlHandler(ApplicationYamlHandler applicationYamlHandler) {
        this.applicationYamlHandler = applicationYamlHandler;
    }
}
