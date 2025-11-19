import java.io.File;
import java.util.Scanner;

public class Content {
    private String contentId;
    private String author;
    private String title;
    private String filePath;

    public Content(String contentId, String author, String title, String filePath) {
        this.contentId = contentId;
        this.author = author;
        this.title = title;
        this.filePath = filePath;
    }

    public String getContentId() {
        return contentId;
    }
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void open() {
        try (Scanner sc = new Scanner(new File(filePath))) {
            while (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
        } catch (Exception e) {
            System.out.println("Error!");
        }
    }
}
