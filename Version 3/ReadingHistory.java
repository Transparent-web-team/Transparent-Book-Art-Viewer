import java.io.Serializable;

public class ReadingHistory implements Serializable {
    private String contentId;
    private float progress;

    public ReadingHistory(String contentId) {
        this.contentId = contentId;
        this.progress = 0.0f;
    }

    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }
    public float getProgress() { return progress; }
    public void setProgress(float progress) {
        if (progress < 0) {
            this.progress = 0;
        }
        // Nếu progress lớn hơn 100 → đặt thành 100
        else if (progress > 100) {
            this.progress = 100;
        }
        // Còn lại trong khoảng 0–100 → giữ nguyên
        else {
            this.progress = progress;
        }
    }
}
