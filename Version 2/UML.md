
# Transparent Books&Art Viewer

A Java-based application for Window users to view book & art in a beautiful way.

## Images
## Main features
**Class Diagram:<br>**

1. Info:<br>
<img width="306" height="195" alt="Ảnh màn hình 2025-09-24 lúc 23 23 38" src="https://github.com/user-attachments/assets/37cbf69e-7fee-4b28-8779-7fc93b313554" /><br>
Info (Thông tin chi tiết): Hiển thị metadata đầy đủ về mỗi item nội dung bao gồm thông tin file như kích thước, định dạng, ngày tạo.<br>
Phân tích:<br>
- Kích thước file: size
- Định dạng: format
- Ngày tạo: dateAdded
- Hàm getter cho size: getSize()<br>
- Hàm setter cho size: setSize()<br>
- Hàm getter cho format: getFormat()<br>
- Hàm setter cho format: setFormat()<br>
- Hàm getter cho dateAdded: getDateAdded()<br>
- Hàm setter cho dateAdded: setDateAdded()<br>
- Hàm chuẩn hoá định dạng cho kích thước file: getSizeFormat()

2. Content:<br>
<img width="306" height="195" alt="Ảnh màn hình 2025-09-24 lúc 23 31 37" src="https://github.com/user-attachments/assets/bd642043-96e2-4ff8-8762-7422b8f874e1" /><br>
Open (Mở file): Hỗ trợ mở và hiển thị thông tin cơ bản bao gồm tiêu đề, tác giả, nội dung file dưới định dạng phổ biến như JPEG, PNG cho tranh ảnh, và file văn bản như TXT cho truyện.<br>
Phân tích:<br>
- Mã nội dung: contentID
- Tiêu đề: title
- Tác giả: author
- Nội dung: filePath
- Mở file và hiển thị: open()

3. Reading History:<br>
<img width="268" height="195" alt="Ảnh màn hình 2025-11-20 lúc 01 17 40" src="https://github.com/user-attachments/assets/5e631981-6ab8-4c91-813a-157872b1c33e" /><br>
Lịch sử đọc (Reading History): Theo dõi và ghi lại hoạt động của người dùng với nội dung, thời lượng xem/đọc, và tiến độ đọc hiện tại đối với truyện dài. Tính năng này giúp người dùng dễ dàng tiếp tục từ vị trí đã dừng và xem lại những nội dung đã tương tác.<br>
Phân tích:<br>
- Mã nội dung: contentID
- Thời lượng xem/đọc: duration
- Cập nhật thời lượng xem/đọc: addDuration()
- Tiến độ đọc: progress
- Cập nhập tiến độ đọc: updateProgress()<br>

4. Favorite:<br>
<img width="381" height="207" alt="Ảnh màn hình 2025-11-20 lúc 01 14 10" src="https://github.com/user-attachments/assets/c1ffe638-d506-4ff7-8491-5af0f8279095" /><br>

Add to Favorite (Thêm vào yêu thích): Cho phép người dùng đánh dấu những nội dung ưa thích để truy cập nhanh chóng sau này. Trạng thái yêu thích có thể được bật/tắt một cách linh hoạt.<br>
Phân tích:<br>
- Mã nội dung: contentID<br>
- Tên truyện: title<br>
- Đường dẫn nội dung: filePath<br>
- Trạng thái yêu thích: isFavorited<br>
- Thêm nội dung vào mục yêu thích: addToFavorites()<br>
- Xoá nội dung khỏi mục yêu thích removeFromFavorites()<br>


5. ContentManager:<br>
<img width="462" height="466" alt="Ảnh màn hình 2025-11-23 lúc 02 20 50" src="https://github.com/user-attachments/assets/a4028b7f-564a-4feb-b3d3-acd83b0dd35c" /><br>

Phân tích:<br>
- allContents: danh sách tài liệu
- libraryContents: danh sách tài liệu đã thêm vào thư viện, lưu dưới dạng mã (ID) và tiến độ đọc của nó
- favorites: danh sách các tài liệu có trạng thái yêu thích
- nextId: biến sinh ID tự động tăng, bắt đầu từ 1

- ContentManager(): khởi tạo các cấu trúc dữ liệu rỗng
- getAllContents(): getter cho danh sách tài liệu 
- addContent(): thêm tài liệu mới vào danh sách tài liệu
- findContentById(): kiểm tra xem tài liệu trong thư viện còn tồn tại trong danh sách tài liệu không
- getLibraryContents(): getter cho danh sách tài liệu trong thư viện đọc
- addToLibrary(): thêm tài liệu vào library 
- removeFromLibrary(): xoá tài liệu khỏi library 
- sortByTitle(): sắp xếp theo tiêu đề
- sortByAuthor(): sắp xếp theo tác giả
- search(): tìm kiếm theo từ khoá có trong tiêu đề/tác giả
- toggleFavorite(): bật/tắt trạng thái Favorite
