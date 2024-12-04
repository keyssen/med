package com.cpo.med.service;

import com.cpo.med.AbstractDataJpaTest;
import com.cpo.med.AbstractMinioClientTest;
import com.cpo.med.DataJPACreator;
import com.cpo.med.persistence.entity.ImageEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ImageServiceTest implements AbstractDataJpaTest, AbstractMinioClientTest {
    private final ImageService underTest;
    private final ProfileRepository profileRepository;
    private final DataJPACreator dataJPACreator;

    @Test
    @Transactional
    public void createImageTest() {
        ProfileEntity profile = dataJPACreator.createProfileEntityDoctor();
        profile = profileRepository.findById(profile.getId()).get();
        ImageEntity imageEntity = underTest.createImage(profile);
        Assertions.assertNotNull(imageEntity);
        Assertions.assertNotNull(imageEntity.getId());
        Assertions.assertEquals(imageEntity.getProfile(), profile);
    }
}
