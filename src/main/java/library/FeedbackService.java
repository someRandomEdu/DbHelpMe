package library;

import library.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public final class FeedbackService extends JpaService<Feedback, Integer> {
    public FeedbackService(JpaRepository<Feedback, Integer> repository) {
        super(repository);
    }
}
