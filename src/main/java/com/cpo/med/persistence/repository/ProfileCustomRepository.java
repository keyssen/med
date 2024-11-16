package com.cpo.med.persistence.repository;

import com.cpo.med.model.enums.DoctorType;
import com.cpo.med.model.enums.ProfileRole;
import com.cpo.med.model.request.SearchProfileRq;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import com.cpo.med.persistence.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cpo.med.utils.Constants.sessionStatusesToPatientRegisteredList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class ProfileCustomRepository {
    private final MedicalSessionRepository medicalSessionRepository;
    private final ProfileRepository profileRepository;


    public Page<ProfileEntity> doctorFindProfile(SearchProfileRq searchProfileRq) {
        Specification<ProfileEntity> specification = defaultFindProfile(searchProfileRq);

        if (nonNull(searchProfileRq.getProfileRole())) {
            specification.and(profileTypeFilter(ProfileRole.DOCTOR));
        }

        PageRequest pageRequest = PageRequest.of(searchProfileRq.getPage(), searchProfileRq.getSize());
        Page<ProfileEntity> profileEntityPage = profileRepository.findAll(specification, pageRequest);
        mapSessionsToDoctors(profileEntityPage.getContent());

        return profileEntityPage;
    }

    public Page<ProfileEntity> adminFindProfile(SearchProfileRq searchProfileRq) {
        Specification<ProfileEntity> specification = defaultFindProfile(searchProfileRq);
        if (nonNull(searchProfileRq.getProfileRole())) {
            specification.and(profileTypeFilter(searchProfileRq.getProfileRole()));
        }
        PageRequest pageRequest = PageRequest.of(searchProfileRq.getPage(), searchProfileRq.getSize());

        return profileRepository.findAll(specification, pageRequest);
    }

    public Specification<ProfileEntity> defaultFindProfile(SearchProfileRq searchProfileRq) {
        Specification<ProfileEntity> specification = Specification.where(null);
        if (nonNull(searchProfileRq.getDoctorType())) {
            specification = specification.and(doctorTypeFilter(searchProfileRq.getDoctorType()));
        }
        if (StringUtils.hasText(searchProfileRq.getFullName())) {
            specification = specification.and(fullNameContainsFilter(searchProfileRq.getFullName()));
        }
        return specification;
    }

    private Specification<ProfileEntity> profileTypeFilter(ProfileRole profileRole) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ProfileEntity.Fields.profileRole), profileRole);
    }

    private Specification<ProfileEntity> doctorTypeFilter(DoctorType doctorType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ProfileEntity.Fields.doctorType), doctorType);
    }

    private Specification<ProfileEntity> fullNameContainsFilter(String fullName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(criteriaBuilder.function("concat", String.class, root.get(ProfileEntity.Fields.surname), criteriaBuilder.literal(" "),
                        root.get(ProfileEntity.Fields.name), criteriaBuilder.literal(" "), root.get(ProfileEntity.Fields.patronymic))),
                "%" + fullName.toLowerCase() + "%"
        );
    }

    private void mapSessionsToDoctors(List<ProfileEntity> doctors) {
        List<UUID> doctorIds = doctors.stream().map(ProfileEntity::getId).toList();
        List<MedicalSessionEntity> medicalSessions = medicalSessionRepository.findByDoctorIds(doctorIds, sessionStatusesToPatientRegisteredList);
        Map<UUID, List<MedicalSessionEntity>> medicalSessionsMap = medicalSessions.stream().collect(groupingBy(medicalSession -> medicalSession.getDoctor().getId()));
        doctors.forEach(profileEntity -> profileEntity.setDoctorSessions(medicalSessionsMap.get(profileEntity.getId())));
    }
}
