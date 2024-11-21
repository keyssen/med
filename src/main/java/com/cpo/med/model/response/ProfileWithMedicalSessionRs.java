package com.cpo.med.model.response;

import com.cpo.med.persistence.entity.enums.DoctorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileWithMedicalSessionRs {
    private DoctorType doctorType;
    private String surname;
    private String name;
    private String patronymic;
    @EqualsAndHashCode.Exclude
    private List<MedicalSessionProfileRs> sessions;
    private String imageUrl;
}
