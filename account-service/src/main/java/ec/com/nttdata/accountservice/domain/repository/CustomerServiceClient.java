package ec.com.nttdata.accountservice.domain.repository;

import reactor.core.publisher.Mono;

public interface CustomerServiceClient {
    Mono<Boolean> customerExists(Long customerId);
}
