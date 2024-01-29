package com.example.application.config;

import com.example.application.condition.SetupFinishedCondition;
import com.example.application.views.main.MainView;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Conditional(SetupFinishedCondition.class)
@Service
public class Manager {

    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();
        //registry.destroySingleton("SetupView");
        //registry.registerSingleton("MainView", new MainView());
        // not needed at the moment
    }
}