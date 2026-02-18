package ec.com.nttdata.customerservice.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {

    protected String name;
    protected String gender;
    protected String identification;
    protected String address;
    protected String phone;

}
