package ec.com.nttdata.customerservice.infrastructure.web.mapper;

import ec.com.nttdata.customerservice.api.model.CustomerRequest;
import ec.com.nttdata.customerservice.api.model.CustomerResponse;
import ec.com.nttdata.customerservice.domain.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerWebMapper {

    public Customer requestToDomain(CustomerRequest dto) {
    return Customer.builder()
            .name(dto.getName())
            .gender(dto.getGender())
            .identification(dto.getIdentification())
            .address(dto.getAddress())
            .phone(dto.getPhone())
            .password(dto.getPassword())
            .status(dto.getStatus())
            .build();
}

    public CustomerResponse domainToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getCustomerId());
        response.setName(customer.getName());
        response.setGender(customer.getGender());
        response.setIdentification(customer.getIdentification());
        response.setAddress(customer.getAddress());
        response.setPhone(customer.getPhone());
        response.setStatus(customer.getStatus());
        return response;
    }


}
