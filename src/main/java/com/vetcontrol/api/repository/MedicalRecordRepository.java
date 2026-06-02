package com.vetcontrol.api.repository;

import com.vetcontrol.api.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPetIdOrderByCreatedAtDesc(Long petId);

    Page<MedicalRecord> findByPetId(Long petId, Pageable pageable);

    @Query("SELECT mr FROM MedicalRecord mr WHERE " +
           "(:petId IS NULL OR mr.pet.id = :petId) AND " +
           "(:veterinarianId IS NULL OR mr.veterinarian.id = :veterinarianId) AND " +
           "(:dateFrom IS NULL OR mr.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR mr.createdAt <= :dateTo) AND " +
           "(:search IS NULL OR LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(mr.treatment) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MedicalRecord> findAllWithFilters(
            @Param("petId") Long petId,
            @Param("veterinarianId") Long veterinarianId,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("search") String search,
            Pageable pageable);
}
