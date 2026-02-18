package ec.com.nttdata.customerservice.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Customer extends Person {

    private Long customerId;
    private String password;
    private Boolean status;

}
