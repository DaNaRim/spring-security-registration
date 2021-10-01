package com.example.springsecurityregistration.config.spring;

import com.example.springsecurityregistration.web.validator.PasswordMatchesValidator;
import com.example.springsecurityregistration.web.validator.ValidEmailValidator;
import com.example.springsecurityregistration.web.validator.ValidPasswordValidator;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableJpaRepositories("com.example.springsecurityregistration.persistence.dao")
@EntityScan("com.example.springsecurityregistration.persistence.model")
@ComponentScan(basePackages = {"com.example.springsecurityregistration"})
public class MvcConfig implements WebMvcConfigurer {

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

//    @Bean
//    public EntityManagerFactory entityManagerFactory() {
//        return new SessionFactoryImpl();
//    }

}
