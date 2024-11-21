package com.cpo.med.mapper;

import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.model.request.ProfileDefaultCreateRq;
import com.cpo.med.model.request.ProfileUpdateRq;
import com.cpo.med.model.response.DoctorProfileRs;
import com.cpo.med.model.response.PaginationProfileDoctorRs;
import com.cpo.med.model.response.ProfileRs;
import com.cpo.med.model.response.ProfileWithMedicalSessionRs;
import com.cpo.med.persistence.entity.ProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.cpo.med.utils.Constants.FILE_KEY_FORMAT;
import static java.util.Objects.isNull;

public class ProfileMapper {
    public static ProfileEntity defaultCreateProfileRqToProfileEntity(ProfileDefaultCreateRq profileDefaultCreateRq) {
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setPassword(profileDefaultCreateRq.getPassword());
        profileEntity.setEmail(profileDefaultCreateRq.getEmail());
        profileEntity.setOtp(profileDefaultCreateRq.getOtp());
        profileEntity.setProfileRole(ProfileRole.PATIENT);
        return profileEntity;
    }

    public static ProfileEntity updateProfile(ProfileUpdateRq defaultCreateProfileRq, ProfileEntity profileEntity) {
        profileEntity.setSurname(defaultCreateProfileRq.getPassword());
        profileEntity.setName(defaultCreateProfileRq.getPassword());
        profileEntity.setPatronymic(defaultCreateProfileRq.getPassword());
        if (StringUtils.hasText(defaultCreateProfileRq.getPassword())){
            profileEntity.setPassword(defaultCreateProfileRq.getPassword());
        }
        return profileEntity;
    }

    public static List<DoctorProfileRs> entityListToDoctorProfileRsList(List<ProfileEntity> profileEntities) {
        if (isNull(profileEntities)) {
            return null;
        }
        return profileEntities.stream().map(ProfileMapper::profileEntityToDoctorProfileRs).toList();
    }

    public static DoctorProfileRs profileEntityToDoctorProfileRs(ProfileEntity profileEntity) {
        DoctorProfileRs doctorProfileRs = new DoctorProfileRs();
        doctorProfileRs.setId(profileEntity.getId());
        doctorProfileRs.setDoctorType(profileEntity.getDoctorType());
        doctorProfileRs.setSurname(profileEntity.getSurname());
        doctorProfileRs.setName(profileEntity.getName());
        doctorProfileRs.setPatronymic(profileEntity.getPatronymic());
        doctorProfileRs.setPatientSessions(MedicalSessionMapper.entityListToAvailableRsList(profileEntity.getPatientSessions()));
        doctorProfileRs.setImageUrl(String.format(FILE_KEY_FORMAT, profileEntity.getId(), profileEntity.getImage().getId()));
        return doctorProfileRs;
    }

    public static PaginationProfileDoctorRs pageDoctorsToPaginationRs(Page<ProfileEntity> pageDoctors) {
        PaginationProfileDoctorRs paginationProfileDoctorRs = new PaginationProfileDoctorRs();
        paginationProfileDoctorRs.setSize(pageDoctors.getSize());
        paginationProfileDoctorRs.setPage(pageDoctors.getNumber());
        paginationProfileDoctorRs.setTotalCount(pageDoctors.getTotalElements());
        paginationProfileDoctorRs.setDoctors(ProfileMapper.entityListToDoctorProfileRsList(pageDoctors.getContent()));
        return paginationProfileDoctorRs;
    }

    public static ProfileWithMedicalSessionRs entityToWithMedicalSessionRs(ProfileEntity profileEntity) {
        ProfileWithMedicalSessionRs profileWithMedicalSessionRs = new ProfileWithMedicalSessionRs();
        profileWithMedicalSessionRs.setDoctorType(profileEntity.getDoctorType());
        profileWithMedicalSessionRs.setSurname(profileEntity.getSurname());
        profileWithMedicalSessionRs.setName(profileEntity.getName());
        profileWithMedicalSessionRs.setPatronymic(profileEntity.getPatronymic());
        profileWithMedicalSessionRs.setImageUrl(String.format(FILE_KEY_FORMAT, profileEntity.getId(), profileEntity.getImage().getId()));
        if (profileEntity.getProfileRole().equals(ProfileRole.DOCTOR)) {
            profileWithMedicalSessionRs.setSessions(MedicalSessionMapper.entityListToRsList(profileEntity.getDoctorSessions()));
        }
        if (profileEntity.getProfileRole().equals(ProfileRole.PATIENT)) {
            profileWithMedicalSessionRs.setSessions(MedicalSessionMapper.entityListToRsList(profileEntity.getPatientSessions()));
        }
        return profileWithMedicalSessionRs;
    }

    public static ProfileRs entityToRs(ProfileEntity profileEntity) {
        ProfileRs profileRs = new ProfileRs();
        profileRs.setId(profileEntity.getId());
        profileRs.setSurname(profileEntity.getSurname());
        profileRs.setName(profileEntity.getName());
        profileRs.setPatronymic(profileEntity.getPatronymic());
        profileRs.setImageUrl(String.format(FILE_KEY_FORMAT, profileEntity.getId(), profileEntity.getImage().getId()));
        profileRs.setDoctorType(profileEntity.getDoctorType());
        return profileRs;
    }
}
