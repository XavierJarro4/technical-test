package ec.com.nttdata.customerservice.domain.exception;

public class CustomerNotFoundException extends RuntimeException{

    public CustomerNotFoundException(Long customerId) {
        super(String.format("Customer with ID %d not found", customerId));
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }

}
