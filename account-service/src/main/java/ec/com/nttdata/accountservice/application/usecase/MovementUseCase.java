package ec.com.nttdata.accountservice.application.usecase;

import ec.com.nttdata.accountservice.domain.exception.AccountNotFoundException;
import ec.com.nttdata.accountservice.domain.exception.InsufficientBalanceException;
import ec.com.nttdata.accountservice.domain.model.Movement;
import ec.com.nttdata.accountservice.domain.repository.AccountRepository;
import ec.com.nttdata.accountservice.domain.repository.MovementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class MovementUseCase {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    public MovementUseCase(MovementRepository movementRepository, AccountRepository accountRepository) {
        this.movementRepository = movementRepository;
        this.accountRepository = accountRepository;
    }

    public Mono<Movement> createMovement(Movement movement) {

        log.info("Creating movement for account ID: {}", movement.getAccountId());

        return accountRepository.findById(movement.getAccountId())
                .switchIfEmpty(Mono.error(
                        new AccountNotFoundException("Account not found")))
                .flatMap(account -> {

                    BigDecimal currentBalance = account.getInitialBalance();

                    BigDecimal newBalance = movement.applyTo(currentBalance);

                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                        return Mono.error(
                                new InsufficientBalanceException("Balance not available"));
                    }

                    account.setInitialBalance(newBalance);

                    movement.setDate(LocalDateTime.now());
                    movement.setBalance(newBalance);

                    return accountRepository.save(account)
                            .then(movementRepository.save(movement));
                });
    }


    public Mono<Movement> getMovementById(Long id) {
        log.info("Fetching movement with ID: {}", id);
        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Movement not found")));
    }

    public Flux<Movement> getAllMovements() {
        log.info("Fetching all movements");
        return movementRepository.findAll();
    }

    public Mono<Movement> updateMovement(Long id, Movement newMovement) {

        log.info("Updating movement with ID: {}", id);

        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Movement not found")))
                .flatMap(existingMovement ->

                        accountRepository.findById(existingMovement.getAccountId())
                                .switchIfEmpty(Mono.error(
                                        new AccountNotFoundException("Account not found")))
                                .flatMap(account -> {

                                    BigDecimal currentBalance = account.getInitialBalance();

                                    // Revertir movimiento anterior
                                    BigDecimal revertedBalance =
                                            existingMovement.revertFrom(currentBalance);

                                    // 2️Aplicar nuevo movimiento
                                    BigDecimal newBalance =
                                            newMovement.applyTo(revertedBalance);

                                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                                        return Mono.error(
                                                new InsufficientBalanceException("Balance not available"));
                                    }

                                    account.setInitialBalance(newBalance);

                                    newMovement.setId(id);
                                    newMovement.setAccountId(existingMovement.getAccountId());
                                    newMovement.setDate(LocalDateTime.now());
                                    newMovement.setBalance(newBalance);

                                    return accountRepository.save(account)
                                            .then(movementRepository.save(newMovement));
                                })
                );
    }

    public Mono<Void> deleteMovement(Long id) {

        log.info("Deleting movement with ID: {}", id);

        return movementRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Movement not found")))
                .flatMap(existingMovement ->

                        accountRepository.findById(existingMovement.getAccountId())
                                .switchIfEmpty(Mono.error(
                                        new AccountNotFoundException("Account not found")))
                                .flatMap(account -> {

                                    BigDecimal currentBalance = account.getInitialBalance();

                                    BigDecimal revertedBalance =
                                            existingMovement.revertFrom(currentBalance);

                                    account.setInitialBalance(revertedBalance);

                                    return accountRepository.save(account)
                                            .then(movementRepository.deleteById(id));
                                })
                );
    }
}
