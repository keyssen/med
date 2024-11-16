package com.cpo.med.persistence.repository;

import com.cpo.med.persistence.entity.TreatmentPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlanEntity, UUID> {
}