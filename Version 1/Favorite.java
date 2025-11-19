package PRJ;

import java.util.*;

public class Favorite {
    private String contentID, filePath, title;
    private boolean isFavorited;

    public Favorite(String contentID, String filePath, String title) {
        this.contentID = contentID;
        this.filePath = filePath;
        this.title = title;
        this.isFavorited = false;
    }

    public void addToFavorites(String filePath, String title) {
        this.filePath = filePath;
        this.title = title;
        this.isFavorited = true;
    }

    public void removeFromFavorites() {
        this.isFavorited = false;
    }

    // getter
    public String getContentID() {
        return contentID;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public String getFilePath() {
        return filePath;
    }

    public String gettitle() {
        return title;
    }

    // setter
    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setDisplayTitle(String title) {
        this.title = title;
    }

    public void setFavorited(boolean favorited) {
        this.isFavorited = favorited;
    }
}
