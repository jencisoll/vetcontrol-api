package com.vetcontrol.api.repository;

import com.vetcontrol.api.entity.Invoice;
import com.vetcontrol.api.entity.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i WHERE " +
           "(:clientId IS NULL OR i.client.id = :clientId) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:dateFrom IS NULL OR i.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR i.createdAt <= :dateTo) AND " +
           "(:search IS NULL OR i.invoiceNumber LIKE CONCAT('%', :search, '%'))")
    Page<Invoice> findAllWithFilters(
            @Param("clientId") Long clientId,
            @Param("status") InvoiceStatus status,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("search") String search,
            Pageable pageable);

    boolean existsByInvoiceNumber(String invoiceNumber);
}
