package com.example.application.views.setup;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("SAPL Server CE First-Time Boot Wizard")
@Route(value = "/setup")
public class SetupView extends VerticalLayout {
    RadioButtonGroup<String> dbms;
    TextField                username, dbmsURL, dbmsUsername;
    PasswordField            pwd, pwdRepeat, dbmsPwd;
    Button                   dbmsTest;

    public SetupView() {
        H2 title   = new H2("SAPL Server CE First-Time Boot Wizard");
        H6 welcome = new H6("Welcome to SAPL Server CE. Finish the following steps to setup the SAPL Server CE");

        Accordion      accordion      = new Accordion();
        accordion.add("Set username and password for the admin-user",
                createAdminUserLayout());
        accordion.add("Setup your DBMS", createDbmsLayout());
        accordion.getStyle().setMargin("50px 0");

        Anchor help = new Anchor("https://github.com/heutelbeck/sapl-server",
                "You need help? Have a look at the documentation.");

        FormLayout formLayout = new FormLayout(title, welcome, accordion, help);
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
        formLayout.setColspan(help, 2);

        add(formLayout);

    }

    private Component createAdminUserLayout() {

        username  = new TextField("Username");
        pwd       = new PasswordField("Password");
        pwdRepeat = new PasswordField("Repeat Password");

        Span adminUserErrorMessage = new Span();
        adminUserErrorMessage.getStyle().set("color", "var(--lumo-error-text-color)");

        FormLayout adminUserLayout = new FormLayout(username, pwd, pwdRepeat);
        adminUserLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        adminUserLayout.setColspan(username, 2);
        adminUserLayout.setColspan(adminUserErrorMessage, 2);

        return adminUserLayout;
    }

    private Component createDbmsLayout() {
        dbms = new RadioButtonGroup<>("DBMS");
        dbms.setItems("H2", "MariaDB");
        dbms.addValueChangeListener(e -> setDbmsConnStringDefault());
        dbmsURL = new TextField("DBMS URL");
        dbmsURL.setVisible(false);
        dbmsUsername = new TextField("DBMS Username");
        dbmsUsername.setVisible(false);
        dbmsPwd = new PasswordField("DBMS Password");
        dbmsPwd.setVisible(false);
        dbmsTest = new Button("Test DBMS-Connection");
        dbmsTest.setVisible(false);

        Span dbmsErrorMessage = new Span();
        dbmsErrorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        dbmsErrorMessage.getStyle().set("padding", "var(--lumo-space-tall-l)");

        FormLayout dbmsLayout = new FormLayout(dbms, dbmsURL, dbmsUsername, dbmsPwd, dbmsErrorMessage, dbmsTest);
        dbmsLayout.setColspan(dbms, 2);
        dbmsLayout.setColspan(dbmsURL, 2);
        dbmsLayout.setColspan(dbmsTest, 2);
        dbmsLayout.setColspan(dbmsErrorMessage, 2);

        return dbmsLayout;

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
    }


}
