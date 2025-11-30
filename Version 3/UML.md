
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

- Constructor của class Info: Info(...)
- Hàm getter cho size: getSize()
- Hàm setter cho size: setSize()
- Hàm getter cho format: getFormat()
- Hàm setter cho format: setFormat()
- Hàm getter cho dateAdded: getDateAdded()
- Hàm setter cho dateAdded: setDateAdded()
- Hàm chuẩn hoá định dạng cho kích thước file: getSizeFormat()

2. Content:<br>
<img width="394" height="510" alt="Screenshot 2025-11-28 230821" src="https://github.com/user-attachments/assets/0402579c-2a83-483a-a579-31e1eaae07d9" /><br>
Open (Mở file): Hỗ trợ mở các file PDF. Khi mở một nội dung, ứng dụng hiển thị toàn bộ nội dung PDF với khả năng đọc trực tiếp.<br>
Phân tích:<br>
- Mã nội dung: contentID
- Tiêu đề: title
- Tác giả: author
- Nội dung: filePath
- Thông tin chi tiết về file: info<br>

- Constructor của class Content: Content(...)
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
<img width="393" height="268" alt="Screenshot 2025-11-28 230623" src="https://github.com/user-attachments/assets/39239fec-ad0f-456d-8898-80248b4e0128" /><br>
Lịch sử đọc (Reading History): Theo dõi và ghi lại hoạt động của người dùng với nội dung, thời lượng xem/đọc, và tiến độ đọc hiện tại đối với truyện dài. Tính năng này giúp người dùng dễ dàng tiếp tục từ vị trí đã dừng và xem lại những nội dung đã tương tác.<br>
Phân tích:<br>
- Mã nội dung: contentID
- Tiến độ đọc: progress

- Hàm constructor cho class ReadingHistory: ReadingHistory(...)
- Hàm getter cho contentId: getContentId()
- Hàm setter cho contentId: setContentId()
- Hàm getter cho progress: getProgress()
- Hàm setter cho progress: setProgress()

4. ContentManager:<br>
<img width="530" height="705" alt="Screenshot 2025-11-30 220517" src="https://github.com/user-attachments/assets/c8957b5e-666c-407b-8311-8639a3bd4854" /><br>

Phân tích:<br>
- Danh sách toàn bộ nội dung: allContents
- Danh sách nội dung có trạng thái yêu thích: favorites
- Tiến độ đọc: readingProgress
- Biến sinh ID tự tăng: nextId

- Hằng số file chứa dữ liệu nội dung: FILE_CONTENTS
- Hằng số file chứa dữ liệu yêu thích: FILE_FAVORITES
- Hằng số file chứa dữ liệu tiến độ: FILE_PROGRESS
- Hằng số file chứa dữ liệu ID: FILE_ID

- Khởi tạo danh sách và load dữ liệu: ContentManager(...)
- Thêm nội dung mới: addContent()
- Tìm nội dung theo ID: findContentById()
- Xóa nội dung: removeContent()
- Lấy danh sách toàn bộ nội dung: getAllContents()
- Kiểm tra nội dung có trạng thái yêu thích không: isFavorite()
- Bật/tắt trạng thái yêu thích: toggleFavorite()
- Cập nhật tiến độ đọc: updateProgress()
- Tìm kiếm theo tiêu đề hoặc tác giả: search()
- Sắp xếp theo tiêu đề: sortByTitle()
- Sắp xếp theo tác giả: sortByAuthor()
- Load dữ liệu từ file: loadData()
- Lưu dữ liệu ra file: saveData()
