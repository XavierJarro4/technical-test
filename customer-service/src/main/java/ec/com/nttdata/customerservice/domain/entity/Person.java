package ec.com.nttdata.customerservice.domain.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.*;

@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {
    protected String name;
    protected String gender;
    protected String identification;
    protected String address;
    protected String phone;
}
