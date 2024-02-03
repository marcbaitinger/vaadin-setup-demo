package com.example.application.views.setup;

import com.example.application.Application;
import com.example.application.applicationyamlhandler.ApplicationYamlHandler;
import com.example.application.condition.SetupNotFinishedCondition;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;

@AnonymousAllowed
@PageTitle("SAPL Server CE First-Time Boot Wizard")
@Route(value = "")
@Conditional(SetupNotFinishedCondition.class)
public class SetupView extends VerticalLayout {
    @Autowired
    private ApplicationYamlHandler applicationYamlHandler;

    private DbmsSetupView dbmsSetupView;
    private HttpEndpointSetupView httpEndpointSetupView;
    private AdminPasswordSetupView adminPasswordSetupView;
    private Button restart;

    public SetupView() {
        H2 title   = new H2("SAPL Server CE First-Time Boot Wizard");
        H6 welcome = new H6("Welcome to SAPL Server CE. Finish the following steps to setup the SAPL Server CE");

        dbmsSetupView = new DbmsSetupView();
        httpEndpointSetupView = new HttpEndpointSetupView();
        adminPasswordSetupView = new AdminPasswordSetupView();

        Accordion      accordion      = new Accordion();
        accordion.add(DbmsSetupView.TITLE, dbmsSetupView.getLayout());
        accordion.add(HttpEndpointSetupView.TITLE, httpEndpointSetupView.getLayout());
        accordion.add(AdminPasswordSetupView.TITLE, adminPasswordSetupView.getLayout());
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

        adminPasswordSetupView.setApplicationYamlHandler(applicationYamlHandler);
    }
}
