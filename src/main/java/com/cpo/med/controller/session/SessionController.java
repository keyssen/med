package com.cpo.med.controller.session;

import com.cpo.med.aspect.ActiveUserCheck;
import com.cpo.med.aspect.CustomSecured;
import com.cpo.med.model.request.AddPatientToMedicalSessionRq;
import com.cpo.med.model.request.ChangeStatusRq;
import com.cpo.med.model.request.MedicalSessionCreateRq;
import com.cpo.med.model.request.TreatmentPlanUpdateRq;
import com.cpo.med.model.response.MedicalSessionAvailableRs;
import com.cpo.med.model.response.MedicalSessionRs;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.service.MedicalSessionService;
import com.cpo.med.service.TreatmentPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/session")
public class SessionController {
    private final MedicalSessionService medicalSessionService;
    private final TreatmentPlanService treatmentPlanService;

    @GetMapping("/{doctorId}")
    @CustomSecured(role = {ProfileRole.AsString.ADMINISTRATOR, ProfileRole.AsString.DOCTOR, ProfileRole.AsString.PATIENT})
    public List<MedicalSessionAvailableRs> findDoctorSession(@PathVariable UUID doctorId) {
        return medicalSessionService.getAvailableRsByDoctorId(doctorId);
    }

    @PostMapping("/create")
    @CustomSecured(role = {ProfileRole.AsString.ADMINISTRATOR})
    public void createMedicalSession(@RequestBody @Valid MedicalSessionCreateRq medicalSessionCreateRq) {
        medicalSessionService.create(medicalSessionCreateRq);
    }

    @PostMapping("/addPatient")
    @CustomSecured(role = {ProfileRole.AsString.ADMINISTRATOR, ProfileRole.AsString.PATIENT})
    public void addPatientToMedicalSession(@RequestBody @Valid AddPatientToMedicalSessionRq addPatientToMedicalSessionRq) {
        medicalSessionService.addPatient(addPatientToMedicalSessionRq.getMedicalSessionId(), addPatientToMedicalSessionRq.getPatientId());
    }

    @PostMapping("/treatmentPlan")
    public void updateTreatmentPlan(@RequestBody @Valid TreatmentPlanUpdateRq treatmentPlanUpdateRq) {
        treatmentPlanService.update(treatmentPlanUpdateRq);
    }

    @PatchMapping("/status")
    public void changeStatus(@RequestBody @Valid ChangeStatusRq treatmentPlanUpdateRq) {
        medicalSessionService.changeStatus(medicalSessionService.getById(treatmentPlanUpdateRq.getId()), treatmentPlanUpdateRq.getStatus());
    }
}