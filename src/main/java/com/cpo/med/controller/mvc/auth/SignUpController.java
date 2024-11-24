package com.cpo.med.controller.mvc.auth;

import com.cpo.med.model.request.ProfileSignUpRq;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.service.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.cpo.med.utils.Constants.SIGNUP_URL;

@Controller
@RequestMapping(SIGNUP_URL)
@RequiredArgsConstructor
@Slf4j
public class SignUpController {
    private final ProfileService profileService;

    @GetMapping
    public String showSignupForm(Model model) {
        model.addAttribute("profileSignUpRq", new ProfileSignUpRq());
        return "signup";
    }

    @PostMapping
    public String signup(@ModelAttribute("userDto") @Valid ProfileSignUpRq profileSignUpRq,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "signup";
        }
        try {
            ProfileEntity profileEntity = profileService.signUp(profileSignUpRq);
            return "redirect:/email?created=" + profileEntity.getEmail();
        } catch (ValidationException e) {
            model.addAttribute("errors", e.getMessage());
            return "signup";
        }
    }
}