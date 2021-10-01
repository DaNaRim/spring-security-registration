package com.example.springsecurityregistration.web.util;

import com.example.springsecurityregistration.error.UnauthorizedException;
import com.example.springsecurityregistration.persistence.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationUtil {

    public static long getUserId() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user instanceof User) {
            User user1 = (User) user;
            return user1.getId();
        } else {
            throw new UnauthorizedException("Can`t get id because user unauthorized");
        }
    }

    public static String getUserEmail() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user instanceof User) {
            User user1 = (User) user;
            return user1.getEmail();
        } else {
            throw new UnauthorizedException("Can`t get email because user unauthorized");
        }
    }
}
