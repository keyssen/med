package com.cpo.med.model.response;

import com.cpo.med.persistence.entity.enums.DoctorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRs {
    private UUID id;
    private String surname;
    private String name;
    private String patronymic;
    private String password;
    private String imageUrl;
    private DoctorType doctorType;
}
