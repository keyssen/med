package com.cpo.med.mapper;

import com.cpo.med.model.request.MedicalSessionCreateRq;
import com.cpo.med.model.request.MedicalSessionUpdateRq;
import com.cpo.med.model.response.MedicalSessionAvailableRs;
import com.cpo.med.model.response.MedicalSessionProfileRs;
import com.cpo.med.model.response.MedicalSessionRs;
import com.cpo.med.model.response.SessionSimpleRs;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.TreatmentPlanEntity;
import com.cpo.med.persistence.entity.enums.SessionStatus;
import com.cpo.med.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class MedicalSessionMapper {

    private final MinioService minioService;

    public static MedicalSessionEntity medicalSessionCreateRqToMedicalSession(MedicalSessionCreateRq medicalSessionCreateRq, ProfileEntity doctor) {
        MedicalSessionEntity medicalSessionEntity = new MedicalSessionEntity();
        medicalSessionEntity.setDoctor(doctor);
        medicalSessionEntity.setSessionStart(medicalSessionCreateRq.getSessionStart());
        medicalSessionEntity.setDuration(medicalSessionCreateRq.getDuration());
        medicalSessionEntity.setPrice(medicalSessionCreateRq.getPrice());
        medicalSessionEntity.setSessionStatus(SessionStatus.CREATED);
        return medicalSessionEntity;
    }

    public static MedicalSessionEntity medicalSessionUpdateRqToMedicalSession(MedicalSessionUpdateRq medicalSessionUpdateRq, MedicalSessionEntity medicalSessionEntity) {
        medicalSessionEntity.setSessionStart(medicalSessionUpdateRq.getSessionStart());
        medicalSessionEntity.setDuration(medicalSessionUpdateRq.getDuration());
        medicalSessionEntity.setPrice(medicalSessionUpdateRq.getPrice());
        return medicalSessionEntity;
    }

    public static List<MedicalSessionAvailableRs> entityListToAvailableRsList(List<MedicalSessionEntity> medicalSessionList) {
        if (isNull(medicalSessionList)) {
            return null;
        }
        return medicalSessionList.stream().map(MedicalSessionMapper::entityToAvailableRs).toList();
    }

    public static MedicalSessionAvailableRs entityToAvailableRs(MedicalSessionEntity medicalSessionEntity) {
        MedicalSessionAvailableRs medicalSessionAvailableRs = new MedicalSessionAvailableRs();
        medicalSessionAvailableRs.setId(medicalSessionEntity.getId());
        medicalSessionAvailableRs.setSessionStart(medicalSessionEntity.getSessionStart());
        medicalSessionAvailableRs.setDuration(medicalSessionEntity.getDuration());
        medicalSessionAvailableRs.setPrice(medicalSessionEntity.getPrice());
        return medicalSessionAvailableRs;
    }

    public static List<MedicalSessionProfileRs> entityListToRsList(List<MedicalSessionEntity> medicalSessionEntities) {
        return medicalSessionEntities.stream().map(MedicalSessionMapper::entityToProfileRs).toList();
    }

    public static MedicalSessionProfileRs entityToProfileRs(MedicalSessionEntity medicalSessionEntity) {
        MedicalSessionProfileRs medicalSessionProfileRs = new MedicalSessionProfileRs();
        medicalSessionProfileRs.setId(medicalSessionEntity.getId());
        medicalSessionProfileRs.setSurname(medicalSessionEntity.getDoctor().getSurname());
        medicalSessionProfileRs.setName(medicalSessionEntity.getDoctor().getName());
        medicalSessionProfileRs.setPatronymic(medicalSessionEntity.getDoctor().getPatronymic());
        medicalSessionProfileRs.setDoctorType(medicalSessionEntity.getDoctor().getDoctorType());
        medicalSessionProfileRs.setSessionStart(medicalSessionEntity.getSessionStart());
        medicalSessionProfileRs.setDuration(medicalSessionEntity.getDuration());
        medicalSessionProfileRs.setPrice(medicalSessionEntity.getPrice());
        return medicalSessionProfileRs;
    }

    public static List<SessionSimpleRs> entitiesToListSimpleRs(List<MedicalSessionEntity> medicalSessionEntityList) {
        return medicalSessionEntityList.stream().map(MedicalSessionMapper::entityToSimpleRs).toList();
    }

    public static SessionSimpleRs entityToSimpleRs(MedicalSessionEntity medicalSessionEntity) {
        SessionSimpleRs sessionSimpleRs = new SessionSimpleRs();
        sessionSimpleRs.setId(medicalSessionEntity.getId());
        sessionSimpleRs.setSessionStart(medicalSessionEntity.getSessionStart());

        return sessionSimpleRs;
    }

    public List<MedicalSessionRs> entitiesToListRs(List<MedicalSessionEntity> medicalSessionEntityList) {
        return medicalSessionEntityList.stream().map(this::entityToRs).toList();
    }

    public MedicalSessionRs entityToRs(MedicalSessionEntity medicalSessionEntity) {
        MedicalSessionRs medicalSessionRs = new MedicalSessionRs();
        ProfileEntity doctor = medicalSessionEntity.getDoctor();
        ProfileEntity patient = medicalSessionEntity.getPatient();
        TreatmentPlanEntity treatmentPlan = medicalSessionEntity.getTreatmentPlan();

        medicalSessionRs.setId(medicalSessionEntity.getId());
        if (nonNull(treatmentPlan)) {
            medicalSessionRs.setTreatmentPlanId(treatmentPlan.getId());
            medicalSessionRs.setDiagnosis(treatmentPlan.getDiagnosis());
            medicalSessionRs.setTherapyPlan(treatmentPlan.getTherapyPlan());
        }
        if (nonNull(patient)) {
            medicalSessionRs.setPatientFullName(ProfileMapper.getFullName(patient));
            if (nonNull(patient.getImage())) {
                medicalSessionRs.setPatientImageUrl(minioService.getImageUrl(patient.getId(), patient.getImage().getId()));
            }
        }
        medicalSessionRs.setDoctorFullName(ProfileMapper.getFullName(doctor));
        if (nonNull(doctor.getImage())) {
            medicalSessionRs.setDoctorImageUrl(minioService.getImageUrl(doctor.getId(), doctor.getImage().getId()));
        }
        medicalSessionRs.setSessionStart(medicalSessionEntity.getSessionStart());
        medicalSessionRs.setSessionStatus(medicalSessionEntity.getSessionStatus());
        medicalSessionRs.setDuration(medicalSessionEntity.getDuration());
        medicalSessionRs.setPrice(medicalSessionEntity.getPrice());

        return medicalSessionRs;
    }
}
