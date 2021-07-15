package com.example.springsecurityregistration.config.spring;

import com.example.springsecurityregistration.web.validator.PasswordMatchesValidator;
import com.example.springsecurityregistration.web.validator.ValidEmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.example.springsecurityregistration" })
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    public ValidEmailValidator usernameValidator() {
        return new ValidEmailValidator();
    }

    @Bean
    public PasswordMatchesValidator passwordMatchesValidator() {
        return new PasswordMatchesValidator();
    }

}
