package ec.com.nttdata.accountservice.infrastructure.persistence.repository;

import ec.com.nttdata.accountservice.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
    List<AccountEntity> findByCustomerId(Long customerId);
}
