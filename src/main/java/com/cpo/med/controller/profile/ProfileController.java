package com.cpo.med.controller.profile;

import com.cpo.med.aspect.CustomSecured;
import com.cpo.med.model.request.AddPatientToMedicalSessionRq;
import com.cpo.med.model.request.MedicalSessionCreateRq;
import com.cpo.med.model.response.ProfileSimpleRs;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.service.MedicalSessionService;
import com.cpo.med.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

//    @GetMapping("/simple")
//    @CustomSecured(role = {ProfileRole.AsString.ADMINISTRATOR})
//    public List<ProfileSimpleRs> findSimplePatient() {
//        return profileService.getSimplePatient();
//    }
}