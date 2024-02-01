package com.example.application.views.setup;

import com.example.application.Application;
import com.example.application.applicationyamlhandler.ApplicationYamlHandler;
import com.example.application.condition.SetupNotFinishedCondition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;


@AnonymousAllowed
@PageTitle("SAPL Server CE First-Time Boot Wizard")
@Route(value = "")
@Conditional(SetupNotFinishedCondition.class)
public class SetupView extends VerticalLayout {
    @Autowired
    private ApplicationYamlHandler applicationYamlHandler;

    private DbmsSetupView dbmsSetupView;
    private HttpEndpointSetupView httpEndpointSetupView;
    private TextField                username;
    private PasswordField            pwd, pwdRepeat;
    private Button                   pwdSaveConfig, restart;
    private Span adminUserErrorMessage;
    private boolean enablePasswordCheck;

    public SetupView() {
        H2 title   = new H2("SAPL Server CE First-Time Boot Wizard");
        H6 welcome = new H6("Welcome to SAPL Server CE. Finish the following steps to setup the SAPL Server CE");

        dbmsSetupView = new DbmsSetupView();
        httpEndpointSetupView = new HttpEndpointSetupView();

        Accordion      accordion      = new Accordion();
        accordion.add(DbmsSetupView.TITLE, dbmsSetupView.getLayout());
        accordion.add(HttpEndpointSetupView.TITLE, httpEndpointSetupView.getLayout());
        accordion.add("Set username and password for the admin-user",
                createAdminUserLayout());
        accordion.getStyle().setMargin("50px 0");

        restart = new Button("Finish Setup-Wizard and restart");
        restart.addClickListener(e -> Application.restart());
        restart.setEnabled(false);

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

    @PostConstruct
    public void init() {
        dbmsSetupView.setApplicationYamlHandler(applicationYamlHandler);
        dbmsSetupView.setButtonRestart(restart);

        httpEndpointSetupView.setApplicationYamlHandler(applicationYamlHandler);
    }

    private Component createAdminUserLayout() {

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
}
