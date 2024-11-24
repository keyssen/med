package com.cpo.med.model.request;

import jakarta.validation.constraints.NotBlank;
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
    private Boolean isActive;
}
