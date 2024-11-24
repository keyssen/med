package com.cpo.med.model.response;

import com.cpo.med.persistence.entity.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalSessionRs {
    private UUID id;
    private UUID treatmentPlanId;
    private String doctorFullName;
    private String doctorImageUrl;
    private String patientFullName;
    private String patientImageUrl;
    private OffsetDateTime sessionStart;
    private SessionStatus sessionStatus;
    private Integer duration;
    private BigDecimal price;
    private String diagnosis;
    private String therapyPlan;
}
