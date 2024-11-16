package com.cpo.med.service.statusSessionTransition.transition;

import com.cpo.med.model.enums.SessionStatus;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.service.statusSessionTransition.SessionStatusTransitionStrategy;
import org.springframework.stereotype.Component;

@Component("PATIENT_REGISTERED_TO_DECLINED")
public class PatientRegisteredToDeclined implements SessionStatusTransitionStrategy {
    @Override
    public void statusTransition(SessionStatus sessionStatus, MedicalSessionEntity medicalSessionEntity) {
        medicalSessionEntity.setSessionStatus(sessionStatus);
        medicalSessionEntity.setPatient(null);
    }
}