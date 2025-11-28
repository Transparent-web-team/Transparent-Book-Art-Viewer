
# Transparent Books&Art Viewer

A Java-based application for Window users to view book & art in a beautiful way.

## Images
## Main features
**Class Diagram:<br>**

1. Info:<br>
<img width="395" height="389" alt="image" src="https://github.com/user-attachments/assets/0359c9e5-fa8e-49c5-9e9e-9b0a4a7d8cc2" /><br>
Info (Thông tin chi tiết): Hiển thị metadata đầy đủ về mỗi item nội dung bao gồm thông tin file như kích thước, định dạng, ngày tạo.<br>
Phân tích:<br>
- Kích thước file: size
- Định dạng: format
- Ngày tạo: dateAdded

- Constructor của class Info: Info()
- Hàm getter cho size: getSize()
- Hàm setter cho size: setSize()
- Hàm getter cho format: getFormat()
- Hàm setter cho format: setFormat()
- Hàm getter cho dateAdded: getDateAdded()
- Hàm setter cho dateAdded: setDateAdded()
- Hàm chuẩn hoá định dạng cho kích thước file: getSizeFormat()

2. Content:<br>
<img width="306" height="195" alt="Ảnh màn hình 2025-09-24 lúc 23 31 37" src="https://github.com/user-attachments/assets/bd642043-96e2-4ff8-8762-7422b8f874e1" /><br>
Open (Mở file): Hỗ trợ mở và hiển thị thông tin cơ bản bao gồm tiêu đề, tác giả, nội dung file dưới định dạng phổ biến như JPEG, PNG cho tranh ảnh, và file văn bản như TXT cho truyện.<br>
Phân tích:<br>
- Mã nội dung: contentID
- Tiêu đề: title
- Tác giả: author
- Nội dung: filePath
- Thông tin chi tiết về file: info<br>

- Constructor của class Content: Content()
- Hàm getter cho contentId: getContentId()
- Hàm setter cho contentId: setContentId()
- Hàm getter cho author: getAuthor()
- Hàm setter cho author: setAuthor()
- Hàm getter cho title: getTitle()
- Hàm setter cho title: setTitle()
- Hàm getter cho filePath: getFilePath()
- Hàm setter cho filePath: setFilePath()
- Hàm getter cho info: getInfo()
- Hàm setter cho info: setInfo()

3. Reading History:<br>
<img width="268" height="195" alt="Ảnh màn hình 2025-11-20 lúc 01 17 40" src="https://github.com/user-attachments/assets/5e631981-6ab8-4c91-813a-157872b1c33e" /><br>
Lịch sử đọc (Reading History): Theo dõi và ghi lại hoạt động của người dùng với nội dung, thời lượng xem/đọc, và tiến độ đọc hiện tại đối với truyện dài. Tính năng này giúp người dùng dễ dàng tiếp tục từ vị trí đã dừng và xem lại những nội dung đã tương tác.<br>
Phân tích:<br>
- Mã nội dung: contentID
- Tiến độ đọc: progress

- Hàm constructor cho class ReadingHistory: ReadingHistory()
- Hàm getter cho contentId: getContentId()
- Hàm setter cho contentId: setContentId()
- Hàm getter cho progress: getProgress()
- Hàm setter cho progress: setProgress()

4. ContentManager:<br>
<img width="462" height="466" alt="Ảnh màn hình 2025-11-23 lúc 02 20 50" src="https://github.com/user-attachments/assets/a4028b7f-564a-4feb-b3d3-acd83b0dd35c" /><br>

Phân tích:<br>
- Danh sách toàn bộ nội dung: allContents
- Danh sách nội dung có trạng thái yêu thích: favorites
- Tiến độ đọc: readingProgress
- Biến sinh ID tự tăng: nextId

- Hằng số file chứa dữ liệu nội dung: FILE_CONTENTS
- Hằng số file chứa dữ liệu yêu thích: FILE_FAVORITES
- Hằng số file chứa dữ liệu tiến độ: FILE_PROGRESS
- Hằng số file chứa dữ liệu ID: FILE_ID

- Khởi tạo danh sách và load dữ liệu: ContentManager()
- Thêm nội dung mới: addContent()
- Tìm nội dung theo ID: findContentById()
- Xóa nội dung: removeContent()
- Lấy danh sách toàn bộ nội dung: getAllContents()
- Lấy danh sách yêu thích: getFavorites()
- Kiểm tra nội dung có trạng thái yêu thích không: isFavorite()
- Bật/tắt trạng thái yêu thích: toggleFavorite()
- Cập nhật tiến độ đọc: updateProgress()
- Tìm kiếm theo tiêu đề hoặc tác giả: search()
- Sắp xếp theo tiêu đề: sortByTitle()
- Sắp xếp theo tác giả: sortByAuthor()
- Load dữ liệu từ file: loadData()
- Lưu dữ liệu ra file: saveData()
