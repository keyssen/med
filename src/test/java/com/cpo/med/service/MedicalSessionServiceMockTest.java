package com.cpo.med.service;

import com.cpo.med.mapper.MedicalSessionMapper;
import com.cpo.med.model.request.MedicalSessionCreateRq;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.SessionStatus;
import com.cpo.med.persistence.repository.MedicalSessionRepository;
import com.cpo.med.service.statusSessionTransition.SessionStatusTransitionStrategy;
import com.cpo.med.service.statusSessionTransition.transition.CreatedToPatientRegistered;
import com.cpo.med.service.statusSessionTransition.transition.PatientRegisteredToInProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static com.cpo.med.persistence.entity.enums.SessionStatus.CREATED;
import static com.cpo.med.persistence.entity.enums.SessionStatus.PATIENT_REGISTERED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MedicalSessionServiceMockTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private MedicalSessionRepository medicalSessionRepository;

    @Mock
    public Map<String, SessionStatusTransitionStrategy> sessionStatusTransitionStrategyMap;

    @Mock
    private MedicalSessionMapper medicalSessionMapper;

    @InjectMocks
    private MedicalSessionService medicalSessionService;

    private MedicalSessionEntity testSession;
    private ProfileEntity testDoctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        medicalSessionService = new MedicalSessionService(
                profileService,
                medicalSessionRepository,
                sessionStatusTransitionStrategyMap,
                medicalSessionMapper
        );

        // Инициализация тестовых данных
        testDoctor = new ProfileEntity();
        testDoctor.setId(UUID.randomUUID());

        testSession = new MedicalSessionEntity();
        testSession.setId(UUID.randomUUID());
        testSession.setDoctor(testDoctor);
        testSession.setSessionStatus(SessionStatus.IN_PROGRESS);
    }

    @Test
    void getById_shouldReturnMedicalSession() {
        // Мокирование
        when(medicalSessionRepository.findById(testSession.getId())).thenReturn(Optional.of(testSession));

        // Тестирование
        MedicalSessionEntity result = medicalSessionService.getById(testSession.getId());

        // Проверки
        assertEquals(testSession, result);
        verify(medicalSessionRepository, times(1)).findById(testSession.getId());
    }

    @Test
    void getById_shouldThrowExceptionIfNotFound() {
        // Мокирование
        when(medicalSessionRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Тестирование
        assertThrows(RuntimeException.class, () -> medicalSessionService.getById(UUID.randomUUID()));

        // Проверки
        verify(medicalSessionRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void create_shouldSaveAndReturnId() {
        // Подготовка данных
        MedicalSessionCreateRq createRq = new MedicalSessionCreateRq();
        createRq.setDoctorId(testDoctor.getId());
        MedicalSessionEntity newSession = new MedicalSessionEntity();
        newSession.setId(UUID.randomUUID());

        // Мокирование
        when(profileService.getById(testDoctor.getId())).thenReturn(testDoctor);
        when(medicalSessionRepository.save(any(MedicalSessionEntity.class))).thenReturn(newSession);

        // Тестирование
        UUID resultId = medicalSessionService.create(createRq);

        // Проверки
        assertEquals(newSession.getId(), resultId);
        verify(profileService, times(1)).getById(testDoctor.getId());
        verify(medicalSessionRepository, times(1)).save(any(MedicalSessionEntity.class));
    }

    @Test
    void addPatient_shouldSetPatientAndChangeStatus() {
        // Подготовка данных
        UUID patientId = UUID.randomUUID();
        ProfileEntity patient = new ProfileEntity();
        patient.setId(patientId);
        testSession.setSessionStatus(CREATED);

        // Мокирование
        when(profileService.getById(patientId)).thenReturn(patient);
        when(medicalSessionRepository.findById(testSession.getId())).thenReturn(Optional.of(testSession));

        CreatedToPatientRegistered strategy = new CreatedToPatientRegistered();

        when(sessionStatusTransitionStrategyMap.get("CREATED_TO_PATIENT_REGISTERED")).thenReturn(strategy);

        // Тестирование
        medicalSessionService.addPatient(testSession.getId(), patientId);

        // Проверки
        assertEquals(patient, testSession.getPatient());
        assertEquals(PATIENT_REGISTERED, testSession.getSessionStatus());
        verify(profileService, times(1)).getById(patientId);
        verify(medicalSessionRepository, times(1)).save(testSession);
    }

    @Test
    void changeStatus_shouldTransitionToValidStatus() {
        // Подготовка данных
        SessionStatus currentStatus = SessionStatus.PATIENT_REGISTERED;
        SessionStatus newStatus = SessionStatus.IN_PROGRESS;
        testSession.setSessionStatus(currentStatus);

        PatientRegisteredToInProgress strategy = new PatientRegisteredToInProgress();

        // Мокирование
        when(sessionStatusTransitionStrategyMap.get("PATIENT_REGISTERED_TO_IN_PROGRESS")).thenReturn(strategy);

        // Тестирование
        medicalSessionService.changeStatus(testSession, newStatus);

        // Проверки
        verify(sessionStatusTransitionStrategyMap, times(1)).get("PATIENT_REGISTERED_TO_IN_PROGRESS");
//        verify(strategyMock, times(1)).statusTransition(eq(newStatus), eq(testSession));
        assertEquals(newStatus, testSession.getSessionStatus(), "Статус должен быть изменен на IN_PROGRESS");
    }

    @Test
    void changeStatus_shouldThrowExceptionForInvalidTransition() {
        // Подготовка данных
        SessionStatus currentStatus = CREATED;
        SessionStatus invalidStatus = SessionStatus.PAID; // Невозможный переход
        testSession.setSessionStatus(currentStatus);

        // Мокирование
        when(sessionStatusTransitionStrategyMap.get("CREATED_TO_PAID")).thenReturn(null);

        // Тестирование
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                medicalSessionService.changeStatus(testSession, invalidStatus)
        );

        // Проверки
        assertEquals(RuntimeException.class, exception.getClass(), "Ожидается RuntimeException");
        verify(sessionStatusTransitionStrategyMap, times(1)).get("CREATED_TO_PAID");
    }
}
