package ec.com.nttdata.accountservice.domain.repository;

import ec.com.nttdata.accountservice.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository {

    Mono<Account> save(Account account);
    Mono<Account> findById(Long id);
    Flux<Account> findAll();
    Mono<Void> deleteById(Long id);

    Flux<Account> findByCustomerId(Long customerId);

}
