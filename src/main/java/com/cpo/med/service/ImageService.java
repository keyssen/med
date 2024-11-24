package com.cpo.med.service;

import com.cpo.med.persistence.entity.ImageEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import com.cpo.med.persistence.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public ImageEntity getImageById(UUID imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Not found images bu imageId "));
    }

    @Transactional(readOnly = true)
    public List<ImageEntity> getImagesByIds(List<UUID> imageIds) {
        return imageRepository.findAllById(imageIds);
    }

    @Transactional
    public ImageEntity createImage(ProfileEntity profile) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setProfile(profile);
        profile.setImage(imageEntity);
        return imageRepository.save(imageEntity);
    }
}
