package com.example.application.views.main;

import com.example.application.Application;
import com.example.application.condition.SetupFinishedCondition;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.context.annotation.Conditional;

@PageTitle("Main")
@Route(value = "")
@Conditional(SetupFinishedCondition.class)
@AnonymousAllowed
public class MainView extends HorizontalLayout {

    private TextField name;
    private Button sayHello, restart;

    public MainView() {
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);

        restart = new Button("Restart Application");
        restart.addClickListener(e -> Application.restart());


        add(name, sayHello,restart);
    }

}
