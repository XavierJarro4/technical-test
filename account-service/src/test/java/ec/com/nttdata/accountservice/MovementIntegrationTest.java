package ec.com.nttdata.accountservice;


import ec.com.nttdata.accountservice.api.model.MovementRequestDTO;
import ec.com.nttdata.accountservice.infrastructure.persistence.entity.AccountEntity;
import ec.com.nttdata.accountservice.infrastructure.persistence.repository.AccountJpaRepository;
import ec.com.nttdata.accountservice.infrastructure.persistence.repository.MovementJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class MovementIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MovementJpaRepository movementJpaRepository;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @BeforeEach
    void setup() {
        movementJpaRepository.deleteAll();
        accountJpaRepository.deleteAll();
    }

    //create movement successfully
    @Test
    void shouldCreateMovementSuccessfully() {

        AccountEntity  account = AccountEntity.builder()
                .number("12345")
                .initialBalance(new BigDecimal("1000"))
                .status(true)
                .build();

        account = accountJpaRepository.save(account);

        MovementRequestDTO request = new MovementRequestDTO(
                account.getId(),
                "DEBIT",
                new BigDecimal("100").doubleValue()
        );

        webTestClient.post()
                .uri("/movements")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.value").isEqualTo(100);
    }
}

