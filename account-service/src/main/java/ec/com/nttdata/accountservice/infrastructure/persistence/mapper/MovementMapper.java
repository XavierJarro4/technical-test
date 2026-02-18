package ec.com.nttdata.accountservice.infrastructure.persistence.mapper;

import ec.com.nttdata.accountservice.domain.model.Movement;
import ec.com.nttdata.accountservice.infrastructure.persistence.entity.MovementEntity;
import org.springframework.stereotype.Component;

@Component
public class MovementMapper {
    public Movement entityToDomain(MovementEntity entity) {
        if (entity == null) {
            return null;
        }

        return Movement.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .date(entity.getDate())
                .type(entity.getType())
                .value(entity.getValue())
                .balance(entity.getBalance())
                .build();
    }

    public MovementEntity domainToEntity(Movement domain) {
        if (domain == null) {
            return null;
        }

        return MovementEntity.builder()
                .id(domain.getId())
                .accountId(domain.getAccountId())
                .date(domain.getDate())
                .type(domain.getType())
                .value(domain.getValue())
                .balance(domain.getBalance())
                .build();
    }
}
