package library;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ReturnDataService {
    private final ReturnDataRepository returnDataRepository;

    public ReturnDataService(ReturnDataRepository returnDataRepository) {
        this.returnDataRepository = returnDataRepository;
    }

    // Lưu bản ghi mới
    public ReturnData save(ReturnData returnData) {
        return returnDataRepository.save(returnData);
    }

    // Tìm kiếm bản ghi dựa trên accountId và bookId
    public Optional<ReturnData> findOneByAccountIdAndBookId(Integer accountId, Integer bookId) {
        return returnDataRepository.findOneByAccountIdAndBookId(accountId, bookId);
    }
}
