package com.cpo.med.service;

import com.cpo.med.AbstractDataJpaTest;
import com.cpo.med.AbstractMinioClientTest;
import com.cpo.med.DataJPACreator;
import com.cpo.med.model.request.ProfileUpdateRq;
import com.cpo.med.model.response.PaginationDoctorProfileRs;
import com.cpo.med.model.response.ProfileSimpleRs;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.persistence.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.cpo.med.DataCreator.createProfileDefaultCreateRq;
import static com.cpo.med.DataCreator.createProfileSignUpRq;
import static com.cpo.med.DataCreator.createProfileUpdateRq;
import static com.cpo.med.DataCreator.createSearchProfileRq;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProfileServiceTest implements AbstractDataJpaTest, AbstractMinioClientTest {
    private static ProfileEntity profileEntityDoctor;
    private final ProfileService underTest;
    private final ProfileRepository profileRepository;
    private final DataJPACreator dataJPACreator;
    @MockBean
    private JavaMailSender javaMailSender;

    @BeforeEach
    @Transactional
    public void setUp() {
        profileEntityDoctor = dataJPACreator.createProfileEntityDoctor();
    }

    @Test
    public void getById() {
        ProfileEntity actual = underTest.getById(profileEntityDoctor.getId());
        Assertions.assertEquals(profileEntityDoctor.getId(), actual.getId());
    }

    @Test
    public void findByEmail() {
        ProfileEntity actual = underTest.findByEmail(profileEntityDoctor.getEmail());
        Assertions.assertEquals(profileEntityDoctor.getId(), actual.getId());
    }

    @Test
    public void getDoctorProfiles() {
        profileEntityDoctor = dataJPACreator.createProfileEntityDoctor();
        profileEntityDoctor.setName("fsdfsdfsdfsdf");
        profileRepository.saveAndFlush(profileEntityDoctor);
        PaginationDoctorProfileRs actual = underTest.getDoctorProfiles(createSearchProfileRq(null, "fsdfsdfsdfsdf", null, null, null));
        Assertions.assertEquals(1, actual.getDoctors().size());
    }

    @Test
    public void getSimplePatient() {
        List<ProfileSimpleRs> actual = underTest.getSimplePatient();
        List<ProfileEntity> profileEntities = profileRepository.findAll().stream()
                .filter(profile -> profile.getProfileRole().equals(ProfileRole.PATIENT))
                .toList();
        Assertions.assertEquals(profileEntities.size(), actual.size());
    }

    @Test
    public void defaultRegistration() {
        String email = "email" + profileRepository.findAll().size();
        ProfileEntity actual = underTest.defaultRegistration(createProfileDefaultCreateRq(email));
        ProfileEntity expected = profileRepository.findOneByEmailIgnoreCase(email);
        Assertions.assertEquals(expected.getId(), actual.getId());
    }

    @Test
    public void signUp() {
        String email = "email" + profileRepository.findAll().size();
        String phone = "phone" + profileRepository.findAll().size();
        ProfileEntity actual = underTest.signUp(createProfileSignUpRq(email, phone));
        ProfileEntity expected = profileRepository.findOneByEmailIgnoreCase(email);
        Assertions.assertEquals(expected.getId(), actual.getId());
    }

    @Test
    public void create() {
        String email = "email" + profileRepository.findAll().size();
        UUID profileId = underTest.create(createProfileDefaultCreateRq(email), ProfileRole.ADMINISTRATOR);
        ProfileEntity profile = profileRepository.findById(profileId).get();
        Assertions.assertNotNull(profileId);
        Assertions.assertEquals(ProfileRole.ADMINISTRATOR, profile.getProfileRole());
    }

    @Test
    public void update() {
        String email = "email" + profileRepository.findAll().size();
        String phone = "phone" + profileRepository.findAll().size();
        ProfileUpdateRq profileUpdateRq = createProfileUpdateRq(email, phone);
        UUID profileId = underTest.update(profileEntityDoctor.getId(), profileUpdateRq);

        ProfileEntity profile = profileRepository.findById(profileId).get();
        Assertions.assertNotNull(profileId);
        Assertions.assertEquals(profileUpdateRq.getName(), profile.getName());
        Assertions.assertEquals(profileUpdateRq.getSurname(), profile.getSurname());
        Assertions.assertEquals(profileUpdateRq.getPatronymic(), profile.getPatronymic());
    }

    @Test
    public void loadUserByUsername() {
        UserDetails userDetails = underTest.loadUserByUsername(profileEntityDoctor.getEmail());
        System.out.println(userDetails.getAuthorities());
        Assertions.assertEquals(profileEntityDoctor.getEmail(), userDetails.getUsername());
        Assertions.assertEquals(profileEntityDoctor.getPassword(), userDetails.getPassword());
    }

    @Test
    public void generateOtp() {
        Integer otp = underTest.generateOtp(profileEntityDoctor);
        Assertions.assertNotNull(otp);
    }

}
