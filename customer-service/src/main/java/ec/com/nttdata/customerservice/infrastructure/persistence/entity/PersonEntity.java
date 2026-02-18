package ec.com.nttdata.customerservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class PersonEntity {
    private String name;
    private String gender;
    private String identification;
    private String address;
    private String phone;
}
