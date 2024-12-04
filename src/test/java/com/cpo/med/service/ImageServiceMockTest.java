package com.cpo.med.service;

import com.cpo.med.persistence.entity.ImageEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageServiceMockTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createImage_ShouldSaveAndReturnImageEntity() {
        ProfileEntity profile = new ProfileEntity();
        ImageEntity savedImage = new ImageEntity();
        savedImage.setProfile(profile);

        when(imageRepository.save(any(ImageEntity.class))).thenReturn(savedImage);

        ImageEntity result = imageService.createImage(profile);

        assertEquals(savedImage, result);
        assertEquals(profile, result.getProfile());
        assertEquals(result, profile.getImage());

        verify(imageRepository, times(1)).save(any(ImageEntity.class));
    }
}
