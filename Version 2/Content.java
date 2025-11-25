import java.io.Serializable;

public class Content implements Serializable {
    private String contentId;
    private String author;
    private String title;
    private String filePath;
    private Info info;

    public Content(String contentId, String author, String title, String filePath, Info info) {
        this.contentId = contentId;
        this.author = author;
        this.title = title;
        this.filePath = filePath;
        this.info = info;
    }

    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Info getInfo() { return info; }
    public void setInfo(Info info) { this.info = info; }

}
