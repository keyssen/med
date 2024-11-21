package com.cpo.med.controller;

import com.cpo.med.model.request.ProfileDefaultCreateRq;
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

@Controller
@RequestMapping(UserSignUpMvcController.SIGNUP_URL)
@RequiredArgsConstructor
@Slf4j
public class UserSignUpMvcController {
    public static final String SIGNUP_URL = "/signup";
    private final ProfileService profileService;

    @GetMapping
    public String showSignupForm(Model model) {
        model.addAttribute("userDto", new ProfileDefaultCreateRq());
        return "signup";
    }

    @PostMapping
    public String signup(@ModelAttribute("userDto") @Valid ProfileDefaultCreateRq profileDefaultCreateRq,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "signup";
        }
        try {
            final ProfileEntity profileEntity = profileService.defaultRegistration(profileDefaultCreateRq);
            return "redirect:/email?created=" + profileEntity.getEmail();
        } catch (ValidationException e) {
            model.addAttribute("errors", e.getMessage());
            return "signup";
        }
    }
}