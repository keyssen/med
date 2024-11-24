package com.cpo.med.service.statusSessionTransition.transition;

import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.enums.SessionStatus;
import com.cpo.med.service.statusSessionTransition.SessionStatusTransitionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("CREATED_TO_PATIENT_REGISTERED")
@RequiredArgsConstructor
public class CreatedToPatientRegistered implements SessionStatusTransitionStrategy {

    @Override
    public void statusTransition(SessionStatus sessionStatus, MedicalSessionEntity medicalSessionEntity) {
        medicalSessionEntity.setSessionStatus(sessionStatus);
    }
}
