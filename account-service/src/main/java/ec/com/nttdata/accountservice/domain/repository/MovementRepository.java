package ec.com.nttdata.accountservice.domain.repository;

import ec.com.nttdata.accountservice.domain.model.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface MovementRepository {

    Mono<Movement> save(Movement movement);
    Mono<Movement> findById(Long id);
    Flux<Movement> findAll();
    Mono<Void> deleteById(Long id);
    Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

}
