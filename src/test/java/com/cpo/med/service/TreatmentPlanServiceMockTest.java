package com.cpo.med.service;

import com.cpo.med.mapper.TreatmentPlanMapper;
import com.cpo.med.model.request.TreatmentPlanUpdateRq;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.TreatmentPlanEntity;
import com.cpo.med.persistence.repository.TreatmentPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static com.cpo.med.persistence.entity.enums.SessionStatus.AWAITING_PAYMENT;
import static com.cpo.med.persistence.entity.enums.SessionStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TreatmentPlanServiceMockTest {

    @Mock
    private MedicalSessionService medicalSessionService;

    @Mock
    private TreatmentPlanRepository treatmentPlanRepository;

    @InjectMocks
    private TreatmentPlanService treatmentPlanService;

    @Mock
    private TreatmentPlanMapper treatmentPlanMapper;

    private MedicalSessionEntity medicalSession;
    private TreatmentPlanUpdateRq updateRq;
    private TreatmentPlanEntity treatmentPlan;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        medicalSession = new MedicalSessionEntity();
        medicalSession.setId(UUID.randomUUID());
        medicalSession.setSessionStatus(IN_PROGRESS);

        treatmentPlan = new TreatmentPlanEntity();
        treatmentPlan.setId(UUID.randomUUID());

        updateRq = new TreatmentPlanUpdateRq();
        updateRq.setMedicalSessionId(medicalSession.getId());
    }

    @Test
    void create_ShouldReturnUuid_WhenSessionInProgress() {
        // Мокирование
        when(treatmentPlanRepository.save(any(TreatmentPlanEntity.class))).thenReturn(treatmentPlan);

        // Тестирование
        UUID result = treatmentPlanService.create(updateRq, medicalSession);

        // Проверка
        assertEquals(treatmentPlan.getId(), result);
        verify(treatmentPlanRepository, times(1)).save(any(TreatmentPlanEntity.class));
    }

    @Test
    void create_ShouldThrowException_WhenSessionNotInProgress() {
        // Подготовка
        medicalSession.setSessionStatus(AWAITING_PAYMENT);

        // Тестирование и проверка
        assertThrows(RuntimeException.class, () -> treatmentPlanService.create(updateRq, medicalSession));
        verify(treatmentPlanRepository, never()).save(any(TreatmentPlanEntity.class));
    }

    @Test
    void update_ShouldCallCreate_WhenNoTreatmentPlanExists() {
        // Мокирование
        when(medicalSessionService.getById(updateRq.getMedicalSessionId())).thenReturn(medicalSession);
        when(treatmentPlanRepository.save(any(TreatmentPlanEntity.class))).thenReturn(treatmentPlan);

        // Тестирование
        UUID result = treatmentPlanService.update(updateRq);

        // Проверка
        assertEquals(treatmentPlan.getId(), result);
        verify(treatmentPlanRepository, times(1)).save(any(TreatmentPlanEntity.class));
    }

    @Test
    void update_ShouldSaveUpdatedTreatmentPlan_WhenTreatmentPlanExists() {
        // Подготовка
        medicalSession.setTreatmentPlan(treatmentPlan);

        // Мокирование
        when(medicalSessionService.getById(updateRq.getMedicalSessionId())).thenReturn(medicalSession);
        when(treatmentPlanRepository.save(any(TreatmentPlanEntity.class))).thenReturn(treatmentPlan);

        // Тестирование
        UUID result = treatmentPlanService.update(updateRq);

        // Проверка
        assertEquals(treatmentPlan.getId(), result);
        verify(treatmentPlanRepository, times(1)).save(any(TreatmentPlanEntity.class));
    }
}
