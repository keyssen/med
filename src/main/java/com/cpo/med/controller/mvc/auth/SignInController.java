package com.cpo.med.controller.mvc.auth;

import com.cpo.med.configuration.CustomSuccessHandler;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

import static com.cpo.med.utils.Constants.LOGIN_URL;

@Controller
@RequestMapping(LOGIN_URL)
@RequiredArgsConstructor
@Slf4j
public class SignInController {
    private final ProfileService profileService;
    private final CustomSuccessHandler customSuccessHandler;

    @GetMapping("/otpVerification")
    public String otpSent(Model model) {
        model.addAttribute("otpValue", 0);
        return "otp-screen";
    }

    @PostMapping("/otpVerification")
    public String otpVerification(@RequestParam Integer otpValue,
                                  HttpServletRequest request, HttpServletResponse response) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
        ProfileEntity users = profileService.findByEmail(user.getUsername());
        if (Objects.equals(users.getOtp(), otpValue)) {
            users.setIsActive(true);
            profileService.callRepositorySave(users);
            return "redirect:/dashboard";
        } else {
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, securityContext.getAuthentication());
            return "redirect:/login/otpVerification?error";
        }
    }

    @PostMapping("/otpVerification/resend")
    public String otpResend(Model model, Integer otpValue) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        customSuccessHandler.handleUserDetails((UserDetails) securityContext.getAuthentication().getPrincipal());
        model.addAttribute("otpValue", otpValue);
        return "otp-screen";
    }
}