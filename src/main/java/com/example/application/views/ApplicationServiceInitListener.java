package com.example.application.views;

import com.example.application.appyamlhandler.AppYamlHandler;
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
    private AppYamlHandler appYamlHandler;

    boolean executed = false;

    @Override
    public void serviceInit(ServiceInitEvent event) {

        if (!executed) {
            boolean setupFinished = appYamlHandler.getValueByPath("spring.datasource.url") != null;

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
