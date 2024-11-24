package com.cpo.med.controller.profile;

import com.cpo.med.aspect.ActiveUserCheck;
import com.cpo.med.aspect.CustomSecured;
import com.cpo.med.exception.UserNotFoundException;
import com.cpo.med.model.request.ProfileUpdateRq;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.service.MinioService;
import com.cpo.med.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.cpo.med.mapper.ProfileMapper.entityToProfileUpdateRq;
import static java.util.Objects.nonNull;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileMvcController {
    private final ProfileService profileService;
    private final MinioService minioService;

    @ActiveUserCheck
    @GetMapping("/edit")
    @CustomSecured(role = {ProfileRole.AsString.PATIENT, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.ADMINISTRATOR})
    public String edit(Model model) {
        ProfileEntity profile = profileService.getCurrentUser();
        if (profile != null) {
            ProfileUpdateRq profileUpdateRq = entityToProfileUpdateRq(profile);
            model.addAttribute("profileUpdateRq", profileUpdateRq);
            if (nonNull(profile.getImage())) {
                model.addAttribute("imageUrl", minioService.getImageUrl(profile.getId(), profile.getImage().getId()));
            }
        }
        return "profile-edit";
    }

    @ActiveUserCheck
    @PostMapping("/edit")
    @CustomSecured(role = {ProfileRole.AsString.PATIENT, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.ADMINISTRATOR})
    public String update(Model model, @ModelAttribute("profileUpdateRq") @Valid ProfileUpdateRq profileUpdateRq,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "profile-edit";
        }
        ProfileEntity profileEntity = profileService.getCurrentUser();
        if (profileEntity == null) {
            throw new UserNotFoundException(null);
        }
        profileService.update(profileEntity.getId(), profileUpdateRq);
        return "redirect:/profile/edit";
    }
}
