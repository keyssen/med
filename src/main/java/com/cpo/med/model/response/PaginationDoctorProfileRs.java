package com.cpo.med.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDoctorProfileRs {
    @EqualsAndHashCode.Exclude
    private List<DoctorProfileRs> doctors;
    private Integer page;
    private Integer size;
    private Long totalCount;
}
