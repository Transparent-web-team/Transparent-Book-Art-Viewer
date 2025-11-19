public class ReadingHistory {
    private String contentId;
    private int duration;
    private float progress;

    public ReadingHistory(String contentId) {
        this.contentId = contentId;
        this.duration = 0;
        this.progress = 0.0f;
    }

    public void updateProgress(float p) {
        this.progress += p;
        if (this.progress > 100) {
            this.progress = 100;
        } else if (this.progress < 0) {
            this.progress = 0;
        }
    }

    public void addDuration(int minutes) {
        this.duration += minutes;
    }

    // Getter và Setter
    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
