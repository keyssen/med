package com.cpo.med.service;

import com.cpo.med.persistence.entity.ImageEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    @Transactional
    public ImageEntity createImage(ProfileEntity profile) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setProfile(profile);
        profile.setImage(imageEntity);
        return imageRepository.save(imageEntity);
    }
}
