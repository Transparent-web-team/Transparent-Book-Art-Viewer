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

    public ContentManager() {
        allContents = new ArrayList<>();
        favorites = new HashSet<>();
        readingProgress = new HashMap<>();
        loadData();
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

    public List<Content> getFavorites() {
        List<Content> result = new ArrayList<>();
        for (String id : favorites) {
            Content c = findContentById(id);
            if (c != null) {
                result.add(c);
            }
        }
        return result;
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

    // SEARCH (title OR author)
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
        // sort in-place, case-insensitive
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
        // sort in-place, case-insensitive
        Collections.sort(contents, new Comparator<Content>() {
            @Override
            public int compare(Content c1, Content c2) {
                String a1 = c1.getAuthor() == null ? "" : c1.getAuthor().toLowerCase();
                String a2 = c2.getAuthor() == null ? "" : c2.getAuthor().toLowerCase();
                return a1.compareTo(a2);
            }
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
