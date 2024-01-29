package com.example.application;

import com.example.application.applicationyamlhandler.ApplicationYamlHandler;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        context = SpringApplication.run(Application.class, args);
    }

    public static ConfigurableApplicationContext context;
    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        VaadinSession.getCurrent().getSession().invalidate();
        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(Application.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }

}
