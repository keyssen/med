package com.cpo.med.service;

import com.cpo.med.mapper.MedicalSessionMapper;
import com.cpo.med.model.request.MedicalSessionCreateRq;
import com.cpo.med.model.request.MedicalSessionUpdateRq;
import com.cpo.med.model.response.MedicalSessionAvailableRs;
import com.cpo.med.model.response.MedicalSessionRs;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.enums.SessionStatus;
import com.cpo.med.persistence.repository.MedicalSessionRepository;
import com.cpo.med.service.statusSessionTransition.SessionStatusTransitionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cpo.med.persistence.entity.enums.SessionStatus.PATIENT_REGISTERED;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class MedicalSessionService {
    private final ProfileService profileService;
    private final MedicalSessionRepository medicalSessionRepository;
    private final Map<String, SessionStatusTransitionStrategy> sessionStatusTransitionStrategyMap;
    private final MedicalSessionMapper medicalSessionMapper;

    @Transactional(readOnly = true)
    public MedicalSessionEntity getById(UUID medicalSessionId) {
        return medicalSessionRepository.findById(medicalSessionId)
                .orElseThrow(RuntimeException::new);
    }

    @Transactional(readOnly = true)
    public List<MedicalSessionAvailableRs> getAvailableRsByDoctorId(UUID doctorId) {
        return MedicalSessionMapper.entityListToAvailableRsList(medicalSessionRepository.findByDoctorId(doctorId));
    }

    @Transactional(readOnly = true)
    public List<MedicalSessionRs> getAllRs() {
        return medicalSessionMapper.entitiesToListRs(medicalSessionRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<MedicalSessionRs> getRsByDoctorId(UUID doctorId) {
        return medicalSessionMapper.entitiesToListRs(medicalSessionRepository.findByDoctorId(doctorId));
    }

    @Transactional(readOnly = true)
    public List<MedicalSessionRs> getRsByPatientId(UUID patientId) {
        return medicalSessionMapper.entitiesToListRs(medicalSessionRepository.findByPatientId(patientId));
    }

    @Transactional(readOnly = true)
    public MedicalSessionRs getRsById(UUID id) {
        MedicalSessionEntity medicalSession = getById(id);
        return medicalSessionMapper.entityToRs(medicalSession);
    }

    @Transactional
    public UUID create(MedicalSessionCreateRq medicalSessionCreateRq) {
        return medicalSessionRepository.save(MedicalSessionMapper.medicalSessionCreateRqToMedicalSession(medicalSessionCreateRq, profileService.getById(medicalSessionCreateRq.getDoctorId()))).getId();
    }

    @Transactional
    public void addPatient(UUID medicalSessionId, UUID patientId) {
        MedicalSessionEntity medicalSession = getById(medicalSessionId);
        changeStatus(medicalSession, PATIENT_REGISTERED);
        medicalSession.setPatient(profileService.getById(patientId));
        medicalSessionRepository.save(medicalSession);
    }

    @Transactional
    public void changeStatus(MedicalSessionEntity medicalSession, SessionStatus status) {
        SessionStatusTransitionStrategy sessionStatusTransitionStrategy = sessionStatusTransitionStrategyMap.get(String.format("%s_TO_%s", medicalSession.getSessionStatus().name(), status.name()));
        if (isNull(sessionStatusTransitionStrategy)) {
            throw new RuntimeException();
        }
        sessionStatusTransitionStrategy.statusTransition(status, medicalSession);
    }
}
