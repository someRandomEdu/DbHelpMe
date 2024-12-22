package library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "feedbacks")
public class Feedback {
    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int feedback_id;

    private int user_id;
    private String status;

    public Feedback() {}

    public Feedback(int feedback_id, int user_id, String content) {
        this.content = content;
        this.feedback_id = feedback_id;
        this.user_id = user_id;
        status = "Pending";
    }

    public Feedback(int feedback_id, int user_id, String title, String content) {
        this.feedback_id = feedback_id;
        this.user_id = user_id;
        this.content = content;
        this.title = title;
        status = "Pending";
    }

    public Feedback(int feedback_id, int user_id, String title, String content, String status) {
        this.feedback_id = feedback_id;
        this.user_id = user_id;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public int getFeedback_id() {
        return feedback_id;
    }

    public void setFeedback_id(int feedback_id) {
        this.feedback_id = feedback_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
