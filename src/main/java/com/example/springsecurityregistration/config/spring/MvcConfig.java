package com.example.springsecurityregistration.config.spring;

import com.example.springsecurityregistration.web.validator.PasswordMatchesValidator;
import com.example.springsecurityregistration.web.validator.ValidEmailValidator;
import com.example.springsecurityregistration.web.validator.ValidPasswordValidator;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableJpaRepositories("com.example.springsecurityregistration.persistence.dao")
@EntityScan("com.example.springsecurityregistration.persistence.model")
@ComponentScan(basePackages = {"com.example.springsecurityregistration"})
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login");
        registry.addViewController("/registration").setViewName("registration");
        registry.addViewController("/badUser");
        registry.addViewController("/updatePassword");
        registry.addViewController("/forgotPassword");
        registry.addViewController("/updateForgottenPassword");
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:/messages/uk_UA"
        );
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public ValidEmailValidator usernameValidator() {
        return new ValidEmailValidator();
    }

    @Bean
    public PasswordMatchesValidator passwordMatchesValidator() {
        return new PasswordMatchesValidator();
    }

    @Bean
    public ValidPasswordValidator validPasswordValidator() {
        return new ValidPasswordValidator();
    }

}
