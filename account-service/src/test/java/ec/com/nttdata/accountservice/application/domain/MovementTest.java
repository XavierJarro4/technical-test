package ec.com.nttdata.accountservice.application.domain;

import ec.com.nttdata.accountservice.domain.exception.InvalidMovementValueException;
import ec.com.nttdata.accountservice.domain.model.Movement;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MovementTest {

    @Test
    void applyDebitShouldSubtract() {
        Movement movement = Movement.builder()
                .type("DEBIT")
                .value(new BigDecimal("50"))
                .build();

        BigDecimal result = movement.applyTo(new BigDecimal("100"));

        assertEquals(new BigDecimal("50"), result);
    }

    @Test
    void applyCreditShouldAdd() {
        Movement movement = Movement.builder()
                .type("CREDIT")
                .value(new BigDecimal("50"))
                .build();

        BigDecimal result = movement.applyTo(new BigDecimal("100"));

        assertEquals(new BigDecimal("150"), result);
    }

    @Test
    void revertDebitShouldAddBack() {
        Movement movement = Movement.builder()
                .type("DEBIT")
                .value(new BigDecimal("50"))
                .build();

        BigDecimal result = movement.revertFrom(new BigDecimal("50"));

        assertEquals(new BigDecimal("100"), result);
    }

    @Test
    void revertCreditShouldSubtract() {
        Movement movement = Movement.builder()
                .type("CREDIT")
                .value(new BigDecimal("50"))
                .build();

        BigDecimal result = movement.revertFrom(new BigDecimal("150"));

        assertEquals(new BigDecimal("100"), result);
    }

    @Test
    void shouldThrowExceptionForInvalidType() {
        Movement movement = Movement.builder()
                .type("INVALID")
                .value(new BigDecimal("50"))
                .build();

        assertThrows(
                InvalidMovementValueException.class,
                () -> movement.applyTo(new BigDecimal("100"))
        );
    }
    @Test
    void shouldApplyCredit() {

        Movement movement = Movement.builder()
                .type("CREDIT")
                .value(new BigDecimal("50"))
                .build();

        BigDecimal result = movement.applyTo(new BigDecimal("100"));

        assertEquals(new BigDecimal("150"), result);
    }

    @Test
    void shouldThrowInvalidMovementValueExceptionWhenValueIsZero() {

        Movement movement = Movement.builder()
                .type("DEBIT")
                .value(BigDecimal.ZERO)
                .build();

        assertThrows(InvalidMovementValueException.class,
                () -> movement.applyTo(new BigDecimal("100")));
    }

    @Test
    void shouldThrowInvalidMovementValueExceptionWhenTypeInvalid() {

        Movement movement = Movement.builder()
                .type("INVALID")
                .value(new BigDecimal("10"))
                .build();

        assertThrows(InvalidMovementValueException.class,
                () -> movement.applyTo(new BigDecimal("100")));
    }
}

