package com.cpo.med.service.statusSessionTransition;

import com.cpo.med.model.enums.SessionStatus;
import com.cpo.med.persistence.entity.MedicalSessionEntity;

public interface SessionStatusTransitionStrategy {
    void statusTransition(SessionStatus sessionStatus, MedicalSessionEntity medicalSessionEntity);
}
