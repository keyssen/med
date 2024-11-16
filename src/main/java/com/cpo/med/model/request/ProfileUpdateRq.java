package com.cpo.med.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileUpdateRq {
    private String surname;
    private String name;
    private String patronymic;
    private String password;
}
