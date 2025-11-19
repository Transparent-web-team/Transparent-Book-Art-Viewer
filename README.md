
# Transparent Books&Art Viewer

A Java-based application for Window users to view book & art in a beautiful way.

## Images
## Main features
**Class Diagram:<br>**
1. Content:<br>
<img width="306" height="195" alt="Ảnh màn hình 2025-09-24 lúc 23 31 37" src="https://github.com/user-attachments/assets/bd642043-96e2-4ff8-8762-7422b8f874e1" /><br>
Open (Mở file): Hỗ trợ mở và hiển thị thông tin cơ bản bao gồm tiêu đề, tác giả, nội dung file dưới định dạng phổ biến như JPEG, PNG cho tranh ảnh, và file văn bản như TXT cho truyện.<br>
Phân tích:<br>
- Mã nội dung: contentID
- Tiêu đề: title
- Tác giả: author
- Nội dung: filePath
- Mở file và hiển thị: open()


2. Info:<br>
<img width="306" height="195" alt="Ảnh màn hình 2025-09-24 lúc 23 23 38" src="https://github.com/user-attachments/assets/37cbf69e-7fee-4b28-8779-7fc93b313554" /><br>
Info (Thông tin chi tiết): Hiển thị metadata đầy đủ về mỗi item nội dung bao gồm thông tin file như kích thước, định dạng, ngày tạo, tag truyện.<br>
Phân tích:<br>
- Kích thước file: size
- Định dạng: format
- Ngày tạo: dayCreated
- Tag truyện: tags.
- Hiển thị: display_info()<br>


3. Reading History:<br>
<img width="231" height="141" alt="ReadingHistory UML Diagram" src="https://github.com/user-attachments/assets/5dba252d-9ebf-40e3-999e-9bdfd6a5c8f7" /><br>
Lịch sử đọc (Reading History): Theo dõi và ghi lại hoạt động của người dùng với nội dung, thời lượng xem/đọc, và tiến độ đọc hiện tại đối với truyện dài. Tính năng này giúp người dùng dễ dàng tiếp tục từ vị trí đã dừng và xem lại những nội dung đã tương tác.<br>
Phân tích:<br>
- Mã nội dung: contentID
- Thời lượng xem/đọc: duration
- Cập nhật thời lượng xem/đọc: addDuration()
- Tiến độ đọc: progress
- Cập nhập tiến độ đọc: updateProgress()<br>

4. Favorite:<br>
<img width="381" height="207" alt="Ảnh màn hình 2025-11-20 lúc 01 14 10" src="https://github.com/user-attachments/assets/c1ffe638-d506-4ff7-8491-5af0f8279095" /><br>

Add to Favorite (Thêm vào yêu thích): Cho phép người dùng đánh dấu những nội dung ưa thích để truy cập nhanh chóng sau này. Trạng thái yêu thích có thể được bật/tắt một cách linh hoạt.<br>
Phân tích:<br>
- Mã nội dung: contentID<br>
- Tên truyện: title<br>
- Đường dẫn nội dung: filePath<br>
- Trạng thái yêu thích: isFavorited<br>






## Technology Used
## Installation
## About
