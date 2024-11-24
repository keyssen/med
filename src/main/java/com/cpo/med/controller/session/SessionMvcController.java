package com.cpo.med.controller.session;

import com.cpo.med.aspect.ActiveUserCheck;
import com.cpo.med.aspect.CustomSecured;
import com.cpo.med.model.response.MedicalSessionRs;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.service.MedicalSessionService;
import com.cpo.med.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/session")
public class SessionMvcController {
    private final MedicalSessionService medicalSessionService;
    private final ProfileService profileService;

    @ActiveUserCheck
    @GetMapping("/doctor")
    @CustomSecured(role = {ProfileRole.AsString.PATIENT, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.ADMINISTRATOR})
    public String doctorSessions(Model model) {
        ProfileEntity profile = profileService.getCurrentUser();
        if (profile != null) {
            model.addAttribute("doctorSessions", medicalSessionService.getRsByDoctorId(profile.getId()));
        }
        return "doctor-session";
    }

    @ActiveUserCheck
    @GetMapping("/patient")
    @CustomSecured(role = {ProfileRole.AsString.PATIENT, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.ADMINISTRATOR})
    public String patientSessions(Model model) {
        ProfileEntity profile = profileService.getCurrentUser();
        if (profile != null) {
            model.addAttribute("patientSessions", medicalSessionService.getRsByPatientId(profile.getId()));
        }
        return "patient-session";
    }

    @ActiveUserCheck
    @GetMapping("/admin")
    @CustomSecured(role = {ProfileRole.AsString.PATIENT, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.ADMINISTRATOR})
    public String adminSessions(Model model) {
        model.addAttribute("adminSessions", medicalSessionService.getAllRs());
        return "admin-session";
    }

    @ActiveUserCheck
    @GetMapping("/{id}")
    @CustomSecured(role = {ProfileRole.AsString.PATIENT, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.ADMINISTRATOR})
    public String session(Model model, @PathVariable UUID id) {
        ProfileEntity profile = profileService.getCurrentUser();
        if (profile != null) {
            MedicalSessionRs medicalSessionRs = medicalSessionService.getRsById(id);
            model.addAttribute("medicalSession", medicalSessionRs);
        }
        return "session";
    }

    @ActiveUserCheck
    @GetMapping("/treatmentPlan")
    @CustomSecured(role = {ProfileRole.AsString.PATIENT, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.ADMINISTRATOR})
    public String updateTreatmentPlan(Model model, @PathVariable UUID id) {
        ProfileEntity profile = profileService.getCurrentUser();
        if (profile != null) {
            MedicalSessionRs medicalSessionRs = medicalSessionService.getRsById(id);
            model.addAttribute("medicalSession", medicalSessionRs);
        }
        return "session";
    }
}