package com.cpo.med.aspect;


import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleSecurityAspect {
    private final ProfileService profileService;

    @Around("@annotation(customSecured)")
    public Object checkActiveUser(ProceedingJoinPoint joinPoint,
                                  CustomSecured customSecured) throws Throwable {
        String[] requiredRoles = customSecured.role();
        ProfileEntity profileEntity = null;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof DefaultOAuth2User userDetails) {
            profileEntity = profileService.findByEmail(userDetails.getAttribute("email"));
        } else if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            profileEntity = profileService.findByEmail(userDetails.getUsername());
        }
        if (profileEntity != null && Arrays.asList(requiredRoles).contains(profileEntity.getProfileRole().getAuthority())) {
            return joinPoint.proceed();
        } else {
            throw new AccessDeniedException("Доступ запрещен");
        }
    }
}