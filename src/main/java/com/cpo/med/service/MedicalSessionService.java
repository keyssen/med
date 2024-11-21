package com.cpo.med.service;

import com.cpo.med.mapper.MedicalSessionMapper;
import com.cpo.med.persistence.entity.enums.SessionStatus;
import com.cpo.med.model.request.MedicalSessionCreateRq;
import com.cpo.med.model.request.MedicalSessionUpdateRq;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.repository.MedicalSessionRepository;
import com.cpo.med.service.statusSessionTransition.SessionStatusTransitionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static com.cpo.med.utils.Constants.sessionStatusesToPatientRegisteredSet;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class MedicalSessionService {
    private final ProfileService profileService;
    private final MedicalSessionRepository medicalSessionRepository;
    private final Map<String, SessionStatusTransitionStrategy> sessionStatusTransitionStrategyMap;

    @Transactional(readOnly = true)
    public MedicalSessionEntity getById(UUID medicalSessionId) {
        return medicalSessionRepository.findById(medicalSessionId)
                .orElseThrow(RuntimeException::new);
    }

    @Transactional
    public UUID create(MedicalSessionCreateRq medicalSessionCreateRq, UUID doctorId) {
        return medicalSessionRepository.save(MedicalSessionMapper.medicalSessionCreateRqToMedicalSession(medicalSessionCreateRq, profileService.getById(doctorId))).getId();
    }

    @Transactional
    public UUID update(MedicalSessionUpdateRq medicalSessionUpdateRq, UUID medicalSessionId) {
        return medicalSessionRepository.save(MedicalSessionMapper.medicalSessionUpdateRqToMedicalSession(medicalSessionUpdateRq, getById(medicalSessionId))).getId();
    }

    @Transactional
    public UUID addPatient(UUID medicalSessionId, UUID patientId) {
        MedicalSessionEntity medicalSessionEntity = getById(medicalSessionId);
        if (sessionStatusesToPatientRegisteredSet.contains(medicalSessionEntity.getSessionStatus())) {
            medicalSessionEntity.setPatient(profileService.getById(patientId));
            medicalSessionEntity.setSessionStatus(SessionStatus.PATIENT_REGISTERED);
            return medicalSessionRepository.save(medicalSessionEntity).getId();
        }
        throw new RuntimeException();
    }

    @Transactional
    public void changeStatus(UUID medicalSessionId, SessionStatus status) {
        MedicalSessionEntity medicalSessionEntity = getById(medicalSessionId);
        SessionStatusTransitionStrategy sessionStatusTransitionStrategy = sessionStatusTransitionStrategyMap.get(String.format("%s_%s", medicalSessionEntity.getSessionStart(), status));
        if (isNull(sessionStatusTransitionStrategy)) {
            throw new RuntimeException();
        }
        sessionStatusTransitionStrategy.statusTransition(status, medicalSessionEntity);
    }

    @Transactional
    public void delete(UUID medicalSessionId) {
        medicalSessionRepository.deleteById(medicalSessionId);
    }
}
