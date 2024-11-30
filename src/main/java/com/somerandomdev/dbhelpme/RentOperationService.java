package com.somerandomdev.dbhelpme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public final class RentOperationService extends JpaService<RentOperation, Long> {
    public RentOperationService(JpaRepository<RentOperation, Long> repository) {
        super(repository);
    }

    public List<RentOperation> getRentHistoryOf(Account account) {
        return findAllBy(value -> Objects.equals(value.getAccountId(), account.getId()));
    }

    public List<RentOperation> getRentHistorySortedNonDesc(Account account) {
        var result = findAllBy(value -> Objects.equals(value.getAccountId(), account.getId()));
        result.sort(Comparator.comparing(RentOperation::getRentTime));
        return result;
    }
}
