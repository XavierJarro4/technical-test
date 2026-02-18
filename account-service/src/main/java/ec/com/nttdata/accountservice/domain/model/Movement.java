package ec.com.nttdata.accountservice.domain.model;

import ec.com.nttdata.accountservice.domain.exception.InvalidMovementValueException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movement {
    private Long id;
    private Long accountId;
    private LocalDateTime date;
    private String type;
    private BigDecimal value;
    private BigDecimal balance;

    public BigDecimal applyTo(BigDecimal balance) {

        validateValue();

        if ("DEBIT".equalsIgnoreCase(this.type)) {
            return balance.subtract(this.value);
        }

        if ("CREDIT".equalsIgnoreCase(this.type)) {
            return balance.add(this.value);
        }

        throw new InvalidMovementValueException("Invalid movement type");
    }

    public BigDecimal revertFrom(BigDecimal balance) {

        validateValue();

        if ("DEBIT".equalsIgnoreCase(this.type)) {
            return balance.add(this.value);
        }

        if ("CREDIT".equalsIgnoreCase(this.type)) {
            return balance.subtract(this.value);
        }

        throw new InvalidMovementValueException("Invalid movement type");
    }

    private void validateValue() {

        if (this.value == null || this.value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMovementValueException(
                    "Movement value must be greater than zero");
        }

        if (this.type == null) {
            throw new InvalidMovementValueException(
                    "Movement type is required");
        }
    }

}
