package com.cpo.med.mapper;

import com.cpo.med.model.request.MedicalSessionCreateRq;
import com.cpo.med.model.request.MedicalSessionUpdateRq;
import com.cpo.med.model.response.MedicalSessionAvailableRs;
import com.cpo.med.model.response.MedicalSessionProfileRs;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.ProfileEntity;

import java.util.List;

import static java.util.Objects.isNull;

public class MedicalSessionMapper {
    public static MedicalSessionEntity medicalSessionCreateRqToMedicalSession(MedicalSessionCreateRq medicalSessionCreateRq, ProfileEntity doctor) {
        MedicalSessionEntity medicalSessionEntity = new MedicalSessionEntity();
        medicalSessionEntity.setDoctor(doctor);
        medicalSessionEntity.setSessionStart(medicalSessionCreateRq.getSessionStart());
        medicalSessionEntity.setDuration(medicalSessionCreateRq.getDuration());
        medicalSessionEntity.setPrice(medicalSessionCreateRq.getPrice());
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
        return medicalSessionEntities.stream().map(MedicalSessionMapper::entityToRs).toList();
    }

    public static MedicalSessionProfileRs entityToRs(MedicalSessionEntity medicalSessionEntity) {
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
}
