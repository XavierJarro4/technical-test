package ec.com.nttdata.accountservice.infrastructure.web.mapper;

import ec.com.nttdata.accountservice.api.model.AccountRequestDTO;
import ec.com.nttdata.accountservice.api.model.AccountResponseDTO;
import ec.com.nttdata.accountservice.domain.model.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountWebMapper {

    public Account requestToDomain(AccountRequestDTO request) {
        if (request == null) {
            return null;
        }

        return Account.builder()
                .customerId(request.getCustomerId())
                .number(request.getNumber())
                .type(request.getType())
                .initialBalance(BigDecimal.valueOf(request.getInitialBalance()))
                .status(request.getStatus() != null ? request.getStatus() : Boolean.TRUE)
                .build();
    }

    public AccountResponseDTO domainToResponse(Account account) {
        if (account == null) {
            return null;
        }

        AccountResponseDTO response = new AccountResponseDTO();
        response.setId(account.getId());
        response.setCustomerId(account.getCustomerId());
        response.setNumber(account.getNumber());
        response.setType(account.getType());
        response.setBalance(
                account.getInitialBalance() != null
                        ? account.getInitialBalance().doubleValue()
                        : null
        );
        response.setStatus(account.getStatus());

        return response;
    }


}
