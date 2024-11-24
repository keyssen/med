package com.cpo.med.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSignUpRq {
    @NotBlank
    @Size(min = 1, max = 64)
    private String password;
    @NotBlank
    @Size(min = 1, max = 64)
    private String passwordConfirm;
    @NotBlank
    @Size(min = 7, max = 64)
//    @Pattern(regexp = "^(.+)@(\\S+)$", message = "Incorrect email value")
    private String email;
    @NotBlank
    @Size(min = 2, max = 64)
    private String surname;
    @NotBlank
    @Size(min = 2, max = 64)
    private String name;
    @NotBlank
    @Size(min = 2, max = 64)
    private String patronymic;
    @Size(min = 1, max = 16)
//    @Pattern(regexp = "^\\+7\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$", message = "Incorrect phone value")
    private String phone;
    private MultipartFile photo;
}
