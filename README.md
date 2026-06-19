# 🎓 SISTEM PAKAR REKOMENDASI JURUSAN
## Metode Forward Chaining | Java Swing + FlatLaf Dark Theme

---

## 📝 DESKRIPSI APLIKASI
Aplikasi **Sistem Pakar Rekomendasi Jurusan** adalah perangkat lunak berbasis desktop yang secara khusus dirancang menggunakan teknologi **Java Swing** dengan antarmuka modern **FlatLaf Dark Theme**. 

Tujuan utama dari aplikasi ini adalah sebagai alat bantu interaktif bagi siswa SMA/SMK dalam menentukan jurusan perkuliahan yang paling sesuai dengan profil mereka. Sistem secara komprehensif mempertimbangkan berbagai aspek penting seperti:
- **Minat dan Bakat** melalui pengisian kuesioner dinamis.
- **Nilai Akademik (Rapor)** siswa.
- **Prestasi dan Hobi** yang relevan dengan bidang ilmu tertentu.

Sistem ini diotaki oleh mesin inferensi (**Inference Engine**) yang menggunakan algoritma **Forward Chaining**. Metode ini bekerja dengan mengumpulkan fakta-fakta dari pengguna (melalui jawaban sesi konsultasi/psikotest) dan mencocokkannya dengan kumpulan aturan (*rules base*) kepakaran untuk menghasilkan kesimpulan akhir yang akurat berupa 5 (lima) rekomendasi jurusan kuliah terbaik.

---

## ✅ PERSYARATAN SISTEM (SYSTEM REQUIREMENTS)
Untuk memastikan aplikasi berjalan dengan lancar dan tanpa hambatan, pastikan perangkat komputer atau laptop Anda memenuhi spesifikasi berikut:
- **Java Development Kit (JDK)**: Versi 11 atau yang lebih baru (Wajib terinstal dan terkonfigurasi di *Environment Variables*).
- **Web Server & Database**: XAMPP / WAMP Server (untuk menjalankan MySQL Server & phpMyAdmin).
- **Integrated Development Environment (IDE)**: Apache NetBeans IDE versi terbaru (Sangat disarankan untuk kemudahan integrasi dan *running* project).
- **Kapasitas RAM**: Minimal 2GB (Disarankan 4GB ke atas untuk performa rendering UI yang maksimal).
- **Sistem Operasi**: Windows 10/11, macOS, atau Linux.

---

## 🛠️ CARA INSTALL & MENJALANKAN APLIKASI

Untuk menjalankan project ini, ikuti langkah-langkah di bawah ini secara berurutan:

### Langkah 1: Persiapan dan Import Database via phpMyAdmin
Aplikasi ini membutuhkan database MySQL untuk menyimpan seluruh data master dan transaksi.
1. Buka aplikasi **XAMPP Control Panel**.
2. Klik tombol **Start** pada modul **Apache** dan **MySQL** hingga indikatornya berwarna hijau.
3. Buka web browser pilihan Anda (Chrome/Firefox/Edge) dan akses URL: `http://localhost/phpmyadmin/`.
4. Pada panel sebelah kiri phpMyAdmin, klik **New** untuk membuat database baru.
5. Masukkan nama database: **`sistempakar_jurusan`** (pastikan penulisan persis, tanpa huruf kapital), lalu klik **Create / Buat**.
6. Klik pada database `sistempakar_jurusan` yang baru saja dibuat.
7. Pilih tab **Import** yang ada di deretan menu atas.
8. Klik tombol **Choose File** atau **Pilih File**.
9. Cari dan pilih file database bernama **`sistempakar_jurusan(1).sql`** yang berada di dalam folder project ini.
10. Scroll ke bagian paling bawah halaman dan klik tombol **Import** (atau **Go**).
11. Tunggu hingga muncul notifikasi sukses berwarna hijau yang menandakan semua tabel dan data awal (seperti admin, guru, aturan FC) telah berhasil diimpor.

### Langkah 2: Buka Project di Apache NetBeans IDE
1. Buka aplikasi **Apache NetBeans IDE**.
2. Pada menu bar di bagian atas, pilih **File** > **Open Project...** (atau tekan `Ctrl+Shift+O`).
3. Navigasikan ke direktori penyimpanan project ini, pilih foldernya, dan klik **Open Project**.
4. Tunggu beberapa saat hingga NetBeans selesai memuat struktur folder project, melakukan pemindaian (*scanning*), dan me-*resolve* semua *dependencies* yang ada.
5. **Penting:** Pastikan semua library pendukung di dalam folder `lib` (seperti konektor MySQL, FlatLaf, PDFBox) sudah terdeteksi di bagian **Libraries** pada *Projects tree* di sebelah kiri.

### Langkah 3: Konfigurasi Koneksi Database (Jika Diperlukan)
Jika konfigurasi XAMPP MySQL Anda menggunakan password (tidak default), Anda harus menyesuaikan kredensial di dalam *source code*.
1. Pada jendela *Projects* di NetBeans, buka hirarki: `Source Packages` > `com.sistempakar.db` > klik ganda pada file `DBConnection.java`.
2. Perhatikan baris kode konfigurasi berikut:
```java
private static final String HOST     = "localhost";  
private static final String PORT     = "3306";       
private static final String DATABASE = "sistempakar_jurusan"; 
private static final String USERNAME = "root";       // Username XAMPP Anda
private static final String PASSWORD = "";           // Isi jika ada password di MySQL XAMPP Anda
```
3. Sesuaikan `USERNAME` dan `PASSWORD` dengan konfigurasi MySQL lokal Anda, lalu simpan file (`Ctrl+S`). Jika Anda menggunakan XAMPP bawaan yang belum diubah, Anda bisa membiarkan konfigurasi ini seperti aslinya.

### Langkah 4: Menjalankan Aplikasi (*Run Project*)
1. Setelah semua konfigurasi selesai, klik tombol **Run Project** (ikon segitiga hijau ▶️) yang ada di toolbar NetBeans.
2. Anda juga dapat menjalankannya dengan menekan tombol **F6** pada keyboard, atau dengan klik kanan pada nama project di sebelah kiri lalu pilih **Run**.
3. Aplikasi akan melalui proses kompilasi (*Build*) sesaat, dan jendela **Login** aplikasi akan langsung muncul di layar Anda.

---

## 👤 AKUN LOGIN BAWAAN (DEFAULT CREDENTIALS)

Sistem sudah dilengkapi dengan beberapa data pengguna (dummy) yang langsung bisa Anda gunakan untuk mencoba fitur-fitur di dalam aplikasi:

| Peran (Role) | Username | Password   | Keterangan Akses                                  |
|--------------|----------|------------|---------------------------------------------------|
| 👑 **Admin**  | `admin`  | `admin123` | Akses penuh seluruh master data, setting & rekap  |
| 👨‍💼 **Konselor**| `bk1`    | `bk1234`   | Akses sesi konsultasi siswa dan input psikotest   |
| 📋 **Guru**   | `guru1`  | `guru123`  | Akses fitur laporan (Rekap Guru) dan pantau siswa |

*(Gunakan akun Admin untuk menambahkan atau mengedit data Master jika diperlukan)*

---

## 📋 FITUR LENGKAP APLIKASI

### 🔐 1. Autentikasi & Keamanan Terenkripsi
- Modul Login dan Registrasi interaktif.
- Enkripsi password menggunakan algoritma **SHA-256** dan **Salt**, menjamin keamanan data pengguna di database.
- Sistem otorisasi berbasis Role (**Role-Based Access Control / RBAC**) yang akan membatasi menu dan fungsi sesuai tingkat akses pengguna yang login.

### 📊 2. Dashboard Informatif & Real-time
- Menyajikan ringkasan statistik terkini: total siswa terdaftar, riwayat konsultasi selesai, jumlah universitas (PTN), dan referensi jurusan.
- Menampilkan grafik atau tabel aktivitas (sesi konsultasi terbaru).
- Menu dan aksi cepat navigasi.

### 👨‍🎓 3. Manajemen Master Data Lengkap (CRUD)
Modul untuk mengelola data inti yang digunakan sebagai landasan konsultasi:
- **Data Siswa**: Input profil lengkap, riwayat akademik (nilai rapor), minat, prestasi, hingga riwayat organisasi.
- **Data Jurusan**: Katalog jurusan perguruan tinggi beserta rumpun ilmu, dekskripsi, dan peluang prospek kerjanya.
- **Data Universitas (PTN)**: Direktori lebih dari 20 Perguruan Tinggi Negeri terkemuka beserta letak geografis dan tingkat persaingan (passing grade).
- **Data Pengguna / Konselor**: Manajemen akun staf dan guru BK.
- **Bank Pertanyaan Psikotest**: Berisi 35 item pertanyaan untuk menggali kepribadian dan tipe bakat siswa.
- **Aturan Kepakaran (Rules)**: Inti metode *Forward Chaining* (28 kondisi bersarang dalam format JSON) yang bisa dimodifikasi.

### 💬 4. Sesi Konsultasi & Inferensi (Modul Transaksi Utama)
Alur konsultasi untuk mendapatkan rekomendasi jurusan bagi siswa:
- **Langkah 1**: Pemilihan data profil Siswa dan penugasan guru Konselor.
- **Langkah 2**: Menjawab rangkaian kuesioner psikotest mini secara *real-time*.
- **Langkah 3**: Eksekusi Algoritma Forward Chaining untuk menghasilkan **5 Rekomendasi Jurusan Terbaik** berdasarkan gabungan pola jawaban dan profil akademik siswa.
- **Langkah 4**: Pencetakan (Export) lembar **Nota Hasil Konsultasi** ke dalam bentuk dokumen **PDF**. Guru juga bisa menambahkan catatan tambahan / *tips* langsung ke dalam laporan PDF.

### 📋 5. Rekapitulasi & Pelaporan (Khusus Guru / Admin)
Modul monitoring yang lengkap untuk memantau perkembangan seluruh siswa:
- Tabel rekapitulasi data seluruh murid beserta indikator status (*Sudah/Belum Konsultasi*).
- Analisis peluang kelolosan siswa ke PTN yang dituju, yang direpresentasikan dengan **Indikator Warna (Hijau: Aman, Kuning: Sedang, Merah: Sulit)**.
- Visualisasi data sebaran jurusan menggunakan **Donut Chart / Pie Chart**.
- Fitur Filter cerdas untuk menyortir siswa berdasarkan jurusan asal SMA (IPA/IPS/Bahasa) atau status pendaftaran.
- Tampilan Detail Laporan *(Double-Click pada tabel)*.
- Opsi **Cetak Massal (Print)** dan Export ke file PDF.

---

## 🔗 DAFTAR DEPENDENCIES & LIBRARIES
Project ini mengandalkan beberapa pustaka eksternal (library) untuk mendukung fitur unggulannya. Pastikan semua file di bawah ini sudah terdapat di dalam folder `lib/`:

| Nama Library            | Versi   | Kegunaan Spesifik                                  |
|-------------------------|---------|----------------------------------------------------|
| **FlatLaf Dark**        | 3.4+    | Menyediakan tema UI modern (*Dark Mode*) yang elegan|
| **MySQL JDBC Driver**   | 2.7.6+  | Menjembatani koneksi dari Java ke database MySQL   |
| **Google Gson**         | 2.10.1  | Parsing logik *rule base* Forward Chaining (JSON)  |
| **Apache PDFBox/iText** | 1.8.16  | Engine utama untuk *generate* laporan berbentuk PDF|

---

## 📁 STRUKTUR FOLDER PROJECT
Berikut adalah panduan struktur *source code* agar Anda mudah bernavigasi:
```text
SistemPakarJurusan_v1.0/
├── dist/                       ← Folder utama
│   ├── sistempakar_jurusan(1).sql ← File backup database untuk di-import (PENTING)
│   ├── README.md               ← File dokumentasi instalasi ini
│   ├── src/                    ← Direktori source code Java (.java)
│   │   └── main/java/com/sistempakar/
│   │       ├── db/             ← Kelas koneksi database
│   │       ├── ui/             ← Antarmuka pengguna (Swing GUI, Form, Panel)
│   │       ├── util/           ← Fungsi utility (PDF Generator, Enkripsi, FC Engine)
│   │       └── model/          ← Struktur model / objek data
│   └── lib/                    ← Direktori penempatan dependencies (Library JAR)
│       ├── flatlaf.jar
│       ├── mysql-connector-java.jar
│       ├── gson.jar
│       ├── pdfbox.jar
│       └── itextpdf.jar
```

---

## 📞 INFORMASI PENGEMBANGAN
**Sistem Pakar Rekomendasi Jurusan v1.0**  
Dikembangkan dengan menggunakan metode pendekatan pakar: **Forward Chaining**
