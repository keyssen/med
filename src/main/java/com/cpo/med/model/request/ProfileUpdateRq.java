package com.cpo.med.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class ProfileUpdateRq {
    @NotBlank
    private String email;
    @NotBlank
    private String surname;
    @NotBlank
    private String name;
    @NotBlank
    private String patronymic;
    private String phone;
    @Size(min = 1, max = 64)
    @NotBlank(message = "Email is required")
    private String password;
    private MultipartFile photo;
}
