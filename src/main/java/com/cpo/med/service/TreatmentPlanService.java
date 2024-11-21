package com.cpo.med.service;

import com.cpo.med.mapper.TreatmentPlanMapper;
import com.cpo.med.model.request.TreatmentPlanCreateRq;
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
    public UUID create(TreatmentPlanCreateRq createRq, UUID medicalSessionId) {
        MedicalSessionEntity medicalSession = medicalSessionService.getById(medicalSessionId);
        if (medicalSession.getSessionStatus().equals(IN_PROGRESS)) {
            return treatmentPlanRepository.save(TreatmentPlanMapper.toEntity(createRq, medicalSession)).getId();
        }
        throw new RuntimeException();
    }

    @Transactional
    public UUID update(TreatmentPlanUpdateRq updateRq, UUID treatmentPlanId) {
        TreatmentPlanEntity treatmentPlan = getById(treatmentPlanId);
        if (treatmentPlan.getMedicalSessionEntity().getSessionStatus().equals(IN_PROGRESS)) {
            return treatmentPlanRepository.save(TreatmentPlanMapper.update(updateRq, treatmentPlan)).getId();
        }
        throw new RuntimeException();
    }
}
