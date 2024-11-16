package com.cpo.med.model.response;

import com.cpo.med.model.enums.DoctorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalSessionProfileRs {
    private UUID id;
    private String surname;
    private String name;
    private String patronymic;
    private DoctorType doctorType;
    private OffsetDateTime sessionStart;
    private Integer duration;
    private BigDecimal price;
}
