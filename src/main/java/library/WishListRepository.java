package library;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Integer> {
    List<WishList> findAllByUserId(Integer userId);
    List<WishList> findAllWishByBookId(Integer bookId);
}
