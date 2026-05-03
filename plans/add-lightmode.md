# Plan: Add Light Mode Support

## Objective
Menambahkan dukungan Light Mode ke seluruh aplikasi dengan tombol toggle di Header `MainFrame`.

## Changes

### 1. File: `com.sistempakar.util.Theme.java`
- Menambahkan variabel `public static boolean isDarkMode = true;`.
- Menambahkan set konstanta warna untuk Light Mode (misal: `BG_LIGHT_SPACE`, `TEXT_DARK`, dll).
- Menambahkan metode `public static void toggleTheme()` yang mengganti state dan memicu refresh UI.
- Memperbarui konstanta warna yang sering diakses menjadi metode statis atau variabel non-final yang bisa berubah nilainya (misal: `getCurrentBG()`, `getCurrentText()`).
- Memodifikasi komponen UI (Button, TextField, Card, dll) agar menggunakan warna yang sesuai dengan state `isDarkMode`.
- Memperbarui `paintDeepSpaceBackground` agar bisa menggambar latar belakang terang.

### 2. File: `com.sistempakar.ui.MainFrame.java`
- Menambahkan tombol toggle (ikon 🌙/☀️) di area Header (pojok kanan atas).
- Menambahkan logika untuk memanggil `Theme.toggleTheme()` dan melakukan `SwingUtilities.updateComponentTreeUI(this)` agar seluruh aplikasi ter-refresh warnanya.
- Memastikan transisi warna mulus.

### 3. Komponen Lain
- Memastikan semua Form (Master, Transaksi, Laporan) menggunakan helper dari `Theme.java` sehingga otomatis berubah saat tema berganti.

## Verification
- Klik tombol toggle di pojok kanan atas.
- Pastikan seluruh aplikasi (Sidebar, Background, Card, Table) berubah menjadi skema warna terang.
- Pastikan teks tetap terbaca dengan jelas (kontras tinggi).
- Klik toggle kembali untuk memastikan fungsionalitas Dark Mode tidak rusak.