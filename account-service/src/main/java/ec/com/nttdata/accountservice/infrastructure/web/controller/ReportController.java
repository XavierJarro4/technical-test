package ec.com.nttdata.accountservice.infrastructure.web.controller;

import ec.com.nttdata.accountservice.api.ReportApi;
import ec.com.nttdata.accountservice.api.model.CustomerReportDTO;
import ec.com.nttdata.accountservice.application.usecase.ReportUseCase;
import ec.com.nttdata.accountservice.infrastructure.web.mapper.MovementWebMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportController implements ReportApi {

    private final ReportUseCase reportUseCase;
    private final MovementWebMapper movementWebMapper;

    @Override
    public Mono<ResponseEntity<CustomerReportDTO>> generateReport(
            Long customerId,
            LocalDate startDate,
            LocalDate endDate,
            ServerWebExchange exchange) {

        log.info("REST request to generate report for customer ID: {} from {} to {}",
                customerId, startDate, endDate);

        return reportUseCase.generateCustomerReport(customerId, startDate, endDate)
                .map(ResponseEntity::ok);
    }


}
