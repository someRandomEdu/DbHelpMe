package com.somerandomdev.dbhelpme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public final class RentDataService extends JpaService<RentData, Long> {
    public RentDataService(JpaRepository<RentData, Long> repository) {
        super(repository);
    }
}
