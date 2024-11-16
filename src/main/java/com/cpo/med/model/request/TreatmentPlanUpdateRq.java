package com.cpo.med.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentPlanUpdateRq {
    @NotBlank
    private String diagnosis;
    @NotBlank
    private String therapyPlan;
}