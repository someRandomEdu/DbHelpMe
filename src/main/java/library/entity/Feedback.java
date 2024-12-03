package library.entity;

public class Feedback {
    private String title;
    private String content;
    private int feedback_id;
    private int user_id;

    public Feedback(int feedback_id, int user_id, String content) {
        this.content = content;
        this.feedback_id = feedback_id;
        this.user_id = user_id;
    }

    public Feedback(int feedback_id, int user_id, String content, String title) {
        this.feedback_id = feedback_id;
        this.user_id = user_id;
        this.content = content;
        this.title = title;
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

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
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
}
