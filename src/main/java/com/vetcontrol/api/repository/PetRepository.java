package com.vetcontrol.api.repository;

import com.vetcontrol.api.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByClientId(Long clientId);

    Page<Pet> findByClientId(Long clientId, Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.species) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.breed) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:clientId IS NULL OR p.client.id = :clientId) AND " +
           "(:species IS NULL OR LOWER(p.species) = LOWER(:species))")
    Page<Pet> findAllWithFilters(
            @Param("search") String search,
            @Param("clientId") Long clientId,
            @Param("species") String species,
            Pageable pageable);

    @Query("SELECT DISTINCT p.species FROM Pet p ORDER BY p.species")
    List<String> findAllSpecies();
}
