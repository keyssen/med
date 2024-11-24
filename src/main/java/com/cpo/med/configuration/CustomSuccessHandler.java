package com.cpo.med.configuration;

import com.cpo.med.model.request.ProfileDefaultCreateRq;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    private final ProfileService profileService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String redirectUrl;

        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            redirectUrl = handleOAuth2User((DefaultOAuth2User) authentication.getPrincipal());
        } else {
            redirectUrl = handleUserDetails((UserDetails) authentication.getPrincipal());
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String handleOAuth2User(DefaultOAuth2User userDetails) {
        String email = userDetails.getAttribute("email") != null ?
                userDetails.getAttribute("email") : userDetails.getAttribute("login") + "@gmail.com";
        ProfileEntity profileEntity = profileService.findByEmail(email);
        if (profileEntity == null) {
            ProfileDefaultCreateRq defaultCreateRq = new ProfileDefaultCreateRq();
            defaultCreateRq.setEmail(email);
            defaultCreateRq.setPassword("password");
            defaultCreateRq.setIsActive(true);
            profileService.defaultRegistration(defaultCreateRq);
        } else {
            profileEntity.setIsActive(true);
            profileService.callRepositorySave(profileEntity);
        }
        return "/dashboard";
    }

    public String handleUserDetails(UserDetails userDetails) {
        ProfileEntity profileEntity = profileService.findByEmail(userDetails.getUsername());
        profileEntity.setOtp(profileService.generateOtp(profileEntity));
        profileService.callRepositorySave(profileEntity);
        return "/login/otpVerification";
    }
}
