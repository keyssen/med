package com.cpo.med.persistence.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.micrometer.common.lang.Nullable;

public enum SessionStatus {
    CREATED("CREATED"),
    PATIENT_REGISTERED("PATIENT_REGISTERED"),
    PATIENT_DECLINED("PATIENT_DECLINED"),
    DECLINED("DECLINED"),
    IN_PROGRESS("IN_PROGRESS"),
    AWAITING_PAYMENT("AWAITING_PAYMENT"),
    PAID("PAID");

    private final String string;

    SessionStatus(String string) {
        this.string = string;
    }

    @JsonCreator
    public static @Nullable SessionStatus fromCode(String string) {
        for (SessionStatus sessionStatus : SessionStatus.values()) {
            if (sessionStatus.name().equals(string) || sessionStatus.string.equals(string)) {
                return sessionStatus;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return string;
    }
}
