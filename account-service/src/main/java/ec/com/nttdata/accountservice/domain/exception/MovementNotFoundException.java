package ec.com.nttdata.accountservice.domain.exception;

public class MovementNotFoundException extends RuntimeException{

    public MovementNotFoundException(String message) {
        super(message);
    }

}
