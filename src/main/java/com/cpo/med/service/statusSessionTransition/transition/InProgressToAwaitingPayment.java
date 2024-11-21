package com.cpo.med.service.statusSessionTransition.transition;

import com.cpo.med.persistence.entity.enums.SessionStatus;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.service.statusSessionTransition.SessionStatusTransitionStrategy;
import org.springframework.stereotype.Component;

@Component("IN_PROGRESS_TO_AWAITING_PAYMENT")
public class InProgressToAwaitingPayment implements SessionStatusTransitionStrategy {
    @Override
    public void statusTransition(SessionStatus sessionStatus, MedicalSessionEntity medicalSessionEntity) {
        medicalSessionEntity.setSessionStatus(sessionStatus);
    }
}
