package ec.com.nttdata.customerservice.infrastructure.web.controller;

import ec.com.nttdata.customerservice.api.CustomerApi;
import ec.com.nttdata.customerservice.api.model.CustomerRequest;
import ec.com.nttdata.customerservice.api.model.CustomerResponse;
import ec.com.nttdata.customerservice.application.usecase.CustomerUseCase;
import ec.com.nttdata.customerservice.infrastructure.web.mapper.CustomerWebMapper;
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
public class CustomerController implements CustomerApi {

    private final CustomerUseCase customerUseCase;
    private final CustomerWebMapper customerWebMapper;

    @Override
    public Mono<ResponseEntity<CustomerResponse>> createCustomer(
            Mono<CustomerRequest> customerRequest,
            ServerWebExchange exchange) {

        log.info("REST request to create customer");

        return customerRequest
                .map(customerWebMapper::requestToDomain)
                .flatMap(customerUseCase::createCustomer)
                .map(customerWebMapper::domainToResponse)
                .map(response -> ResponseEntity.status(201).body(response));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> getCustomerById(
            Long id,
            ServerWebExchange exchange) {

        log.info("REST request to get customer with ID: {}", id);

        return customerUseCase.getCustomerById(id)
                .map(customerWebMapper::domainToResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<CustomerResponse>>> getAllCustomers(
            ServerWebExchange exchange) {

        log.info("REST request to get all customers");

        Flux<CustomerResponse> responseFlux =
                customerUseCase.getAllCustomers()
                        .map(customerWebMapper::domainToResponse);

        return Mono.just(ResponseEntity.ok(responseFlux));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> updateCustomer(
            Long id,
            Mono<CustomerRequest> customerRequest,
            ServerWebExchange exchange) {

        log.info("REST request to update customer with ID: {}", id);

        return customerRequest
                .map(customerWebMapper::requestToDomain)
                .flatMap(customer -> customerUseCase.updateCustomer(id, customer))
                .map(customerWebMapper::domainToResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(
            Long id,
            ServerWebExchange exchange) {

        log.info("REST request to delete customer with ID: {}", id);

        return customerUseCase.deleteCustomer(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
