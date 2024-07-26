package com.xaral.tubesail.components;

import com.xaral.tubesail.domain.User;
import com.xaral.tubesail.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    public UserRepository userRepository;

    @ModelAttribute("username")
    public String populateUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    @ModelAttribute("highRole")
    public String populateRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken)
            return null;
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return user.getHighestRole();
    }
}