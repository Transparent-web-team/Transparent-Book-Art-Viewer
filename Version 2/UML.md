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
