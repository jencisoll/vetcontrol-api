package com.vetcontrol.api.repository;

import com.vetcontrol.api.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByDni(String dni);

    boolean existsByDni(String dni);

    @Query("SELECT c FROM Client c WHERE " +
           "(:search IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.dni) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:hasPets IS NULL OR (:hasPets = true AND SIZE(c.pets) > 0) OR (:hasPets = false AND SIZE(c.pets) = 0))")
    Page<Client> findAllWithFilters(
            @Param("search") String search,
            @Param("hasPets") Boolean hasPets,
            Pageable pageable);
}
