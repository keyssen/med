package com.cpo.med.model.response;

import com.cpo.med.persistence.entity.enums.DoctorType;
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
    private String fullName;
    @EqualsAndHashCode.Exclude
    private List<MedicalSessionAvailableRs> medicalSessions;
    private String imageUrl;
}
