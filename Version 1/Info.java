import java.time.LocalDate;

public class Info {
    private long size;
    private String format;
    private LocalDate daycreated;
    private String[] tags;

    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }
    public LocalDate getDaycreated() {
        return daycreated;
    }
    public void setDaycreated(LocalDate daycreated) {
        this.daycreated = daycreated;
    }
    public String[] getTags() {
        return tags;
    }
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Info(long size,String format,LocalDate daycreated,String[] tags) {
        this.size=size;
        this.format=format;
        this.daycreated=daycreated;
        this.tags=tags;
    }

}
