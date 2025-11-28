import java.time.LocalDate;
import java.io.Serializable;

public class Info implements Serializable {
    private long size;
    private String format;
    private LocalDate dateAdded;

    public Info(long size, String format, LocalDate dateAdded) {
        this.size = size;
        this.format = format;
        this.dateAdded = dateAdded;
    }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public LocalDate getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDate dateAdded) { this.dateAdded = dateAdded; }

    public String getSizeFormat() {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        return String.format("%.2f MB", size / (1024.0 * 1024.0));
    }
}
