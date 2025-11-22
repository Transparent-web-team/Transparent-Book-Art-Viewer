import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class ContentManager {
    private List<Content> allContents;
    private Map<String, ReadingHistory> libraryContents;
    private Set<String> favorites;
    private int nextId = 1;

    private static final String FILE_CONTENTS = "contents.dat";
    private static final String FILE_LIBRARY = "library.dat";
    private static final String FILE_FAVORITES = "favorites.dat";
    private static final String FILE_ID = "nextid.dat";

    public ContentManager() {
        allContents = new ArrayList<>();
        libraryContents = new HashMap<>();
        favorites = new HashSet<>();
        loadData();
    }
  
    public List<Content> getAllContents() {
        return new ArrayList<>(allContents);
    }
  
    public Content  findContentById(String contentId) {
        for (Content c : allContents) {
            if (c.getContentId().equals(contentId)) {
                return c;
            }
        }
        return null;
    }
  
    public List<Content> getLibraryContents() {
        List<Content> library = new ArrayList<>();
        for (String contentId : libraryContents.keySet()) {
            Content c = findContentById(contentId);
            if (c != null) {
                library.add(c);
            }
        }
        return library;
    }
