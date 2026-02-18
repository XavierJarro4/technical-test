package ec.com.nttdata.accountservice.infrastructure.client;

import ec.com.nttdata.accountservice.domain.repository.CustomerServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomerServiceClientAdapter implements CustomerServiceClient {

    private final WebClient webClient;

    public CustomerServiceClientAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${customer.service.url}") String customerServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(customerServiceUrl).build();
    }

    @Override
    public Mono<Boolean> customerExists(Long customerId) {
        log.info("Checking if customer exists with ID: {}", customerId);

        return webClient.get()
                .uri("/api/v1/customers/{id}", customerId)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        log.info("Customer found with ID: {}", customerId);
                        return response.bodyToMono(Void.class).thenReturn(true);
                    } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.warn("Customer not found with ID: {}", customerId);
                        return Mono.just(false);
                    } else {
                        log.error("Error checking customer existence: {}", response.statusCode());
                        return Mono.just(false);
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error communicating with customer service: {}", error.getMessage());
                    return Mono.just(false);
                });
    }
}
