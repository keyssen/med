package com.cpo.med.controller.mvc.auth;

import com.cpo.med.aspect.ActiveUserCheck;
import com.cpo.med.aspect.CustomSecured;
import com.cpo.med.mapper.ProfileMapper;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final ProfileService profileService;

    @GetMapping
    public String mainPage(Model model) {
        return "default";
    }

    @ActiveUserCheck
    @CustomSecured(role = {ProfileRole.AsString.ADMINISTRATOR, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.PATIENT})
    @GetMapping("/dashboard")
    public String displayDashboard(Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication().getPrincipal() instanceof DefaultOAuth2User user) {
            ProfileEntity profileEntity = profileService.findByEmail(user.getAttribute("email"));
            model.addAttribute("userDetails", ProfileMapper.getFullName(profileEntity));
        } else {
            User user = (User) securityContext.getAuthentication().getPrincipal();
            ProfileEntity profileEntity = profileService.findByEmail(user.getUsername());
            model.addAttribute("userDetails", ProfileMapper.getFullName(profileEntity));
        }
        return "dashboard";
    }
}