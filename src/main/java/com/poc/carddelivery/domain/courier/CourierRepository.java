package com.poc.carddelivery.domain.courier;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, Long> {

    List<Courier> findAllByActiveTrue();
}
