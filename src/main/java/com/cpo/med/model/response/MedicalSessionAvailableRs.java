package com.cpo.med.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalSessionAvailableRs {
    private UUID id;
    private OffsetDateTime sessionStart;
    private Integer duration;
    private BigDecimal price;
}
