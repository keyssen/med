package com.cpo.med.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.micrometer.common.lang.Nullable;

public enum DoctorType {

    THERAPIST("Терапевт"),
    CARDIOLOGIST("Кардиолог"),
    DENTIST("Стоматолог"),
    PEDIATRICIAN("Педиатр"),
    SURGEON("Хирург"),
    NEUROLOGIST("Невролог");

    private final String string;

    DoctorType(String string) {
        this.string = string;
    }

    @JsonCreator
    public static @Nullable DoctorType fromCode(String string) {
        for (DoctorType doctorType : DoctorType.values()) {
            if (doctorType.name().equals(string) || doctorType.string.equals(string)) {
                return doctorType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return string;
    }
}
