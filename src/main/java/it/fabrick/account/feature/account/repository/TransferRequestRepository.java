package it.fabrick.account.feature.account.repository;

import it.fabrick.account.feature.account.entity.TransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRequestRepository extends JpaRepository<TransferRequest, Long> {
}
