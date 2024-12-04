package com.cpo.med.service;

import com.cpo.med.AbstractDataJpaTest;
import com.cpo.med.DataJPACreator;
import com.cpo.med.model.request.MedicalSessionCreateRq;
import com.cpo.med.model.response.MedicalSessionRs;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.SessionStatus;
import com.cpo.med.persistence.repository.MedicalSessionRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.cpo.med.DataCreator.createMedicalSession;
import static com.cpo.med.DataCreator.createMedicalSessionCreateRq;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MedicalSessionServiceTest extends AbstractDataJpaTest {
    private static MedicalSessionEntity medicalSessionEntity;
    private static ProfileEntity profileEntityDoctor;
    private final MedicalSessionRepository medicalSessionRepository;
    private final MedicalSessionService underTest;
    private final DataJPACreator dataJPACreator;

    @BeforeEach
    @Transactional
    public void setUp() {
        profileEntityDoctor = dataJPACreator.createProfileEntityDoctor();
        ProfileEntity profileEntityPatient = dataJPACreator.createProfileEntityPatient();
        MedicalSessionEntity medicalSession = createMedicalSession(profileEntityDoctor, profileEntityPatient, null);
        medicalSessionEntity = medicalSessionRepository.saveAndFlush(medicalSession);
    }

    @Test
    public void getByIdTest() {
        MedicalSessionEntity actual = underTest.getById(medicalSessionEntity.getId());
        Assertions.assertEquals(medicalSessionEntity.getId(), actual.getId());
    }

    @Test
    public void getAvailableRsByDoctorId() {
        List<MedicalSessionRs> actual = underTest.getAllRs();
        Assertions.assertEquals(medicalSessionRepository.findAll().size(), actual.size());
    }

    @Test
    public void getRsByDoctorId() {
        List<MedicalSessionRs> actual = underTest.getRsByDoctorId(medicalSessionEntity.getDoctor().getId());
        Assertions.assertEquals(medicalSessionEntity.getDoctor().getDoctorSessions().size(), actual.size());
    }

    @Test
    public void getRsByPatientId() {
        List<MedicalSessionRs> actual = underTest.getRsByPatientId(medicalSessionEntity.getPatient().getId());
        Assertions.assertEquals(medicalSessionEntity.getPatient().getPatientSessions().size(), actual.size());
    }

    @Test
    public void getRsById() {
        MedicalSessionRs actual = underTest.getRsById(medicalSessionEntity.getId());
        Assertions.assertEquals(medicalSessionEntity.getId(), actual.getId());
    }

    @Test
    public void create() {
        MedicalSessionCreateRq medicalSessionCreateRq = createMedicalSessionCreateRq(profileEntityDoctor.getId());
        UUID actual = underTest.create(medicalSessionCreateRq);
        Assertions.assertEquals(medicalSessionRepository.findById(actual).get().getId(), actual);
    }

    @Test
    public void addPatient() {
        ProfileEntity profile = dataJPACreator.createProfileEntityDoctor();
        MedicalSessionEntity medicalSession = createMedicalSession(profile, null, null);
        medicalSession = medicalSessionRepository.saveAndFlush(medicalSession);

        ProfileEntity actual = dataJPACreator.createProfileEntityPatient();
        underTest.addPatient(medicalSession.getId(), actual.getId());
    }

    @Test
    public void changeStatus() {
        underTest.changeStatus(medicalSessionEntity, SessionStatus.PATIENT_REGISTERED);
        medicalSessionRepository.saveAndFlush(medicalSessionEntity);
        MedicalSessionEntity medicalSession = medicalSessionRepository.findById(medicalSessionEntity.getId()).get();
        Assertions.assertEquals(medicalSession.getSessionStatus(), SessionStatus.PATIENT_REGISTERED);
    }
}
