package com.cpo.med.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDefaultCreateRq {
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotNull
    @Min(value = 1, message = "Значение должно быть больше 0")
    private Integer otp;
    private Boolean isActive;
}
