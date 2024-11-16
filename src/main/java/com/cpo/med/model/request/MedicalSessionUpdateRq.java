package com.cpo.med.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class MedicalSessionUpdateRq {
    @NotNull
    private OffsetDateTime sessionStart;
    @NotNull
    @Min(value = 1, message = "Значение должно быть больше 0")
    private Integer duration;
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true, message = "Значение должно быть больше 0")
    private BigDecimal price;
}
