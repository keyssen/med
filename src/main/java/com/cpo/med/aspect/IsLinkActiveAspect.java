package com.cpo.med.aspect;

import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IsLinkActiveAspect {
    private final ProfileService profileService;

    @Before("execution(* *(..)) && @within(org.springframework.stereotype.Controller)  && args(model,..) ")
    public void isActive(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof DefaultOAuth2User oAuthUserDetails) {
            ProfileEntity profileEntity = profileService.findByEmail(oAuthUserDetails.getAttribute("email"));
            if (profileEntity != null) {
                model.addAttribute("active", profileEntity.getIsActive());
                model.addAttribute("userRole", profileEntity.getProfileRole().toString());
            }
        } else if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            ProfileEntity profileEntity  = profileService.findByEmail(userDetails.getUsername());
            model.addAttribute("active", profileEntity.getIsActive());
            model.addAttribute("userRole", profileEntity.getProfileRole().toString());
        } else {
            model.addAttribute("active", false);
        }
    }
}
