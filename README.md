# 🏋️ GymHome - Ứng dụng Hỗ trợ Tập luyện & Chăm sóc Sức khỏe
**GymHome** là ứng dụng hỗ trợ tập luyện thể dục và chăm sóc sức khỏe trên nền tảng Android được phát triển cho Đồ án tốt nghiệp ngành Công nghệ Thông tin - Trường Đại học Giao thông Vận tải.

Ứng dụng cung cấp các lộ trình tập luyện cá nhân hóa, theo dõi chỉ số sức khỏe và cung cấp kiến thức dinh dưỡng, giúp người dùng đạt được mục tiêu thể hình ngay tại nhà.

## 🚀 Tính năng chính 
### 💪 Tập luyện 
* Lộ trình 30 ngày:** Các kế hoạch tập luyện được thiết kế sẵn cho người mới bắt đầu và người tập nâng cao
* Thư viện bài tập đa dạng:** Phân loại bài tập theo các vùng cơ (Ngực, Vai, Tay, Bụng, Lưng, Chân, Toàn thân)
* Hướng dẫn chi tiết:** Mỗi bài tập đi kèm mô tả, hình ảnh và video hướng dẫn trực quan

### 🏆 Thử thách 
* Tham gia các thử thách tập luyện theo cấp độ

### 📈 Theo dõi sức khỏe
* Ghi lại và theo dõi các chỉ số cơ thể (BMI, cân nặng, chiều cao)
* Thống kê tự động lượng calo tiêu thụ, thời gian tập luyện và số lượng bài tập đã hoàn thành

### 📰 Tin tức & Kiến thức
* Cập nhật các bài báo mới nhất về sức khỏe, chế độ dinh dưỡng và mẹo tập luyện
* Lưu trữ các bài viết bổ ích để xem lại sau

### 🔔 Thông báo & Nhắc nhở
* Nhận thông báo nhắc nhở tập luyện hàng ngày theo khung giờ cá nhân
* Nhận các thông báo quan trọng từ hệ thống quản trị

### 🤖 Trợ lý AI Gemini
* Chọn ảnh từ máy hoặc chụp ảnh để AI phân tích calo
* Giải đáp các câu hỏi về dinh dưỡng, sức khỏe và tập luyện
* Lưu lịch sử trò chuyện để người dùng dễ dàng xem lại

### 💬 Hỗ trợ khách hàng
* Gửi yêu cầu hỗ trợ hoặc đóng góp ý kiến trực tiếp đến quản trị viên
* Theo dõi trạng thái phản hồi và lịch sử hỗ trợ

## 🛠️ Công nghệ sử dụng
* Ngôn ngữ lập trình: Java 
* IDE: Android Studio
* UI/UX: XML Layouts, Material Design Components.
* Backend & Cloud: 
    * Firebase Authentication
    * Cloud Firestore
    * Firebase Storage
    * Firebase Cloud Messaging (FCM)
    * Cloud Functions

## ⚙️ Hướng dẫn cài đặt
### 1. Yêu cầu hệ thống
* Android Studio (Phiên bản mới nhất được khuyến nghị)
* JDK 17+
* Thiết bị Android hoặc máy ảo Emulator chạy Android 9.0 trở lên (API 28+)

### 2. Clone dự án
```bash
git clone https://github.com/ongthanhvlog/appGymHome
cd GymHome
```
### 3. Mở dự án trong Android Studio
* Khởi động Android Studio
* Chọn File -> Open...
* Điều hướng đến thư mục GymHome và nhấn OK

### 4.Đồng bộ hóa Gradle:
* Đợi Android Studio tải các thư viện cần thiết và lập chỉ mục (index) các tệp tin
* Nếu có thông báo lỗi, hãy thử nhấn vào nút Try Again hoặc Sync Project with Gradle Files

### 5.Chạy ứng dụng:
* Kết nối thiết bị thật qua USB hoặc khởi động máy ảo Android
* Nhấn nút Run (biểu tượng tam giác xanh lá) trên thanh công cụ