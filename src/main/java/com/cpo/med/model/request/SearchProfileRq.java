package com.cpo.med.model.request;

import com.cpo.med.persistence.entity.enums.DoctorType;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchProfileRq {
    private DoctorType doctorType;
    private String fullName;
    private ProfileRole profileRole;
    private Integer page;
    private Integer size;
}
