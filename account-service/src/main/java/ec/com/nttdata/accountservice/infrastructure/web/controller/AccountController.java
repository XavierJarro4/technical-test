package ec.com.nttdata.accountservice.infrastructure.web.controller;

import ec.com.nttdata.accountservice.api.AccountApi;
import ec.com.nttdata.accountservice.api.model.AccountRequestDTO;
import ec.com.nttdata.accountservice.api.model.AccountResponseDTO;
import ec.com.nttdata.accountservice.application.usecase.AccountUseCase;
import ec.com.nttdata.accountservice.infrastructure.web.mapper.AccountWebMapper;
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
public class AccountController implements AccountApi {

    private final AccountUseCase accountUseCase;
    private final AccountWebMapper accountWebMapper;

    @Override
    public Mono<ResponseEntity<AccountResponseDTO>> createAccount(
            Mono<AccountRequestDTO> accountRequest,
            ServerWebExchange exchange) {

        log.info("REST request to create Account");

        return accountRequest
                .map(accountWebMapper::requestToDomain)
                .flatMap(accountUseCase::createAccount)
                .map(accountWebMapper::domainToResponse)
                .map(response -> ResponseEntity.status(201).body(response));
    }

    @Override
    public Mono<ResponseEntity<AccountResponseDTO>> getAccountById(
            Long id,
            ServerWebExchange exchange) {

        log.info("REST request to get Account with ID: {}", id);

        return accountUseCase.getAccountById(id)
                .map(accountWebMapper::domainToResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountResponseDTO>>> getAllAccounts(
            ServerWebExchange exchange) {

        log.info("REST request to get all Accounts");

        Flux<AccountResponseDTO> responseFlux =
                accountUseCase.getAllAccounts()
                        .map(accountWebMapper::domainToResponse);

        return Mono.just(ResponseEntity.ok(responseFlux));
    }

    @Override
    public Mono<ResponseEntity<AccountResponseDTO>> updateAccount(
            Long id,
            Mono<AccountRequestDTO> accountRequest,
            ServerWebExchange exchange) {

        log.info("REST request to update Account with ID: {}", id);

        return accountRequest
                .map(accountWebMapper::requestToDomain)
                .flatMap(account -> accountUseCase.updateAccount(id, account))
                .map(accountWebMapper::domainToResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(
            Long id,
            ServerWebExchange exchange) {

        log.info("REST request to delete Account with ID: {}", id);

        return accountUseCase.deleteAccount(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }


}
