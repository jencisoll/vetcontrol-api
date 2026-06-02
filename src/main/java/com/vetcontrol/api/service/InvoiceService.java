package com.vetcontrol.api.service;

import com.vetcontrol.api.dto.request.InvoiceItemRequest;
import com.vetcontrol.api.dto.request.InvoiceRequest;
import com.vetcontrol.api.dto.response.*;
import com.vetcontrol.api.entity.*;
import com.vetcontrol.api.entity.enums.InvoiceStatus;
import com.vetcontrol.api.exception.BusinessException;
import com.vetcontrol.api.exception.ResourceNotFoundException;
import com.vetcontrol.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final AppointmentRepository appointmentRepository;

    public PageResponse<InvoiceResponse> getAllInvoices(
            Long clientId, InvoiceStatus status, LocalDateTime dateFrom, LocalDateTime dateTo,
            String search, Pageable pageable) {
        Page<Invoice> page = invoiceRepository.findAllWithFilters(clientId, status, dateFrom, dateTo, search, pageable);
        return mapToPageResponse(page);
    }

    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
        return mapToResponse(invoice);
    }

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClientId()));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", request.getAppointmentId()));
        }

        String invoiceNumber = generateInvoiceNumber();
        while (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            invoiceNumber = generateInvoiceNumber();
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .client(client)
                .appointment(appointment)
                .status(InvoiceStatus.PENDING)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        for (InvoiceItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", itemReq.getProductId()));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new BusinessException("Stock insuficiente para: " + product.getName());
            }

            product.setStock(product.getStock() - itemReq.getQuantity());
            productRepository.save(product);

            InvoiceItem item = InvoiceItem.builder()
                    .invoice(savedInvoice)
                    .product(product)
                    .description(itemReq.getDescription())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();

            savedInvoice.getItems().add(item);
        }

        savedInvoice.calculateTotals();
        Invoice finalInvoice = invoiceRepository.save(savedInvoice);
        log.info("Factura creada: {} - Cliente: {} - Total: {}",
                finalInvoice.getInvoiceNumber(), client.getFullName(), finalInvoice.getTotal());
        return mapToResponse(finalInvoice);
    }

    @Transactional
    public InvoiceResponse updateInvoiceStatus(Long id, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));

        if (invoice.getStatus() == InvoiceStatus.PAID && status == InvoiceStatus.CANCELLED) {
            throw new BusinessException("No se puede cancelar una factura ya pagada");
        }

        invoice.setStatus(status);
        Invoice updated = invoiceRepository.save(invoice);
        log.info("Estado de factura actualizado: {} -> {}", updated.getInvoiceNumber(), status);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("No se puede eliminar una factura pagada");
        }

        // Restore stock
        for (InvoiceItem item : invoice.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        invoiceRepository.delete(invoice);
        log.info("Factura eliminada: {}", invoice.getInvoiceNumber());
    }

    private String generateInvoiceNumber() {
        return "F" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PageResponse<InvoiceResponse> mapToPageResponse(Page<Invoice> page) {
        return PageResponse.<InvoiceResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .client(ClientSummaryResponse.builder()
                        .id(invoice.getClient().getId())
                        .fullName(invoice.getClient().getFullName())
                        .phone(invoice.getClient().getPhone())
                        .build())
                .appointment(invoice.getAppointment() != null ?
                        AppointmentSummaryResponse.builder()
                                .id(invoice.getAppointment().getId())
                                .appointmentDate(invoice.getAppointment().getAppointmentDate())
                                .reason(invoice.getAppointment().getReason())
                                .build() : null)
                .items(invoice.getItems().stream().map(this::mapItemToResponse).collect(Collectors.toList()))
                .subtotal(invoice.getSubtotal())
                .tax(invoice.getTax())
                .total(invoice.getTotal())
                .status(invoice.getStatus())
                .createdAt(invoice.getCreatedAt())
                .build();
    }

    private InvoiceItemResponse mapItemToResponse(InvoiceItem item) {
        return InvoiceItemResponse.builder()
                .id(item.getId())
                .product(ProductResponse.builder()
                        .id(item.getProduct().getId())
                        .name(item.getProduct().getName())
                        .description(item.getProduct().getDescription())
                        .type(item.getProduct().getType())
                        .price(item.getProduct().getPrice())
                        .stock(item.getProduct().getStock())
                        .active(item.getProduct().isActive())
                        .createdAt(item.getProduct().getCreatedAt())
                        .build())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
