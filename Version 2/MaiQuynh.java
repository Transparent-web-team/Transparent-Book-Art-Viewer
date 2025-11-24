import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class MyContentFeatures {

    private List<Content> allContents = new ArrayList<>();
    private Set<String> favorites = new HashSet<>();

    // SORT BY TITLE
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

    // SORT BY AUTHOR
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

    // TOGGLE FAVORITE (không kiểm tra hợp lệ theo yêu cầu của bạn)
    public void toggleFavorite(String contentId) {
        if (favorites.contains(contentId)) {
            favorites.remove(contentId);
        } else {
            favorites.add(contentId);
        }
        saveData();
    }

    // Dummy saveData để tránh lỗi biên dịch
    private void saveData() {
        // không làm gì – placeholder
    }
}
