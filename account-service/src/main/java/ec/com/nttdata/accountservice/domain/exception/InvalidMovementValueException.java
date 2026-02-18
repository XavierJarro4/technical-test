package ec.com.nttdata.accountservice.domain.exception;

public class InvalidMovementValueException extends RuntimeException{
    public InvalidMovementValueException(String message) {
        super(message);
    }
}
