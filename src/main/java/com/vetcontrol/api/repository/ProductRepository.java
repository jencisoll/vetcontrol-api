package com.vetcontrol.api.repository;

import com.vetcontrol.api.entity.Product;
import com.vetcontrol.api.entity.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTypeAndActiveTrue(ProductType type);

    List<Product> findByActiveTrue();

    @Query("SELECT p FROM Product p WHERE " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:active IS NULL OR p.active = :active)")
    Page<Product> findAllWithFilters(
            @Param("search") String search,
            @Param("type") ProductType type,
            @Param("active") Boolean active,
            Pageable pageable);
}
