package com.cpo.med.service;

import com.cpo.med.AbstractDataJpaTest;
import com.cpo.med.DataJPACreator;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.repository.MedicalSessionRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.cpo.med.DataCreator.createTreatmentPlanUpdateRq;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TreatmentPlanServiceTest extends AbstractDataJpaTest {
    private static MedicalSessionEntity medicalSessionEntity;
    private static ProfileEntity profileEntityDoctor;
    private static ProfileEntity profileEntityPatient;
    private final MedicalSessionRepository medicalSessionRepository;
    private final TreatmentPlanService underTest;
    private final DataJPACreator dataJPACreator;

    @BeforeEach
    @Transactional
    public void setUp() {
        profileEntityDoctor = dataJPACreator.createProfileEntityDoctor();
        profileEntityPatient = dataJPACreator.createProfileEntityPatient();
        medicalSessionEntity = dataJPACreator.createMedicalSession(profileEntityDoctor, profileEntityPatient);
    }

    @Test
    public void createTest() {
        UUID treatmentPlanId = underTest.update(createTreatmentPlanUpdateRq(medicalSessionEntity.getId()));
        Assertions.assertNotNull(treatmentPlanId);
    }

    @Test
    public void updateTest() {
        underTest.update(createTreatmentPlanUpdateRq(medicalSessionEntity.getId()));
        UUID treatmentPlanId = underTest.update(createTreatmentPlanUpdateRq(medicalSessionEntity.getId()));
        Assertions.assertNotNull(treatmentPlanId);
    }
}
