package ec.com.nttdata.customerservice.application.usecase;

import ec.com.nttdata.customerservice.domain.model.Customer;
import ec.com.nttdata.customerservice.domain.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class CustomerUseCase {

    private final CustomerRepository customerRepository;

    public CustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<Customer> createCustomer(Customer customer) {

        log.info("Creating customer with identification: {}", customer.getIdentification());

        return customerRepository.save(customer);
    }

    public Mono<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Customer not found")));
    }

    public Flux<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Mono<Customer> updateCustomer(Long id, Customer customer) {

        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Customer not found")))
                .flatMap(existing -> {
                    customer.setCustomerId(id);
                    return customerRepository.save(customer);
                });
    }

    public Mono<Void> deleteCustomer(Long id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new IllegalStateException("Customer not found")))
                .flatMap(existing -> customerRepository.deleteById(id));
    }
}

