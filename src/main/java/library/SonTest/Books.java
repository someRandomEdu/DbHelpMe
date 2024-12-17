package library.SonTest;

public class Books {
    private String title;
    private String author;
    private String coverUrl;

    public Books(String title, String author, String coverUrl) {
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCoverUrl() { return coverUrl; }
}
