package com.cpo.med.persistence.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.micrometer.common.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;

import static com.cpo.med.utils.Constants.PREFIX_ROLE;

public enum ProfileRole implements GrantedAuthority {
    PATIENT("PATIENT"),
    ADMINISTRATOR("ADMINISTRATOR"),
    DOCTOR("DOCTOR");

    private final String string;

    ProfileRole(String string) {
        this.string = string;
    }

    @JsonCreator
    public static @Nullable ProfileRole fromCode(String string) {
        for (ProfileRole profileRole : ProfileRole.values()) {
            if (profileRole.name().equals(string) || profileRole.string.equals(string)) {
                return profileRole;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public String getAuthority() {
        return PREFIX_ROLE + this.name();
    }
    public static final class AsString {
        public static final String PATIENT = PREFIX_ROLE + "PATIENT";
        public static final String ADMINISTRATOR = PREFIX_ROLE + "ADMINISTRATOR";
        public static final String DOCTOR = PREFIX_ROLE + "DOCTOR";
    }
}
