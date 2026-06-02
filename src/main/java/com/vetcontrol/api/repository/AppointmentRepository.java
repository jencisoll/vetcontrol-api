package com.vetcontrol.api.repository;

import com.vetcontrol.api.entity.Appointment;
import com.vetcontrol.api.entity.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPetId(Long petId);

    List<Appointment> findByVeterinarianId(Long veterinarianId);

    List<Appointment> findByAppointmentDate(LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:dateFrom IS NULL OR a.appointmentDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR a.appointmentDate <= :dateTo) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:petId IS NULL OR a.pet.id = :petId) AND " +
           "(:veterinarianId IS NULL OR a.veterinarian.id = :veterinarianId)")
    Page<Appointment> findAllWithFilters(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("status") AppointmentStatus status,
            @Param("petId") Long petId,
            @Param("veterinarianId") Long veterinarianId,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date AND a.status = 'PENDING'")
    long countPendingByDate(@Param("date") LocalDate date);
}
