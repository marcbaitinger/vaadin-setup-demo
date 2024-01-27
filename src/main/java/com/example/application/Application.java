package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        context = SpringApplication.run(Application.class, args);
    }


    //Um Anwendung nach Änderungen in der application.yml neu zu starten
    //Mal sehen, ob das nötig ist, da nach auch nach Änderungen in der application.yml die Anwendung scheinbar neu gestartet wird
    static ConfigurableApplicationContext context;
    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(Application.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }

}
