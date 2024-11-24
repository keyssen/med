package com.cpo.med.model.request;

import com.cpo.med.persistence.entity.enums.SessionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ChangeStatusRq {
    private UUID id;
    private SessionStatus status;
}
