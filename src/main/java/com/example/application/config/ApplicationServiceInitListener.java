package com.example.application.config;

import com.example.application.applicationyamlhandler.ApplicationYamlHandler;
import com.example.application.views.main.MainView;
import com.example.application.views.setup.SetupView;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationServiceInitListener
        implements VaadinServiceInitListener {

    @Autowired
    private ApplicationYamlHandler applicationYamlHandler;

    boolean executed = false;

    @Override
    public void serviceInit(ServiceInitEvent event) {

        if (!executed) {
            boolean setupFinished = applicationYamlHandler.getAt("spring/datasource/url") != null;

            System.out.println("********* ApplicationServiceInitListener: " + setupFinished);
            RouteConfiguration configuration =
                    RouteConfiguration.forApplicationScope();

            configuration.setRoute(
                    "", //path
                    setupFinished ?
                            MainView.class : // add view only during setup no finished
                            SetupView.class // add view only during setup no finished
            );
            executed = true;
        }
    }
}