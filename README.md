# PlantCare — Ứng dụng Quản lý Cây Cảnh Thông Minh 🌱

> Đồ án Môn học: Lập trình trên thiết bị di động — Đại học Nông Lâm TP.HCM (NLU)  
> Nền tảng: Android Native (Java)  
> Cấu hình: minSdk 24 (Android 7.0), targetSdk 34 (Android 14), compileSdk 34, Java 17, Gradle Kotlin DSL (`.kts`)

---

## 1. Giới thiệu
**PlantCare** là ứng dụng quản lý cây cảnh thông minh tại nhà, kết hợp giữa lưu trữ offline-first bằng **Room Database**, đồng bộ đám mây bằng **Firebase**, tích hợp **Google Gemini AI** để nhận diện giống cây và chẩn đoán sức khỏe lá/thân, cùng hệ thống thông báo định kỳ tự động bằng **WorkManager & NotificationCompat**.

Thiết kế giao diện tuân thủ chặt chẽ tài liệu phong cách thực vật **DESIGN.md** (Material 3 botanical palette, gam màu chủ đạo Xanh lá cây tự nhiên `#2E7D4F` / `#0B6438`, góc bo cong tự nhiên 16dp / 24dp, typography tối giản, thoáng đãng).

---

## 2. 7 Chức năng chính

| STT | Chức năng | Công nghệ | Mô tả chi tiết |
|:---:|:---|:---|:---|
| **1** | **Đăng nhập / Đăng ký / Quên mật khẩu / Hồ sơ** | Firebase Auth + Firestore + Room | Đăng ký & đăng nhập bằng Email/Password, khôi phục mật khẩu qua Firebase Email Reset, quản lý hồ sơ, hỗ trợ chế độ Offline nếu mất kết nối mạng. |
| **2** | **Quản lý danh sách cây** | Room + RecyclerView + Glide | Thêm, sửa, xóa, tìm kiếm theo tên hoặc giống cây; lọc theo trạng thái: Cần tưới, Cần bón phân, Tất cả. |
| **3** | **Nhắc tưới nước** | WorkManager + NotificationCompat | Đặt chu kỳ tưới (ngày/lần), tự động tính ngày tưới tiếp theo, gửi thông báo kèm nút "Đã tưới xong" ngay trên Notification. |
| **4** | **Nhắc bón phân** | WorkManager + NotificationCompat | Đặt chu kỳ bón phân và loại phân bón (NPK, phân trùn quế,...), cảnh báo khi đến hạn hoặc quá hạn. |
| **5** | **Nhật ký phát triển cây** | Room + Glide + Timeline | Ghi chép chiều cao cây (cm), ảnh chụp theo từng giai đoạn và ghi chú tình trạng phát triển của cây. |
| **6** | **AI Scan (Gemini API)** | CameraX / Photo Picker + Retrofit + Gemini | Chụp ảnh hoặc tải ảnh từ thư viện để: (1) Nhận diện loại cây (ánh sáng, tưới nước, nhiệt độ); (2) Khám bệnh lá/thân cây và đưa ra phác đồ chăm sóc; tạo cây mới hoặc lưu vào nhật ký từ kết quả AI. |
| **7** | **Thống kê chăm sóc** | Room + MPAndroidChart | Biểu đồ cột thể hiện hoạt động chăm sóc 7 ngày gần nhất; biểu đồ tròn tỷ lệ chăm sóc (Tưới / Bón phân / Nhật ký); tôn vinh cây được chăm sóc nhiều nhất. |

---

## 3. Cấu trúc thư mục mã nguồn

```
PlantCare/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── DESIGN.md
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/vn/edu/nlu/plantcare/
│       │   ├── PlantCareApplication.java
│       │   ├── data/
│       │   │   ├── local/
│       │   │   │   ├── AppDatabase.java
│       │   │   │   ├── dao/ (PlantDao, UserDao, WateringLogDao, FertilizerLogDao, JournalEntryDao, AIResultDao)
│       │   │   │   └── entity/ (Plant, User, WateringLog, FertilizerLog, JournalEntry, AIResult)
│       │   │   ├── remote/gemini/ (GeminiApiClient, GeminiApiService, GeminiRequest, GeminiResponse)
│       │   │   └── repository/ (PlantRepository, CareRepository, JournalRepository, AIRepository, StatisticsRepository, AuthRepository)
│       │   ├── notification/ (CareReminderWorker, ReminderManager, CareActionReceiver)
│       │   └── ui/
│       │       ├── MainActivity.java
│       │       ├── auth/ (SplashActivity, LoginActivity, RegisterActivity, AuthViewModel)
│       │       ├── home/ (HomeFragment, TodayCareAdapter)
│       │       ├── plant/ (PlantsFragment, PlantDetailFragment, AddEditPlantFragment, PlantAdapter, CareHistoryAdapter, PlantViewModel, CareViewModel)
│       │       ├── journal/ (JournalAdapter, JournalViewModel)
│       │       ├── ai/ (AIScanFragment, AIViewModel)
│       │       ├── statistics/ (StatisticsFragment, StatisticsViewModel)
│       │       └── profile/ (ProfileFragment)
│       └── res/
│           ├── layout/ (activity_*.xml, fragment_*.xml, item_*.xml, dialog_*.xml)
│           ├── drawable/ (bg_card_surface.xml, vector icons: ic_leaf, ic_water_drop, ic_fertilizer,...)
│           ├── values/ (colors.xml, dimens.xml, strings.xml, themes.xml)
│           └── menu/ (bottom_nav_menu.xml)
```

---

## 4. Hướng dẫn mở và chạy trên Android Studio

1. **Mở dự án:**
   - Mở **Android Studio** -> chọn **File > Open...** -> chọn thư mục `PlantCare` (hoặc `Plant`).
   - Android Studio sẽ tự động nhận diện file `settings.gradle.kts` và tiến hành tải dependencies (Room, Firebase, MPAndroidChart, Retrofit, CameraX, Glide).

2. **Cấu hình Firebase (Tùy chọn):**
   - Dự án đã bao gồm sẵn cấu hình mặc định trong `app/google-services.json`.
   - Nếu muốn kết nối vào tài khoản Firebase riêng của bạn:
     1. Truy cập [Firebase Console](https://console.firebase.google.com/).
     2. Tạo dự án với Package Name: `vn.edu.nlu.plantcare`.
     3. Tải file `google-services.json` và chép đè vào thư mục `app/google-services.json`.
     4. Bật **Authentication (Email/Password)** và **Cloud Firestore Database**.

3. **Cấu hình Google Gemini API:**
   - Trong ứng dụng, tại màn hình **Hồ sơ (Profile)** có sẵn ô nhập **Gemini API Key**.
   - Bạn chỉ cần lấy API key miễn phí tại [Google AI Studio](https://aistudio.google.com/) và dán vào, nhấn **Lưu API Key**.
   - *(Ứng dụng cũng có sẵn cơ chế Offline Fallback thông minh giúp việc demo không bao giờ bị gián đoạn ngay cả khi không có mạng hoặc hết quota).*

4. **Kịch bản Demo bài tập lớn:**
   1. Đăng ký tài khoản mới hoặc đăng nhập.
   2. Xem Dashboard: ứng dụng đã tự động tạo sẵn 3 cây mẫu trực quan (Cây Lưỡi Hổ, Cây Kim Tiền, Cây Trầu Bà Lỗ).
   3. Nhấn **Xong** hoặc **Tưới ngay** để xác nhận chăm sóc cây.
   4. Vào chi tiết cây -> Nhấn **Thêm nhật ký phát triển** kèm chiều cao cây.
   5. Chuyển sang tab **AI Scan** -> Thử nghiệm **Nhận diện loại cây** và **Kiểm tra bệnh cây**.
   6. Xem tab **Thống kê** để quan sát biểu đồ cột 7 ngày và biểu đồ tròn tỷ lệ chăm sóc.
   7. Vào tab **Hồ sơ** -> Bấm **Kiểm tra thông báo nhắc việc ngay (Demo)** để thấy Notification xuất hiện trên thanh thông báo hệ thống Android.
