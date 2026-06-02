package com.vetcontrol.api.seed;

import com.vetcontrol.api.entity.*;
import com.vetcontrol.api.entity.enums.*;
import com.vetcontrol.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class DataSeeder {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("Base de datos ya contiene datos. Saltando seed...");
                return;
            }

            log.info("Iniciando carga de datos de prueba...");

            User admin = userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@vetcontrol.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrador Principal")
                    .phone("+51 999 888 777")
                    .role(Role.ADMIN)
                    .active(true)
                    .build());

            User vet1 = userRepository.save(User.builder()
                    .username("drgarcia")
                    .email("dr.garcia@vetcontrol.com")
                    .password(passwordEncoder.encode("vet123"))
                    .fullName("Dr. Carlos García")
                    .phone("+51 999 888 666")
                    .role(Role.VETERINARIAN)
                    .active(true)
                    .build());

            User vet2 = userRepository.save(User.builder()
                    .username("dramirez")
                    .email("dr.ramirez@vetcontrol.com")
                    .password(passwordEncoder.encode("vet123"))
                    .fullName("Dra. María Ramírez")
                    .phone("+51 999 888 555")
                    .role(Role.VETERINARIAN)
                    .active(true)
                    .build());

            User receptionist = userRepository.save(User.builder()
                    .username("recepcion")
                    .email("recepcion@vetcontrol.com")
                    .password(passwordEncoder.encode("rec123"))
                    .fullName("Ana López")
                    .phone("+51 999 888 444")
                    .role(Role.RECEPTIONIST)
                    .active(true)
                    .build());

            log.info("Usuarios creados: admin, drgarcia, dramirez, recepcion");

            Client client1 = clientRepository.save(Client.builder()
                    .fullName("Juan Pérez")
                    .dni("12345678")
                    .phone("+51 999 111 222")
                    .email("juan.perez@email.com")
                    .address("Av. Principal 123, Lima")
                    .build());

            Client client2 = clientRepository.save(Client.builder()
                    .fullName("María Torres")
                    .dni("87654321")
                    .phone("+51 999 333 444")
                    .email("maria.torres@email.com")
                    .address("Calle Secundaria 456, Lima")
                    .build());

            Client client3 = clientRepository.save(Client.builder()
                    .fullName("Pedro Sánchez")
                    .dni("45678912")
                    .phone("+51 999 555 666")
                    .email("pedro.sanchez@email.com")
                    .address("Jr. Comercio 789, Lima")
                    .build());

            log.info("Clientes creados: 3");

            Pet pet1 = petRepository.save(Pet.builder()
                    .name("Max")
                    .species("Perro")
                    .breed("Golden Retriever")
                    .age(3)
                    .gender("Macho")
                    .medicalHistory("Vacunación completa. Alergia a polen.")
                    .client(client1)
                    .build());

            Pet pet2 = petRepository.save(Pet.builder()
                    .name("Luna")
                    .species("Gato")
                    .breed("Siamés")
                    .age(2)
                    .gender("Hembra")
                    .medicalHistory("Esterilizada. Sin alergias conocidas.")
                    .client(client1)
                    .build());

            Pet pet3 = petRepository.save(Pet.builder()
                    .name("Rocky")
                    .species("Perro")
                    .breed("Bulldog Francés")
                    .age(4)
                    .gender("Macho")
                    .medicalHistory("Problemas respiratorios leves.")
                    .client(client2)
                    .build());

            Pet pet4 = petRepository.save(Pet.builder()
                    .name("Mia")
                    .species("Gato")
                    .breed("Persa")
                    .age(1)
                    .gender("Hembra")
                    .medicalHistory("Primera vacunación completada.")
                    .client(client3)
                    .build());

            log.info("Mascotas creadas: 4");

            Appointment app1 = appointmentRepository.save(Appointment.builder()
                    .pet(pet1)
                    .veterinarian(vet1)
                    .appointmentDate(LocalDate.now().plusDays(1))
                    .appointmentTime(LocalTime.of(10, 0))
                    .reason("Revisión anual y vacunación")
                    .status(AppointmentStatus.PENDING)
                    .notes("Traer cartilla de vacunación")
                    .build());

            Appointment app2 = appointmentRepository.save(Appointment.builder()
                    .pet(pet2)
                    .veterinarian(vet2)
                    .appointmentDate(LocalDate.now())
                    .appointmentTime(LocalTime.of(14, 30))
                    .reason("Control post-operatorio")
                    .status(AppointmentStatus.ATTENDED)
                    .notes("Cirugía de esterilización hace 7 días")
                    .build());

            Appointment app3 = appointmentRepository.save(Appointment.builder()
                    .pet(pet3)
                    .veterinarian(vet1)
                    .appointmentDate(LocalDate.now().plusDays(3))
                    .appointmentTime(LocalTime.of(16, 0))
                    .reason("Dificultad para respirar")
                    .status(AppointmentStatus.PENDING)
                    .notes("Urgencia leve")
                    .build());

            log.info("Citas creadas: 3");

            medicalRecordRepository.save(MedicalRecord.builder()
                    .pet(pet2)
                    .veterinarian(vet2)
                    .diagnosis("Esterilización exitosa")
                    .treatment("Reposo 7 días. Analgésicos por 3 días.")
                    .observations("Paciente estable. Sin complicaciones.")
                    .prescriptions("Carprofeno 50mg - 1 comprimido cada 24h x 3 días")
                    .build());

            medicalRecordRepository.save(MedicalRecord.builder()
                    .pet(pet1)
                    .veterinarian(vet1)
                    .diagnosis("Conjuntivitis leve")
                    .treatment("Colirio antibiótico 3 veces al día por 5 días")
                    .observations("Ojo izquierdo con leve secreción. Revisar en 5 días.")
                    .prescriptions("Tobramicina colirio - 1 gota cada 8h x 5 días")
                    .build());

            log.info("Registros médicos creados: 2");

            Product prod1 = productRepository.save(Product.builder()
                    .name("Vacuna Pentavalente")
                    .description("Vacuna para perros contra parvovirus, moquillo, hepatitis, parainfluenza y leptospirosis")
                    .type(ProductType.MEDICATION)
                    .price(new BigDecimal("45.00"))
                    .stock(50)
                    .active(true)
                    .build());

            Product prod2 = productRepository.save(Product.builder()
                    .name("Vacuna Triple Felina")
                    .description("Vacuna para gatos contra panleucopenia, rinotraqueítis y calicivirus")
                    .type(ProductType.MEDICATION)
                    .price(new BigDecimal("35.00"))
                    .stock(30)
                    .active(true)
                    .build());

            Product prod3 = productRepository.save(Product.builder()
                    .name("Consulta General")
                    .description("Consulta veterinaria general incluye examen físico completo")
                    .type(ProductType.SERVICE)
                    .price(new BigDecimal("50.00"))
                    .stock(999)
                    .active(true)
                    .build());

            Product prod4 = productRepository.save(Product.builder()
                    .name("Desparasitante Canino")
                    .description("Tableta desparasitante para perros de 10-20kg")
                    .type(ProductType.MEDICATION)
                    .price(new BigDecimal("15.00"))
                    .stock(100)
                    .active(true)
                    .build());

            Product prod5 = productRepository.save(Product.builder()
                    .name("Cirugía de Esterilización")
                    .description("Procedimiento quirúrgico de esterilización/orquiectomía")
                    .type(ProductType.SERVICE)
                    .price(new BigDecimal("200.00"))
                    .stock(999)
                    .active(true)
                    .build());

            Product prod6 = productRepository.save(Product.builder()
                    .name("Alimento Premium Perro")
                    .description("Bolsa 15kg alimento premium para perros adultos")
                    .type(ProductType.SUPPLY)
                    .price(new BigDecimal("120.00"))
                    .stock(20)
                    .active(true)
                    .build());

            log.info("Productos creados: 6");
            log.info("✅ Seed de datos completado exitosamente!");
            log.info("Credenciales de prueba:");
            log.info("  Admin: admin / admin123");
            log.info("  Vet 1: drgarcia / vet123");
            log.info("  Vet 2: dramirez / vet123");
            log.info("  Recepcionista: recepcion / rec123");
        };
    }
}
