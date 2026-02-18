package ec.com.nttdata.customerservice.infrastructure.persistence.mapper;

import ec.com.nttdata.customerservice.domain.model.Customer;
import ec.com.nttdata.customerservice.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer entityToDomain(CustomerEntity entity) {
        return Customer.builder()
                .customerId(entity.getCustomerId())
                .name(entity.getName())
                .gender(entity.getGender())
                .identification(entity.getIdentification())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .password(entity.getPassword())
                .status(entity.getStatus())
                .build();
    }

    public CustomerEntity domainToEntity(Customer customer) {
        return CustomerEntity.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .gender(customer.getGender())
                .identification(customer.getIdentification())
                .address(customer.getAddress())
                .phone(customer.getPhone())
                .password(customer.getPassword())
                .status(customer.getStatus())
                .build();
    }
}
