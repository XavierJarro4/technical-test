package ec.com.nttdata.accountservice.infrastructure.web.controller;

import ec.com.nttdata.accountservice.api.model.MovementRequestDTO;
import ec.com.nttdata.accountservice.api.model.MovementResponseDTO;
import ec.com.nttdata.accountservice.application.usecase.MovementUseCase;
import ec.com.nttdata.accountservice.domain.model.Movement;
import ec.com.nttdata.accountservice.infrastructure.web.mapper.MovementWebMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovementController.class)
public class MovementControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovementUseCase movementUseCase;

    @MockBean
    private MovementWebMapper movementWebMapper;

    // =============================
    // CREATE
    // =============================
    @Test
    void shouldCreateMovement() {

        MovementRequestDTO requestDTO = new MovementRequestDTO();
        requestDTO.setAccountId(1L);
        requestDTO.setType("DEBIT");
        requestDTO.setValue(BigDecimal.valueOf(100).doubleValue());

        Movement domain = new Movement(
                1L,
                1L,
                LocalDateTime.now(),
                "DEBIT",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(900)
        );

        MovementResponseDTO responseDTO = new MovementResponseDTO();

        when(movementWebMapper.requestToDomain(any())).thenReturn(domain);
        when(movementUseCase.createMovement(any())).thenReturn(Mono.just(domain));
        when(movementWebMapper.domainToResponse(any())).thenReturn(responseDTO);

        webTestClient.post()
                .uri("/movements")
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated();
    }

    // =============================
    // GET BY ID
    // =============================
    @Test
    void shouldReturnMovementById() {

        Movement domain = new Movement(
                1L,
                1L,
                LocalDateTime.now(),
                "CREDIT",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(1100)
        );

        MovementResponseDTO responseDTO = new MovementResponseDTO();

        when(movementUseCase.getMovementById(1L))
                .thenReturn(Mono.just(domain));

        when(movementWebMapper.domainToResponse(domain))
                .thenReturn(responseDTO);

        webTestClient.get()
                .uri("/movements/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNotFoundWhenMovementDoesNotExist() {

        when(movementUseCase.getMovementById(99L))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/movements/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    // =============================
    // GET ALL
    // =============================
    @Test
    void shouldReturnAllMovements() {

        Movement domain = new Movement(
                1L,
                1L,
                LocalDateTime.now(),
                "DEBIT",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(900)
        );

        MovementResponseDTO responseDTO = new MovementResponseDTO();

        when(movementUseCase.getAllMovements())
                .thenReturn(Flux.just(domain));

        when(movementWebMapper.domainToResponse(domain))
                .thenReturn(responseDTO);

        webTestClient.get()
                .uri("/movements")
                .exchange()
                .expectStatus().isOk();
    }

    // =============================
    // UPDATE
    // =============================
    @Test
    void shouldUpdateMovement() {

        MovementRequestDTO requestDTO = new MovementRequestDTO();
        requestDTO.setAccountId(1L);
        requestDTO.setType("DEBIT");
        requestDTO.setValue(BigDecimal.valueOf(200).doubleValue());

        Movement domain = new Movement(
                1L,
                1L,
                LocalDateTime.now(),
                "DEBIT",
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(800)
        );

        MovementResponseDTO responseDTO = new MovementResponseDTO();

        when(movementWebMapper.requestToDomain(any())).thenReturn(domain);
        when(movementUseCase.updateMovement(Mockito.eq(1L), any()))
                .thenReturn(Mono.just(domain));
        when(movementWebMapper.domainToResponse(any()))
                .thenReturn(responseDTO);

        webTestClient.put()
                .uri("/movements/1")
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk();
    }

    // =============================
    // DELETE
    // =============================
    @Test
    void shouldDeleteMovement() {

        when(movementUseCase.deleteMovement(1L))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/movements/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
