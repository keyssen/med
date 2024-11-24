package com.cpo.med.controller.mvc;

import com.cpo.med.aspect.ActiveUserCheck;
import com.cpo.med.aspect.CustomSecured;
import com.cpo.med.exception.UserNotFoundException;
import com.cpo.med.model.request.ProfileUpdateRq;
import com.cpo.med.model.request.SearchProfileRq;
import com.cpo.med.model.response.PaginationDoctorProfileRs;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.service.MinioService;
import com.cpo.med.service.ProfileService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorsController {
    private final ProfileService profileService;
    private final MinioService minioService;

    @GetMapping
    @ActiveUserCheck
    @CustomSecured(role = {ProfileRole.AsString.PATIENT, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.ADMINISTRATOR})
    public String getDoctorProfiles(Model model, @ModelAttribute SearchProfileRq searchProfileRq, HttpServletResponse response) {

        if (searchProfileRq.getPage() == null) {
            searchProfileRq.setPage(0);
        }
        if (searchProfileRq.getSize() == null) {
            searchProfileRq.setSize(10);
        }

        PaginationDoctorProfileRs doctorProfiles = profileService.getDoctorProfiles(searchProfileRq);

        model.addAttribute("doctorProfiles", doctorProfiles);
        model.addAttribute("searchProfileRq", searchProfileRq);
        model.addAttribute("patients", profileService.getSimplePatient());
        ProfileEntity profileEntity = profileService.getCurrentUser();
        if (profileEntity.getProfileRole().equals(ProfileRole.PATIENT)) {
            Cookie patientIdCookie = new Cookie("patientId", profileEntity.getId().toString());
            patientIdCookie.setMaxAge(3600);
            response.addCookie(patientIdCookie);
        }
        return "doctors";
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
