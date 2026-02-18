package ec.com.nttdata.accountservice.application.usecase;

import ec.com.nttdata.accountservice.domain.exception.CustomerNotFoundException;
import ec.com.nttdata.accountservice.domain.model.Account;
import ec.com.nttdata.accountservice.domain.repository.AccountRepository;
import ec.com.nttdata.accountservice.domain.repository.CustomerServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AccountUseCase {

    private final AccountRepository accountRepository;
    private final CustomerServiceClient customerServiceClient;

    public AccountUseCase(AccountRepository accountRepository, CustomerServiceClient customerServiceClient) {
        this.accountRepository = accountRepository;
        this.customerServiceClient = customerServiceClient;
    }

    public Mono<Account> createAccount(Account account) {

        log.info("Creating account for customer ID: {}", account.getCustomerId());

        return customerServiceClient.customerExists(account.getCustomerId())
                .flatMap(exists -> {
                    if (!exists) {
                        log.error("Customer not found with ID: {}", account.getCustomerId());
                        return Mono.error(new CustomerNotFoundException(
                                "Customer not found with ID: " + account.getCustomerId()));
                    }
                    return accountRepository.save(account);
                });
    }

    public Mono<Account> getAccountById(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Account not found")));
    }

    public Flux<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Mono<Account> updateAccount(Long id, Account account) {

        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Account not found")))
                .flatMap(existing -> {
                    account.setId(id);
                    return accountRepository.save(account);
                });
    }

    public Mono<Void> deleteAccount(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Account not found")))
                .flatMap(existing -> accountRepository.deleteById(id));
    }


}
