package com.cpo.med.mapper;

import com.cpo.med.model.request.TreatmentPlanCreateRq;
import com.cpo.med.model.request.TreatmentPlanUpdateRq;
import com.cpo.med.model.response.TreatmentPlanRs;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.TreatmentPlanEntity;

public class TreatmentPlanMapper {
    public static TreatmentPlanRs toResponse(TreatmentPlanEntity entity) {
        return new TreatmentPlanRs(
                entity.getId(),
                entity.getDiagnosis(),
                entity.getTherapyPlan()
        );
    }

    public static TreatmentPlanEntity toEntity(TreatmentPlanUpdateRq updateRq, MedicalSessionEntity medicalSession) {
        TreatmentPlanEntity treatmentPlan = new TreatmentPlanEntity();
        treatmentPlan.setDiagnosis(updateRq.getDiagnosis());
        treatmentPlan.setTherapyPlan(updateRq.getTherapyPlan());
        treatmentPlan.setMedicalSessionEntity(medicalSession);
        medicalSession.setTreatmentPlan(treatmentPlan);
        return treatmentPlan;
    }

    public static TreatmentPlanEntity update(TreatmentPlanUpdateRq updateRq, TreatmentPlanEntity entity) {
        entity.setDiagnosis(updateRq.getDiagnosis());
        entity.setTherapyPlan(updateRq.getTherapyPlan());
        return entity;
    }
}
