package ec.com.nttdata.accountservice.application.usecase;

import ec.com.nttdata.accountservice.api.model.CustomerReportDTO;
import ec.com.nttdata.accountservice.domain.exception.AccountNotFoundException;
import ec.com.nttdata.accountservice.domain.repository.AccountRepository;
import ec.com.nttdata.accountservice.domain.repository.MovementRepository;
import ec.com.nttdata.accountservice.infrastructure.web.mapper.ReportWebMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
public class ReportUseCase {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final ReportWebMapper reportWebMapper;


    public ReportUseCase(AccountRepository accountRepository, MovementRepository movementRepository, ReportWebMapper reportWebMapper) {
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.reportWebMapper = reportWebMapper;

    }


    public Mono<CustomerReportDTO> generateCustomerReport(
            Long customerId,
            LocalDate startDate,
            LocalDate endDate) {

        log.info("Generating report for customer ID: {} from {} to {}",
                customerId, startDate, endDate);

        // ✅ Validación profesional
        if (startDate.isAfter(endDate)) {
            return Mono.error(new IllegalArgumentException(
                    "startDate cannot be after endDate"));
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return accountRepository.findByCustomerId(customerId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(
                        "No accounts found for customer ID: " + customerId)))
                .flatMap(account ->
                        movementRepository
                                .findByAccountIdAndDateBetween(
                                        account.getId(),
                                        startDateTime,
                                        endDateTime)
                                .collectList()
                                .map(movements ->
                                        reportWebMapper.domainToResponse(account, movements)
                                )
                )
                .collectList()
                .map(accountReports -> {
                    CustomerReportDTO report = new CustomerReportDTO();
                    report.setCustomerId(customerId);
                    report.setAccounts(accountReports);
                    return report;
                });
    }



}
