package ec.com.nttdata.accountservice.infrastructure.web.mapper;

import ec.com.nttdata.accountservice.api.model.AccountReportDTO;
import ec.com.nttdata.accountservice.api.model.MovementResponseDTO;
import ec.com.nttdata.accountservice.domain.model.Account;
import ec.com.nttdata.accountservice.domain.model.Movement;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportWebMapper {


    public AccountReportDTO domainToResponse(
            Account account,
            List<Movement> movements) {

        AccountReportDTO dto = new AccountReportDTO();

        dto.setId(account.getId());
        dto.setNumber(account.getNumber());
        dto.setBalance(account.getInitialBalance().doubleValue());
        dto.setType(account.getType());
        dto.setStatus(account.getStatus());

        List<MovementResponseDTO> movementDTOs = movements.stream()
                .map(this::movementToResponse)
                .collect(Collectors.toList());

        dto.setMovements(movementDTOs);

        return dto;
    }

    private MovementResponseDTO movementToResponse(Movement movement) {

        MovementResponseDTO dto = new MovementResponseDTO();
        dto.setId(movement.getId());
        dto.setAccountId(movement.getAccountId());
        dto.setDate(movement.getDate().atOffset(ZoneOffset.UTC));
        dto.setType(movement.getType());
        dto.setValue(movement.getValue().doubleValue());
        dto.setBalance(movement.getBalance().doubleValue());

        return dto;
    }

}
