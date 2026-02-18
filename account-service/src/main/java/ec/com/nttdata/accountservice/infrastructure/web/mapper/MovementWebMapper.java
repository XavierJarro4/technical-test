package ec.com.nttdata.accountservice.infrastructure.web.mapper;

import ec.com.nttdata.accountservice.api.model.MovementRequestDTO;
import ec.com.nttdata.accountservice.api.model.MovementResponseDTO;
import ec.com.nttdata.accountservice.domain.model.Movement;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class MovementWebMapper {

    public Movement requestToDomain(MovementRequestDTO request) {

        if (request == null) {
            return null;
        }

        return Movement.builder()
                .accountId(request.getAccountId())
                .type(request.getType())
                .value(BigDecimal.valueOf(request.getValue()))
                .date(LocalDateTime.now()) // fecha se genera en backend
                .build();
    }

    public MovementResponseDTO domainToResponse(Movement movement) {

        if (movement == null) {
            return null;
        }

        return new MovementResponseDTO()
                .id(movement.getId())
                .accountId(movement.getAccountId())
                .type(movement.getType())
                .value(movement.getValue().doubleValue())
                .balance(movement.getBalance().doubleValue())
                .date(movement.getDate().atOffset(ZoneOffset.UTC));
    }
}
