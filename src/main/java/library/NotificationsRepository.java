package library;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
    List<Notifications> findByUserIdAndStatusOrderByCreatedAtDesc(int userId, String status);
    List<Notifications> findByUserIdOrderByCreatedAtDesc(int userId);
}
