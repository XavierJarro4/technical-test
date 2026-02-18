package ec.com.nttdata.accountservice.application.usecase;

import ec.com.nttdata.accountservice.domain.exception.AccountNotFoundException;
import ec.com.nttdata.accountservice.domain.exception.InsufficientBalanceException;
import ec.com.nttdata.accountservice.domain.exception.InvalidMovementValueException;
import ec.com.nttdata.accountservice.domain.model.Account;
import ec.com.nttdata.accountservice.domain.model.Movement;
import ec.com.nttdata.accountservice.domain.repository.AccountRepository;
import ec.com.nttdata.accountservice.domain.repository.MovementRepository;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class MovementUseCaseTest {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private MovementUseCase movementUseCase;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .initialBalance(new BigDecimal("100"))
                .build();
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenCreating() {

        Movement movement = Movement.builder()
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("50"))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.createMovement(movement))
                .expectError(AccountNotFoundException.class)
                .verify();
    }


    @Test
    void shouldThrowInvalidMovementTypeWhenCreating() {

        Movement movement = Movement.builder()
                .accountId(1L)
                .type("INVALID")
                .value(new BigDecimal("10"))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(account));

        StepVerifier.create(movementUseCase.createMovement(movement))
                .expectError(InvalidMovementValueException.class)
                .verify();
    }

    @Test
    void shouldAllowBalanceToBeZero() {

        Movement movement = Movement.builder()
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("100"))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(account));

        when(accountRepository.save(any()))
                .thenReturn(Mono.just(account));

        when(movementRepository.save(any()))
                .thenReturn(Mono.just(movement));

        StepVerifier.create(movementUseCase.createMovement(movement))
                .expectNextMatches(saved ->
                        saved.getBalance().compareTo(BigDecimal.ZERO) == 0)
                .verifyComplete();
    }

    @Test
    void shouldThrowAccountNotFoundWhenCreating() {

        Movement movement = Movement.builder()
                .accountId(99L)
                .type("DEBIT")
                .value(new BigDecimal("10"))
                .build();

        when(accountRepository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.createMovement(movement))
                .expectError(AccountNotFoundException.class)
                .verify();

        verify(movementRepository, never()).save(any());
    }


    @Test
    void shouldCreateMovementSuccessfully() {

        Movement movement = Movement.builder()
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("50"))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        when(movementRepository.save(any(Movement.class)))
                .thenReturn(Mono.just(movement));

        StepVerifier.create(movementUseCase.createMovement(movement))
                .expectNextMatches(saved ->
                        saved.getValue().equals(new BigDecimal("50")) &&
                                saved.getBalance().equals(new BigDecimal("50")))
                .verifyComplete();

        verify(accountRepository).save(any(Account.class));
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    void shouldThrowInsufficientBalanceException() {

        Movement movement = Movement.builder()
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("200")) // Mayor que el saldo
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(account));

        StepVerifier.create(movementUseCase.createMovement(movement))
                .expectError(InsufficientBalanceException.class)
                .verify();

        verify(accountRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
    }

    @Test
    void shouldCreateCreditMovementSuccessfully() {

        Movement movement = Movement.builder()
                .accountId(1L)
                .type("CREDIT")
                .value(new BigDecimal("50"))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        when(movementRepository.save(any(Movement.class)))
                .thenReturn(Mono.just(movement));

        StepVerifier.create(movementUseCase.createMovement(movement))
                .expectNextMatches(saved ->
                        saved.getBalance().equals(new BigDecimal("150")))
                .verifyComplete();

        verify(accountRepository).save(any(Account.class));
        verify(movementRepository).save(any(Movement.class));
    }


    //update
    @Test
    void shouldThrowExceptionWhenUpdatingMovementNotFound() {

        when(movementRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.updateMovement(1L, new Movement()))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void shouldThrowAccountNotFoundExceptionWhenUpdating() {

        Movement existingMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("20"))
                .build();

        when(movementRepository.findById(1L))
                .thenReturn(Mono.just(existingMovement));

        when(accountRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.updateMovement(1L, new Movement()))
                .expectError(AccountNotFoundException.class)
                .verify();
    }


    @Test
    void shouldThrowWhenUpdatingNonExistingMovement() {

        when(movementRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.updateMovement(1L, new Movement()))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void shouldThrowAccountNotFoundWhenUpdating() {

        Movement existingMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("20"))
                .build();

        when(movementRepository.findById(1L))
                .thenReturn(Mono.just(existingMovement));

        when(accountRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.updateMovement(1L, new Movement()))
                .expectError(AccountNotFoundException.class)
                .verify();
    }

    @Test
    void shouldUpdateMovementSuccessfully() {

        Movement existingMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("20"))
                .build();

        Movement newMovement = Movement.builder()
                .type("DEBIT")
                .value(new BigDecimal("30"))
                .build();

        // Saldo actual de cuenta
        account.setInitialBalance(new BigDecimal("80"));
        // Explicación:
        // Antes había un débito de 20 → saldo real ya es 80
        // Al revertirlo → vuelve a 100
        // Luego aplicamos nuevo débito de 30 → queda 70

        when(movementRepository.findById(1L))
                .thenReturn(Mono.just(existingMovement));

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        when(movementRepository.save(any(Movement.class)))
                .thenReturn(Mono.just(newMovement));

        StepVerifier.create(movementUseCase.updateMovement(1L, newMovement))
                .expectNextMatches(updated ->
                        updated.getBalance().equals(new BigDecimal("70")))
                .verifyComplete();

        verify(accountRepository).save(any(Account.class));
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionWhenUpdating() {

        Movement existingMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("20"))
                .build();

        Movement newMovement = Movement.builder()
                .type("DEBIT")
                .value(new BigDecimal("200"))
                .build();

        account.setInitialBalance(new BigDecimal("80"));

        when(movementRepository.findById(1L))
                .thenReturn(Mono.just(existingMovement));

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(account));

        StepVerifier.create(movementUseCase.updateMovement(1L, newMovement))
                .expectError(InsufficientBalanceException.class)
                .verify();

        verify(accountRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
    }

    @Test
    void shouldUpdateMovementFromDebitToCredit() {

        Movement existingMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("20"))
                .build();

        Movement newMovement = Movement.builder()
                .type("CREDIT")
                .value(new BigDecimal("30"))
                .build();

        account.setInitialBalance(new BigDecimal("80"));
        // Revert 20 → 100
        // Apply credit 30 → 130

        when(movementRepository.findById(1L))
                .thenReturn(Mono.just(existingMovement));

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        when(movementRepository.save(any(Movement.class)))
                .thenReturn(Mono.just(newMovement));

        StepVerifier.create(movementUseCase.updateMovement(1L, newMovement))
                .expectNextMatches(updated ->
                        updated.getBalance().equals(new BigDecimal("130")))
                .verifyComplete();
    }

    //delete
    @Test
    void shouldThrowExceptionWhenDeletingMovementNotFound() {

        when(movementRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.deleteMovement(1L))
                .expectError(IllegalStateException.class)
                .verify();
    }
    @Test
    void shouldThrowAccountNotFoundExceptionWhenDeleting() {

        Movement existingMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("20"))
                .build();

        when(movementRepository.findById(1L))
                .thenReturn(Mono.just(existingMovement));

        when(accountRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.deleteMovement(1L))
                .expectError(AccountNotFoundException.class)
                .verify();
    }

    @Test
    void shouldThrowAccountNotFoundWhenDeleting() {

        Movement existingMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("20"))
                .build();

        when(movementRepository.findById(1L))
                .thenReturn(Mono.just(existingMovement));

        when(accountRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.deleteMovement(1L))
                .expectError(AccountNotFoundException.class)
                .verify();
    }


    @Test
    void shouldDeleteMovementSuccessfully() {

        Movement existingMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("DEBIT")
                .value(new BigDecimal("20"))
                .build();

        // Saldo actual ya con el débito aplicado
        account.setInitialBalance(new BigDecimal("80"));

        // Al revertir el débito de 20 → saldo vuelve a 100

        when(movementRepository.findById(1L))
                .thenReturn(Mono.just(existingMovement));

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(account));

        when(movementRepository.deleteById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.deleteMovement(1L))
                .verifyComplete();

        verify(accountRepository).save(any(Account.class));
        verify(movementRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingMovement() {

        when(movementRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.deleteMovement(1L))
                .expectError(IllegalStateException.class)
                .verify();

        verify(accountRepository, never()).save(any());
    }



    //get by ud
    @Test
    void shouldReturnMovementById() {

        Movement movement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .type("CREDIT")
                .value(new BigDecimal("50"))
                .build();

        when(movementRepository.findById(1L))
                .thenReturn(Mono.just(movement));

        StepVerifier.create(movementUseCase.getMovementById(1L))
                .expectNext(movement)
                .verifyComplete();

        verify(movementRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenMovementNotFound() {

        when(movementRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(movementUseCase.getMovementById(1L))
                .expectError(IllegalStateException.class)
                .verify();

        verify(movementRepository).findById(1L);
    }

    //get all

    @Test
    void shouldReturnAllMovements() {

        Movement movement1 = Movement.builder().id(1L).build();
        Movement movement2 = Movement.builder().id(2L).build();

        when(movementRepository.findAll())
                .thenReturn(Flux.just(movement1, movement2));

        StepVerifier.create(movementUseCase.getAllMovements())
                .expectNext(movement1)
                .expectNext(movement2)
                .verifyComplete();

        verify(movementRepository).findAll();
    }




}
