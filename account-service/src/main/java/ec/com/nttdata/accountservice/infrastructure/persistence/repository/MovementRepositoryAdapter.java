package ec.com.nttdata.accountservice.infrastructure.persistence.repository;

import ec.com.nttdata.accountservice.domain.model.Movement;
import ec.com.nttdata.accountservice.domain.repository.MovementRepository;
import ec.com.nttdata.accountservice.infrastructure.persistence.mapper.MovementMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Slf4j
@Repository
public class MovementRepositoryAdapter implements MovementRepository {

    private final MovementJpaRepository movementJpaRepository;
    private final MovementMapper movementMapper;

    public MovementRepositoryAdapter(MovementJpaRepository movementJpaRepository, MovementMapper movementMapper) {
        this.movementJpaRepository = movementJpaRepository;
        this.movementMapper = movementMapper;

    }

    @Override
    public Mono<Movement> save(Movement movement) {
        log.debug("Saving movement to database");
        return Mono.fromCallable(() -> movementJpaRepository.save(movementMapper.domainToEntity(movement)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(movementMapper::entityToDomain);
    }

    @Override
    public Mono<Movement> findById(Long id) {
        log.debug("Finding movement by ID: {}", id);
        return Mono.fromCallable(() -> movementJpaRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional ->
                        optional.map(entity -> Mono.just(movementMapper.entityToDomain(entity)))
                                .orElseGet(Mono::empty)
                );
    }

    @Override
    public Flux<Movement> findAll() {
        log.debug("All movements");
        return Mono.fromCallable(movementJpaRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(movementMapper::entityToDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        log.debug("Deleting movement by ID: {}", id);
        return Mono.fromRunnable(() -> movementJpaRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return Mono.fromCallable(() -> movementJpaRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(movementMapper::entityToDomain);
    }

}
