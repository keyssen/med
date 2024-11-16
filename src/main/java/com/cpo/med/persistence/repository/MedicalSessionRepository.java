package com.cpo.med.persistence.repository;

import com.cpo.med.model.enums.SessionStatus;
import com.cpo.med.persistence.entity.MedicalSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalSessionRepository extends JpaRepository<MedicalSessionEntity, UUID> {
    @Query("""
            SELECT mse
            FROM MedicalSessionEntity mse
            WHERE mse.doctor.id IN :doctorIds
            AND mse.sessionStatus IN :sessionStatuses
            """)
    List<MedicalSessionEntity> findByDoctorIds(List<UUID> doctorIds, List<SessionStatus> sessionStatuses);
}