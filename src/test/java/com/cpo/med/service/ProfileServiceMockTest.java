package com.cpo.med.service;

import com.cpo.med.mapper.ProfileMapper;
import com.cpo.med.model.request.ProfileSignUpRq;
import com.cpo.med.model.request.ProfileUpdateRq;
import com.cpo.med.model.request.SearchProfileRq;
import com.cpo.med.model.response.PaginationDoctorProfileRs;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.entity.enums.ProfileRole;
import com.cpo.med.persistence.repository.ProfileCustomRepository;
import com.cpo.med.persistence.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfileServiceMockTest {
    @Mock
    private ImageService imageService;
    @Mock
    private MinioService minioService;

    @Mock
    private ProfileCustomRepository profileCustomRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;

    private ProfileService spyProfileService;

    private ProfileEntity testProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        spyProfileService = spy(new ProfileService(
                imageService,
                minioService,
                profileCustomRepository,
                profileRepository,
                profileMapper,
                javaMailSender
        ));

        ReflectionTestUtils.setField(spyProfileService, "profileService", spyProfileService);

        testProfile = new ProfileEntity();
        testProfile.setId(UUID.randomUUID());
        testProfile.setEmail("test@example.com");
        testProfile.setPassword("password123");
        testProfile.setProfileRole(ProfileRole.PATIENT);
    }

    @Test
    void getById_shouldReturnProfile() {
        // Мокирование
        when(profileRepository.findById(testProfile.getId())).thenReturn(Optional.of(testProfile));

        // Тестирование
        ProfileEntity result = spyProfileService.getById(testProfile.getId());

        // Проверки
        assertEquals(testProfile, result);
        verify(profileRepository, times(1)).findById(testProfile.getId());
    }

    @Test
    void getById_shouldThrowExceptionIfNotFound() {
        // Мокирование
        when(profileRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Тестирование
        assertThrows(RuntimeException.class, () -> spyProfileService.getById(UUID.randomUUID()));

        // Проверки
        verify(profileRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUser() {
        // Мокирование
        when(profileRepository.findOneByEmailIgnoreCase(testProfile.getEmail())).thenReturn(testProfile);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(testProfile.getEmail());
        // Тестирование
        ProfileEntity result = spyProfileService.getCurrentUser();

        // Проверки
        assertEquals(testProfile, result);
        verify(profileRepository, times(1)).findOneByEmailIgnoreCase(testProfile.getEmail());
    }

    @Test
    void getDoctorProfiles_shouldReturnPaginationDoctorProfileRs() {
        // Подготовка данных
        SearchProfileRq searchProfileRq = new SearchProfileRq(); // Добавьте необходимые параметры для теста
        Page<ProfileEntity> mockPage = mock(Page.class);
        PaginationDoctorProfileRs mockPaginationDoctorProfileRs = new PaginationDoctorProfileRs();

        // Мокирование profileMapper
        when(profileMapper.pageDoctorsToPaginationRs(mockPage)).thenReturn(mockPaginationDoctorProfileRs);

        // Мокирование profileCustomRepository
        when(profileCustomRepository.doctorFindProfile(searchProfileRq)).thenReturn(mockPage);

        // Тестирование
        PaginationDoctorProfileRs result = spyProfileService.getDoctorProfiles(searchProfileRq);

        // Проверки
        assertEquals(mockPaginationDoctorProfileRs, result);
        verify(profileCustomRepository, times(1)).doctorFindProfile(searchProfileRq);
        verify(profileMapper, times(1)).pageDoctorsToPaginationRs(mockPage);
    }

    @Test
    void signUp_shouldReturnSavedProfile() {
        // Подготовка
        ProfileSignUpRq signUpRq = new ProfileSignUpRq();
        signUpRq.setEmail("signupuser@example.com");
        signUpRq.setPassword("password123");

        // Мокирование
        when(profileRepository.save(any(ProfileEntity.class))).thenReturn(testProfile);

        // Тестирование
        ProfileEntity result = spyProfileService.signUp(signUpRq);

        // Проверки
        assertEquals(testProfile, result);
        verify(profileRepository, times(1)).save(any(ProfileEntity.class));
    }

    @Test
    void update_shouldReturnUpdatedProfile() {
        // Подготовка
        UUID profileId = testProfile.getId();
        ProfileUpdateRq updateRq = new ProfileUpdateRq();
        updateRq.setEmail("updateduser@example.com");

        // Мокирование
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(testProfile)).thenReturn(testProfile);

        // Тестирование
        UUID result = spyProfileService.update(profileId, updateRq);

        // Проверки
        assertEquals(testProfile.getId(), result);
        verify(profileRepository, times(1)).save(testProfile);
    }

    @Test
    void generateOtp_shouldReturnGeneratedOtp() {
        // Мокирование
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        // Тестирование
        Integer otp = spyProfileService.generateOtp(testProfile);

        // Проверки
        assertNotNull(otp);
        assertTrue(otp >= 1000 && otp <= 9999); // Проверка, что OTP в допустимом диапазоне
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        // Мокирование
        when(profileRepository.findOneByEmailIgnoreCase(testProfile.getEmail())).thenReturn(testProfile);

        // Тестирование
        UserDetails result = spyProfileService.loadUserByUsername(testProfile.getEmail());

        // Проверки
        assertNotNull(result);
        assertEquals(testProfile.getEmail(), result.getUsername());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(String.format("ROLE_%s", testProfile.getProfileRole().name()))));
    }
}
