    package library;

    import jakarta.persistence.*;

    import java.time.LocalDateTime;
    import java.util.Date;

    @Entity
    @Table(name = "wishlist")
    public final class WishList {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer Id;

        @Column(name = "user_id")
        private Integer userId;

        @Column(name = "book_id")
        private Integer bookId;

        @Column(name = "added_date")
        private LocalDateTime addedDate;

        public WishList() {}

        public WishList(Integer Id, Integer userId, Integer bookId, LocalDateTime addedDate) {
            this.Id = Id;
            this.userId = userId;
            this.bookId = bookId;
            this.addedDate = addedDate;
        }

        public Integer getId() {
            return Id;
        }

        public void setId(Integer id) {
            Id = id;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getBookId() {
            return bookId;
        }

        public void setBookId(Integer bookId) {
            this.bookId = bookId;
        }

        public LocalDateTime getAddedDate() {
            return addedDate;
        }

        public void setAddedDate(LocalDateTime addedDate) {
            this.addedDate = addedDate;
        }
    }
