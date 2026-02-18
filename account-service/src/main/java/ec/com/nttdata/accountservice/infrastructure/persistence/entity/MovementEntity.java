package ec.com.nttdata.accountservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountId;
    private LocalDateTime date;
    private String type;
    @Column(name = "\"value\"")
    private BigDecimal value;
    private BigDecimal balance;
}
