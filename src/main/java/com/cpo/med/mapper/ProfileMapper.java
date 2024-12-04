package com.cpo.med.mapper;

import com.cpo.med.model.request.ProfileDefaultCreateRq;
import com.cpo.med.model.request.ProfileSignUpRq;
import com.cpo.med.model.request.ProfileUpdateRq;
import com.cpo.med.model.response.DoctorProfileRs;
import com.cpo.med.model.response.PaginationDoctorProfileRs;
import com.cpo.med.model.response.ProfileSimpleRs;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class ProfileMapper {


    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MinioService minioService;

    public static ProfileEntity defaultCreateProfileRqToProfileEntity(ProfileDefaultCreateRq profileDefaultCreateRq) {
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setPassword(passwordEncoder.encode(profileDefaultCreateRq.getPassword()));
        profileEntity.setEmail(profileDefaultCreateRq.getEmail());
        profileEntity.setIsActive(profileDefaultCreateRq.getIsActive());
        profileEntity.setProfileRole(ProfileRole.PATIENT);
        return profileEntity;
    }

    public static ProfileEntity profileSignUpRqToProfileEntity(ProfileSignUpRq profileDefaultCreateRq) {
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setPassword(passwordEncoder.encode(profileDefaultCreateRq.getPassword()));
        profileEntity.setEmail(profileDefaultCreateRq.getEmail());
        profileEntity.setSurname(profileDefaultCreateRq.getSurname());
        profileEntity.setName(profileDefaultCreateRq.getName());
        profileEntity.setPatronymic(profileDefaultCreateRq.getPatronymic());
        profileEntity.setPhone(profileDefaultCreateRq.getPhone());
        profileEntity.setProfileRole(ProfileRole.PATIENT);
        return profileEntity;
    }

    public static ProfileEntity updateProfile(ProfileUpdateRq updateRq, ProfileEntity profileEntity) {
        profileEntity.setSurname(updateRq.getSurname());
        profileEntity.setName(updateRq.getName());
        profileEntity.setPatronymic(updateRq.getPatronymic());
        if (StringUtils.hasText(updateRq.getPassword())) {
            profileEntity.setPassword(passwordEncoder.encode(updateRq.getPassword()));
        }
        return profileEntity;
    }

    public static ProfileUpdateRq entityToProfileUpdateRq(ProfileEntity profileEntity) {
        ProfileUpdateRq profileUpdateRq = new ProfileUpdateRq();
        profileUpdateRq.setEmail(profileEntity.getEmail());
        profileUpdateRq.setSurname(profileEntity.getSurname());
        profileUpdateRq.setName(profileEntity.getName());
        profileUpdateRq.setPatronymic(profileEntity.getPatronymic());
        profileUpdateRq.setPhone(profileEntity.getPhone());
        profileUpdateRq.setPassword(profileEntity.getPassword());
        return profileUpdateRq;
    }

    public static List<ProfileSimpleRs> profilesToProfilesSimpleRs(List<ProfileEntity> profileEntities) {
        return profileEntities.stream().map(ProfileMapper::profileEntityToSimpleRs).toList();
    }

    public static ProfileSimpleRs profileEntityToSimpleRs(ProfileEntity profileEntity) {
        ProfileSimpleRs profileSimpleRs = new ProfileSimpleRs();
        profileSimpleRs.setId(profileEntity.getId());
        profileSimpleRs.setFullName(getFullName(profileEntity));
        return profileSimpleRs;
    }

    public static String getFullName(ProfileEntity profileEntity) {
        String surname = profileEntity.getSurname();
        String name = profileEntity.getName();
        String patronymic = profileEntity.getPatronymic();

        return String.join(" ",
                nonNull(surname) ? surname : "",
                nonNull(name) ? name : "",
                nonNull(patronymic) ? patronymic : ""
        ).trim();
    }

    public PaginationDoctorProfileRs pageDoctorsToPaginationRs(Page<ProfileEntity> pageDoctors) {
        PaginationDoctorProfileRs paginationDoctorProfileRs = new PaginationDoctorProfileRs();
        paginationDoctorProfileRs.setSize(pageDoctors.getSize());
        paginationDoctorProfileRs.setPage(pageDoctors.getNumber());
        paginationDoctorProfileRs.setTotalCount(pageDoctors.getTotalElements());
        paginationDoctorProfileRs.setDoctors(entityListToDoctorProfileRsList(pageDoctors.getContent()));
        return paginationDoctorProfileRs;
    }

    public List<DoctorProfileRs> entityListToDoctorProfileRsList(List<ProfileEntity> profileEntities) {
        if (isNull(profileEntities)) {
            return null;
        }
        return profileEntities.stream().map(this::profileEntityToDoctorProfileRs).toList();
    }

    public DoctorProfileRs profileEntityToDoctorProfileRs(ProfileEntity profileEntity) {
        DoctorProfileRs doctorProfileRs = new DoctorProfileRs();
        doctorProfileRs.setId(profileEntity.getId());
        doctorProfileRs.setDoctorType(profileEntity.getDoctorType());
        doctorProfileRs.setFullName(getFullName(profileEntity));
        doctorProfileRs.setMedicalSessions(MedicalSessionMapper.entityListToAvailableRsList(profileEntity.getDoctorSessions()));
        if (nonNull(profileEntity.getImage())) {
            doctorProfileRs.setImageUrl(minioService.getImageUrl(profileEntity.getId(), profileEntity.getImage().getId()));
        }
        return doctorProfileRs;
    }

}
