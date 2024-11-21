package com.cpo.med.service;

import com.cpo.med.mapper.ProfileMapper;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.model.request.ProfileDefaultCreateRq;
import com.cpo.med.model.request.ProfileUpdateRq;
import com.cpo.med.model.request.SearchProfileRq;
import com.cpo.med.model.response.PaginationProfileDoctorRs;
import com.cpo.med.model.response.ProfileRs;
import com.cpo.med.model.response.ProfileWithMedicalSessionRs;
import com.cpo.med.persistence.entity.ImageEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.repository.ProfileCustomRepository;
import com.cpo.med.persistence.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProfileService extends DefaultOAuth2UserService implements UserDetailsService {
    private final ImageService imageService;
    private final S3Service s3Service;
    private final ProfileCustomRepository profileCustomRepository;
    private final ProfileRepository profileRepository;
    private final JavaMailSender javaMailSender;
    @Value("{spring.mail.username}")
    private String emailFrom;

    @Transactional
    public void callRepositorySave(ProfileEntity profileEntity) {
        profileRepository.save(profileEntity);
    }

    @Transactional(readOnly = true)
    public ProfileEntity getById(UUID profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(RuntimeException::new);
    }

    @Transactional(readOnly = true)
    public ProfileEntity findByEmail(String email) {
        return profileRepository.findOneByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public ProfileWithMedicalSessionRs getWithMedicalSessionRsById(UUID profileId) {
        ProfileEntity profileEntity = getById(profileId);
        return ProfileMapper.entityToWithMedicalSessionRs(profileEntity);
    }

    @Transactional(readOnly = true)
    public ProfileRs getWithRsById(UUID profileId) {
        ProfileEntity profileEntity = getById(profileId);
        return ProfileMapper.entityToRs(profileEntity);
    }


    public PaginationProfileDoctorRs getDoctorProfiles(SearchProfileRq searchProfileRq) {
        Page<ProfileEntity> doctors = profileCustomRepository.doctorFindProfile(searchProfileRq);
        return ProfileMapper.pageDoctorsToPaginationRs(doctors);
    }

    public PaginationProfileDoctorRs getProfiles(SearchProfileRq searchProfileRq) {
        Page<ProfileEntity> doctors = profileCustomRepository.adminFindProfile(searchProfileRq);
        return ProfileMapper.pageDoctorsToPaginationRs(doctors);
    }


    @Transactional
    public ProfileEntity defaultRegistration(ProfileDefaultCreateRq profileDefaultCreateRq) {
        return profileRepository.save(ProfileMapper.defaultCreateProfileRqToProfileEntity(profileDefaultCreateRq));
    }

    @Transactional
    public UUID create(ProfileDefaultCreateRq profileDefaultCreateRq, ProfileRole profileRole) {
        ProfileEntity profileEntity = ProfileMapper.defaultCreateProfileRqToProfileEntity(profileDefaultCreateRq);
        profileEntity.setProfileRole(profileRole);
        return profileRepository.save(profileEntity).getId();
    }

    @Transactional
    public UUID update(UUID profileId, ProfileUpdateRq profileUpdateRq) {
        ProfileEntity profileEntity = getById(profileId);
        return profileRepository.save(ProfileMapper.updateProfile(profileUpdateRq, profileEntity)).getId();
    }

    @Transactional
    public UUID changeOtp(UUID profileId, Integer otp) {
        ProfileEntity profileEntity = getById(profileId);
        profileEntity.setOtp(otp);
        return profileRepository.save(profileEntity).getId();
    }

    @Transactional(readOnly = true)
    public Boolean verificationOtp(UUID profileId, Integer otp) {
        ProfileEntity profileEntity = getById(profileId);
        return profileEntity.getOtp().equals(otp);
    }

    @Transactional
    public String addImage(UUID profileId, MultipartFile file) {
        ImageEntity imageEntity = imageService.createImage(getById(profileId));
        if (file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload.");
        }
        try {
            return s3Service.addFile(profileId, imageEntity.getId(), file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file.");
        }
    }

    @Transactional
    public void deleteImage(UUID profileId) {
        ProfileEntity profileEntity = getById(profileId);
        s3Service.deleteFile(profileEntity.getId(), profileEntity.getImage().getId());
    }

    @Transactional
    public void delete(UUID profileId) {
        profileRepository.deleteById(profileId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final ProfileEntity profileEntity = findByEmail(username);
        if (profileEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(
                profileEntity.getEmail(), profileEntity.getPassword(), Collections.singleton(profileEntity.getProfileRole()));
    }

    public Integer generateOtp(ProfileEntity profileEntity) {
        profileEntity.setIsActive(false);
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(emailFrom);
        msg.setTo(profileEntity.getEmail());
        msg.setText("Hello \n\n" + "Your Login OTP: " + randomPIN + ". Please Verify. \n\n");
        CompletableFuture.runAsync(() -> javaMailSender.send(msg));
        return randomPIN;
    }
}
