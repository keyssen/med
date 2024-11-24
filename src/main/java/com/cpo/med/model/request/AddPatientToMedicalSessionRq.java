package com.cpo.med.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPatientToMedicalSessionRq {
    @NotNull
    private UUID medicalSessionId;
    @NotNull
    private UUID patientId;
}
