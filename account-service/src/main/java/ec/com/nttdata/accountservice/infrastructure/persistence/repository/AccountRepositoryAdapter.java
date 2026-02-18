package ec.com.nttdata.accountservice.infrastructure.persistence.repository;

import ec.com.nttdata.accountservice.domain.model.Account;
import ec.com.nttdata.accountservice.domain.model.Movement;
import ec.com.nttdata.accountservice.domain.repository.AccountRepository;
import ec.com.nttdata.accountservice.infrastructure.persistence.mapper.AccountMapper;
import ec.com.nttdata.accountservice.infrastructure.persistence.mapper.MovementMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;
    private final AccountMapper accountMapper;

    public AccountRepositoryAdapter(AccountJpaRepository accountJpaRepository, AccountMapper accountMapper) {
        this.accountJpaRepository = accountJpaRepository;
        this.accountMapper = accountMapper;

    }

    @Override
    public Mono<Account> save(Account account) {
        log.debug("Saving account to database");
        return Mono.fromCallable(() -> accountJpaRepository.save(accountMapper.domainToEntity(account)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(accountMapper::entityToDomain);
    }

    @Override
    public Mono<Account> findById(Long id) {
        log.debug("Finding account by ID: {}", id);
        return Mono.fromCallable(() -> accountJpaRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional ->
                        optional.map(entity -> Mono.just(accountMapper.entityToDomain(entity)))
                                .orElseGet(Mono::empty)
                );
    }

    @Override
    public Flux<Account> findAll() {
        log.debug("All accounts");
        return Mono.fromCallable(accountJpaRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(accountMapper::entityToDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        log.debug("Deleting account by ID: {}", id);
        return Mono.fromRunnable(() -> accountJpaRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Flux<Account> findByCustomerId(Long customerId) {
        return Mono.fromCallable(() -> accountJpaRepository.findByCustomerId(customerId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(accountMapper::entityToDomain);
    }


}
