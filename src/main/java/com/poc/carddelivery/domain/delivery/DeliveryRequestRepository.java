package com.poc.carddelivery.domain.delivery;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest, Long> {

    @EntityGraph(attributePaths = "cardIssue")
    List<DeliveryRequest> findAllByCourierCodeAndStatus(String courierCode, DeliveryStatus status);
}
