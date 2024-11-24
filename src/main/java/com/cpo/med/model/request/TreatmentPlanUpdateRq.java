package com.cpo.med.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentPlanUpdateRq {
    @NotNull
    private UUID medicalSessionId;
    @NotBlank
    private String diagnosis;
    @NotBlank
    private String therapyPlan;
}
