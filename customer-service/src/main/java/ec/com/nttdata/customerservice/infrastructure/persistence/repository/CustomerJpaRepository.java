package ec.com.nttdata.customerservice.infrastructure.persistence.repository;

import ec.com.nttdata.customerservice.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {
}
