package com.cpo.med.model.response;

import com.cpo.med.model.enums.DoctorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileRs {
    private UUID id;
    private DoctorType doctorType;
    private String surname;
    private String name;
    private String patronymic;
    @EqualsAndHashCode.Exclude
    private List<MedicalSessionAvailableRs> patientSessions;
    private String imageUrl;
}
