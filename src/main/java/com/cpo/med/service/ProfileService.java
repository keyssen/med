package com.cpo.med.service;

import com.cpo.med.configuration.properties.MinioProperties;
import com.cpo.med.mapper.ProfileMapper;
import com.cpo.med.model.request.ProfileDefaultCreateRq;
import com.cpo.med.model.request.ProfileSignUpRq;
import com.cpo.med.model.request.ProfileUpdateRq;
import com.cpo.med.model.request.SearchProfileRq;
import com.cpo.med.model.response.PaginationDoctorProfileRs;
import com.cpo.med.model.response.ProfileRs;
import com.cpo.med.model.response.ProfileSimpleRs;
import com.cpo.med.model.response.ProfileWithMedicalSessionRs;
import com.cpo.med.persistence.entity.ImageEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.persistence.repository.ProfileCustomRepository;
import com.cpo.med.persistence.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ProfileService extends DefaultOAuth2UserService implements UserDetailsService {
    private final ImageService imageService;
    private final MinioService minioService;
    private final ProfileCustomRepository profileCustomRepository;
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final JavaMailSender javaMailSender;
    @Autowired
    @Lazy
    private ProfileService profileService;
    @Value("{spring.mail.username}")
    private String emailFrom;

    @Transactional(readOnly = true)
    public ProfileEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof DefaultOAuth2User userDetails) {
                String email = userDetails.getAttribute("email");
                return findByEmail(email);
            } else if (principal instanceof UserDetails userDetails) {
                String email = userDetails.getUsername();
                return findByEmail(email);
            }
        }
        return null;
    }

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

    @Transactional(readOnly = true)
    public PaginationDoctorProfileRs getDoctorProfiles(SearchProfileRq searchProfileRq) {
        Page<ProfileEntity> doctors = profileCustomRepository.doctorFindProfile(searchProfileRq);
        return profileMapper.pageDoctorsToPaginationRs(doctors);
    }

    public PaginationDoctorProfileRs getProfiles(SearchProfileRq searchProfileRq) {
        Page<ProfileEntity> doctors = profileCustomRepository.adminFindProfile(searchProfileRq);
        return profileMapper.pageDoctorsToPaginationRs(doctors);
    }

    @Transactional(readOnly = true)
    public List<ProfileSimpleRs> getSimplePatient() {
        List<ProfileEntity> patients = profileRepository.findByProfileRoleIn(List.of(ProfileRole.PATIENT));
        return ProfileMapper.profilesToProfilesSimpleRs(patients);
    }

    @Transactional
    public ProfileEntity defaultRegistration(ProfileDefaultCreateRq profileDefaultCreateRq) {
        return profileRepository.save(ProfileMapper.defaultCreateProfileRqToProfileEntity(profileDefaultCreateRq));
    }

    @Transactional
    public ProfileEntity signUp(ProfileSignUpRq profileSignUpRq) {
        ProfileEntity profileEntity = profileRepository.save(ProfileMapper.profileSignUpRqToProfileEntity(profileSignUpRq));
        if (nonNull(profileSignUpRq.getPhoto())) {
            addImage(profileEntity.getId(), profileSignUpRq.getPhoto());
        }
        return profileEntity;
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
        ProfileMapper.updateProfile(profileUpdateRq, profileEntity);
        updateImage(profileId, profileUpdateRq.getPhoto());
        return profileRepository.save(profileEntity).getId();
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
        return minioService.addFile(profileId, imageEntity.getId(), file);
    }

    @Transactional
    public void deleteImage(UUID profileId) {
        ProfileEntity profileEntity = getById(profileId);
        minioService.deleteFile(profileEntity.getId(), profileEntity.getImage().getId());
    }

    @Transactional
    public void updateImage(UUID profileId, MultipartFile file) {
        ProfileEntity profileEntity = getById(profileId);
        if (nonNull(profileEntity.getImage())) {
            minioService.deleteFile(profileEntity.getId(), profileEntity.getImage().getId());
        }
        addImage(profileEntity.getId(), file);
    }

    @Transactional
    public void delete(UUID profileId) {
        profileRepository.deleteById(profileId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final ProfileEntity profileEntity = profileService.findByEmail(username);
        if (profileEntity == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(
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
