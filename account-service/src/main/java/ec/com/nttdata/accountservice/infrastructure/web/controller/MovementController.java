package ec.com.nttdata.accountservice.infrastructure.web.controller;

import ec.com.nttdata.accountservice.api.MovementApi;
import ec.com.nttdata.accountservice.api.model.MovementRequestDTO;
import ec.com.nttdata.accountservice.api.model.MovementResponseDTO;
import ec.com.nttdata.accountservice.application.usecase.MovementUseCase;
import ec.com.nttdata.accountservice.infrastructure.web.mapper.MovementWebMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MovementController implements MovementApi {

    private final MovementUseCase movementUseCase;
    private final MovementWebMapper movementWebMapper;

    @Override
    public Mono<ResponseEntity<MovementResponseDTO>> registerMovement(
            Mono<MovementRequestDTO> movementRequest,
            ServerWebExchange exchange) {

        log.info("REST request to create Movement");

        return movementRequest
                .map(movementWebMapper::requestToDomain)
                .flatMap(movementUseCase::createMovement)
                .map(movementWebMapper::domainToResponse)
                .map(response -> ResponseEntity.status(201).body(response));
    }

    @Override
    public Mono<ResponseEntity<MovementResponseDTO>> getMovementById(
            Long id,
            ServerWebExchange exchange) {

        log.info("REST request to get movement with ID: {}", id);

        return movementUseCase.getMovementById(id)
                .map(movementWebMapper::domainToResponse)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Override
    public Mono<ResponseEntity<Flux<MovementResponseDTO>>> getAllMovements(
            ServerWebExchange exchange) {

        log.info("REST request to get all Movements");

        Flux<MovementResponseDTO> responseFlux =movementUseCase.getAllMovements().map(movementWebMapper::domainToResponse);

        return Mono.just(ResponseEntity.ok(responseFlux));
    }

    @Override
    public Mono<ResponseEntity<MovementResponseDTO>> updateMovement(
            Long id,
            Mono<MovementRequestDTO> movementRequest,
            ServerWebExchange exchange) {

        log.info("REST request to update Movement with ID: {}", id);

        return movementRequest
                .map(movementWebMapper::requestToDomain)
                .flatMap(movement -> movementUseCase.updateMovement(id, movement))
                .map(movementWebMapper::domainToResponse)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteMovement(
            Long id,
            ServerWebExchange exchange) {

        log.info("REST request to delete Movement with ID: {}", id);

        return movementUseCase.deleteMovement(id)
                .thenReturn(ResponseEntity.<Void>noContent().build());



    }

}
