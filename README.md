# 🎓 SISTEM PAKAR REKOMENDASI JURUSAN
## Metode Forward Chaining | Java Swing + FlatLaf Dark Theme

---

## ✅ PERSYARATAN SISTEM
- Java JDK 11 atau lebih baru  
- MySQL Server 8.0+  
- RAM minimal 512MB  

---

## 🛠️ CARA INSTALL & MENJALANKAN

### 1. Siapkan Database MySQL
```sql
-- Buka MySQL Workbench atau terminal MySQL
mysql -u root -p
SOURCE schema.sql;
```

### 2. Sesuaikan Koneksi Database
Edit file: `src/main/java/com/sistempakar/db/DBConnection.java`
```java
private static final String HOST     = "localhost";  // sesuaikan
private static final String PORT     = "3306";
private static final String DATABASE = "sistempakar_jurusan";
private static final String USERNAME = "root";       // sesuaikan
private static final String PASSWORD = "";           // isi password Anda
```
Lalu compile ulang atau langsung edit DBConnection.class jika menggunakan JAR.

### 3. Jalankan Aplikasi
```
Windows : double-click run.bat  
Linux   : bash run.sh  
Manual  : java -jar SistemPakarJurusan.jar
```

---

## 👤 AKUN LOGIN BAWAAN

| Role       | Username | Password   |
|------------|----------|------------|
| 👑 Admin    | admin    | admin123   |
| 👨‍💼 Konselor | bk1      | bk1234     |
| 📋 Guru     | guru1    | guru123    |

---

## 📋 FITUR LENGKAP

### 🔐 Login & Register
- Login dengan username + password (SHA-256 + salt)
- Register akun baru (role: admin / konselor / guru)
- Role-based access control

### 📊 Dashboard
- Statistik real-time: total siswa, konsultasi, PTN, jurusan
- Konsultasi terbaru
- Aksi cepat berdasarkan role

### 👨‍🎓 Master Data (6 Form)
1. **Data Siswa** - profil lengkap, nilai rapor, hobi, prestasi, organisasi
2. **Data Jurusan** - jurusan kuliah + kategori + prospek kerja
3. **Data Universitas** - 20 PTN Indonesia + data peluang masuk
4. **Data Konselor** - profil guru BK / konselor
5. **Pertanyaan Psikotest** - 35 pertanyaan minat & bakat
6. **Aturan/Rules** - 28 aturan forward chaining (JSON conditions)

### 💬 Konsultasi (Transaksi)
**Step 1** – Pilih Siswa & Konselor  
**Step 2** – Mini Psikotest 35 pertanyaan  
**Step 3** – Analisis Forward Chaining → Rekomendasi 5 Jurusan terbaik  
**Step 4** – Catatan konselor + Tips belajar + Export Nota PDF  

### 📋 Rekap Guru
- Tabel rekap seluruh siswa + status konsultasi
- Peluang masuk PTN per siswa (warna: hijau/kuning/merah)
- Donut chart distribusi
- Filter: belum konsultasi / sudah / jurusan SMA
- Double-click siswa → Detail lengkap
- Export PDF rekap + Print

---

## 🔗 DEPENDENCIES
| Library            | Versi   | Fungsi                      |
|--------------------|---------|-----------------------------|
| FlatLaf Dark       | 3.4+    | Look & Feel modern          |
| MariaDB/MySQL JDBC | 2.7.6   | Koneksi database            |
| Gson               | 2.10.1  | Parse JSON aturan FC        |
| PDFBox / iText     | 1.8.16  | Generate nota PDF           |

**Untuk FlatLaf nyata (bukan stub):**  
Download dari: https://github.com/JFormDesigner/FlatLaf/releases  
→ taruh `flatlaf-3.x.jar` di folder `lib/`

---

## 📁 STRUKTUR PROJECT
```
sistempakar/
├── SistemPakarJurusan.jar     ← Main executable
├── schema.sql                  ← Database schema + seed data
├── run.bat / run.sh            ← Script jalankan
├── README.md
└── lib/
    ├── flatlaf.jar
    ├── mysql.jar
    ├── gson.jar
    ├── pdfbox.jar
    └── itextpdf.jar
```

---

## 📞 KONTAK
Sistem Pakar Rekomendasi Jurusan v1.0  
Menggunakan metode Forward Chaining  
