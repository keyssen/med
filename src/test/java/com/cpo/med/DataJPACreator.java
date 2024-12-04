package com.cpo.med;

import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.repository.MedicalSessionRepository;
import com.cpo.med.persistence.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.cpo.med.persistence.entity.enums.SessionStatus.IN_PROGRESS;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DataJPACreator {
    private final ProfileRepository profileRepository;
    private final MedicalSessionRepository medicalSessionRepository;

    @Transactional
    public ProfileEntity createProfileEntityDoctor() {
        ProfileEntity profileEntity = DataCreator.createProfileEntityDoctor("email" + profileRepository.count(), "phone" + profileRepository.count());
        return profileRepository.saveAndFlush(profileEntity);
    }

    @Transactional
    public ProfileEntity createProfileEntityPatient() {
        ProfileEntity profileEntity = DataCreator.createProfileEntityPatient("email" + profileRepository.count(), "phone" + profileRepository.count());
        return profileRepository.saveAndFlush(profileEntity);
    }

    @Transactional
    public MedicalSessionEntity createMedicalSession(ProfileEntity profileEntityDoctor, ProfileEntity profileEntityPatient) {
        MedicalSessionEntity medicalSession = DataCreator.createMedicalSession(profileEntityDoctor, profileEntityPatient, null);
        medicalSession.setSessionStatus(IN_PROGRESS);
        return medicalSessionRepository.saveAndFlush(medicalSession);
    }
}
