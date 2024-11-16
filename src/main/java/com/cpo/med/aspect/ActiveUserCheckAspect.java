package com.cpo.med.aspect;

import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ActiveUserCheckAspect {
    private final ProfileService profileService;

    @Around("@annotation(activeUserCheck)")
    public Object checkActiveUser(ProceedingJoinPoint joinPoint, ActiveUserCheck activeUserCheck) throws Throwable {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            ProfileEntity profileEntity = profileService.findByEmail(userDetails.getUsername());
            if (profileEntity != null) {
                boolean isActive = profileService.findByEmail(userDetails.getUsername()).getIsActive();
                if (!isActive) {
                    throw new DisabledException("User account is disabled");
                }
            }
        }
        return joinPoint.proceed();
    }
}
