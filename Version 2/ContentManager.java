import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class ContentManager {
    private List<Content> allContents;
    private Set<String> favorites;
    private Map<String, ReadingHistory> readingProgress;
    private int nextId = 1;

    private static final String FILE_CONTENTS = "contents.dat";
    private static final String FILE_FAVORITES = "favorites.dat";
    private static final String FILE_PROGRESS = "progress.dat";
    private static final String FILE_ID = "nextid.dat";
    private static final String USER_GUIDE_FLAG = "user_guide_loaded.dat";

    public ContentManager() {
        allContents = new ArrayList<>();
        favorites = new HashSet<>();
        readingProgress = new HashMap<>();
        loadData();
        loadUserGuideIfNeeded();
    }

    private void loadUserGuideIfNeeded() {
        // Check if User Guide already loaded
        File flagFile = new File(USER_GUIDE_FLAG);
        if (flagFile.exists()) {
            return; 
        }

        try {
            // Extract user-guide.pdf from resources to temp location
            InputStream is = getClass().getResourceAsStream("/User_Guide.pdf");
            if (is == null) {
                System.out.println("User Guide not found in resources");
                return;
            }

            // Create app data folder for user guide
            File appDataDir = new File(System.getProperty("user.home"), ".transparent");
            if (!appDataDir.exists()) {
                appDataDir.mkdirs();
            }

            File userGuidePdf = new File(appDataDir, "User-Guide-Transparent.pdf");

            // Copy from resources to file system
            try (FileOutputStream fos = new FileOutputStream(userGuidePdf)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            is.close();

            String title = "User Guide - Transparent";
            String author = "Transparent Team";
            String filePath = userGuidePdf.getAbsolutePath();

            long size = userGuidePdf.length();
            String format = "PDF";
            LocalDate dateAdded = LocalDate.now();
            Info info = new Info(size, format, dateAdded);
            String newId = String.format("C%04d", nextId++);

            Content userGuide = new Content(newId, author, title, filePath, info);
            allContents.add(userGuide);
            
            saveData();

            // Create flag file so we don't load it again
            flagFile.createNewFile();

        } catch (Exception e) {
            System.err.println("Failed to load User Guide: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String addContent(String title, String author, String filePath) throws Exception {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist");
        }

        String fileName = file.getName();
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("File must be a PDF");
        }

        long size = file.length();
        String format = "PDF";
        LocalDate dateAdded = LocalDate.now();
        Info info = new Info(size, format, dateAdded);
        String newId = String.format("C%04d", nextId++);

        Content newContent = new Content(newId, author.trim(), title.trim(), filePath, info);
        allContents.add(newContent);
        saveData();

        return newId;
    }

    public Content findContentById(String contentId) {
        for (Content c : allContents) {
            if (c.getContentId().equals(contentId)) {
                return c;
            }
        }
        return null;
    }

    public boolean removeContent(String contentId) {
        Content content = findContentById(contentId);
        if (content != null) {
            allContents.remove(content);
            favorites.remove(contentId);
            readingProgress.remove(contentId); // Xóa luôn tiến độ đọc
            saveData();
            return true;
        }
        return false;
    }

    public List<Content> getAllContents() {
        return new ArrayList<>(allContents);
    }
    
    public boolean isFavorite(String contentId) {
        return favorites.contains(contentId);
    }

    public void toggleFavorite(String contentId) {
        if (favorites.contains(contentId)) {
            favorites.remove(contentId);
        } else {
            favorites.add(contentId);
        }
        saveData();
    }

    public void updateProgress(String contentId, float progress) {
        if (!readingProgress.containsKey(contentId)) {
            readingProgress.put(contentId, new ReadingHistory(contentId));
        }
        ReadingHistory history = readingProgress.get(contentId);
        history.setProgress(progress);
        saveData();
    }
    public ReadingHistory getReadingHistory(String contentId) {
        return readingProgress.get(contentId);
    }
    
    public List<Content> search(String keyword) {
        List<Content> result = new ArrayList<>();
        if (keyword == null) keyword = "";
        String q = keyword.trim().toLowerCase();

        // nếu rỗng -> trả về toàn bộ danh sách contents (bản sao)
        if (q.isEmpty()) {
            result.addAll(allContents);
            return result;
        }

        for (Content c : allContents) {
            String title = c.getTitle() == null ? "" : c.getTitle().toLowerCase();
            String author = c.getAuthor() == null ? "" : c.getAuthor().toLowerCase();
            if (title.contains(q) || author.contains(q)) {
                result.add(c);
            }
        }
        return result;
    }

    public void sortByTitle(List<Content> contents) {
        if (contents == null) return;
        Collections.sort(contents, new Comparator<Content>() {
            @Override
            public int compare(Content c1, Content c2) {
                String t1 = c1.getTitle() == null ? "" : c1.getTitle().toLowerCase();
                String t2 = c2.getTitle() == null ? "" : c2.getTitle().toLowerCase();
                return t1.compareTo(t2);
            }
        });
    }

    public void sortByAuthor(List<Content> contents) {
        if (contents == null) return;
        Collections.sort(contents, new Comparator<Content>() {
            @Override
            public int compare(Content c1, Content c2) {
                String a1 = c1.getAuthor() == null ? "" : c1.getAuthor().toLowerCase();
                String a2 = c2.getAuthor() == null ? "" : c2.getAuthor().toLowerCase();
                return a1.compareTo(a2);
            }
        });
    }

    public void sortByFavorite(List<Content> list) {
        list.sort((c1, c2) -> {
            boolean fav1 = isFavorite(c1.getContentId());
            boolean fav2 = isFavorite(c2.getContentId());

            // Favorite items first
            if (fav1 && !fav2) return -1;
            if (!fav1 && fav2) return 1;

            // If both favorite or both not favorite, sort by title
            return c1.getTitle().compareToIgnoreCase(c2.getTitle());
        });
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_CONTENTS))) {
            allContents = (List<Content>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File chưa tồn tại, bỏ qua
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_FAVORITES))) {
            favorites = (Set<String>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File chưa tồn tại, bỏ qua
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PROGRESS))) {
            readingProgress = (Map<String, ReadingHistory>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File chưa tồn tại, bỏ qua
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_ID))) {
            nextId = (Integer) ois.readObject();
        } catch (FileNotFoundException e) {
            // File chưa tồn tại, bỏ qua
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_CONTENTS))) {
            oos.writeObject(allContents);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_FAVORITES))) {
            oos.writeObject(favorites);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PROGRESS))) {
            oos.writeObject(readingProgress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_ID))) {
            oos.writeObject(nextId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
