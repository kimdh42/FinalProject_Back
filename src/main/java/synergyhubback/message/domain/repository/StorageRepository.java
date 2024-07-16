package synergyhubback.message.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synergyhubback.message.domain.entity.Storage;

public interface StorageRepository extends JpaRepository<Storage, Integer> {
}
