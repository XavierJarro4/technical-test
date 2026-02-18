package ec.com.nttdata.customerservice.infrastructure.persistence.repository;

import ec.com.nttdata.customerservice.domain.model.Customer;
import ec.com.nttdata.customerservice.domain.repository.CustomerRepository;
import ec.com.nttdata.customerservice.infrastructure.persistence.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerMapper customerMapper;

    public CustomerRepositoryAdapter(CustomerJpaRepository customerJpaRepository, CustomerMapper customerMapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public Mono<Customer> save(Customer customer) {
        log.debug("Saving customer to database");
        return Mono.fromCallable(() -> customerJpaRepository.save(customerMapper.domainToEntity(customer)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(customerMapper::entityToDomain);
    }

    @Override
    public Mono<Customer> findById(Long id) {
        log.debug("Finding customer by ID: {}", id);
        return Mono.fromCallable(() -> customerJpaRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional ->
                        optional.map(entity -> Mono.just(customerMapper.entityToDomain(entity)))
                                .orElseGet(Mono::empty)
                );
    }

    @Override
    public Flux<Customer> findAll() {
        log.debug("All customers");
        return Mono.fromCallable(customerJpaRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(customerMapper::entityToDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        log.debug("Deleting customer by ID: {}", id);
        return Mono.fromRunnable(() -> customerJpaRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
