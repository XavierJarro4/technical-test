package ec.com.nttdata.accountservice.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;
    private Long customerId;
    private String number;
    private String type;
    private BigDecimal initialBalance;
    private Boolean status;
}
