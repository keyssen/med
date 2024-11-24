package com.cpo.med.service;

import com.cpo.med.mapper.TreatmentPlanMapper;
import com.cpo.med.model.request.TreatmentPlanUpdateRq;
import com.cpo.med.model.response.TreatmentPlanRs;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.TreatmentPlanEntity;
import com.cpo.med.persistence.repository.TreatmentPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.cpo.med.persistence.entity.enums.SessionStatus.IN_PROGRESS;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class TreatmentPlanService {
    private final MedicalSessionService medicalSessionService;
    private final TreatmentPlanRepository treatmentPlanRepository;

    private TreatmentPlanEntity getById(UUID treatmentPlanId) {
        return treatmentPlanRepository.findById(treatmentPlanId)
                .orElseThrow(RuntimeException::new);
    }

    @Transactional
    public TreatmentPlanRs getRsById(UUID treatmentPlanId) {
        return TreatmentPlanMapper.toResponse(getById(treatmentPlanId));
    }

    @Transactional
    public UUID create(TreatmentPlanUpdateRq createRq, MedicalSessionEntity medicalSession) {
        if (medicalSession.getSessionStatus().equals(IN_PROGRESS)) {
            return treatmentPlanRepository.save(TreatmentPlanMapper.toEntity(createRq, medicalSession)).getId();
        }
        throw new RuntimeException();
    }

    @Transactional
    public UUID update(TreatmentPlanUpdateRq updateRq) {
        MedicalSessionEntity medicalSession = medicalSessionService.getById(updateRq.getMedicalSessionId());
        if (isNull(medicalSession.getTreatmentPlan())) {
            return create(updateRq, medicalSession);
        } else {
            return treatmentPlanRepository.save(TreatmentPlanMapper.update(updateRq, medicalSession.getTreatmentPlan())).getId();
        }
    }
}
