package ec.com.nttdata.accountservice.infrastructure.persistence.mapper;

import ec.com.nttdata.accountservice.domain.model.Account;
import ec.com.nttdata.accountservice.infrastructure.persistence.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account entityToDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        return Account.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .number(entity.getNumber())
                .type(entity.getType())
                .initialBalance(entity.getInitialBalance())
                .status(entity.getStatus())
                .build();
    }

    public AccountEntity domainToEntity(Account domain) {
        if (domain == null) {
            return null;
        }
        return AccountEntity.builder()
                .id(domain.getId())
                .customerId(domain.getCustomerId())
                .number(domain.getNumber())
                .type(domain.getType())
                .initialBalance(domain.getInitialBalance())
                .status(domain.getStatus())
                .build();
    }
}
