package com.cpo.med;

import com.cpo.med.model.request.MedicalSessionCreateRq;
import com.cpo.med.model.request.ProfileDefaultCreateRq;
import com.cpo.med.model.request.ProfileSignUpRq;
import com.cpo.med.model.request.ProfileUpdateRq;
import com.cpo.med.model.request.SearchProfileRq;
import com.cpo.med.model.request.TreatmentPlanUpdateRq;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.TreatmentPlanEntity;
import com.cpo.med.persistence.entity.enums.DoctorType;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.persistence.entity.enums.SessionStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class DataCreator {
    public static ProfileEntity createProfileEntityDoctor(String email, String phone) {
        ProfileEntity profile = new ProfileEntity();
        profile.setId(UUID.randomUUID());
        profile.setProfileRole(ProfileRole.DOCTOR);
        profile.setDoctorType(DoctorType.CARDIOLOGIST);
        profile.setSurname("Иванов");
        profile.setName("Иван");
        profile.setPatronymic("Иванович");
        profile.setPassword("securepassword");
        profile.setEmail(email);
        profile.setOtp(123456);
        profile.setIsActive(true);
//        profile.setPhone("+79991234567");
        profile.setPhone(phone);
        profile.setDoctorSessions(Collections.emptyList());
        profile.setPatientSessions(Collections.emptyList());
        return profile;
    }

    public static ProfileEntity createProfileEntityPatient(String email, String phone) {
        ProfileEntity profile = new ProfileEntity();
        profile.setId(UUID.randomUUID());
        profile.setProfileRole(ProfileRole.PATIENT);
        profile.setDoctorType(null);
        profile.setSurname("Иванов");
        profile.setName("Иван");
        profile.setPatronymic("Иванович");
        profile.setPassword("securepassword");
        profile.setEmail(email);
        profile.setOtp(123456);
        profile.setIsActive(true);
//        profile.setPhone("+79991234567");
        profile.setPhone(phone);
        profile.setDoctorSessions(Collections.emptyList());
        profile.setPatientSessions(Collections.emptyList());
        return profile;
    }

    public static MedicalSessionEntity createMedicalSession(ProfileEntity doctor, ProfileEntity patient, TreatmentPlanEntity treatmentPlan) {
        MedicalSessionEntity session = new MedicalSessionEntity();
        session.setId(UUID.randomUUID());
        session.setDoctor(doctor);
        doctor.getDoctorSessions().add(session);
        session.setPatient(patient);
        if (nonNull(patient)) {
            patient.getPatientSessions().add(session);
        }
        session.setTreatmentPlan(treatmentPlan);
        session.setSessionStart(OffsetDateTime.now());
        session.setDuration(60);
        session.setPrice(BigDecimal.valueOf(100.00));
        session.setSessionStatus(SessionStatus.CREATED);
        return session;
    }

    public static MedicalSessionCreateRq createMedicalSessionCreateRq(UUID doctorId) {
        MedicalSessionCreateRq request = new MedicalSessionCreateRq();
        request.setDoctorId(doctorId);
        request.setSessionStart(OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC));
        request.setDuration(60);
        request.setPrice(BigDecimal.valueOf(100.00));
        return request;
    }

    public static ProfileDefaultCreateRq createProfileDefaultCreateRq(String email) {
        ProfileDefaultCreateRq request = new ProfileDefaultCreateRq();
        request.setPassword("password");
        request.setEmail(email);
        request.setIsActive(false);
        return request;
    }

    public static ProfileSignUpRq createProfileSignUpRq(String email, String phone) {
        ProfileSignUpRq request = new ProfileSignUpRq();
        request.setPassword("password");
        request.setPasswordConfirm("password");
        request.setEmail(email);
        request.setSurname("Surname");
        request.setName("Name");
        request.setPatronymic("Patronymic");
        request.setPhone(phone);
        request.setPhoto(null);
        return request;
    }

    public static ProfileUpdateRq createProfileUpdateRq(String email, String phone) {
        ProfileUpdateRq request = new ProfileUpdateRq();
        request.setEmail(email);
        request.setSurname("Surname1");
        request.setName("Name1");
        request.setPatronymic("Patronymic1");
        request.setPhone(phone);
        request.setPassword("password");
        request.setPhoto(null);
        return request;
    }

    public static SearchProfileRq createSearchProfileRq(DoctorType doctorType, String fullName, ProfileRole profileRole, Integer page, Integer size) {
        SearchProfileRq request = new SearchProfileRq();
        request.setDoctorType(doctorType);
        request.setFullName(fullName);
        request.setProfileRole(profileRole);
        request.setPage(nonNull(page) ? page : 0);
        request.setSize(nonNull(size) ? size : 10);
        return request;
    }

    public static TreatmentPlanUpdateRq createTreatmentPlanUpdateRq(UUID medicalSessionId) {
        TreatmentPlanUpdateRq request = new TreatmentPlanUpdateRq();
        request.setMedicalSessionId(medicalSessionId);
        request.setDiagnosis("diagnosis");
        request.setTherapyPlan("therapyPlan");
        return request;
    }
}
