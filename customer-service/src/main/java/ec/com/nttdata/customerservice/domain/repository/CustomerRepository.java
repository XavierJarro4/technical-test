package ec.com.nttdata.customerservice.domain.repository;

import ec.com.nttdata.customerservice.domain.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository {

    Mono<Customer> save(Customer customer);

    Mono<Customer> findById(Long id);

    Flux<Customer> findAll();

    Mono<Void> deleteById(Long id);
}
