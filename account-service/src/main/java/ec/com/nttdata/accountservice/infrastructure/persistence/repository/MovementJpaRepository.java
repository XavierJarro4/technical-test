package ec.com.nttdata.accountservice.infrastructure.persistence.repository;

import ec.com.nttdata.accountservice.infrastructure.persistence.entity.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovementJpaRepository extends JpaRepository<MovementEntity, Long> {
    List<MovementEntity> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}
