package com.cpo.med.service.statusSessionTransition.transition;

import com.cpo.med.model.enums.SessionStatus;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.service.statusSessionTransition.SessionStatusTransitionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("AWAITING_PAYMENT_TO_PAID")
@RequiredArgsConstructor
public class AwaitingPaymentToPaid implements SessionStatusTransitionStrategy {

    @Override
    public void statusTransition(SessionStatus sessionStatus, MedicalSessionEntity medicalSessionEntity) {
        medicalSessionEntity.setSessionStatus(sessionStatus);
    }
}
