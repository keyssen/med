package com.cpo.med.persistence.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.micrometer.common.lang.Nullable;

public enum SessionStatus {
    CREATED("Создано"),
    PATIENT_REGISTERED("Пациент зарегистрирован"),
    PATIENT_DECLINED("Пациент отказался"),
    DECLINED("Отказано пациенту"),
    IN_PROGRESS("В процессе"),
    AWAITING_PAYMENT("Ожидание оплаты"),
    PAID("Оплачено");

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
