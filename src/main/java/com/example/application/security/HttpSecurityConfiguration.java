package com.example.application.security;

import com.example.application.condition.SetupFinishedCondition;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@Conditional(SetupFinishedCondition.class)
public class HttpSecurityConfiguration extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

    }

}
