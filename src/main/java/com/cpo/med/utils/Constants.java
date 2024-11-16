package com.cpo.med.utils;

import com.cpo.med.model.enums.SessionStatus;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

import static com.cpo.med.model.enums.SessionStatus.CREATED;
import static com.cpo.med.model.enums.SessionStatus.DECLINED;
import static com.cpo.med.model.enums.SessionStatus.PATIENT_DECLINED;

final public class Constants {
    public static final String FILE_KEY_FORMAT = "%s/%s";
    public static final Set<SessionStatus> sessionStatusesToPatientRegisteredSet = ImmutableSet.of(CREATED, DECLINED, PATIENT_DECLINED);
    public static final List<SessionStatus> sessionStatusesToPatientRegisteredList = List.of(CREATED, DECLINED, PATIENT_DECLINED);
    public static final String PREFIX_ROLE = "ROLE_";
}
