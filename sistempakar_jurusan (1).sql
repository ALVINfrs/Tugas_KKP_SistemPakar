-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 18, 2026 at 07:05 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sistempakar_jurusan`
--

-- --------------------------------------------------------

--
-- Table structure for table `aturan`
--

CREATE TABLE `aturan` (
  `id` int(11) NOT NULL,
  `kode_aturan` varchar(20) NOT NULL,
  `nama_aturan` varchar(200) DEFAULT NULL,
  `kondisi_json` text NOT NULL COMMENT 'JSON kondisi forward chaining',
  `jurusan_id` int(11) NOT NULL,
  `prioritas` int(11) DEFAULT 5,
  `confidence` decimal(5,2) DEFAULT 90.00,
  `aktif` tinyint(1) DEFAULT 1,
  `keterangan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `aturan`
--

INSERT INTO `aturan` (`id`, `kode_aturan`, `nama_aturan`, `kondisi_json`, `jurusan_id`, `prioritas`, `confidence`, `aktif`, `keterangan`) VALUES
(1, 'R001', 'Teknik Informatika - Teknologi Tinggi', '{\"skor_teknologi\":{\"min\":14},\"skor_sains\":{\"min\":8},\"nilai_matematika\":{\"min\":75}}', 1, 9, 92.00, 1, NULL),
(2, 'R002', 'Sistem Informasi - Teknologi + Bisnis', '{\"skor_teknologi\":{\"min\":10},\"skor_bisnis\":{\"min\":8},\"nilai_matematika\":{\"min\":70}}', 2, 8, 88.00, 1, NULL),
(3, 'R003', 'Teknik Komputer - Hardware Focus', '{\"skor_teknologi\":{\"min\":12},\"skor_sains\":{\"min\":10},\"nilai_ipa\":{\"min\":75}}', 3, 8, 86.00, 1, NULL),
(4, 'R004', 'Data Science - Analitik Kuat', '{\"skor_teknologi\":{\"min\":10},\"skor_sains\":{\"min\":12},\"nilai_matematika\":{\"min\":80}}', 4, 9, 90.00, 1, NULL),
(5, 'R005', 'Teknik Mesin - Sains + Eksak', '{\"skor_sains\":{\"min\":14},\"nilai_ipa\":{\"min\":78},\"nilai_matematika\":{\"min\":75}}', 5, 8, 87.00, 1, NULL),
(6, 'R006', 'Teknik Elektro - Fisika + Tekno', '{\"skor_sains\":{\"min\":12},\"skor_teknologi\":{\"min\":8},\"nilai_ipa\":{\"min\":80}}', 6, 8, 88.00, 1, NULL),
(7, 'R007', 'Teknik Sipil - Sains + Desain', '{\"skor_sains\":{\"min\":12},\"skor_seni\":{\"min\":6},\"nilai_matematika\":{\"min\":72}}', 7, 7, 85.00, 1, NULL),
(8, 'R008', 'Fisika - Sains Murni', '{\"skor_sains\":{\"min\":16},\"nilai_ipa\":{\"min\":82},\"nilai_matematika\":{\"min\":80}}', 9, 8, 88.00, 1, NULL),
(9, 'R009', 'Kimia - Sains Lab', '{\"skor_sains\":{\"min\":14},\"nilai_ipa\":{\"min\":80}}', 10, 7, 84.00, 1, NULL),
(10, 'R010', 'Biologi - Sains Hayati', '{\"skor_sains\":{\"min\":12},\"skor_kesehatan\":{\"min\":6},\"nilai_ipa\":{\"min\":75}}', 11, 7, 83.00, 1, NULL),
(11, 'R011', 'Matematika - Logika Tinggi', '{\"skor_sains\":{\"min\":16},\"nilai_matematika\":{\"min\":85}}', 12, 8, 88.00, 1, NULL),
(12, 'R012', 'Psikologi - Sosial + Empati', '{\"skor_sosial\":{\"min\":14},\"nilai_ips\":{\"min\":72}}', 13, 8, 87.00, 1, NULL),
(13, 'R013', 'Hukum - Sosial + Bahasa', '{\"skor_sosial\":{\"min\":12},\"skor_bahasa\":{\"min\":8},\"nilai_ips\":{\"min\":75}}', 14, 8, 86.00, 1, NULL),
(14, 'R014', 'Komunikasi - Sosial + Kreativitas', '{\"skor_sosial\":{\"min\":10},\"skor_seni\":{\"min\":8},\"skor_bahasa\":{\"min\":6}}', 15, 7, 84.00, 1, NULL),
(15, 'R015', 'HI - Sosial + Bahasa Tinggi', '{\"skor_sosial\":{\"min\":12},\"skor_bahasa\":{\"min\":12},\"nilai_bahasa_ing\":{\"min\":78}}', 16, 8, 86.00, 1, NULL),
(16, 'R016', 'DKV - Seni + Kreativitas Tinggi', '{\"skor_seni\":{\"min\":16},\"nilai_seni\":{\"min\":78}}', 17, 9, 90.00, 1, NULL),
(17, 'R017', 'Desain Interior - Seni + Teknis', '{\"skor_seni\":{\"min\":14},\"skor_sains\":{\"min\":6}}', 18, 8, 86.00, 1, NULL),
(18, 'R018', 'Arsitektur - Seni + Sains', '{\"skor_seni\":{\"min\":12},\"skor_sains\":{\"min\":10},\"nilai_matematika\":{\"min\":72}}', 20, 8, 87.00, 1, NULL),
(19, 'R019', 'Manajemen - Bisnis Tinggi', '{\"skor_bisnis\":{\"min\":14},\"nilai_ips\":{\"min\":72}}', 21, 8, 86.00, 1, NULL),
(20, 'R020', 'Akuntansi - Bisnis + Matematika', '{\"skor_bisnis\":{\"min\":12},\"nilai_matematika\":{\"min\":75},\"nilai_ips\":{\"min\":72}}', 22, 8, 87.00, 1, NULL),
(21, 'R021', 'Ekonomi - Bisnis + Analitik', '{\"skor_bisnis\":{\"min\":12},\"skor_sains\":{\"min\":8},\"nilai_matematika\":{\"min\":75}}', 23, 7, 85.00, 1, NULL),
(22, 'R022', 'Sastra Inggris - Bahasa Tinggi', '{\"skor_bahasa\":{\"min\":16},\"nilai_bahasa_ing\":{\"min\":82}}', 26, 9, 90.00, 1, NULL),
(23, 'R023', 'Sastra Indonesia - Bahasa + Tulis', '{\"skor_bahasa\":{\"min\":14},\"nilai_bahasa_ind\":{\"min\":80}}', 25, 8, 86.00, 1, NULL),
(24, 'R024', 'Kedokteran - Sains + Kesehatan Tinggi', '{\"skor_kesehatan\":{\"min\":14},\"skor_sains\":{\"min\":14},\"nilai_ipa\":{\"min\":88},\"nilai_matematika\":{\"min\":82}}', 28, 10, 95.00, 1, NULL),
(25, 'R025', 'Farmasi - Kimia + Kesehatan', '{\"skor_kesehatan\":{\"min\":10},\"skor_sains\":{\"min\":12},\"nilai_ipa\":{\"min\":78}}', 30, 8, 87.00, 1, NULL),
(26, 'R026', 'Gizi - Kesehatan + Biologi', '{\"skor_kesehatan\":{\"min\":12},\"skor_sains\":{\"min\":8},\"nilai_ipa\":{\"min\":72}}', 31, 7, 84.00, 1, NULL),
(27, 'R027', 'Keperawatan - Kesehatan + Empati', '{\"skor_kesehatan\":{\"min\":12},\"skor_sosial\":{\"min\":8}}', 32, 8, 85.00, 1, NULL),
(28, 'R028', 'Kedokteran Gigi - Sains + Teliti', '{\"skor_kesehatan\":{\"min\":12},\"skor_sains\":{\"min\":10},\"nilai_ipa\":{\"min\":82}}', 29, 9, 90.00, 1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `detail_jawaban`
--

CREATE TABLE `detail_jawaban` (
  `id` int(11) NOT NULL,
  `konsultasi_id` int(11) NOT NULL,
  `pertanyaan_id` int(11) NOT NULL,
  `jawaban_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `jurusan_kuliah`
--

CREATE TABLE `jurusan_kuliah` (
  `id` int(11) NOT NULL,
  `kode` varchar(20) NOT NULL,
  `nama` varchar(200) NOT NULL,
  `kategori_id` int(11) DEFAULT NULL,
  `deskripsi` text DEFAULT NULL,
  `prospek_kerja` text DEFAULT NULL,
  `mata_kuliah_utama` text DEFAULT NULL,
  `syarat_nilai` text DEFAULT NULL COMMENT 'JSON misal: {"mtk":75,"ipa":70}',
  `rumpun` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `jurusan_kuliah`
--

INSERT INTO `jurusan_kuliah` (`id`, `kode`, `nama`, `kategori_id`, `deskripsi`, `prospek_kerja`, `mata_kuliah_utama`, `syarat_nilai`, `rumpun`, `created_at`) VALUES
(1, 'TI', 'Teknik Informatika', 1, 'Mempelajari algoritma, pemrograman, dan pengembangan sistem software', 'Software Engineer, Data Scientist, AI Engineer, Cybersecurity Analyst', NULL, NULL, 'Teknologi Informasi', '2026-04-21 04:26:37'),
(2, 'SI', 'Sistem Informasi', 1, 'Mengintegrasikan teknologi informasi dengan proses bisnis organisasi', 'Business Analyst, IT Consultant, System Analyst, IT Project Manager', NULL, NULL, 'Teknologi Informasi', '2026-04-21 04:26:37'),
(3, 'TK', 'Teknik Komputer', 1, 'Merancang hardware komputer dan sistem embedded', 'Hardware Engineer, IoT Developer, Network Engineer', NULL, NULL, 'Teknologi Informasi', '2026-04-21 04:26:37'),
(4, 'DS', 'Data Science', 1, 'Menganalisis dan menginterpretasikan data besar untuk pengambilan keputusan', 'Data Analyst, Machine Learning Engineer, BI Developer', NULL, NULL, 'Teknologi Informasi', '2026-04-21 04:26:37'),
(5, 'TM', 'Teknik Mesin', 2, 'Merancang dan menganalisis sistem mekanik dan termal', 'Mechanical Engineer, Automotive Engineer, Manufacturing Engineer', NULL, NULL, 'Teknik', '2026-04-21 04:26:37'),
(6, 'TL', 'Teknik Listrik/Elektro', 2, 'Mempelajari sistem kelistrikan, elektronika, dan instrumentasi', 'Electrical Engineer, Control Systems Engineer, Power Engineer', NULL, NULL, 'Teknik', '2026-04-21 04:26:37'),
(7, 'TS', 'Teknik Sipil', 2, 'Merancang dan membangun infrastruktur fisik', 'Civil Engineer, Structural Engineer, Project Manager Konstruksi', NULL, NULL, 'Teknik', '2026-04-21 04:26:37'),
(8, 'TK2', 'Teknik Kimia', 2, 'Mengubah bahan mentah menjadi produk bernilai melalui proses kimia', 'Chemical Engineer, Process Engineer, Quality Control', NULL, NULL, 'Teknik', '2026-04-21 04:26:37'),
(9, 'FK', 'Fisika', 2, 'Mempelajari fenomena alam dari skala subatomik hingga kosmik', 'Fisikawan, Peneliti, Radiolog, Konsultan Energi', NULL, NULL, 'MIPA', '2026-04-21 04:26:37'),
(10, 'KI', 'Kimia', 2, 'Mempelajari komposisi, sifat, dan transformasi materi', 'Kimiawan, Peneliti, Analis Laboratorium, Formulator', NULL, NULL, 'MIPA', '2026-04-21 04:26:37'),
(11, 'BI', 'Biologi', 2, 'Mempelajari kehidupan makhluk hidup dan ekosistem', 'Biolog, Peneliti, Konsultan Lingkungan, Bioteknik', NULL, NULL, 'MIPA', '2026-04-21 04:26:37'),
(12, 'MA', 'Matematika', 2, 'Mempelajari logika, struktur, dan hubungan kuantitas', 'Aktuaris, Data Analyst, Peneliti, Guru Matematika', NULL, NULL, 'MIPA', '2026-04-21 04:26:37'),
(13, 'PS', 'Psikologi', 3, 'Mempelajari perilaku dan proses mental manusia', 'Psikolog, HRD Manager, Konselor, Researcher', NULL, NULL, 'Sosial', '2026-04-21 04:26:37'),
(14, 'HK', 'Hukum/Ilmu Hukum', 3, 'Mempelajari sistem hukum dan perundang-undangan', 'Lawyer, Notaris, Jaksa, Hakim, Legal Consultant', NULL, NULL, 'Sosial', '2026-04-21 04:26:37'),
(15, 'KM', 'Komunikasi', 3, 'Mempelajari proses komunikasi massa dan interpersonal', 'Jurnalis, PR Manager, Content Creator, Broadcasting', NULL, NULL, 'Sosial', '2026-04-21 04:26:37'),
(16, 'HI', 'Hubungan Internasional', 3, 'Mempelajari politik luar negeri dan diplomasi internasional', 'Diplomat, Analis Politik, International NGO, Researcher', NULL, NULL, 'Sosial', '2026-04-21 04:26:37'),
(17, 'DKV', 'Desain Komunikasi Visual', 4, 'Menggabungkan seni visual dengan komunikasi untuk menyampaikan pesan', 'Graphic Designer, UI/UX Designer, Art Director, Brand Designer', NULL, NULL, 'Seni', '2026-04-21 04:26:37'),
(18, 'DI', 'Desain Interior', 4, 'Merancang ruang interior yang fungsional dan estetis', 'Interior Designer, Konsultan Desain, Project Manager Desain', NULL, NULL, 'Seni', '2026-04-21 04:26:37'),
(19, 'SN', 'Seni Rupa', 4, 'Mengekspresikan ide dan emosi melalui media visual', 'Seniman, Kurator, Art Teacher, Galeri Manager', NULL, NULL, 'Seni', '2026-04-21 04:26:37'),
(20, 'AR', 'Arsitektur', 4, 'Merancang bangunan yang memadukan fungsi, estetika, dan teknik', 'Arsitek, Urban Planner, Project Manager, Interior Arsitek', NULL, NULL, 'Seni', '2026-04-21 04:26:37'),
(21, 'MJ', 'Manajemen', 5, 'Mempelajari perencanaan, pengorganisasian, dan pengendalian organisasi', 'Manajer, Entrepreneur, Konsultan Bisnis, Marketing Manager', NULL, NULL, 'Bisnis', '2026-04-21 04:26:37'),
(22, 'AK', 'Akuntansi', 5, 'Mempelajari pencatatan, pengklasifikasian, dan pelaporan transaksi keuangan', 'Akuntan, Auditor, Tax Consultant, CFO', NULL, NULL, 'Bisnis', '2026-04-21 04:26:37'),
(23, 'EK', 'Ekonomi', 5, 'Mempelajari perilaku ekonomi individu, perusahaan, dan negara', 'Ekonom, Analis Keuangan, Research Ekonomi, Policy Analyst', NULL, NULL, 'Bisnis', '2026-04-21 04:26:37'),
(24, 'PE', 'Pendidikan Ekonomi', 5, 'Mempersiapkan tenaga pendidik bidang ekonomi', 'Guru Ekonomi, Dosen, Pengembang Kurikulum', NULL, NULL, 'Bisnis', '2026-04-21 04:26:37'),
(25, 'BI2', 'Bahasa & Sastra Indonesia', 6, 'Mempelajari linguistik, sastra, dan budaya Indonesia', 'Editor, Penulis, Jurnalis, Guru Bahasa, EYD Consultant', NULL, NULL, 'Bahasa', '2026-04-21 04:26:37'),
(26, 'BE', 'Bahasa & Sastra Inggris', 6, 'Mempelajari bahasa, sastra, dan budaya Inggris secara mendalam', 'Translator, Teacher, Content Writer, Copywriter', NULL, NULL, 'Bahasa', '2026-04-21 04:26:37'),
(27, 'SA', 'Sastra Arab', 6, 'Mempelajari bahasa, sastra, dan budaya Arab', 'Translator Arab, Diplomat, Jurnalis Timur Tengah', NULL, NULL, 'Bahasa', '2026-04-21 04:26:37'),
(28, 'PK', 'Pendidikan Kedokteran', 7, 'Mempersiapkan dokter umum yang kompeten dan profesional', 'Dokter Umum, Dokter Spesialis, Peneliti Medis', NULL, NULL, 'Kesehatan', '2026-04-21 04:26:37'),
(29, 'KG', 'Kedokteran Gigi', 7, 'Mempersiapkan dokter gigi yang kompeten', 'Dokter Gigi, Orthodontist, Oral Surgeon', NULL, NULL, 'Kesehatan', '2026-04-21 04:26:37'),
(30, 'FA', 'Farmasi', 7, 'Mempelajari obat-obatan, formulasi, dan penggunaannya', 'Apoteker, Farmasis, Peneliti Obat, Medical Representative', NULL, NULL, 'Kesehatan', '2026-04-21 04:26:37'),
(31, 'GZ', 'Gizi', 7, 'Mempelajari nutrisi, pangan, dan kesehatan masyarakat', 'Ahli Gizi, Dietisien, Nutritionist, Food Technologist', NULL, NULL, 'Kesehatan', '2026-04-21 04:26:37'),
(32, 'KP', 'Keperawatan', 7, 'Mempersiapkan perawat profesional', 'Perawat, Nurse Manager, Clinical Instructor', NULL, NULL, 'Kesehatan', '2026-04-21 04:26:37');

-- --------------------------------------------------------

--
-- Table structure for table `kategori_minat`
--

CREATE TABLE `kategori_minat` (
  `id` int(11) NOT NULL,
  `kode` varchar(10) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `warna_hex` varchar(10) DEFAULT '#4ECDC4',
  `deskripsi` text DEFAULT NULL,
  `icon` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `kategori_minat`
--

INSERT INTO `kategori_minat` (`id`, `kode`, `nama`, `warna_hex`, `deskripsi`, `icon`) VALUES
(1, 'TEKNO', 'Teknologi & Komputer', '#4FC3F7', 'Minat di bidang IT, pemrograman, dan teknologi digital', '💻'),
(2, 'SAINS', 'Sains & Eksakta', '#81C784', 'Minat di bidang matematika, fisika, kimia, biologi', '🔬'),
(3, 'SOSIAL', 'Sosial & Humaniora', '#FFB74D', 'Minat di bidang sosial, psikologi, hukum, komunikasi', '🤝'),
(4, 'SENI', 'Seni & Kreatif', '#CE93D8', 'Minat di bidang desain, musik, film, sastra', '🎨'),
(5, 'BISNIS', 'Bisnis & Ekonomi', '#F48FB1', 'Minat di bidang manajemen, akuntansi, ekonomi', '📊'),
(6, 'BAHASA', 'Bahasa & Sastra', '#80DEEA', 'Minat di bidang bahasa, sastra, linguistik', '📚'),
(7, 'KESEHATAN', 'Kesehatan & Medis', '#A5D6A7', 'Minat di bidang kedokteran, farmasi, kesehatan', '🏥');

-- --------------------------------------------------------

--
-- Table structure for table `konselor`
--

CREATE TABLE `konselor` (
  `id` int(11) NOT NULL,
  `nip` varchar(25) NOT NULL,
  `nama` varchar(120) NOT NULL,
  `gelar` varchar(50) DEFAULT NULL,
  `email` varchar(120) DEFAULT NULL,
  `telepon` varchar(20) DEFAULT NULL,
  `spesialisasi` varchar(150) DEFAULT NULL,
  `pengalaman` int(11) DEFAULT 0 COMMENT 'tahun pengalaman',
  `foto_path` varchar(255) DEFAULT NULL,
  `aktif` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `konselor`
--

INSERT INTO `konselor` (`id`, `nip`, `nama`, `gelar`, `email`, `telepon`, `spesialisasi`, `pengalaman`, `foto_path`, `aktif`, `created_at`) VALUES
(1, '19800101200501001', 'Budi Santoso', 'M.Pd', 'budi.santoso@sman1.sch.id', '081234567890', 'Bimbingan Karir & Konseling Pendidikan', 15, NULL, 1, '2026-04-21 04:26:37'),
(2, '19850515201001002', 'Sari Dewi', 'S.Psi', 'sari.dewi@sman1.sch.id', '081234567891', 'Psikologi Pendidikan & Minat Bakat', 10, NULL, 1, '2026-04-21 04:26:37'),
(3, '19750320199801003', 'Ahmad Fauzi', 'M.Pd, Kons', 'ahmad.fauzi@sman1.sch.id', '081234567892', 'Konseling Karir & Pengembangan Diri', 20, NULL, 1, '2026-04-21 04:26:37');

-- --------------------------------------------------------

--
-- Table structure for table `konsultasi`
--

CREATE TABLE `konsultasi` (
  `id` int(11) NOT NULL,
  `no_konsultasi` varchar(30) NOT NULL,
  `siswa_id` int(11) NOT NULL,
  `konselor_id` int(11) NOT NULL,
  `tanggal_konsultasi` datetime DEFAULT current_timestamp(),
  `skor_teknologi` int(11) DEFAULT 0,
  `skor_sains` int(11) DEFAULT 0,
  `skor_sosial` int(11) DEFAULT 0,
  `skor_seni` int(11) DEFAULT 0,
  `skor_bisnis` int(11) DEFAULT 0,
  `skor_bahasa` int(11) DEFAULT 0,
  `skor_kesehatan` int(11) DEFAULT 0,
  `rekomendasi_utama_id` int(11) DEFAULT NULL,
  `rekomendasi_alt1_id` int(11) DEFAULT NULL,
  `rekomendasi_alt2_id` int(11) DEFAULT NULL,
  `catatan_konselor` text DEFAULT NULL,
  `tips_belajar` text DEFAULT NULL,
  `rekomendasi_kegiatan` text DEFAULT NULL,
  `status` enum('draft','selesai','follow_up') DEFAULT 'draft',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `konsultasi`
--

INSERT INTO `konsultasi` (`id`, `no_konsultasi`, `siswa_id`, `konselor_id`, `tanggal_konsultasi`, `skor_teknologi`, `skor_sains`, `skor_sosial`, `skor_seni`, `skor_bisnis`, `skor_bahasa`, `skor_kesehatan`, `rekomendasi_utama_id`, `rekomendasi_alt1_id`, `rekomendasi_alt2_id`, `catatan_konselor`, `tips_belajar`, `rekomendasi_kegiatan`, `status`, `created_at`) VALUES
(1, 'KNS-20260421-113947', 3, 1, '2026-04-21 11:39:47', 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, '', '', '', 'selesai', '2026-04-21 04:39:47'),
(2, 'KNS-20260423-161509', 1, 1, '2026-04-23 16:15:10', 32, 28, 18, 16, 24, 18, 22, 1, 4, 29, 'LUMAUAHUHAUHIAHI', '📌 Tips Menuju Teknik Informatika:\n\n• Perkuat Matematika: pelajari aljabar linier, statistik, dan logika matematika\n• Mulai belajar programming: Python (data/AI) atau Java/C++ (software)\n• Ikuti kelas online Dicoding, Coursera, atau freeCodeCamp\n• Bangun portfolio proyek di GitHub\n• Pelajari struktur data dan algoritma\n• Persiapkan UTBK: fokus TPS & TKA Saintek (Matematika, Fisika)', '🎯 Rekomendasi Kegiatan:\n• Ikuti hackathon atau programming contest\n• Bergabung di komunitas developer (GitHub, Stack Overflow)\n• Buat aplikasi sederhana sebagai proyek latihan\n• Ikuti program Google Developer Student Club di PTN tujuan', 'selesai', '2026-04-23 09:15:10'),
(3, 'KNS-20260424-195959', 7, 3, '2026-04-24 19:59:59', 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, '', '', '', 'selesai', '2026-04-24 12:59:59'),
(4, 'KNS-20260424-202703', 7, 3, '2026-04-24 20:27:03', 24, 29, 24, 20, 23, 20, 32, 28, 1, 4, 'sejuujurnya kamu sangat potensial untuk masik fakultas kedokteran', '📌 Tips Menuju Kedokteran:\n\n• Tingkatkan nilai Biologi, Kimia, dan Fisika ke angka 90+\n• Latihan soal UTBK intensif, terutama TKA Saintek\n• Ikuti bimbel khusus kedokteran (Primagama, Ganesha, dll)\n• Perkuat pemahaman anatomi dan fisiologi dasar\n• Magang/volunteer di klinik atau puskesmas\n• Target nilai UTBK minimal 700+ untuk PTN favorit', '🎯 Rekomendasi Kegiatan:\n• Volunteer PMI atau kegiatan kesehatan masyarakat\n• Ikuti olimpiade biologi atau kimia\n• Magang di klinik atau apotek terdekat\n• Gabung komunitas first aid / pertolongan pertama', 'selesai', '2026-04-24 13:27:03'),
(5, 'KNS-20260429-183616', 3, 3, '2026-04-29 18:36:16', 24, 32, 31, 19, 20, 16, 22, 1, 29, 2, 'akakjakajkjkljslkjlajljaljdlj', '📌 Tips Menuju Teknik Informatika:\n\n• Perkuat Matematika: pelajari aljabar linier, statistik, dan logika matematika\n• Mulai belajar programming: Python (data/AI) atau Java/C++ (software)\n• Ikuti kelas online Dicoding, Coursera, atau freeCodeCamp\n• Bangun portfolio proyek di GitHub\n• Pelajari struktur data dan algoritma\n• Persiapkan UTBK: fokus TPS & TKA Saintek (Matematika, Fisika)', '🎯 Rekomendasi Kegiatan:\n• Ikuti hackathon atau programming contest\n• Bergabung di komunitas developer (GitHub, Stack Overflow)\n• Buat aplikasi sederhana sebagai proyek latihan\n• Ikuti program Google Developer Student Club di PTN tujuan', 'selesai', '2026-04-29 11:36:16'),
(6, 'KNS-20260508-125623', 7, 3, '2026-05-08 12:56:23', 20, 25, 24, 18, 21, 18, 32, 28, 1, 4, 'kayaknya keren cuiidia bagus coii', '📌 Tips Menuju Kedokteran:\n\n• Tingkatkan nilai Biologi, Kimia, dan Fisika ke angka 90+\n• Latihan soal UTBK intensif, terutama TKA Saintek\n• Ikuti bimbel khusus kedokteran (Primagama, Ganesha, dll)\n• Perkuat pemahaman anatomi dan fisiologi dasar\n• Magang/volunteer di klinik atau puskesmas\n• Target nilai UTBK minimal 700+ untuk PTN favorit', '🎯 Rekomendasi Kegiatan:\n• Volunteer PMI atau kegiatan kesehatan masyarakat\n• Ikuti olimpiade biologi atau kimia\n• Magang di klinik atau apotek terdekat\n• Gabung komunitas first aid / pertolongan pertama', 'selesai', '2026-05-08 05:56:23'),
(7, 'KNS-20260605-131424', 8, 1, '2026-06-05 13:14:24', 32, 24, 20, 24, 21, 16, 17, 1, 2, 20, 'ehhfaihiuwfhuhwfiu', '📌 Tips Menuju Teknik Informatika:\n\n• Perkuat Matematika: pelajari aljabar linier, statistik, dan logika matematika\n• Mulai belajar programming: Python (data/AI) atau Java/C++ (software)\n• Ikuti kelas online Dicoding, Coursera, atau freeCodeCamp\n• Bangun portfolio proyek di GitHub\n• Pelajari struktur data dan algoritma\n• Persiapkan UTBK: fokus TPS & TKA Saintek (Matematika, Fisika)', '🎯 Rekomendasi Kegiatan:\n• Ikuti hackathon atau programming contest\n• Bergabung di komunitas developer (GitHub, Stack Overflow)\n• Buat aplikasi sederhana sebagai proyek latihan\n• Ikuti program Google Developer Student Club di PTN tujuan', 'selesai', '2026-06-05 06:14:24'),
(8, 'KNS-20260605-151950', 8, 3, '2026-06-05 15:19:50', 27, 26, 27, 20, 20, 30, 31, 1, 2, 20, '', '📌 Tips Menuju Teknik Informatika:\n\n• Perkuat Matematika: pelajari aljabar linier, statistik, dan logika matematika\n• Mulai belajar programming: Python (data/AI) atau Java/C++ (software)\n• Ikuti kelas online Dicoding, Coursera, atau freeCodeCamp\n• Bangun portfolio proyek di GitHub\n• Pelajari struktur data dan algoritma\n• Persiapkan UTBK: fokus TPS & TKA Saintek (Matematika, Fisika)', '🎯 Rekomendasi Kegiatan:\n• Ikuti hackathon atau programming contest\n• Bergabung di komunitas developer (GitHub, Stack Overflow)\n• Buat aplikasi sederhana sebagai proyek latihan\n• Ikuti program Google Developer Student Club di PTN tujuan', 'selesai', '2026-06-05 08:19:50');

-- --------------------------------------------------------

--
-- Table structure for table `log_aktivitas`
--

CREATE TABLE `log_aktivitas` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `aksi` varchar(255) NOT NULL,
  `tanggal` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `log_aktivitas`
--

INSERT INTO `log_aktivitas` (`id`, `user_id`, `aksi`, `tanggal`) VALUES
(1, 4, 'Status Aktif diubah', '2026-05-28 17:25:47'),
(2, 4, 'Cetak Kartu Akses', '2026-05-28 17:27:06'),
(3, 2, 'Status Aktif diubah', '2026-05-28 17:45:10'),
(4, 2, 'Status Aktif diubah', '2026-05-28 17:45:11'),
(5, 2, 'Status Aktif diubah', '2026-05-28 17:45:11'),
(6, 2, 'Status Aktif diubah', '2026-05-28 17:45:17');

-- --------------------------------------------------------

--
-- Table structure for table `pengguna`
--

CREATE TABLE `pengguna` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama` varchar(120) NOT NULL,
  `email` varchar(120) DEFAULT NULL,
  `role` enum('admin','konselor','guru') DEFAULT 'guru',
  `aktif` tinyint(1) DEFAULT 1,
  `last_login` datetime DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `terakhir_login` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pengguna`
--

INSERT INTO `pengguna` (`id`, `username`, `password`, `nama`, `email`, `role`, `aktif`, `last_login`, `created_at`, `terakhir_login`) VALUES
(1, 'admin', '12da04304e357c9f61aadd4397584cdb85fb20a57331862c974445b78ba2a008', 'Administrator', 'admin@sekolah.sch.id', 'admin', 1, '2026-06-18 11:27:01', '2026-04-21 04:27:00', NULL),
(2, 'bk1', '7dd4e84ebd7d95201e56ff96f1db833bfc95ea63b10a88b5b6a70c58fdb3789c', 'Budi Santoso, M.Pd', 'budi@sekolah.sch.id', 'konselor', 1, '2026-05-04 12:01:58', '2026-04-21 04:27:00', NULL),
(3, 'guru1', '309b33de8d7eb1dfe2a6e6a1485987cc3737880bacd3213b806edd707d38c24e', 'Sari Dewi, S.Psi', 'sari@sekolah.sch.id', 'guru', 1, '2026-05-04 08:12:14', '2026-04-21 04:27:00', NULL),
(4, 'alvnfrs', '38acf6cbc25c02cd77ceab53abf62f0228e811b49d8233d171dd7b971aafaba0', 'Muhammad Alvin Faris', 'alvinfaris59@gmail.com', 'admin', 0, '2026-05-04 13:07:12', '2026-05-04 06:07:04', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `pertanyaan_psikotest`
--

CREATE TABLE `pertanyaan_psikotest` (
  `id` int(11) NOT NULL,
  `kode` varchar(20) NOT NULL,
  `pertanyaan` text NOT NULL,
  `kategori_id` int(11) NOT NULL,
  `aspek` varchar(100) DEFAULT NULL COMMENT 'minat/bakat/kepribadian',
  `bobot` int(11) DEFAULT 1,
  `urutan` int(11) DEFAULT 1,
  `aktif` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pertanyaan_psikotest`
--

INSERT INTO `pertanyaan_psikotest` (`id`, `kode`, `pertanyaan`, `kategori_id`, `aspek`, `bobot`, `urutan`, `aktif`) VALUES
(1, 'P001', 'Apakah kamu suka membuat atau mengoperasikan program/aplikasi komputer?', 1, 'minat', 2, 1, 1),
(2, 'P002', 'Seberapa sering kamu bermain game atau mengeksplorasi teknologi terbaru?', 1, 'minat', 1, 2, 1),
(3, 'P003', 'Apakah kamu tertarik belajar coding atau pemrograman?', 1, 'bakat', 2, 3, 1),
(4, 'P004', 'Apakah kamu senang memecahkan masalah dengan solusi berbasis teknologi?', 1, 'bakat', 2, 4, 1),
(5, 'P005', 'Apakah kamu suka merakit atau memperbaiki perangkat elektronik?', 1, 'minat', 1, 5, 1),
(6, 'P006', 'Apakah kamu menyukai pelajaran Matematika dan merasa mudah memahaminya?', 2, 'bakat', 2, 6, 1),
(7, 'P007', 'Apakah kamu tertarik dengan eksperimen sains dan laboratorium?', 2, 'minat', 2, 7, 1),
(8, 'P008', 'Apakah kamu suka menganalisis data dan mencari pola dari fakta?', 2, 'bakat', 2, 8, 1),
(9, 'P009', 'Apakah fisika atau kimia adalah mata pelajaran favoritmu?', 2, 'minat', 1, 9, 1),
(10, 'P010', 'Apakah kamu suka mengamati fenomena alam dan mencari penjelasannya?', 2, 'minat', 1, 10, 1),
(11, 'P011', 'Apakah kamu mudah bergaul dan suka berinteraksi dengan banyak orang?', 3, 'kepribadian', 1, 11, 1),
(12, 'P012', 'Apakah kamu tertarik membantu orang lain menyelesaikan masalah mereka?', 3, 'minat', 2, 12, 1),
(13, 'P013', 'Apakah kamu suka diskusi tentang isu sosial, politik, atau hukum?', 3, 'minat', 2, 13, 1),
(14, 'P014', 'Apakah kamu nyaman berbicara di depan umum atau menjadi pemimpin?', 3, 'bakat', 1, 14, 1),
(15, 'P015', 'Apakah kamu peduli dengan keadilan dan hak-hak masyarakat?', 3, 'minat', 2, 15, 1),
(16, 'P016', 'Apakah kamu suka menggambar, melukis, atau membuat karya visual?', 4, 'minat', 2, 16, 1),
(17, 'P017', 'Apakah kamu tertarik dengan desain grafis, fashion, atau arsitektur?', 4, 'minat', 2, 17, 1),
(18, 'P018', 'Apakah kamu memiliki selera estetika yang kuat dan suka keindahan?', 4, 'bakat', 1, 18, 1),
(19, 'P019', 'Apakah kamu suka musik, film, atau pertunjukan seni?', 4, 'minat', 1, 19, 1),
(20, 'P020', 'Apakah kamu sering punya ide kreatif dan unik dalam memecahkan masalah?', 4, 'bakat', 2, 20, 1),
(21, 'P021', 'Apakah kamu tertarik dengan dunia usaha, berdagang, atau berwirausaha?', 5, 'minat', 2, 21, 1),
(22, 'P022', 'Apakah kamu suka mengatur keuangan dan mencatat pemasukan-pengeluaran?', 5, 'bakat', 2, 22, 1),
(23, 'P023', 'Apakah kamu senang membuat strategi atau rencana untuk mencapai tujuan?', 5, 'bakat', 2, 23, 1),
(24, 'P024', 'Apakah kamu suka negosiasi dan persuasi dalam kehidupan sehari-hari?', 5, 'minat', 1, 24, 1),
(25, 'P025', 'Apakah kamu tertarik dengan analisis pasar atau tren ekonomi?', 5, 'minat', 1, 25, 1),
(26, 'P026', 'Apakah kamu suka membaca buku, novel, atau karya sastra?', 6, 'minat', 2, 26, 1),
(27, 'P027', 'Apakah kamu menikmati menulis cerita, puisi, atau artikel?', 6, 'bakat', 2, 27, 1),
(28, 'P028', 'Apakah kamu mudah mempelajari bahasa asing?', 6, 'bakat', 2, 28, 1),
(29, 'P029', 'Apakah kamu suka berbicara dalam bahasa Inggris atau bahasa lainnya?', 6, 'minat', 1, 29, 1),
(30, 'P030', 'Apakah kamu tertarik dengan jurnalistik atau komunikasi massa?', 6, 'minat', 1, 30, 1),
(31, 'P031', 'Apakah kamu tertarik dengan ilmu kedokteran, kesehatan, atau biologi manusia?', 7, 'minat', 2, 31, 1),
(32, 'P032', 'Apakah kamu memiliki empati tinggi dan suka merawat orang yang sakit?', 7, 'kepribadian', 2, 32, 1),
(33, 'P033', 'Apakah kamu tidak takut dengan darah, jarum suntik, atau prosedur medis?', 7, 'kepribadian', 1, 33, 1),
(34, 'P034', 'Apakah kamu suka belajar tentang tubuh manusia, penyakit, dan obat-obatan?', 7, 'minat', 2, 34, 1),
(35, 'P035', 'Apakah kamu disiplin dan teliti dalam mengerjakan sesuatu?', 7, 'bakat', 1, 35, 1);

-- --------------------------------------------------------

--
-- Table structure for table `pilihan_jawaban`
--

CREATE TABLE `pilihan_jawaban` (
  `id` int(11) NOT NULL,
  `pertanyaan_id` int(11) NOT NULL,
  `huruf` char(1) NOT NULL,
  `teks_jawaban` text NOT NULL,
  `skor` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pilihan_jawaban`
--

INSERT INTO `pilihan_jawaban` (`id`, `pertanyaan_id`, `huruf`, `teks_jawaban`, `skor`) VALUES
(1, 1, 'A', 'Sangat suka, itu aktivitas favoritku sehari-hari', 4),
(2, 1, 'B', 'Cukup suka, sering kulakukan di waktu luang', 3),
(3, 1, 'C', 'Biasa saja, hanya kalau ada tugas', 2),
(4, 1, 'D', 'Tidak terlalu suka, lebih suka aktivitas lain', 1),
(5, 2, 'A', 'Setiap hari, selalu update teknologi terbaru', 4),
(6, 2, 'B', 'Beberapa kali seminggu', 3),
(7, 2, 'C', 'Sesekali saja', 2),
(8, 2, 'D', 'Jarang sekali', 1),
(9, 3, 'A', 'Sangat tertarik, sudah pernah belajar coding', 4),
(10, 3, 'B', 'Tertarik dan ingin mencoba', 3),
(11, 3, 'C', 'Sedikit tertarik tapi belum pernah coba', 2),
(12, 3, 'D', 'Tidak tertarik sama sekali', 1),
(13, 4, 'A', 'Selalu mencari solusi teknologi untuk setiap masalah', 4),
(14, 4, 'B', 'Sering menggunakan teknologi untuk solusi', 3),
(15, 4, 'C', 'Kadang-kadang saja', 2),
(16, 4, 'D', 'Lebih suka solusi tradisional/manual', 1),
(17, 5, 'A', 'Sangat suka, sudah punya pengalaman merakit', 4),
(18, 5, 'B', 'Suka mencoba walaupun belum mahir', 3),
(19, 5, 'C', 'Hanya suka menggunakan, tidak merakit', 2),
(20, 5, 'D', 'Tidak suka sama sekali', 1),
(21, 6, 'A', 'Sangat suka dan selalu dapat nilai bagus', 4),
(22, 6, 'B', 'Suka dan nilainya rata-rata bagus', 3),
(23, 6, 'C', 'Biasa saja, cukup untuk lulus', 2),
(24, 6, 'D', 'Tidak suka, matematika itu sulit', 1),
(25, 7, 'A', 'Sangat suka, laboratorium adalah tempat favoritku', 4),
(26, 7, 'B', 'Suka eksperimen meskipun tidak terlalu sering', 3),
(27, 7, 'C', 'Biasa saja, hanya ikuti pelajaran', 2),
(28, 7, 'D', 'Tidak suka, lebih suka teori', 1),
(29, 8, 'A', 'Sangat suka menganalisis data dan mencari pola', 4),
(30, 8, 'B', 'Suka melakukannya untuk keperluan belajar', 3),
(31, 8, 'C', 'Kadang-kadang melakukannya', 2),
(32, 8, 'D', 'Tidak terlalu suka analisis data', 1),
(33, 9, 'A', 'Fisika DAN kimia keduanya favorit', 4),
(34, 9, 'B', 'Salah satunya adalah favorit', 3),
(35, 9, 'C', 'Biasa saja untuk keduanya', 2),
(36, 9, 'D', 'Tidak suka keduanya', 1),
(37, 10, 'A', 'Selalu bertanya kenapa sebuah fenomena terjadi', 4),
(38, 10, 'B', 'Sering penasaran dan mencari tahu', 3),
(39, 10, 'C', 'Kadang penasaran tapi malas mencari tahu', 2),
(40, 10, 'D', 'Tidak pernah terlalu peduli dengan fenomena alam', 1),
(41, 11, 'A', 'Sangat mudah bergaul, punya banyak teman', 4),
(42, 11, 'B', 'Cukup mudah bergaul', 3),
(43, 11, 'C', 'Selektif dalam berteman', 2),
(44, 11, 'D', 'Lebih suka sendiri / introvert', 1),
(45, 12, 'A', 'Sangat suka, itu panggilan hidupku', 4),
(46, 12, 'B', 'Suka membantu kalau diminta', 3),
(47, 12, 'C', 'Kadang-kadang saja', 2),
(48, 12, 'D', 'Tidak terlalu suka urusan orang lain', 1),
(49, 13, 'A', 'Sangat tertarik dan sering berdiskusi', 4),
(50, 13, 'B', 'Cukup tertarik, mengikuti perkembangan berita', 3),
(51, 13, 'C', 'Kadang mengikuti kalau ada info menarik', 2),
(52, 13, 'D', 'Tidak tertarik dengan isu-isu tersebut', 1),
(53, 14, 'A', 'Sangat nyaman, suka jadi pemimpin/presenter', 4),
(54, 14, 'B', 'Cukup nyaman walau sedikit nervous', 3),
(55, 14, 'C', 'Kurang nyaman tapi bisa kalau dipaksa', 2),
(56, 14, 'D', 'Tidak nyaman, lebih suka di belakang layar', 1),
(57, 15, 'A', 'Sangat peduli dan sering terlibat isu sosial', 4),
(58, 15, 'B', 'Cukup peduli dan mengikuti perkembangan', 3),
(59, 15, 'C', 'Kadang-kadang peduli', 2),
(60, 15, 'D', 'Kurang peduli, fokus urusan sendiri', 1),
(61, 16, 'A', 'Sangat suka, menggambar adalah hobinya', 4),
(62, 16, 'B', 'Suka menggambar di waktu luang', 3),
(63, 16, 'C', 'Pernah mencoba tapi tidak rutin', 2),
(64, 16, 'D', 'Tidak suka menggambar', 1),
(65, 17, 'A', 'Sangat tertarik dan punya banyak referensi desain', 4),
(66, 17, 'B', 'Tertarik dan suka mengamati desain bagus', 3),
(67, 17, 'C', 'Biasa saja', 2),
(68, 17, 'D', 'Tidak tertarik dengan dunia desain', 1),
(69, 18, 'A', 'Sangat kuat, selalu perhatikan detail estetika', 4),
(70, 18, 'B', 'Cukup sensitif terhadap keindahan', 3),
(71, 18, 'C', 'Biasa saja', 2),
(72, 18, 'D', 'Tidak terlalu perhatikan hal estetika', 1),
(73, 19, 'A', 'Sangat suka, aktif bermusik atau di dunia seni', 4),
(74, 19, 'B', 'Suka menikmati seni walau tidak aktif berkarya', 3),
(75, 19, 'C', 'Sesekali saja', 2),
(76, 19, 'D', 'Tidak terlalu menikmati', 1),
(77, 20, 'A', 'Selalu punya ide kreatif dan tidak konvensional', 4),
(78, 20, 'B', 'Sering punya ide baru yang menarik', 3),
(79, 20, 'C', 'Kadang-kadang', 2),
(80, 20, 'D', 'Lebih suka mengikuti prosedur yang sudah ada', 1),
(81, 21, 'A', 'Sangat tertarik, sudah pernah berjualan', 4),
(82, 21, 'B', 'Tertarik dan ingin mencoba wirausaha', 3),
(83, 21, 'C', 'Agak tertarik tapi masih ragu', 2),
(84, 21, 'D', 'Tidak tertarik dengan dunia bisnis', 1),
(85, 22, 'A', 'Sangat suka, selalu catat keuangan dengan detail', 4),
(86, 22, 'B', 'Suka dan sudah punya kebiasaan mencatat', 3),
(87, 22, 'C', 'Sesekali mencatat kalau dibutuhkan', 2),
(88, 22, 'D', 'Tidak suka, keuangan itu membosankan', 1),
(89, 23, 'A', 'Selalu membuat perencanaan detail sebelum bertindak', 4),
(90, 23, 'B', 'Sering merencanakan untuk hal penting', 3),
(91, 23, 'C', 'Kadang-kadang merencanakan', 2),
(92, 23, 'D', 'Lebih suka spontan tanpa rencana', 1),
(93, 24, 'A', 'Sangat suka, jago negosiasi dan persuasi', 4),
(94, 24, 'B', 'Cukup suka dan cukup pintar negosiasi', 3),
(95, 24, 'C', 'Biasa saja', 2),
(96, 24, 'D', 'Tidak suka negosiasi', 1),
(97, 25, 'A', 'Sangat tertarik, sering baca analisis ekonomi', 4),
(98, 25, 'B', 'Cukup tertarik dengan tren pasar', 3),
(99, 25, 'C', 'Sesekali saja', 2),
(100, 25, 'D', 'Tidak tertarik sama sekali', 1),
(101, 26, 'A', 'Sangat suka, membaca adalah kebutuhan harian', 4),
(102, 26, 'B', 'Suka membaca terutama genre favorit', 3),
(103, 26, 'C', 'Baca kalau ada keperluan saja', 2),
(104, 26, 'D', 'Tidak suka membaca', 1),
(105, 27, 'A', 'Sangat suka, sudah punya banyak karya tulis', 4),
(106, 27, 'B', 'Suka menulis di waktu luang', 3),
(107, 27, 'C', 'Pernah mencoba tapi tidak rutin', 2),
(108, 27, 'D', 'Tidak suka menulis', 1),
(109, 28, 'A', 'Sangat mudah, bisa 2-3 bahasa asing', 4),
(110, 28, 'B', 'Cukup mudah, sudah lancar 1 bahasa asing', 3),
(111, 28, 'C', 'Agak kesulitan tapi masih bisa', 2),
(112, 28, 'D', 'Sulit sekali belajar bahasa asing', 1),
(113, 29, 'A', 'Sangat suka dan sudah lancar berbicara', 4),
(114, 29, 'B', 'Suka dan sedang berlatih', 3),
(115, 29, 'C', 'Mau tapi masih malu', 2),
(116, 29, 'D', 'Tidak tertarik berbicara bahasa asing', 1),
(117, 30, 'A', 'Sangat tertarik, suka nulis artikel/reportase', 4),
(118, 30, 'B', 'Tertarik dengan dunia jurnalistik', 3),
(119, 30, 'C', 'Biasa saja', 2),
(120, 30, 'D', 'Tidak tertarik', 1),
(121, 31, 'A', 'Sangat tertarik, itu passionku', 4),
(122, 31, 'B', 'Tertarik dan sering cari info kesehatan', 3),
(123, 31, 'C', 'Cukup tertarik', 2),
(124, 31, 'D', 'Tidak tertarik', 1),
(125, 32, 'A', 'Sangat empatik, senang merawat dan membantu', 4),
(126, 32, 'B', 'Cukup empatik', 3),
(127, 32, 'C', 'Biasa saja', 2),
(128, 32, 'D', 'Kurang empatik dalam hal merawat orang sakit', 1),
(129, 33, 'A', 'Sama sekali tidak takut, sudah terbiasa', 4),
(130, 33, 'B', 'Sedikit tidak nyaman tapi bisa menghadapinya', 3),
(131, 33, 'C', 'Agak takut tapi bisa dipaksakan', 2),
(132, 33, 'D', 'Sangat takut, tidak mau terlibat', 1),
(133, 34, 'A', 'Sangat suka, sering baca buku/artikel medis', 4),
(134, 34, 'B', 'Suka dan penasaran dengan ilmu medis', 3),
(135, 34, 'C', 'Cukup suka', 2),
(136, 34, 'D', 'Tidak tertarik dengan topik medis', 1),
(137, 35, 'A', 'Sangat disiplin dan sangat teliti dalam segala hal', 4),
(138, 35, 'B', 'Cukup disiplin dan teliti untuk hal penting', 3),
(139, 35, 'C', 'Kadang disiplin kadang tidak', 2),
(140, 35, 'D', 'Kurang disiplin dan sering teledor', 1);

-- --------------------------------------------------------

--
-- Table structure for table `rekomendasi_univ`
--

CREATE TABLE `rekomendasi_univ` (
  `id` int(11) NOT NULL,
  `konsultasi_id` int(11) NOT NULL,
  `universitas_jurusan_id` int(11) NOT NULL,
  `peluang_personal` decimal(5,2) DEFAULT NULL,
  `rank_urutan` int(11) DEFAULT NULL,
  `catatan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `rekomendasi_univ`
--

INSERT INTO `rekomendasi_univ` (`id`, `konsultasi_id`, `universitas_jurusan_id`, `peluang_personal`, `rank_urutan`, `catatan`) VALUES
(333, 2, 369, 95.00, 1, NULL),
(334, 2, 375, 95.00, 2, NULL),
(335, 2, 377, 95.00, 3, NULL),
(336, 2, 14, 50.10, 4, NULL),
(337, 2, 8, 55.10, 5, NULL),
(338, 2, 131, 95.00, 1, NULL),
(339, 2, 133, 95.00, 2, NULL),
(340, 2, 134, 95.00, 3, NULL),
(341, 2, 54, 60.05, 4, NULL),
(342, 2, 53, 62.55, 5, NULL),
(343, 2, 97, 95.00, 1, NULL),
(344, 2, 330, 95.00, 2, NULL),
(345, 2, 331, 95.00, 3, NULL),
(346, 2, 336, 41.80, 4, NULL),
(347, 2, 96, 86.76, 5, NULL),
(348, 4, 23, 95.00, 1, NULL),
(349, 4, 31, 95.00, 2, NULL),
(350, 4, 41, 95.00, 3, NULL),
(351, 4, 12, 93.91, 4, NULL),
(352, 4, 7, 94.66, 5, NULL),
(353, 4, 1, 95.00, 1, NULL),
(354, 4, 8, 95.00, 2, NULL),
(355, 4, 14, 95.00, 3, NULL),
(356, 4, 112, 95.00, 4, NULL),
(357, 4, 19, 95.00, 5, NULL),
(358, 4, 52, 95.00, 1, NULL),
(359, 4, 53, 95.00, 2, NULL),
(360, 4, 54, 95.00, 3, NULL),
(361, 4, 136, 95.00, 4, NULL),
(362, 4, 55, 95.00, 5, NULL),
(363, 5, 369, 94.54, 1, NULL),
(364, 5, 389, 94.50, 2, NULL),
(365, 5, 377, 93.86, 3, NULL),
(366, 5, 19, 43.00, 4, NULL),
(367, 5, 27, 53.00, 5, NULL),
(368, 5, 1280, 95.00, 1, NULL),
(369, 5, 334, 94.91, 2, NULL),
(370, 5, 330, 94.87, 3, NULL),
(371, 5, 96, 83.45, 4, NULL),
(372, 5, 1271, 84.69, 5, NULL),
(373, 5, 113, 94.58, 1, NULL),
(374, 5, 117, 94.55, 2, NULL),
(375, 5, 408, 94.04, 3, NULL),
(376, 5, 120, 40.37, 4, NULL),
(377, 5, 56, 83.45, 5, NULL),
(378, 6, 23, 95.00, 1, NULL),
(379, 6, 31, 95.00, 2, NULL),
(380, 6, 41, 95.00, 3, NULL),
(381, 6, 12, 93.91, 4, NULL),
(382, 6, 7, 94.66, 5, NULL),
(383, 6, 1, 95.00, 1, NULL),
(384, 6, 8, 95.00, 2, NULL),
(385, 6, 14, 95.00, 3, NULL),
(386, 6, 112, 95.00, 4, NULL),
(387, 6, 19, 95.00, 5, NULL),
(388, 6, 52, 95.00, 1, NULL),
(389, 6, 53, 95.00, 2, NULL),
(390, 6, 54, 95.00, 3, NULL),
(391, 6, 136, 95.00, 4, NULL),
(392, 6, 55, 95.00, 5, NULL),
(393, 7, 369, 90.72, 1, NULL),
(394, 7, 389, 90.67, 1, NULL),
(395, 7, 113, 90.76, 2, NULL),
(396, 7, 117, 90.72, 2, NULL),
(397, 7, 995, 91.32, 3, NULL),
(398, 7, 983, 91.18, 3, NULL),
(399, 8, 369, 90.72, 1, NULL),
(400, 8, 389, 90.67, 1, NULL),
(401, 8, 113, 90.76, 2, NULL),
(402, 8, 117, 90.72, 2, NULL),
(403, 8, 995, 91.32, 3, NULL),
(404, 8, 983, 91.18, 3, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `siswa`
--

CREATE TABLE `siswa` (
  `id` int(11) NOT NULL,
  `nis` varchar(20) NOT NULL,
  `nama` varchar(120) NOT NULL,
  `jenis_kelamin` enum('L','P') NOT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `tempat_lahir` varchar(100) DEFAULT NULL,
  `kelas` varchar(10) DEFAULT NULL,
  `jurusan_sma` enum('IPA','IPS','Bahasa','SMK-TI','SMK-Bisnis','SMK-Seni','Lainnya') DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `telepon` varchar(20) DEFAULT NULL,
  `email` varchar(120) DEFAULT NULL,
  `nama_ortu` varchar(120) DEFAULT NULL,
  `telepon_ortu` varchar(20) DEFAULT NULL,
  `hobi` text DEFAULT NULL COMMENT 'dipisah koma',
  `prestasi` text DEFAULT NULL,
  `organisasi` text DEFAULT NULL,
  `nilai_matematika` decimal(5,2) DEFAULT 0.00,
  `nilai_ipa` decimal(5,2) DEFAULT 0.00,
  `nilai_ips` decimal(5,2) DEFAULT 0.00,
  `nilai_bahasa_ind` decimal(5,2) DEFAULT 0.00,
  `nilai_bahasa_ing` decimal(5,2) DEFAULT 0.00,
  `nilai_seni` decimal(5,2) DEFAULT 0.00,
  `nilai_olahraga` decimal(5,2) DEFAULT 0.00,
  `nilai_komputer` decimal(5,2) DEFAULT 0.00,
  `cita_cita` varchar(200) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `siswa`
--

INSERT INTO `siswa` (`id`, `nis`, `nama`, `jenis_kelamin`, `tanggal_lahir`, `tempat_lahir`, `kelas`, `jurusan_sma`, `alamat`, `telepon`, `email`, `nama_ortu`, `telepon_ortu`, `hobi`, `prestasi`, `organisasi`, `nilai_matematika`, `nilai_ipa`, `nilai_ips`, `nilai_bahasa_ind`, `nilai_bahasa_ing`, `nilai_seni`, `nilai_olahraga`, `nilai_komputer`, `cita_cita`, `created_at`) VALUES
(1, '2024001', 'Andi Pratama', 'L', '2007-03-15', 'Jakarta', 'XII IPA 1', 'IPA', NULL, NULL, NULL, NULL, NULL, 'Coding, Gaming, Robotik', 'Juara 1 Olimpiade Komputer Kota 2023', NULL, 88.00, 82.00, 75.00, 78.00, 80.00, 70.00, 75.00, 92.00, 'Software Engineer', '2026-04-21 04:26:37'),
(2, '2024002', 'Siti Rahayu', 'P', '2007-07-22', 'Bandung', 'XII IPS 2', 'IPS', NULL, NULL, NULL, NULL, NULL, 'Menggambar, Melukis, Desain Digital', 'Juara 2 Lomba Desain Poster Provinsi', NULL, 72.00, 68.00, 85.00, 88.00, 78.00, 92.00, 80.00, 75.00, 'UI/UX Designer', '2026-04-21 04:26:37'),
(3, '2024003', 'Budi Setiawan', 'L', '2007-01-10', 'Surabaya', 'XII IPA 3', 'IPA', NULL, NULL, NULL, NULL, NULL, 'Biologi, Berkebun, Memasak', 'Juara 3 Olimpiade Biologi Regional', NULL, 78.00, 88.00, 72.00, 80.00, 72.00, 68.00, 75.00, 70.00, 'Dokter', '2026-04-21 04:26:37'),
(4, '2024004', 'Dewi Sartika', 'P', '2007-09-05', 'Yogyakarta', 'XII IPS 1', 'IPS', NULL, NULL, NULL, NULL, NULL, 'Membaca, Menulis, Debat', 'Finalis Lomba Debat Bahasa Inggris Nasional', NULL, 80.00, 72.00, 88.00, 90.00, 92.00, 75.00, 70.00, 78.00, 'Diplomat/HI', '2026-04-21 04:26:37'),
(5, '2024005', 'Rizky Firmansyah', 'L', '2007-12-18', 'Medan', 'XII IPA 2', 'IPA', NULL, NULL, NULL, NULL, NULL, 'Matematika, Fisika, Chess', 'Juara 1 Olimpiade Matematika Regional', NULL, 95.00, 90.00, 68.00, 75.00, 75.00, 65.00, 72.00, 85.00, 'Data Scientist', '2026-04-21 04:26:37'),
(6, '202889899', 'Gresella Adhalia', 'P', '2007-04-23', 'Bogor', 'XII IPA 3', 'IPA', 'jkkokosjoijsoijoqjosjoj', '9808098080', 'aokok@gamaiklljij', 'jhwkehkwe', '089898098080', 'Sanggar Tari', 'Juara kimia,Juara Kesehatan', 'Ketua Osis', 100.00, 100.00, 91.00, 93.50, 100.00, 90.50, 92.00, 94.50, 'Dokter Speseialis Jantung', '2026-04-23 09:24:34'),
(7, '20004516', 'Kayla Fasya', 'P', '2007-04-17', '', 'XII IPA 5', 'IPA', 'akhhiohoi akoiahoaihao', '087897652415', 'kayla@gmail.com', 'kahjhiuh', '09878678668121', 'hobi dan minat fashion:Fashion ', '-', 'Wakil Ketua Osis', 98.00, 99.00, 90.00, 95.00, 99.00, 87.00, 92.00, 87.00, 'Pramugari', '2026-04-24 12:56:29'),
(8, '209209090909', 'Muhammad Nafis', 'L', '2005-05-21', 'Bks', '17890', 'SMK-TI', 'hiuhwihfihfwihiwf', '0909090909', 'apiss@gmail.com', 'Danang', '0909090990', '', '', '', 75.00, 75.00, 75.00, 75.00, 75.00, 75.00, 75.00, 75.00, 'Jadi Pria Solo', '2026-06-05 06:09:19');

-- --------------------------------------------------------

--
-- Table structure for table `universitas`
--

CREATE TABLE `universitas` (
  `id` int(11) NOT NULL,
  `kode` varchar(20) NOT NULL,
  `nama` varchar(250) NOT NULL,
  `singkatan` varchar(20) DEFAULT NULL,
  `kota` varchar(100) DEFAULT NULL,
  `provinsi` varchar(100) DEFAULT NULL,
  `wilayah` enum('Jawa','Sumatra','Kalimantan','Sulawesi','Bali-Nusa','Maluku-Papua') DEFAULT NULL,
  `akreditasi` varchar(5) DEFAULT 'A',
  `website` varchar(200) DEFAULT NULL,
  `tipe` enum('PTN-BH','PTN-BLU','PTN') DEFAULT 'PTN',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `universitas`
--

INSERT INTO `universitas` (`id`, `kode`, `nama`, `singkatan`, `kota`, `provinsi`, `wilayah`, `akreditasi`, `website`, `tipe`, `created_at`) VALUES
(1, 'UI', 'Universitas Indonesia', 'UI', 'Depok', 'Jawa Barat', 'Jawa', 'Unggu', 'https://ui.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(2, 'UGM', 'Universitas Gadjah Mada', 'UGM', 'Yogyakarta', 'DI Yogyakarta', 'Jawa', 'Unggu', 'https://ugm.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(3, 'ITB', 'Institut Teknologi Bandung', 'ITB', 'Bandung', 'Jawa Barat', 'Jawa', 'Unggu', 'https://itb.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(4, 'ITS', 'Institut Teknologi Sepuluh Nopember', 'ITS', 'Surabaya', 'Jawa Timur', 'Jawa', 'A', 'https://its.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(5, 'UNAIR', 'Universitas Airlangga', 'UNAIR', 'Surabaya', 'Jawa Timur', 'Jawa', 'Unggu', 'https://unair.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(6, 'IPB', 'Institut Pertanian Bogor', 'IPB', 'Bogor', 'Jawa Barat', 'Jawa', 'A', 'https://ipb.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(7, 'UNDIP', 'Universitas Diponegoro', 'UNDIP', 'Semarang', 'Jawa Tengah', 'Jawa', 'A', 'https://undip.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(8, 'UNPAD', 'Universitas Padjadjaran', 'UNPAD', 'Bandung', 'Jawa Barat', 'Jawa', 'A', 'https://unpad.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(9, 'UB', 'Universitas Brawijaya', 'UB', 'Malang', 'Jawa Timur', 'Jawa', 'A', 'https://ub.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(10, 'UNS', 'Universitas Sebelas Maret', 'UNS', 'Surakarta', 'Jawa Tengah', 'Jawa', 'A', 'https://uns.ac.id', 'PTN-BLU', '2026-04-21 04:26:37'),
(11, 'USU', 'Universitas Sumatera Utara', 'USU', 'Medan', 'Sumatera Utara', 'Sumatra', 'A', 'https://usu.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(12, 'UNAND', 'Universitas Andalas', 'UNAND', 'Padang', 'Sumatera Barat', 'Sumatra', 'A', 'https://unand.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(13, 'UNSRI', 'Universitas Sriwijaya', 'UNSRI', 'Palembang', 'Sumatera Selatan', 'Sumatra', 'A', 'https://unsri.ac.id', 'PTN-BLU', '2026-04-21 04:26:37'),
(14, 'UNHAS', 'Universitas Hasanuddin', 'UNHAS', 'Makassar', 'Sulawesi Selatan', 'Sulawesi', 'Unggu', 'https://unhas.ac.id', 'PTN-BH', '2026-04-21 04:26:37'),
(15, 'UNMUL', 'Universitas Mulawarman', 'UNMUL', 'Samarinda', 'Kalimantan Timur', 'Kalimantan', 'A', 'https://unmul.ac.id', 'PTN-BLU', '2026-04-21 04:26:37'),
(16, 'UDAYANA', 'Universitas Udayana', 'UDAYANA', 'Denpasar', 'Bali', 'Bali-Nusa', 'A', 'https://udayana.ac.id', 'PTN-BLU', '2026-04-21 04:26:37'),
(17, 'UNJ', 'Universitas Negeri Jakarta', 'UNJ', 'Jakarta', 'DKI Jakarta', 'Jawa', 'A', 'https://unj.ac.id', 'PTN-BLU', '2026-04-21 04:26:37'),
(18, 'UPI', 'Universitas Pendidikan Indonesia', 'UPI', 'Bandung', 'Jawa Barat', 'Jawa', 'A', 'https://upi.edu', 'PTN-BH', '2026-04-21 04:26:37'),
(19, 'UM', 'Universitas Negeri Malang', 'UM', 'Malang', 'Jawa Timur', 'Jawa', 'A', 'https://um.ac.id', 'PTN-BLU', '2026-04-21 04:26:37'),
(20, 'UNEJ', 'Universitas Jember', 'UNEJ', 'Jember', 'Jawa Timur', 'Jawa', 'A', 'https://unej.ac.id', 'PTN-BLU', '2026-04-21 04:26:37'),
(25, 'ITR', 'Institut Teknologi Sumatera (ITERA)', NULL, 'Lampung Selatan', 'Lampung', 'Sumatra', 'B', NULL, 'PTN', '2026-05-28 09:18:50'),
(26, 'PNJ', 'Politeknik Negeri Jakarta (PNJ)', NULL, 'Depok', 'Jawa Barat', 'Jawa', 'B', NULL, 'PTN', '2026-05-28 09:18:50'),
(28, 'UNA', 'Universitas Andalas (UNAND)', NULL, 'Padang', 'Sumatera Barat', 'Sumatra', 'A', NULL, 'PTN', '2026-05-28 09:18:50'),
(31, 'UNH', 'Universitas Hasanuddin (UNHAS)', NULL, 'Makassar', 'Sulawesi Selatan', 'Sulawesi', 'A', NULL, 'PTN', '2026-05-28 09:18:50'),
(32, 'UAI', 'Universitas Airlangga (UNAIR)', NULL, 'Surabaya', 'Jawa Timur', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:18:50'),
(43, 'UPNVJ', 'UPN Veteran Jakarta', NULL, 'Jakarta', 'DKI Jakarta', 'Jawa', 'B', NULL, 'PTN', '2026-05-28 09:34:07'),
(44, 'UPNVY', 'UPN Veteran Yogyakarta', NULL, 'Sleman', 'DI Yogyakarta', 'Jawa', 'B', NULL, 'PTN', '2026-05-28 09:34:07'),
(45, 'UPNVJT', 'UPN Veteran Jawa Timur', NULL, 'Surabaya', 'Jawa Timur', 'Jawa', 'B', NULL, 'PTN', '2026-05-28 09:34:07'),
(50, 'UNSOED', 'Universitas Jenderal Soedirman (UNSOED)', NULL, 'Purwokerto', 'Jawa Tengah', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(52, 'USK', 'Universitas Syiah Kuala (USK)', NULL, 'Banda Aceh', 'Aceh', 'Sumatra', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(53, 'UNIMED', 'Universitas Negeri Medan (UNIMED)', NULL, 'Medan', 'Sumatera Utara', 'Sumatra', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(54, 'UNTAN', 'Universitas Tanjungpura (UNTAN)', NULL, 'Pontianak', 'Kalimantan Barat', 'Kalimantan', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(55, 'ULM', 'Universitas Lambung Mangkurat (ULM)', NULL, 'Banjarmasin', 'Kalimantan Selatan', 'Kalimantan', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(57, 'UNUD', 'Universitas Udayana (UNUD)', NULL, 'Badung', 'Bali', 'Bali-Nusa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(58, 'UNESA', 'Universitas Negeri Surabaya (UNESA)', NULL, 'Surabaya', 'Jawa Timur', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(59, 'PENS', 'Politeknik Elektronika Negeri Surabaya (PENS)', NULL, 'Surabaya', 'Jawa Timur', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(60, 'UNNES', 'Universitas Negeri Semarang (UNNES)', NULL, 'Semarang', 'Jawa Tengah', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(62, 'UNM', 'Universitas Negeri Makassar (UNM)', NULL, 'Makassar', 'Sulawesi Selatan', 'Sulawesi', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(63, 'UNP', 'Universitas Negeri Padang (UNP)', NULL, 'Padang', 'Sumatera Barat', 'Sumatra', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(64, 'UNY', 'Universitas Negeri Yogyakarta (UNY)', NULL, 'Yogyakarta', 'DI Yogyakarta', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(66, 'POLBAN', 'Politeknik Negeri Bandung (POLBAN)', NULL, 'Bandung', 'Jawa Barat', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(67, 'POLINEMA', 'Politeknik Negeri Malang (POLINEMA)', NULL, 'Malang', 'Jawa Timur', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(68, 'UINJKT', 'UIN Syarif Hidayatullah', NULL, 'Jakarta', 'DKI Jakarta', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(69, 'UINSUKA', 'UIN Sunan Kalijaga', NULL, 'Yogyakarta', 'DI Yogyakarta', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(70, 'UINSGD', 'UIN Sunan Gunung Djati', NULL, 'Bandung', 'Jawa Barat', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(71, 'UINMALIKI', 'UIN Maulana Malik Ibrahim', NULL, 'Malang', 'Jawa Timur', 'Jawa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(72, 'UNRAM', 'Universitas Mataram (UNRAM)', NULL, 'Mataram', 'Nusa Tenggara Barat', 'Bali-Nusa', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(73, 'UNDANA', 'Universitas Nusa Cendana (UNDANA)', NULL, 'Kupang', 'Nusa Tenggara Timur', 'Bali-Nusa', 'B', NULL, 'PTN', '2026-05-28 09:34:07'),
(74, 'UNPATTI', 'Universitas Pattimura (UNPATTI)', NULL, 'Ambon', 'Maluku', 'Maluku-Papua', 'B', NULL, 'PTN', '2026-05-28 09:34:07'),
(75, 'UNCEN', 'Universitas Cenderawasih (UNCEN)', NULL, 'Jayapura', 'Papua', 'Maluku-Papua', 'B', NULL, 'PTN', '2026-05-28 09:34:07'),
(76, 'UNTAD', 'Universitas Tadulako (UNTAD)', NULL, 'Palu', 'Sulawesi Tengah', 'Sulawesi', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(77, 'UHO', 'Universitas Halu Oleo (UHO)', NULL, 'Kendari', 'Sulawesi Tenggara', 'Sulawesi', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(78, 'UNSRAT', 'Universitas Sam Ratulangi (UNSRAT)', NULL, 'Manado', 'Sulawesi Utara', 'Sulawesi', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(79, 'UNIB', 'Universitas Bengkulu (UNIB)', NULL, 'Bengkulu', 'Bengkulu', 'Sumatra', 'A', NULL, 'PTN', '2026-05-28 09:34:07'),
(80, 'UNJA', 'Universitas Jambi (UNJA)', NULL, 'Jambi', 'Jambi', 'Sumatra', 'B', NULL, 'PTN', '2026-05-28 09:34:07'),
(81, 'UNRI', 'Universitas Riau (UNRI)', NULL, 'Pekanbaru', 'Riau', 'Sumatra', 'A', NULL, 'PTN', '2026-05-28 09:34:07');

-- --------------------------------------------------------

--
-- Table structure for table `universitas_jurusan`
--

CREATE TABLE `universitas_jurusan` (
  `id` int(11) NOT NULL,
  `universitas_id` int(11) NOT NULL,
  `jurusan_id` int(11) NOT NULL,
  `daya_tampung_snbp` int(11) DEFAULT 0,
  `daya_tampung_snbt` int(11) DEFAULT 0,
  `peminat_snbp` int(11) DEFAULT 0,
  `peminat_snbt` int(11) DEFAULT 0,
  `passing_grade_snbt` decimal(6,3) DEFAULT 0.000,
  `passing_grade_snbp` decimal(6,3) DEFAULT 0.000,
  `peluang_masuk` decimal(5,2) DEFAULT 0.00 COMMENT 'persen 0-100',
  `akreditasi_prodi` varchar(10) DEFAULT NULL,
  `biaya_kuliah` bigint(20) DEFAULT 0 COMMENT 'per semester',
  `tahun_data` year(4) DEFAULT 2024
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `universitas_jurusan`
--

INSERT INTO `universitas_jurusan` (`id`, `universitas_id`, `jurusan_id`, `daya_tampung_snbp`, `daya_tampung_snbt`, `peminat_snbp`, `peminat_snbt`, `passing_grade_snbt`, `passing_grade_snbp`, `peluang_masuk`, `akreditasi_prodi`, `biaya_kuliah`, `tahun_data`) VALUES
(1, 1, 1, 35, 60, 2100, 8500, 687.500, 0.000, 3.20, 'Unggu', 12500000, '2024'),
(2, 1, 3, 30, 50, 1800, 7200, 682.000, 0.000, 3.50, 'Unggu', 11000000, '2024'),
(3, 1, 13, 40, 70, 1500, 5000, 620.000, 0.000, 6.50, 'Unggu', 8000000, '2024'),
(4, 1, 14, 30, 50, 1200, 4500, 635.000, 0.000, 5.80, 'Unggu', 8500000, '2024'),
(5, 1, 21, 45, 80, 900, 3200, 580.000, 0.000, 9.80, 'Unggu', 7500000, '2024'),
(6, 1, 22, 35, 60, 750, 2800, 595.000, 0.000, 10.20, 'Unggu', 7500000, '2024'),
(7, 1, 28, 40, 70, 3500, 12000, 720.000, 0.000, 2.10, 'Unggu', 20000000, '2024'),
(8, 2, 1, 40, 70, 2500, 10000, 690.000, 0.000, 2.80, 'Unggu', 10000000, '2024'),
(9, 2, 21, 50, 90, 800, 3000, 560.000, 0.000, 11.00, 'Unggu', 6500000, '2024'),
(10, 2, 22, 40, 70, 650, 2500, 570.000, 0.000, 10.80, 'Unggu', 6500000, '2024'),
(11, 2, 13, 45, 80, 1300, 4800, 615.000, 0.000, 7.00, 'Unggu', 7000000, '2024'),
(12, 2, 28, 35, 60, 4000, 15000, 725.000, 0.000, 1.80, 'Unggu', 22000000, '2024'),
(13, 2, 5, 45, 80, 1200, 5000, 650.000, 0.000, 5.50, 'Unggu', 9000000, '2024'),
(14, 3, 1, 50, 90, 3000, 12500, 695.000, 0.000, 2.40, 'Unggu', 12000000, '2024'),
(15, 3, 6, 45, 80, 1500, 6500, 665.000, 0.000, 4.50, 'Unggu', 11000000, '2024'),
(16, 3, 5, 40, 70, 1300, 5500, 660.000, 0.000, 4.80, 'Unggu', 10500000, '2024'),
(17, 3, 17, 35, 60, 1800, 7000, 658.000, 0.000, 4.10, 'Unggu', 9500000, '2024'),
(18, 3, 20, 40, 70, 2000, 8000, 662.000, 0.000, 3.80, 'Unggu', 10000000, '2024'),
(19, 4, 1, 45, 80, 2200, 9000, 680.000, 0.000, 3.10, 'Unggu', 9000000, '2024'),
(20, 4, 6, 40, 70, 1400, 5800, 660.000, 0.000, 4.60, 'Unggu', 8500000, '2024'),
(21, 4, 5, 40, 70, 1200, 5000, 655.000, 0.000, 5.00, 'Unggu', 8000000, '2024'),
(22, 4, 3, 35, 60, 1600, 6500, 665.000, 0.000, 4.20, 'Unggu', 8500000, '2024'),
(23, 5, 28, 35, 60, 3800, 14000, 715.000, 0.000, 1.90, 'Unggu', 22000000, '2024'),
(24, 5, 13, 40, 70, 1100, 4200, 605.000, 0.000, 7.40, 'Unggu', 7000000, '2024'),
(25, 5, 30, 35, 60, 600, 2200, 580.000, 0.000, 10.50, 'Unggu', 7500000, '2024'),
(26, 5, 21, 45, 80, 700, 2600, 550.000, 0.000, 12.00, 'Unggu', 6500000, '2024'),
(27, 7, 1, 40, 70, 1800, 7500, 670.000, 0.000, 3.90, 'Unggu', 8000000, '2024'),
(28, 7, 22, 40, 70, 550, 2000, 545.000, 0.000, 13.00, 'Unggu', 5500000, '2024'),
(29, 7, 14, 35, 60, 900, 3500, 605.000, 0.000, 7.10, 'Unggu', 6000000, '2024'),
(30, 7, 7, 40, 70, 1000, 4000, 625.000, 0.000, 6.40, 'Unggu', 7000000, '2024'),
(31, 8, 28, 30, 50, 3200, 12500, 708.000, 0.000, 2.00, 'Unggu', 20000000, '2024'),
(32, 8, 13, 40, 70, 1000, 3800, 600.000, 0.000, 7.80, 'Unggu', 6500000, '2024'),
(33, 8, 30, 30, 50, 500, 1800, 570.000, 0.000, 11.00, 'Unggu', 7000000, '2024'),
(34, 8, 25, 35, 60, 400, 1500, 540.000, 0.000, 14.00, 'A', 4500000, '2024'),
(35, 9, 1, 40, 70, 1600, 6500, 665.000, 0.000, 4.30, 'Unggu', 7500000, '2024'),
(36, 9, 21, 45, 80, 650, 2400, 540.000, 0.000, 13.20, 'Unggu', 5500000, '2024'),
(37, 9, 22, 40, 70, 500, 1800, 535.000, 0.000, 14.00, 'Unggu', 5500000, '2024'),
(38, 10, 14, 35, 60, 800, 3200, 590.000, 0.000, 8.60, 'A', 5000000, '2024'),
(39, 10, 22, 40, 70, 450, 1700, 530.000, 0.000, 14.80, 'A', 4500000, '2024'),
(40, 10, 7, 35, 60, 900, 3500, 605.000, 0.000, 7.00, 'A', 5500000, '2024'),
(41, 11, 28, 25, 40, 2500, 9000, 695.000, 0.000, 2.30, 'A', 15000000, '2024'),
(42, 11, 1, 35, 60, 1200, 5000, 645.000, 0.000, 5.20, 'A', 6000000, '2024'),
(43, 11, 14, 30, 50, 700, 2800, 575.000, 0.000, 9.80, 'A', 5000000, '2024'),
(44, 14, 1, 35, 60, 1300, 5200, 650.000, 0.000, 5.00, 'Unggu', 6500000, '2024'),
(45, 14, 28, 25, 40, 2800, 10500, 700.000, 0.000, 2.10, 'A', 18000000, '2024'),
(46, 14, 7, 35, 60, 800, 3200, 595.000, 0.000, 8.00, 'A', 5500000, '2024'),
(47, 15, 1, 30, 50, 500, 2000, 590.000, 0.000, 11.00, 'A', 5000000, '2024'),
(48, 15, 21, 35, 60, 300, 1200, 510.000, 0.000, 18.00, 'B', 3500000, '2024'),
(49, 16, 28, 20, 35, 1800, 6500, 685.000, 0.000, 2.50, 'A', 12000000, '2024'),
(50, 16, 13, 35, 60, 600, 2400, 565.000, 0.000, 11.50, 'A', 5000000, '2024'),
(51, 16, 21, 40, 70, 300, 1200, 505.000, 0.000, 19.00, 'A', 4000000, '2024'),
(52, 1, 4, 30, 50, 1500, 6000, 670.000, 0.000, 4.20, 'Unggul', 11000000, '2024'),
(53, 2, 4, 35, 60, 1800, 7000, 680.000, 0.000, 3.50, 'Unggul', 9500000, '2024'),
(54, 3, 4, 40, 70, 2000, 8500, 685.000, 0.000, 3.00, 'Unggul', 11500000, '2024'),
(55, 4, 4, 35, 60, 1400, 5800, 665.000, 0.000, 4.80, 'Unggul', 9000000, '2024'),
(56, 1, 2, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(57, 10, 2, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(58, 1, 8, 40, 70, 1200, 4000, 690.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(59, 10, 8, 50, 90, 800, 2500, 620.000, 0.000, 12.50, 'B', 5000000, '2024'),
(60, 1, 9, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(61, 10, 9, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(62, 15, 9, 45, 80, 900, 3000, 600.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(63, 1, 10, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(64, 10, 10, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(65, 15, 10, 45, 80, 900, 3000, 600.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(66, 1, 11, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(67, 10, 11, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(68, 15, 11, 45, 80, 900, 3000, 600.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(69, 1, 12, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(70, 10, 12, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(71, 15, 12, 45, 80, 900, 3000, 600.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(72, 1, 15, 40, 70, 1200, 4000, 620.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(73, 10, 15, 50, 90, 800, 2500, 550.000, 0.000, 12.50, 'B', 5000000, '2024'),
(74, 15, 15, 45, 80, 900, 3000, 580.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(75, 1, 16, 40, 70, 1200, 4000, 620.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(76, 10, 16, 50, 90, 800, 2500, 550.000, 0.000, 12.50, 'B', 5000000, '2024'),
(77, 15, 16, 45, 80, 900, 3000, 580.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(78, 1, 18, 40, 70, 1200, 4000, 620.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(79, 10, 18, 50, 90, 800, 2500, 550.000, 0.000, 12.50, 'B', 5000000, '2024'),
(80, 15, 18, 45, 80, 900, 3000, 580.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(81, 1, 19, 40, 70, 1200, 4000, 620.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(82, 10, 19, 50, 90, 800, 2500, 550.000, 0.000, 12.50, 'B', 5000000, '2024'),
(83, 15, 19, 45, 80, 900, 3000, 580.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(84, 1, 23, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(85, 10, 23, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(86, 15, 23, 45, 80, 900, 3000, 600.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(87, 1, 24, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(88, 10, 24, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(89, 15, 24, 45, 80, 900, 3000, 600.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(90, 1, 26, 40, 70, 1200, 4000, 620.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(91, 10, 26, 50, 90, 800, 2500, 550.000, 0.000, 12.50, 'B', 5000000, '2024'),
(92, 15, 26, 45, 80, 900, 3000, 580.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(93, 1, 27, 40, 70, 1200, 4000, 620.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(94, 10, 27, 50, 90, 800, 2500, 550.000, 0.000, 12.50, 'B', 5000000, '2024'),
(95, 15, 27, 45, 80, 900, 3000, 580.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(96, 1, 29, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(97, 10, 29, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(98, 15, 29, 45, 80, 900, 3000, 600.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(99, 1, 31, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(100, 10, 31, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(101, 15, 31, 45, 80, 900, 3000, 600.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(102, 1, 32, 40, 70, 1200, 4000, 640.000, 0.000, 4.50, 'Unggul', 10000000, '2024'),
(103, 10, 32, 50, 90, 800, 2500, 570.000, 0.000, 12.50, 'B', 5000000, '2024'),
(104, 15, 32, 45, 80, 900, 3000, 600.000, 0.000, 8.00, 'Baik', 6500000, '2024'),
(107, 6, 1, 0, 0, 0, 0, 598.396, 71.808, 80.32, 'B', 5509765, '2024'),
(108, 25, 1, 0, 0, 0, 0, 624.550, 74.946, 75.09, 'B', 7814886, '2024'),
(109, 26, 1, 0, 0, 0, 0, 595.202, 71.424, 80.96, 'B', 7314030, '2024'),
(110, 28, 1, 0, 0, 0, 0, 594.729, 71.367, 81.05, 'B', 8472919, '2024'),
(111, 31, 1, 0, 0, 0, 0, 599.132, 71.896, 80.17, 'B', 7452819, '2024'),
(112, 32, 1, 0, 0, 0, 0, 687.764, 82.532, 62.45, 'A', 6991700, '2024'),
(113, 3, 2, 0, 0, 0, 0, 565.782, 67.894, 85.00, 'B', 6795058, '2024'),
(114, 4, 2, 0, 0, 0, 0, 569.551, 68.346, 85.00, 'B', 6331741, '2024'),
(115, 6, 2, 0, 0, 0, 0, 585.451, 70.254, 82.91, 'B', 6139678, '2024'),
(116, 25, 2, 0, 0, 0, 0, 624.756, 74.971, 75.05, 'B', 8024677, '2024'),
(117, 26, 2, 0, 0, 0, 0, 566.020, 67.922, 85.00, 'B', 7417385, '2024'),
(118, 28, 2, 0, 0, 0, 0, 605.595, 72.671, 78.88, 'A', 7736936, '2024'),
(119, 31, 2, 0, 0, 0, 0, 591.536, 70.984, 81.69, 'B', 8554737, '2024'),
(120, 32, 2, 0, 0, 0, 0, 682.631, 81.916, 63.47, 'A', 5881049, '2024'),
(121, 3, 3, 0, 0, 0, 0, 560.082, 67.210, 85.00, 'B', 7795607, '2024'),
(123, 6, 3, 0, 0, 0, 0, 594.669, 71.360, 81.07, 'B', 8185208, '2024'),
(124, 25, 3, 0, 0, 0, 0, 576.280, 69.154, 84.74, 'B', 8195472, '2024'),
(125, 26, 3, 0, 0, 0, 0, 593.374, 71.205, 81.33, 'B', 7957053, '2024'),
(126, 28, 3, 0, 0, 0, 0, 589.854, 70.782, 82.03, 'B', 8213682, '2024'),
(127, 31, 3, 0, 0, 0, 0, 620.124, 74.415, 75.98, 'A', 5723341, '2024'),
(128, 32, 3, 0, 0, 0, 0, 676.347, 81.162, 64.73, 'A', 8034801, '2024'),
(131, 6, 4, 0, 0, 0, 0, 563.171, 67.581, 85.00, 'B', 7243482, '2024'),
(132, 25, 4, 0, 0, 0, 0, 606.889, 72.827, 78.62, 'B', 6172875, '2024'),
(133, 26, 4, 0, 0, 0, 0, 583.808, 70.057, 83.24, 'B', 7786391, '2024'),
(134, 28, 4, 0, 0, 0, 0, 582.415, 69.890, 83.52, 'B', 7843405, '2024'),
(135, 31, 4, 0, 0, 0, 0, 626.009, 75.121, 74.80, 'A', 5369622, '2024'),
(136, 32, 4, 0, 0, 0, 0, 675.542, 81.065, 64.89, 'A', 8640932, '2024'),
(139, 6, 5, 0, 0, 0, 0, 567.435, 68.092, 85.00, 'B', 5949048, '2024'),
(140, 25, 5, 0, 0, 0, 0, 619.003, 74.280, 76.20, 'B', 6319295, '2024'),
(141, 26, 5, 0, 0, 0, 0, 629.762, 75.571, 74.05, 'B', 6077462, '2024'),
(142, 28, 5, 0, 0, 0, 0, 605.091, 72.611, 78.98, 'A', 5607116, '2024'),
(143, 31, 5, 0, 0, 0, 0, 623.068, 74.768, 75.39, 'A', 7747716, '2024'),
(144, 32, 5, 0, 0, 0, 0, 646.644, 77.597, 70.67, 'A', 8335928, '2024'),
(147, 6, 6, 0, 0, 0, 0, 573.937, 68.872, 85.00, 'B', 7682985, '2024'),
(148, 25, 6, 0, 0, 0, 0, 585.068, 70.208, 82.99, 'B', 6037512, '2024'),
(149, 26, 6, 0, 0, 0, 0, 555.293, 66.635, 85.00, 'B', 6190292, '2024'),
(150, 28, 6, 0, 0, 0, 0, 578.301, 69.396, 84.34, 'B', 7772808, '2024'),
(151, 31, 6, 0, 0, 0, 0, 661.245, 79.349, 67.75, 'A', 5109327, '2024'),
(152, 32, 6, 0, 0, 0, 0, 706.414, 84.770, 58.72, 'A', 5298656, '2024'),
(153, 3, 7, 0, 0, 0, 0, 605.363, 72.644, 78.93, 'Unggu', 6346497, '2024'),
(154, 4, 7, 0, 0, 0, 0, 567.659, 68.119, 85.00, 'B', 6357610, '2024'),
(155, 6, 7, 0, 0, 0, 0, 625.119, 75.014, 74.98, 'A', 7672634, '2024'),
(156, 25, 7, 0, 0, 0, 0, 563.242, 67.589, 85.00, 'B', 5934858, '2024'),
(157, 26, 7, 0, 0, 0, 0, 627.116, 75.254, 74.58, 'B', 5432765, '2024'),
(158, 28, 7, 0, 0, 0, 0, 603.084, 72.370, 79.38, 'A', 6282295, '2024'),
(159, 31, 7, 0, 0, 0, 0, 605.168, 72.620, 78.97, 'A', 6248769, '2024'),
(160, 32, 7, 0, 0, 0, 0, 652.784, 78.334, 69.44, 'A', 7984864, '2024'),
(161, 3, 8, 0, 0, 0, 0, 611.403, 73.368, 77.72, 'Unggu', 5463112, '2024'),
(162, 4, 8, 0, 0, 0, 0, 579.206, 69.505, 84.16, 'B', 5256800, '2024'),
(163, 6, 8, 0, 0, 0, 0, 620.651, 74.478, 75.87, 'A', 7481952, '2024'),
(164, 25, 8, 0, 0, 0, 0, 553.397, 66.408, 85.00, 'B', 5628027, '2024'),
(165, 26, 8, 0, 0, 0, 0, 590.950, 70.914, 81.81, 'B', 8711127, '2024'),
(166, 28, 8, 0, 0, 0, 0, 613.427, 73.611, 77.31, 'A', 7278678, '2024'),
(167, 31, 8, 0, 0, 0, 0, 648.075, 77.769, 70.38, 'A', 5438639, '2024'),
(168, 32, 8, 0, 0, 0, 0, 636.015, 76.322, 72.80, 'A', 6502302, '2024'),
(169, 3, 9, 0, 0, 0, 0, 609.598, 73.152, 78.08, 'Unggu', 6802445, '2024'),
(170, 4, 9, 0, 0, 0, 0, 634.677, 76.161, 73.06, 'A', 5126017, '2024'),
(171, 6, 9, 0, 0, 0, 0, 614.026, 73.683, 77.19, 'A', 6892496, '2024'),
(172, 25, 9, 0, 0, 0, 0, 553.424, 66.411, 85.00, 'B', 7188508, '2024'),
(173, 26, 9, 0, 0, 0, 0, 593.185, 71.182, 81.36, 'B', 8071133, '2024'),
(174, 28, 9, 0, 0, 0, 0, 558.903, 67.068, 85.00, 'B', 7774437, '2024'),
(175, 31, 9, 0, 0, 0, 0, 601.059, 72.127, 79.79, 'A', 5756106, '2024'),
(176, 32, 9, 0, 0, 0, 0, 690.162, 82.819, 61.97, 'A', 8626466, '2024'),
(177, 3, 10, 0, 0, 0, 0, 589.893, 70.787, 82.02, 'B', 7195623, '2024'),
(178, 4, 10, 0, 0, 0, 0, 604.088, 72.491, 79.18, 'A', 5003784, '2024'),
(179, 6, 10, 0, 0, 0, 0, 624.448, 74.934, 75.11, 'A', 7175027, '2024'),
(180, 25, 10, 0, 0, 0, 0, 615.862, 73.903, 76.83, 'B', 8228650, '2024'),
(181, 26, 10, 0, 0, 0, 0, 587.855, 70.543, 82.43, 'B', 6129803, '2024'),
(182, 28, 10, 0, 0, 0, 0, 597.623, 71.715, 80.48, 'B', 6460145, '2024'),
(183, 31, 10, 0, 0, 0, 0, 637.615, 76.514, 72.48, 'A', 6799831, '2024'),
(184, 32, 10, 0, 0, 0, 0, 684.854, 82.183, 63.03, 'A', 6456974, '2024'),
(185, 3, 11, 0, 0, 0, 0, 577.856, 69.343, 84.43, 'B', 8344906, '2024'),
(186, 4, 11, 0, 0, 0, 0, 588.836, 70.660, 82.23, 'B', 5438810, '2024'),
(187, 6, 11, 0, 0, 0, 0, 564.046, 67.686, 85.00, 'B', 7751743, '2024'),
(188, 25, 11, 0, 0, 0, 0, 624.830, 74.980, 75.03, 'B', 6628339, '2024'),
(189, 26, 11, 0, 0, 0, 0, 588.924, 70.671, 82.22, 'B', 5397811, '2024'),
(190, 28, 11, 0, 0, 0, 0, 619.499, 74.340, 76.10, 'A', 6653810, '2024'),
(191, 31, 11, 0, 0, 0, 0, 653.511, 78.421, 69.30, 'A', 6797513, '2024'),
(192, 32, 11, 0, 0, 0, 0, 657.389, 78.887, 68.52, 'A', 7714562, '2024'),
(193, 3, 12, 0, 0, 0, 0, 604.625, 72.555, 79.08, 'Unggu', 7841387, '2024'),
(194, 4, 12, 0, 0, 0, 0, 565.119, 67.814, 85.00, 'B', 6259645, '2024'),
(195, 6, 12, 0, 0, 0, 0, 560.396, 67.248, 85.00, 'B', 6249137, '2024'),
(196, 25, 12, 0, 0, 0, 0, 553.278, 66.393, 85.00, 'B', 6902315, '2024'),
(197, 26, 12, 0, 0, 0, 0, 607.889, 72.947, 78.42, 'B', 5492500, '2024'),
(198, 28, 12, 0, 0, 0, 0, 579.824, 69.579, 84.04, 'B', 5390841, '2024'),
(199, 31, 12, 0, 0, 0, 0, 601.965, 72.236, 79.61, 'A', 5104058, '2024'),
(200, 32, 12, 0, 0, 0, 0, 685.135, 82.216, 62.97, 'A', 5678705, '2024'),
(201, 3, 13, 0, 0, 0, 0, 622.158, 74.659, 75.57, 'Unggu', 7073317, '2024'),
(202, 4, 13, 0, 0, 0, 0, 617.669, 74.120, 76.47, 'A', 7484954, '2024'),
(203, 6, 13, 0, 0, 0, 0, 594.355, 71.323, 81.13, 'B', 7941475, '2024'),
(204, 25, 13, 0, 0, 0, 0, 600.548, 72.066, 79.89, 'B', 6208102, '2024'),
(205, 26, 13, 0, 0, 0, 0, 556.691, 66.803, 85.00, 'B', 6241517, '2024'),
(206, 28, 13, 0, 0, 0, 0, 563.157, 67.579, 85.00, 'B', 6654638, '2024'),
(207, 31, 13, 0, 0, 0, 0, 644.133, 77.296, 71.17, 'A', 7090371, '2024'),
(208, 32, 13, 0, 0, 0, 0, 658.249, 78.990, 68.35, 'A', 5765864, '2024'),
(209, 3, 14, 0, 0, 0, 0, 563.811, 67.657, 85.00, 'B', 6025055, '2024'),
(210, 4, 14, 0, 0, 0, 0, 603.799, 72.456, 79.24, 'A', 7374424, '2024'),
(211, 6, 14, 0, 0, 0, 0, 613.207, 73.585, 77.36, 'A', 6584904, '2024'),
(212, 25, 14, 0, 0, 0, 0, 610.245, 73.229, 77.95, 'B', 5994566, '2024'),
(213, 26, 14, 0, 0, 0, 0, 555.941, 66.713, 85.00, 'B', 6677079, '2024'),
(214, 28, 14, 0, 0, 0, 0, 617.527, 74.103, 76.49, 'A', 8747808, '2024'),
(215, 31, 14, 0, 0, 0, 0, 594.917, 71.390, 81.02, 'B', 5422774, '2024'),
(216, 32, 14, 0, 0, 0, 0, 632.708, 75.925, 73.46, 'A', 6748361, '2024'),
(217, 3, 15, 0, 0, 0, 0, 590.209, 70.825, 81.96, 'B', 6966647, '2024'),
(218, 4, 15, 0, 0, 0, 0, 567.759, 68.131, 85.00, 'B', 7620294, '2024'),
(219, 6, 15, 0, 0, 0, 0, 599.122, 71.895, 80.18, 'B', 6642186, '2024'),
(220, 25, 15, 0, 0, 0, 0, 568.427, 68.211, 85.00, 'B', 7291106, '2024'),
(221, 26, 15, 0, 0, 0, 0, 568.456, 68.215, 85.00, 'B', 5920856, '2024'),
(222, 28, 15, 0, 0, 0, 0, 558.947, 67.074, 85.00, 'B', 7804006, '2024'),
(223, 31, 15, 0, 0, 0, 0, 624.076, 74.889, 75.18, 'A', 7733053, '2024'),
(224, 32, 15, 0, 0, 0, 0, 683.649, 82.038, 63.27, 'A', 6613871, '2024'),
(225, 3, 16, 0, 0, 0, 0, 600.448, 72.054, 79.91, 'Unggu', 5034017, '2024'),
(226, 4, 16, 0, 0, 0, 0, 585.405, 70.249, 82.92, 'B', 7679502, '2024'),
(227, 6, 16, 0, 0, 0, 0, 632.930, 75.952, 73.41, 'A', 8898048, '2024'),
(228, 25, 16, 0, 0, 0, 0, 622.101, 74.652, 75.58, 'B', 7637457, '2024'),
(229, 26, 16, 0, 0, 0, 0, 598.112, 71.773, 80.38, 'B', 7959488, '2024'),
(230, 28, 16, 0, 0, 0, 0, 586.239, 70.349, 82.75, 'B', 7440401, '2024'),
(231, 31, 16, 0, 0, 0, 0, 612.656, 73.519, 77.47, 'A', 8679127, '2024'),
(232, 32, 16, 0, 0, 0, 0, 699.026, 83.883, 60.19, 'A', 7065550, '2024'),
(234, 4, 17, 0, 0, 0, 0, 639.187, 76.702, 72.16, 'A', 6246470, '2024'),
(235, 6, 17, 0, 0, 0, 0, 593.206, 71.185, 81.36, 'B', 5465171, '2024'),
(236, 25, 17, 0, 0, 0, 0, 578.033, 69.364, 84.39, 'B', 5654130, '2024'),
(237, 26, 17, 0, 0, 0, 0, 614.002, 73.680, 77.20, 'B', 8310721, '2024'),
(238, 28, 17, 0, 0, 0, 0, 586.178, 70.341, 82.76, 'B', 8550768, '2024'),
(239, 31, 17, 0, 0, 0, 0, 604.732, 72.568, 79.05, 'A', 8396339, '2024'),
(240, 32, 17, 0, 0, 0, 0, 633.576, 76.029, 73.28, 'A', 6662776, '2024'),
(241, 3, 18, 0, 0, 0, 0, 625.524, 75.063, 74.90, 'Unggu', 5134802, '2024'),
(242, 4, 18, 0, 0, 0, 0, 563.439, 67.613, 85.00, 'B', 7866502, '2024'),
(243, 6, 18, 0, 0, 0, 0, 570.663, 68.480, 85.00, 'B', 6180792, '2024'),
(244, 25, 18, 0, 0, 0, 0, 601.875, 72.225, 79.63, 'B', 7253467, '2024'),
(245, 26, 18, 0, 0, 0, 0, 559.917, 67.190, 85.00, 'B', 8580168, '2024'),
(246, 28, 18, 0, 0, 0, 0, 568.937, 68.272, 85.00, 'B', 5239199, '2024'),
(247, 31, 18, 0, 0, 0, 0, 634.461, 76.135, 73.11, 'A', 7339377, '2024'),
(248, 32, 18, 0, 0, 0, 0, 703.097, 84.372, 59.38, 'A', 8602527, '2024'),
(249, 3, 19, 0, 0, 0, 0, 606.010, 72.721, 78.80, 'Unggu', 5460041, '2024'),
(250, 4, 19, 0, 0, 0, 0, 579.990, 69.599, 84.00, 'B', 8689818, '2024'),
(251, 6, 19, 0, 0, 0, 0, 597.866, 71.744, 80.43, 'B', 6393934, '2024'),
(252, 25, 19, 0, 0, 0, 0, 597.597, 71.712, 80.48, 'B', 7491327, '2024'),
(253, 26, 19, 0, 0, 0, 0, 569.127, 68.295, 85.00, 'B', 8908700, '2024'),
(254, 28, 19, 0, 0, 0, 0, 593.854, 71.263, 81.23, 'B', 8894451, '2024'),
(255, 31, 19, 0, 0, 0, 0, 608.444, 73.013, 78.31, 'A', 7514817, '2024'),
(256, 32, 19, 0, 0, 0, 0, 679.044, 81.485, 64.19, 'A', 5141372, '2024'),
(258, 4, 20, 0, 0, 0, 0, 624.085, 74.890, 75.18, 'A', 7366202, '2024'),
(259, 6, 20, 0, 0, 0, 0, 614.250, 73.710, 77.15, 'A', 6143270, '2024'),
(260, 25, 20, 0, 0, 0, 0, 627.238, 75.269, 74.55, 'B', 6059398, '2024'),
(261, 26, 20, 0, 0, 0, 0, 624.488, 74.939, 75.10, 'B', 8310458, '2024'),
(262, 28, 20, 0, 0, 0, 0, 625.513, 75.062, 74.90, 'A', 6404905, '2024'),
(263, 31, 20, 0, 0, 0, 0, 603.411, 72.409, 79.32, 'A', 6104292, '2024'),
(264, 32, 20, 0, 0, 0, 0, 643.432, 77.212, 71.31, 'A', 8658100, '2024'),
(265, 3, 21, 0, 0, 0, 0, 616.417, 73.970, 76.72, 'Unggu', 6763360, '2024'),
(266, 4, 21, 0, 0, 0, 0, 580.615, 69.674, 83.88, 'B', 7080281, '2024'),
(267, 6, 21, 0, 0, 0, 0, 626.459, 75.175, 74.71, 'A', 6725593, '2024'),
(268, 25, 21, 0, 0, 0, 0, 570.969, 68.516, 85.00, 'B', 6418128, '2024'),
(269, 26, 21, 0, 0, 0, 0, 583.849, 70.062, 83.23, 'B', 6901486, '2024'),
(270, 28, 21, 0, 0, 0, 0, 600.124, 72.015, 79.98, 'A', 6514164, '2024'),
(271, 31, 21, 0, 0, 0, 0, 659.206, 79.105, 68.16, 'A', 7478288, '2024'),
(272, 32, 21, 0, 0, 0, 0, 684.053, 82.086, 63.19, 'A', 7356959, '2024'),
(273, 3, 22, 0, 0, 0, 0, 634.093, 76.091, 73.18, 'Unggu', 7472098, '2024'),
(274, 4, 22, 0, 0, 0, 0, 590.663, 70.880, 81.87, 'B', 6727433, '2024'),
(275, 6, 22, 0, 0, 0, 0, 623.988, 74.879, 75.20, 'A', 6654934, '2024'),
(276, 25, 22, 0, 0, 0, 0, 587.461, 70.495, 82.51, 'B', 8570287, '2024'),
(277, 26, 22, 0, 0, 0, 0, 610.779, 73.293, 77.84, 'B', 8019002, '2024'),
(278, 28, 22, 0, 0, 0, 0, 582.234, 69.868, 83.55, 'B', 8317260, '2024'),
(279, 31, 22, 0, 0, 0, 0, 667.090, 80.051, 66.58, 'A', 7600560, '2024'),
(280, 32, 22, 0, 0, 0, 0, 690.006, 82.801, 62.00, 'A', 7611537, '2024'),
(281, 3, 23, 0, 0, 0, 0, 622.292, 74.675, 75.54, 'Unggu', 7270920, '2024'),
(282, 4, 23, 0, 0, 0, 0, 588.365, 70.604, 82.33, 'B', 7230309, '2024'),
(283, 6, 23, 0, 0, 0, 0, 610.147, 73.218, 77.97, 'A', 7012285, '2024'),
(284, 25, 23, 0, 0, 0, 0, 574.575, 68.949, 85.00, 'B', 5475150, '2024'),
(285, 26, 23, 0, 0, 0, 0, 610.606, 73.273, 77.88, 'B', 6231278, '2024'),
(286, 28, 23, 0, 0, 0, 0, 571.735, 68.608, 85.00, 'B', 5897503, '2024'),
(287, 31, 23, 0, 0, 0, 0, 633.362, 76.003, 73.33, 'A', 8823178, '2024'),
(288, 32, 23, 0, 0, 0, 0, 687.540, 82.505, 62.49, 'A', 8949250, '2024'),
(289, 3, 24, 0, 0, 0, 0, 628.331, 75.400, 74.33, 'Unggu', 7985031, '2024'),
(290, 4, 24, 0, 0, 0, 0, 628.077, 75.369, 74.38, 'A', 6995992, '2024'),
(291, 6, 24, 0, 0, 0, 0, 628.328, 75.399, 74.33, 'A', 8185960, '2024'),
(292, 25, 24, 0, 0, 0, 0, 590.362, 70.843, 81.93, 'B', 5103310, '2024'),
(293, 26, 24, 0, 0, 0, 0, 551.766, 66.212, 85.00, 'B', 7238157, '2024'),
(294, 28, 24, 0, 0, 0, 0, 561.192, 67.343, 85.00, 'B', 7103942, '2024'),
(295, 31, 24, 0, 0, 0, 0, 638.283, 76.594, 72.34, 'A', 7072194, '2024'),
(296, 32, 24, 0, 0, 0, 0, 636.207, 76.345, 72.76, 'A', 5676471, '2024'),
(297, 3, 25, 0, 0, 0, 0, 617.804, 74.136, 76.44, 'Unggu', 7605445, '2024'),
(298, 4, 25, 0, 0, 0, 0, 601.860, 72.223, 79.63, 'A', 8513770, '2024'),
(299, 6, 25, 0, 0, 0, 0, 583.596, 70.032, 83.28, 'B', 8002574, '2024'),
(300, 25, 25, 0, 0, 0, 0, 594.368, 71.324, 81.13, 'B', 8132106, '2024'),
(301, 26, 25, 0, 0, 0, 0, 592.464, 71.096, 81.51, 'B', 5662190, '2024'),
(302, 28, 25, 0, 0, 0, 0, 563.486, 67.618, 85.00, 'B', 8469457, '2024'),
(303, 31, 25, 0, 0, 0, 0, 610.317, 73.238, 77.94, 'A', 5423939, '2024'),
(304, 32, 25, 0, 0, 0, 0, 685.330, 82.240, 62.93, 'A', 5144274, '2024'),
(305, 3, 26, 0, 0, 0, 0, 598.898, 71.868, 80.22, 'B', 5775440, '2024'),
(306, 4, 26, 0, 0, 0, 0, 587.274, 70.473, 82.55, 'B', 6093545, '2024'),
(307, 6, 26, 0, 0, 0, 0, 619.579, 74.349, 76.08, 'A', 7248651, '2024'),
(308, 25, 26, 0, 0, 0, 0, 589.265, 70.712, 82.15, 'B', 7281688, '2024'),
(309, 26, 26, 0, 0, 0, 0, 572.653, 68.718, 85.00, 'B', 6730398, '2024'),
(310, 28, 26, 0, 0, 0, 0, 569.759, 68.371, 85.00, 'B', 8269475, '2024'),
(311, 31, 26, 0, 0, 0, 0, 595.686, 71.482, 80.86, 'B', 8071939, '2024'),
(312, 32, 26, 0, 0, 0, 0, 650.787, 78.094, 69.84, 'A', 8654808, '2024'),
(313, 3, 27, 0, 0, 0, 0, 612.027, 73.443, 77.59, 'Unggu', 6167627, '2024'),
(314, 4, 27, 0, 0, 0, 0, 585.487, 70.258, 82.90, 'B', 8744856, '2024'),
(315, 6, 27, 0, 0, 0, 0, 570.683, 68.482, 85.00, 'B', 8582856, '2024'),
(316, 25, 27, 0, 0, 0, 0, 562.779, 67.533, 85.00, 'B', 6291702, '2024'),
(317, 26, 27, 0, 0, 0, 0, 591.317, 70.958, 81.74, 'B', 5846659, '2024'),
(318, 28, 27, 0, 0, 0, 0, 557.682, 66.922, 85.00, 'B', 7721313, '2024'),
(319, 31, 27, 0, 0, 0, 0, 603.234, 72.388, 79.35, 'A', 8684459, '2024'),
(320, 32, 27, 0, 0, 0, 0, 672.094, 80.651, 65.58, 'A', 5008290, '2024'),
(321, 3, 28, 0, 0, 0, 0, 624.772, 74.973, 75.05, 'Unggu', 5751654, '2024'),
(322, 4, 28, 0, 0, 0, 0, 569.693, 68.363, 85.00, 'B', 8196535, '2024'),
(323, 6, 28, 0, 0, 0, 0, 609.610, 73.153, 78.08, 'A', 6838848, '2024'),
(324, 25, 28, 0, 0, 0, 0, 589.947, 70.794, 82.01, 'B', 8538596, '2024'),
(325, 26, 28, 0, 0, 0, 0, 629.073, 75.489, 74.19, 'B', 8054722, '2024'),
(326, 28, 28, 0, 0, 0, 0, 590.035, 70.804, 81.99, 'B', 6324139, '2024'),
(327, 31, 28, 0, 0, 0, 0, 624.606, 74.953, 75.08, 'A', 7152675, '2024'),
(328, 32, 28, 0, 0, 0, 0, 689.820, 82.778, 62.04, 'A', 6690633, '2024'),
(329, 3, 29, 0, 0, 0, 0, 612.141, 73.457, 77.57, 'Unggu', 8952532, '2024'),
(330, 4, 29, 0, 0, 0, 0, 563.850, 67.662, 85.00, 'B', 6056986, '2024'),
(331, 6, 29, 0, 0, 0, 0, 570.196, 68.423, 85.00, 'B', 8718766, '2024'),
(332, 25, 29, 0, 0, 0, 0, 575.673, 69.081, 84.87, 'B', 5293555, '2024'),
(333, 26, 29, 0, 0, 0, 0, 616.087, 73.930, 76.78, 'B', 5391845, '2024'),
(334, 28, 29, 0, 0, 0, 0, 563.588, 67.631, 85.00, 'B', 5339567, '2024'),
(335, 31, 29, 0, 0, 0, 0, 629.739, 75.569, 74.05, 'A', 8729464, '2024'),
(336, 32, 29, 0, 0, 0, 0, 703.296, 84.396, 59.34, 'A', 6701686, '2024'),
(337, 3, 30, 0, 0, 0, 0, 622.135, 74.656, 75.57, 'Unggu', 8699581, '2024'),
(338, 4, 30, 0, 0, 0, 0, 631.720, 75.806, 73.66, 'A', 6115086, '2024'),
(339, 6, 30, 0, 0, 0, 0, 625.554, 75.067, 74.89, 'A', 7877332, '2024'),
(340, 25, 30, 0, 0, 0, 0, 557.599, 66.912, 85.00, 'B', 5621063, '2024'),
(341, 26, 30, 0, 0, 0, 0, 585.294, 70.235, 82.94, 'B', 6133057, '2024'),
(342, 28, 30, 0, 0, 0, 0, 608.309, 72.997, 78.34, 'A', 5412869, '2024'),
(343, 31, 30, 0, 0, 0, 0, 657.665, 78.920, 68.47, 'A', 7558827, '2024'),
(344, 32, 30, 0, 0, 0, 0, 648.652, 77.838, 70.27, 'A', 6024113, '2024'),
(345, 3, 31, 0, 0, 0, 0, 626.632, 75.196, 74.67, 'Unggu', 8366011, '2024'),
(346, 4, 31, 0, 0, 0, 0, 576.230, 69.148, 84.75, 'B', 5781716, '2024'),
(347, 6, 31, 0, 0, 0, 0, 561.539, 67.385, 85.00, 'B', 5396585, '2024'),
(348, 25, 31, 0, 0, 0, 0, 569.299, 68.316, 85.00, 'B', 5918087, '2024'),
(349, 26, 31, 0, 0, 0, 0, 550.937, 66.112, 85.00, 'B', 5926046, '2024'),
(350, 28, 31, 0, 0, 0, 0, 576.876, 69.225, 84.62, 'B', 8334823, '2024'),
(351, 31, 31, 0, 0, 0, 0, 642.626, 77.115, 71.47, 'A', 8595929, '2024'),
(352, 32, 31, 0, 0, 0, 0, 657.152, 78.858, 68.57, 'A', 5732886, '2024'),
(353, 3, 32, 0, 0, 0, 0, 597.581, 71.710, 80.48, 'B', 6599115, '2024'),
(354, 4, 32, 0, 0, 0, 0, 589.141, 70.697, 82.17, 'B', 8671911, '2024'),
(355, 6, 32, 0, 0, 0, 0, 565.513, 67.862, 85.00, 'B', 5651235, '2024'),
(356, 25, 32, 0, 0, 0, 0, 583.771, 70.053, 83.25, 'B', 5093886, '2024'),
(357, 26, 32, 0, 0, 0, 0, 552.418, 66.290, 85.00, 'B', 6816670, '2024'),
(358, 28, 32, 0, 0, 0, 0, 584.505, 70.141, 83.10, 'B', 8944168, '2024'),
(359, 31, 32, 0, 0, 0, 0, 655.503, 78.660, 68.90, 'A', 5917771, '2024'),
(360, 32, 32, 0, 0, 0, 0, 693.855, 83.263, 61.23, 'A', 5745408, '2024'),
(369, 50, 1, 0, 0, 0, 0, 566.065, 67.928, 85.00, 'B', 6297812, '2024'),
(370, 52, 1, 0, 0, 0, 0, 633.984, 76.078, 73.20, 'A', 8299370, '2024'),
(371, 53, 1, 0, 0, 0, 0, 591.086, 70.930, 81.78, 'B', 5865902, '2024'),
(372, 54, 1, 0, 0, 0, 0, 613.551, 73.626, 77.29, 'A', 5956110, '2024'),
(373, 55, 1, 0, 0, 0, 0, 590.925, 70.911, 81.81, 'B', 5174667, '2024'),
(374, 57, 1, 0, 0, 0, 0, 612.993, 73.559, 77.40, 'A', 7066041, '2024'),
(375, 58, 1, 0, 0, 0, 0, 582.237, 69.868, 83.55, 'B', 7672502, '2024'),
(376, 59, 1, 0, 0, 0, 0, 614.883, 73.786, 77.02, 'A', 6621231, '2024'),
(377, 60, 1, 0, 0, 0, 0, 570.575, 68.469, 85.00, 'B', 8761449, '2024'),
(378, 62, 1, 0, 0, 0, 0, 605.353, 72.642, 78.93, 'A', 7064251, '2024'),
(379, 63, 1, 0, 0, 0, 0, 574.886, 68.986, 85.00, 'B', 6535604, '2024'),
(380, 64, 1, 0, 0, 0, 0, 626.194, 75.143, 74.76, 'A', 8249265, '2024'),
(381, 66, 1, 0, 0, 0, 0, 608.923, 73.071, 78.22, 'A', 7816494, '2024'),
(382, 67, 1, 0, 0, 0, 0, 635.489, 76.259, 72.90, 'A', 6770963, '2024'),
(383, 72, 1, 0, 0, 0, 0, 608.113, 72.974, 78.38, 'A', 6120311, '2024'),
(384, 73, 1, 0, 0, 0, 0, 576.272, 69.153, 84.75, 'B', 7923160, '2024'),
(385, 74, 1, 0, 0, 0, 0, 638.000, 76.560, 72.40, 'B', 6977054, '2024'),
(386, 75, 1, 0, 0, 0, 0, 621.567, 74.588, 75.69, 'B', 7896570, '2024'),
(387, 76, 1, 0, 0, 0, 0, 610.444, 73.253, 77.91, 'A', 6931764, '2024'),
(388, 77, 1, 0, 0, 0, 0, 595.475, 71.457, 80.91, 'B', 8297897, '2024'),
(389, 78, 1, 0, 0, 0, 0, 566.366, 67.964, 85.00, 'B', 7593225, '2024'),
(390, 79, 1, 0, 0, 0, 0, 623.808, 74.857, 75.24, 'A', 7670062, '2024'),
(391, 80, 1, 0, 0, 0, 0, 638.877, 76.665, 72.22, 'B', 5255636, '2024'),
(392, 81, 1, 0, 0, 0, 0, 624.090, 74.891, 75.18, 'A', 7416034, '2024'),
(401, 50, 2, 0, 0, 0, 0, 579.523, 69.543, 84.10, 'B', 8583634, '2024'),
(402, 52, 2, 0, 0, 0, 0, 619.770, 74.372, 76.05, 'A', 8121477, '2024'),
(403, 53, 2, 0, 0, 0, 0, 619.304, 74.316, 76.14, 'A', 8037790, '2024'),
(404, 54, 2, 0, 0, 0, 0, 587.634, 70.516, 82.47, 'B', 7002127, '2024'),
(405, 55, 2, 0, 0, 0, 0, 573.738, 68.849, 85.00, 'B', 5559323, '2024'),
(406, 57, 2, 0, 0, 0, 0, 583.915, 70.070, 83.22, 'B', 8689369, '2024'),
(407, 58, 2, 0, 0, 0, 0, 634.924, 76.191, 73.02, 'A', 8825350, '2024'),
(408, 59, 2, 0, 0, 0, 0, 569.427, 68.331, 85.00, 'B', 8813608, '2024'),
(409, 60, 2, 0, 0, 0, 0, 579.160, 69.499, 84.17, 'B', 7439077, '2024'),
(410, 62, 2, 0, 0, 0, 0, 599.831, 71.980, 80.03, 'B', 7338377, '2024'),
(411, 63, 2, 0, 0, 0, 0, 600.387, 72.046, 79.92, 'A', 7793756, '2024'),
(412, 64, 2, 0, 0, 0, 0, 609.525, 73.143, 78.09, 'A', 6015154, '2024'),
(413, 66, 2, 0, 0, 0, 0, 626.523, 75.183, 74.70, 'A', 5610568, '2024'),
(414, 67, 2, 0, 0, 0, 0, 615.647, 73.878, 76.87, 'A', 7114200, '2024'),
(415, 72, 2, 0, 0, 0, 0, 608.483, 73.018, 78.30, 'A', 8347812, '2024'),
(416, 73, 2, 0, 0, 0, 0, 582.973, 69.957, 83.41, 'B', 5255784, '2024'),
(417, 74, 2, 0, 0, 0, 0, 579.241, 69.509, 84.15, 'B', 8463952, '2024'),
(418, 75, 2, 0, 0, 0, 0, 639.283, 76.714, 72.14, 'B', 8513775, '2024'),
(419, 76, 2, 0, 0, 0, 0, 610.395, 73.247, 77.92, 'A', 5427218, '2024'),
(420, 77, 2, 0, 0, 0, 0, 602.140, 72.257, 79.57, 'A', 7436262, '2024'),
(421, 78, 2, 0, 0, 0, 0, 586.228, 70.347, 82.75, 'B', 7399622, '2024'),
(422, 79, 2, 0, 0, 0, 0, 631.804, 75.817, 73.64, 'A', 7225511, '2024'),
(423, 80, 2, 0, 0, 0, 0, 610.399, 73.248, 77.92, 'B', 8683391, '2024'),
(424, 81, 2, 0, 0, 0, 0, 581.376, 69.765, 83.72, 'B', 5042227, '2024'),
(433, 50, 3, 0, 0, 0, 0, 625.900, 75.108, 74.82, 'A', 8222848, '2024'),
(434, 52, 3, 0, 0, 0, 0, 625.544, 75.065, 74.89, 'A', 6221441, '2024'),
(435, 53, 3, 0, 0, 0, 0, 618.386, 74.206, 76.32, 'A', 8336589, '2024'),
(436, 54, 3, 0, 0, 0, 0, 634.071, 76.089, 73.19, 'A', 8943730, '2024'),
(437, 55, 3, 0, 0, 0, 0, 616.082, 73.930, 76.78, 'A', 6559570, '2024'),
(438, 57, 3, 0, 0, 0, 0, 617.455, 74.095, 76.51, 'A', 8364305, '2024'),
(439, 58, 3, 0, 0, 0, 0, 566.819, 68.018, 85.00, 'B', 8266405, '2024'),
(440, 59, 3, 0, 0, 0, 0, 560.747, 67.290, 85.00, 'B', 6463361, '2024'),
(441, 60, 3, 0, 0, 0, 0, 598.262, 71.791, 80.35, 'B', 8973408, '2024'),
(442, 62, 3, 0, 0, 0, 0, 595.788, 71.495, 80.84, 'B', 6320646, '2024'),
(443, 63, 3, 0, 0, 0, 0, 585.923, 70.311, 82.82, 'B', 6859350, '2024'),
(444, 64, 3, 0, 0, 0, 0, 612.495, 73.499, 77.50, 'A', 7095230, '2024'),
(445, 66, 3, 0, 0, 0, 0, 590.045, 70.805, 81.99, 'B', 8337088, '2024'),
(446, 67, 3, 0, 0, 0, 0, 601.874, 72.225, 79.63, 'A', 5189520, '2024'),
(447, 72, 3, 0, 0, 0, 0, 592.396, 71.087, 81.52, 'B', 6435962, '2024'),
(448, 73, 3, 0, 0, 0, 0, 612.770, 73.532, 77.45, 'B', 5984352, '2024'),
(449, 74, 3, 0, 0, 0, 0, 631.584, 75.790, 73.68, 'B', 6540005, '2024'),
(450, 75, 3, 0, 0, 0, 0, 613.197, 73.584, 77.36, 'B', 8879651, '2024'),
(451, 76, 3, 0, 0, 0, 0, 605.595, 72.671, 78.88, 'A', 8937150, '2024'),
(452, 77, 3, 0, 0, 0, 0, 618.421, 74.210, 76.32, 'A', 8847833, '2024'),
(453, 78, 3, 0, 0, 0, 0, 602.815, 72.338, 79.44, 'A', 8669814, '2024'),
(454, 79, 3, 0, 0, 0, 0, 581.788, 69.815, 83.64, 'B', 8304740, '2024'),
(455, 80, 3, 0, 0, 0, 0, 597.897, 71.748, 80.42, 'B', 8337348, '2024'),
(456, 81, 3, 0, 0, 0, 0, 620.632, 74.476, 75.87, 'A', 5825578, '2024'),
(465, 50, 4, 0, 0, 0, 0, 574.921, 68.991, 85.00, 'B', 8986359, '2024'),
(466, 52, 4, 0, 0, 0, 0, 580.543, 69.665, 83.89, 'B', 8489683, '2024'),
(467, 53, 4, 0, 0, 0, 0, 607.690, 72.923, 78.46, 'A', 5276263, '2024'),
(468, 54, 4, 0, 0, 0, 0, 567.052, 68.046, 85.00, 'B', 7455655, '2024'),
(469, 55, 4, 0, 0, 0, 0, 561.411, 67.369, 85.00, 'B', 7612601, '2024'),
(470, 57, 4, 0, 0, 0, 0, 616.121, 73.935, 76.78, 'A', 6487889, '2024'),
(471, 58, 4, 0, 0, 0, 0, 560.249, 67.230, 85.00, 'B', 8176893, '2024'),
(472, 59, 4, 0, 0, 0, 0, 588.355, 70.603, 82.33, 'B', 5518452, '2024'),
(473, 60, 4, 0, 0, 0, 0, 594.167, 71.300, 81.17, 'B', 8251132, '2024'),
(474, 62, 4, 0, 0, 0, 0, 593.732, 71.248, 81.25, 'B', 6729174, '2024'),
(475, 63, 4, 0, 0, 0, 0, 587.141, 70.457, 82.57, 'B', 5926587, '2024'),
(476, 64, 4, 0, 0, 0, 0, 605.916, 72.710, 78.82, 'A', 5087629, '2024'),
(477, 66, 4, 0, 0, 0, 0, 620.209, 74.425, 75.96, 'A', 5240439, '2024'),
(478, 67, 4, 0, 0, 0, 0, 579.904, 69.588, 84.02, 'B', 6975569, '2024'),
(479, 72, 4, 0, 0, 0, 0, 561.674, 67.401, 85.00, 'B', 6062636, '2024'),
(480, 73, 4, 0, 0, 0, 0, 636.251, 76.350, 72.75, 'B', 5859131, '2024'),
(481, 74, 4, 0, 0, 0, 0, 584.286, 70.114, 83.14, 'B', 6840500, '2024'),
(482, 75, 4, 0, 0, 0, 0, 563.993, 67.679, 85.00, 'B', 8297826, '2024'),
(483, 76, 4, 0, 0, 0, 0, 579.391, 69.527, 84.12, 'B', 5218809, '2024'),
(484, 77, 4, 0, 0, 0, 0, 565.655, 67.879, 85.00, 'B', 6061962, '2024'),
(485, 78, 4, 0, 0, 0, 0, 590.525, 70.863, 81.90, 'B', 6588189, '2024'),
(486, 79, 4, 0, 0, 0, 0, 581.621, 69.795, 83.68, 'B', 6725810, '2024'),
(487, 80, 4, 0, 0, 0, 0, 564.092, 67.691, 85.00, 'B', 7774834, '2024'),
(488, 81, 4, 0, 0, 0, 0, 567.428, 68.091, 85.00, 'B', 5826473, '2024'),
(497, 50, 5, 0, 0, 0, 0, 561.003, 67.320, 85.00, 'B', 8905082, '2024'),
(498, 52, 5, 0, 0, 0, 0, 632.524, 75.903, 73.50, 'A', 6674287, '2024'),
(499, 53, 5, 0, 0, 0, 0, 580.173, 69.621, 83.97, 'B', 7388596, '2024'),
(500, 54, 5, 0, 0, 0, 0, 630.693, 75.683, 73.86, 'A', 5953117, '2024'),
(501, 55, 5, 0, 0, 0, 0, 580.686, 69.682, 83.86, 'B', 8181248, '2024'),
(502, 57, 5, 0, 0, 0, 0, 568.757, 68.251, 85.00, 'B', 7330455, '2024'),
(503, 58, 5, 0, 0, 0, 0, 603.953, 72.474, 79.21, 'A', 7021493, '2024'),
(504, 59, 5, 0, 0, 0, 0, 639.503, 76.740, 72.10, 'A', 6182945, '2024'),
(505, 60, 5, 0, 0, 0, 0, 623.482, 74.818, 75.30, 'A', 6950197, '2024'),
(506, 62, 5, 0, 0, 0, 0, 568.316, 68.198, 85.00, 'B', 6170994, '2024'),
(507, 63, 5, 0, 0, 0, 0, 638.914, 76.670, 72.22, 'A', 6738737, '2024'),
(508, 64, 5, 0, 0, 0, 0, 606.151, 72.738, 78.77, 'A', 7362217, '2024'),
(509, 66, 5, 0, 0, 0, 0, 630.869, 75.704, 73.83, 'A', 8121893, '2024'),
(510, 67, 5, 0, 0, 0, 0, 577.427, 69.291, 84.51, 'B', 5355532, '2024'),
(511, 72, 5, 0, 0, 0, 0, 610.174, 73.221, 77.97, 'A', 8720401, '2024'),
(512, 73, 5, 0, 0, 0, 0, 587.368, 70.484, 82.53, 'B', 6917742, '2024'),
(513, 74, 5, 0, 0, 0, 0, 622.548, 74.706, 75.49, 'B', 6323230, '2024'),
(514, 75, 5, 0, 0, 0, 0, 562.128, 67.455, 85.00, 'B', 5992094, '2024'),
(515, 76, 5, 0, 0, 0, 0, 579.974, 69.597, 84.01, 'B', 7109765, '2024'),
(516, 77, 5, 0, 0, 0, 0, 586.360, 70.363, 82.73, 'B', 8410978, '2024'),
(517, 78, 5, 0, 0, 0, 0, 582.505, 69.901, 83.50, 'B', 5427270, '2024'),
(518, 79, 5, 0, 0, 0, 0, 576.751, 69.210, 84.65, 'B', 8242575, '2024'),
(519, 80, 5, 0, 0, 0, 0, 609.366, 73.124, 78.13, 'B', 5899125, '2024'),
(520, 81, 5, 0, 0, 0, 0, 586.323, 70.359, 82.74, 'B', 7423902, '2024'),
(529, 50, 6, 0, 0, 0, 0, 605.754, 72.691, 78.85, 'A', 6394942, '2024'),
(530, 52, 6, 0, 0, 0, 0, 570.267, 68.432, 85.00, 'B', 6506005, '2024'),
(531, 53, 6, 0, 0, 0, 0, 604.892, 72.587, 79.02, 'A', 7348196, '2024'),
(532, 54, 6, 0, 0, 0, 0, 601.535, 72.184, 79.69, 'A', 7716810, '2024'),
(533, 55, 6, 0, 0, 0, 0, 634.872, 76.185, 73.03, 'A', 5878975, '2024'),
(534, 57, 6, 0, 0, 0, 0, 613.037, 73.564, 77.39, 'A', 7160626, '2024'),
(535, 58, 6, 0, 0, 0, 0, 595.581, 71.470, 80.88, 'B', 5673327, '2024'),
(536, 59, 6, 0, 0, 0, 0, 591.680, 71.002, 81.66, 'B', 7042007, '2024'),
(537, 60, 6, 0, 0, 0, 0, 615.630, 73.876, 76.87, 'A', 7664810, '2024'),
(538, 62, 6, 0, 0, 0, 0, 610.840, 73.301, 77.83, 'A', 5267057, '2024'),
(539, 63, 6, 0, 0, 0, 0, 617.838, 74.141, 76.43, 'A', 8713144, '2024'),
(540, 64, 6, 0, 0, 0, 0, 577.614, 69.314, 84.48, 'B', 7185589, '2024'),
(541, 66, 6, 0, 0, 0, 0, 591.507, 70.981, 81.70, 'B', 5464602, '2024'),
(542, 67, 6, 0, 0, 0, 0, 566.592, 67.991, 85.00, 'B', 8242138, '2024'),
(543, 72, 6, 0, 0, 0, 0, 626.290, 75.155, 74.74, 'A', 5841525, '2024'),
(544, 73, 6, 0, 0, 0, 0, 585.936, 70.312, 82.81, 'B', 5610159, '2024'),
(545, 74, 6, 0, 0, 0, 0, 630.013, 75.602, 74.00, 'B', 7393500, '2024'),
(546, 75, 6, 0, 0, 0, 0, 630.327, 75.639, 73.93, 'B', 5231413, '2024'),
(547, 76, 6, 0, 0, 0, 0, 582.889, 69.947, 83.42, 'B', 6652912, '2024'),
(548, 77, 6, 0, 0, 0, 0, 626.730, 75.208, 74.65, 'A', 5155189, '2024'),
(549, 78, 6, 0, 0, 0, 0, 613.163, 73.580, 77.37, 'A', 8672714, '2024'),
(550, 79, 6, 0, 0, 0, 0, 572.707, 68.725, 85.00, 'B', 8669678, '2024'),
(551, 80, 6, 0, 0, 0, 0, 594.262, 71.311, 81.15, 'B', 7084442, '2024'),
(552, 81, 6, 0, 0, 0, 0, 603.363, 72.404, 79.33, 'A', 7440461, '2024'),
(561, 50, 7, 0, 0, 0, 0, 560.477, 67.257, 85.00, 'B', 6381873, '2024'),
(562, 52, 7, 0, 0, 0, 0, 606.070, 72.728, 78.79, 'A', 7823254, '2024'),
(563, 53, 7, 0, 0, 0, 0, 609.499, 73.140, 78.10, 'A', 8097490, '2024'),
(564, 54, 7, 0, 0, 0, 0, 571.048, 68.526, 85.00, 'B', 8017907, '2024'),
(565, 55, 7, 0, 0, 0, 0, 577.440, 69.293, 84.51, 'B', 6594881, '2024'),
(566, 57, 7, 0, 0, 0, 0, 617.971, 74.156, 76.41, 'A', 6370318, '2024'),
(567, 58, 7, 0, 0, 0, 0, 604.194, 72.503, 79.16, 'A', 5538967, '2024'),
(568, 59, 7, 0, 0, 0, 0, 595.116, 71.414, 80.98, 'B', 5186909, '2024'),
(569, 60, 7, 0, 0, 0, 0, 635.350, 76.242, 72.93, 'A', 8030440, '2024'),
(570, 62, 7, 0, 0, 0, 0, 591.748, 71.010, 81.65, 'B', 7777227, '2024'),
(571, 63, 7, 0, 0, 0, 0, 584.732, 70.168, 83.05, 'B', 7066224, '2024'),
(572, 64, 7, 0, 0, 0, 0, 575.271, 69.033, 84.95, 'B', 7846068, '2024'),
(573, 66, 7, 0, 0, 0, 0, 629.733, 75.568, 74.05, 'A', 8558608, '2024'),
(574, 67, 7, 0, 0, 0, 0, 606.509, 72.781, 78.70, 'A', 6702656, '2024'),
(575, 72, 7, 0, 0, 0, 0, 635.451, 76.254, 72.91, 'A', 8922972, '2024'),
(576, 73, 7, 0, 0, 0, 0, 600.136, 72.016, 79.97, 'B', 8923712, '2024'),
(577, 74, 7, 0, 0, 0, 0, 611.265, 73.352, 77.75, 'B', 8029125, '2024'),
(578, 75, 7, 0, 0, 0, 0, 566.088, 67.931, 85.00, 'B', 5454911, '2024'),
(579, 76, 7, 0, 0, 0, 0, 593.526, 71.223, 81.29, 'B', 5221463, '2024'),
(580, 77, 7, 0, 0, 0, 0, 579.134, 69.496, 84.17, 'B', 5607880, '2024'),
(581, 78, 7, 0, 0, 0, 0, 580.657, 69.679, 83.87, 'B', 5148313, '2024'),
(582, 79, 7, 0, 0, 0, 0, 617.602, 74.112, 76.48, 'A', 6007556, '2024'),
(583, 80, 7, 0, 0, 0, 0, 561.984, 67.438, 85.00, 'B', 7300956, '2024'),
(584, 81, 7, 0, 0, 0, 0, 608.586, 73.030, 78.28, 'A', 5267346, '2024'),
(593, 50, 8, 0, 0, 0, 0, 608.664, 73.040, 78.27, 'A', 6426819, '2024'),
(594, 52, 8, 0, 0, 0, 0, 638.254, 76.590, 72.35, 'A', 8697119, '2024'),
(595, 53, 8, 0, 0, 0, 0, 562.749, 67.530, 85.00, 'B', 8971730, '2024'),
(596, 54, 8, 0, 0, 0, 0, 608.128, 72.975, 78.37, 'A', 6243057, '2024'),
(597, 55, 8, 0, 0, 0, 0, 576.863, 69.224, 84.63, 'B', 6618701, '2024'),
(598, 57, 8, 0, 0, 0, 0, 566.399, 67.968, 85.00, 'B', 5599735, '2024'),
(599, 58, 8, 0, 0, 0, 0, 572.765, 68.732, 85.00, 'B', 6683430, '2024'),
(600, 59, 8, 0, 0, 0, 0, 575.280, 69.034, 84.94, 'B', 8340025, '2024'),
(601, 60, 8, 0, 0, 0, 0, 585.200, 70.224, 82.96, 'B', 6848816, '2024'),
(602, 62, 8, 0, 0, 0, 0, 623.131, 74.776, 75.37, 'A', 8725285, '2024'),
(603, 63, 8, 0, 0, 0, 0, 567.141, 68.057, 85.00, 'B', 6938465, '2024'),
(604, 64, 8, 0, 0, 0, 0, 567.142, 68.057, 85.00, 'B', 6778704, '2024'),
(605, 66, 8, 0, 0, 0, 0, 573.791, 68.855, 85.00, 'B', 5625834, '2024'),
(606, 67, 8, 0, 0, 0, 0, 585.741, 70.289, 82.85, 'B', 7083895, '2024'),
(607, 72, 8, 0, 0, 0, 0, 582.624, 69.915, 83.48, 'B', 7651403, '2024'),
(608, 73, 8, 0, 0, 0, 0, 589.011, 70.681, 82.20, 'B', 6508081, '2024'),
(609, 74, 8, 0, 0, 0, 0, 634.332, 76.120, 73.13, 'B', 7905427, '2024'),
(610, 75, 8, 0, 0, 0, 0, 564.177, 67.701, 85.00, 'B', 6396877, '2024'),
(611, 76, 8, 0, 0, 0, 0, 589.626, 70.755, 82.07, 'B', 7643958, '2024'),
(612, 77, 8, 0, 0, 0, 0, 584.620, 70.154, 83.08, 'B', 7260318, '2024'),
(613, 78, 8, 0, 0, 0, 0, 589.403, 70.728, 82.12, 'B', 7140994, '2024'),
(614, 79, 8, 0, 0, 0, 0, 624.632, 74.956, 75.07, 'A', 8567435, '2024'),
(615, 80, 8, 0, 0, 0, 0, 600.899, 72.108, 79.82, 'B', 8239782, '2024'),
(616, 81, 8, 0, 0, 0, 0, 638.517, 76.622, 72.30, 'A', 6989360, '2024'),
(625, 50, 9, 0, 0, 0, 0, 568.502, 68.220, 85.00, 'B', 6850734, '2024'),
(626, 52, 9, 0, 0, 0, 0, 633.657, 76.039, 73.27, 'A', 5556349, '2024'),
(627, 53, 9, 0, 0, 0, 0, 576.993, 69.239, 84.60, 'B', 7823990, '2024'),
(628, 54, 9, 0, 0, 0, 0, 581.598, 69.792, 83.68, 'B', 6019389, '2024'),
(629, 55, 9, 0, 0, 0, 0, 579.123, 69.495, 84.18, 'B', 6765805, '2024'),
(630, 57, 9, 0, 0, 0, 0, 617.497, 74.100, 76.50, 'A', 5862376, '2024'),
(631, 58, 9, 0, 0, 0, 0, 580.678, 69.681, 83.86, 'B', 5955182, '2024'),
(632, 59, 9, 0, 0, 0, 0, 615.380, 73.846, 76.92, 'A', 6979834, '2024'),
(633, 60, 9, 0, 0, 0, 0, 628.056, 75.367, 74.39, 'A', 6212923, '2024'),
(634, 62, 9, 0, 0, 0, 0, 561.224, 67.347, 85.00, 'B', 6523929, '2024'),
(635, 63, 9, 0, 0, 0, 0, 581.830, 69.820, 83.63, 'B', 5519427, '2024'),
(636, 64, 9, 0, 0, 0, 0, 576.795, 69.215, 84.64, 'B', 7690279, '2024'),
(637, 66, 9, 0, 0, 0, 0, 615.052, 73.806, 76.99, 'A', 7945089, '2024'),
(638, 67, 9, 0, 0, 0, 0, 604.813, 72.578, 79.04, 'A', 7137207, '2024'),
(639, 72, 9, 0, 0, 0, 0, 594.810, 71.377, 81.04, 'B', 8028176, '2024'),
(640, 73, 9, 0, 0, 0, 0, 564.970, 67.796, 85.00, 'B', 6800301, '2024'),
(641, 74, 9, 0, 0, 0, 0, 613.185, 73.582, 77.36, 'B', 6520242, '2024'),
(642, 75, 9, 0, 0, 0, 0, 639.132, 76.696, 72.17, 'B', 8211770, '2024'),
(643, 76, 9, 0, 0, 0, 0, 623.775, 74.853, 75.24, 'A', 8329254, '2024'),
(644, 77, 9, 0, 0, 0, 0, 615.410, 73.849, 76.92, 'A', 7906744, '2024'),
(645, 78, 9, 0, 0, 0, 0, 572.571, 68.708, 85.00, 'B', 8525818, '2024'),
(646, 79, 9, 0, 0, 0, 0, 594.574, 71.349, 81.09, 'B', 7096331, '2024'),
(647, 80, 9, 0, 0, 0, 0, 627.031, 75.244, 74.59, 'B', 7437985, '2024'),
(648, 81, 9, 0, 0, 0, 0, 598.672, 71.841, 80.27, 'B', 5509906, '2024'),
(657, 50, 10, 0, 0, 0, 0, 635.371, 76.245, 72.93, 'A', 8859581, '2024'),
(658, 52, 10, 0, 0, 0, 0, 610.140, 73.217, 77.97, 'A', 5312358, '2024'),
(659, 53, 10, 0, 0, 0, 0, 610.681, 73.282, 77.86, 'A', 7150166, '2024'),
(660, 54, 10, 0, 0, 0, 0, 622.707, 74.725, 75.46, 'A', 7339511, '2024'),
(661, 55, 10, 0, 0, 0, 0, 570.203, 68.424, 85.00, 'B', 6502195, '2024'),
(662, 57, 10, 0, 0, 0, 0, 609.273, 73.113, 78.15, 'A', 8670028, '2024'),
(663, 58, 10, 0, 0, 0, 0, 626.226, 75.147, 74.75, 'A', 7111136, '2024'),
(664, 59, 10, 0, 0, 0, 0, 618.864, 74.264, 76.23, 'A', 6465916, '2024'),
(665, 60, 10, 0, 0, 0, 0, 569.609, 68.353, 85.00, 'B', 5632010, '2024'),
(666, 62, 10, 0, 0, 0, 0, 628.909, 75.469, 74.22, 'A', 7099773, '2024'),
(667, 63, 10, 0, 0, 0, 0, 577.291, 69.275, 84.54, 'B', 6172744, '2024'),
(668, 64, 10, 0, 0, 0, 0, 585.854, 70.302, 82.83, 'B', 8684301, '2024'),
(669, 66, 10, 0, 0, 0, 0, 610.517, 73.262, 77.90, 'A', 7977067, '2024'),
(670, 67, 10, 0, 0, 0, 0, 638.706, 76.645, 72.26, 'A', 8143483, '2024'),
(671, 72, 10, 0, 0, 0, 0, 603.546, 72.426, 79.29, 'A', 6494348, '2024'),
(672, 73, 10, 0, 0, 0, 0, 576.286, 69.154, 84.74, 'B', 5149761, '2024'),
(673, 74, 10, 0, 0, 0, 0, 570.235, 68.428, 85.00, 'B', 5318912, '2024'),
(674, 75, 10, 0, 0, 0, 0, 607.762, 72.931, 78.45, 'B', 7599214, '2024'),
(675, 76, 10, 0, 0, 0, 0, 605.912, 72.709, 78.82, 'A', 5391536, '2024'),
(676, 77, 10, 0, 0, 0, 0, 617.405, 74.089, 76.52, 'A', 5769381, '2024'),
(677, 78, 10, 0, 0, 0, 0, 634.585, 76.150, 73.08, 'A', 6890981, '2024'),
(678, 79, 10, 0, 0, 0, 0, 598.291, 71.795, 80.34, 'B', 6902054, '2024'),
(679, 80, 10, 0, 0, 0, 0, 566.570, 67.988, 85.00, 'B', 8403576, '2024'),
(680, 81, 10, 0, 0, 0, 0, 566.054, 67.927, 85.00, 'B', 8675992, '2024'),
(689, 50, 11, 0, 0, 0, 0, 604.340, 72.521, 79.13, 'A', 8669334, '2024'),
(690, 52, 11, 0, 0, 0, 0, 619.084, 74.290, 76.18, 'A', 5484230, '2024'),
(691, 53, 11, 0, 0, 0, 0, 570.862, 68.503, 85.00, 'B', 8213976, '2024'),
(692, 54, 11, 0, 0, 0, 0, 627.631, 75.316, 74.47, 'A', 6686510, '2024'),
(693, 55, 11, 0, 0, 0, 0, 598.594, 71.831, 80.28, 'B', 6069511, '2024'),
(694, 57, 11, 0, 0, 0, 0, 601.066, 72.128, 79.79, 'A', 8157769, '2024'),
(695, 58, 11, 0, 0, 0, 0, 569.116, 68.294, 85.00, 'B', 6941651, '2024'),
(696, 59, 11, 0, 0, 0, 0, 611.918, 73.430, 77.62, 'A', 7163957, '2024'),
(697, 60, 11, 0, 0, 0, 0, 600.869, 72.104, 79.83, 'A', 8156584, '2024'),
(698, 62, 11, 0, 0, 0, 0, 598.213, 71.786, 80.36, 'B', 8018345, '2024'),
(699, 63, 11, 0, 0, 0, 0, 616.238, 73.949, 76.75, 'A', 8561120, '2024'),
(700, 64, 11, 0, 0, 0, 0, 596.491, 71.579, 80.70, 'B', 6386006, '2024'),
(701, 66, 11, 0, 0, 0, 0, 585.318, 70.238, 82.94, 'B', 5489346, '2024'),
(702, 67, 11, 0, 0, 0, 0, 599.359, 71.923, 80.13, 'B', 7038156, '2024'),
(703, 72, 11, 0, 0, 0, 0, 607.408, 72.889, 78.52, 'A', 8055671, '2024'),
(704, 73, 11, 0, 0, 0, 0, 609.595, 73.151, 78.08, 'B', 7943203, '2024'),
(705, 74, 11, 0, 0, 0, 0, 578.838, 69.461, 84.23, 'B', 5819291, '2024'),
(706, 75, 11, 0, 0, 0, 0, 631.645, 75.797, 73.67, 'B', 5928997, '2024'),
(707, 76, 11, 0, 0, 0, 0, 565.823, 67.899, 85.00, 'B', 6775306, '2024'),
(708, 77, 11, 0, 0, 0, 0, 572.682, 68.722, 85.00, 'B', 7623430, '2024'),
(709, 78, 11, 0, 0, 0, 0, 593.624, 71.235, 81.28, 'B', 5662815, '2024'),
(710, 79, 11, 0, 0, 0, 0, 599.227, 71.907, 80.15, 'B', 7978203, '2024'),
(711, 80, 11, 0, 0, 0, 0, 618.363, 74.204, 76.33, 'B', 6740515, '2024'),
(712, 81, 11, 0, 0, 0, 0, 611.659, 73.399, 77.67, 'A', 7403232, '2024'),
(721, 50, 12, 0, 0, 0, 0, 612.674, 73.521, 77.47, 'A', 7630976, '2024'),
(722, 52, 12, 0, 0, 0, 0, 567.892, 68.147, 85.00, 'B', 6323711, '2024'),
(723, 53, 12, 0, 0, 0, 0, 575.684, 69.082, 84.86, 'B', 5171960, '2024'),
(724, 54, 12, 0, 0, 0, 0, 639.974, 76.797, 72.01, 'A', 5158950, '2024'),
(725, 55, 12, 0, 0, 0, 0, 637.789, 76.535, 72.44, 'A', 8610026, '2024'),
(726, 57, 12, 0, 0, 0, 0, 616.050, 73.926, 76.79, 'A', 7208970, '2024'),
(727, 58, 12, 0, 0, 0, 0, 632.964, 75.956, 73.41, 'A', 5395536, '2024'),
(728, 59, 12, 0, 0, 0, 0, 565.967, 67.916, 85.00, 'B', 8570745, '2024'),
(729, 60, 12, 0, 0, 0, 0, 600.664, 72.080, 79.87, 'A', 6503411, '2024'),
(730, 62, 12, 0, 0, 0, 0, 572.807, 68.737, 85.00, 'B', 6609091, '2024'),
(731, 63, 12, 0, 0, 0, 0, 578.107, 69.373, 84.38, 'B', 6771331, '2024'),
(732, 64, 12, 0, 0, 0, 0, 613.932, 73.672, 77.21, 'A', 8067359, '2024'),
(733, 66, 12, 0, 0, 0, 0, 595.216, 71.426, 80.96, 'B', 5681391, '2024'),
(734, 67, 12, 0, 0, 0, 0, 638.170, 76.580, 72.37, 'A', 8536200, '2024'),
(735, 72, 12, 0, 0, 0, 0, 614.506, 73.741, 77.10, 'A', 8407595, '2024'),
(736, 73, 12, 0, 0, 0, 0, 572.400, 68.688, 85.00, 'B', 8729724, '2024'),
(737, 74, 12, 0, 0, 0, 0, 625.790, 75.095, 74.84, 'B', 7736741, '2024'),
(738, 75, 12, 0, 0, 0, 0, 639.631, 76.756, 72.07, 'B', 5366076, '2024'),
(739, 76, 12, 0, 0, 0, 0, 595.641, 71.477, 80.87, 'B', 8380392, '2024'),
(740, 77, 12, 0, 0, 0, 0, 561.996, 67.439, 85.00, 'B', 8922068, '2024'),
(741, 78, 12, 0, 0, 0, 0, 564.124, 67.695, 85.00, 'B', 8230785, '2024'),
(742, 79, 12, 0, 0, 0, 0, 595.910, 71.509, 80.82, 'B', 5129169, '2024'),
(743, 80, 12, 0, 0, 0, 0, 636.422, 76.371, 72.72, 'B', 8273706, '2024'),
(744, 81, 12, 0, 0, 0, 0, 622.968, 74.756, 75.41, 'A', 7800839, '2024'),
(753, 50, 13, 0, 0, 0, 0, 602.328, 72.279, 79.53, 'A', 6355500, '2024'),
(754, 52, 13, 0, 0, 0, 0, 602.484, 72.298, 79.50, 'A', 6940215, '2024'),
(755, 53, 13, 0, 0, 0, 0, 576.893, 69.227, 84.62, 'B', 7198481, '2024'),
(756, 54, 13, 0, 0, 0, 0, 593.264, 71.192, 81.35, 'B', 8595874, '2024'),
(757, 55, 13, 0, 0, 0, 0, 608.789, 73.055, 78.24, 'A', 6291649, '2024'),
(758, 57, 13, 0, 0, 0, 0, 623.732, 74.848, 75.25, 'A', 5580292, '2024'),
(759, 58, 13, 0, 0, 0, 0, 627.799, 75.336, 74.44, 'A', 7705044, '2024'),
(760, 59, 13, 0, 0, 0, 0, 620.178, 74.421, 75.96, 'A', 5439173, '2024'),
(761, 60, 13, 0, 0, 0, 0, 638.138, 76.577, 72.37, 'A', 5180518, '2024'),
(762, 62, 13, 0, 0, 0, 0, 570.509, 68.461, 85.00, 'B', 8090372, '2024'),
(763, 63, 13, 0, 0, 0, 0, 581.562, 69.787, 83.69, 'B', 7439229, '2024'),
(764, 64, 13, 0, 0, 0, 0, 628.953, 75.474, 74.21, 'A', 7772056, '2024'),
(765, 66, 13, 0, 0, 0, 0, 589.102, 70.692, 82.18, 'B', 5309549, '2024'),
(766, 67, 13, 0, 0, 0, 0, 568.494, 68.219, 85.00, 'B', 5257130, '2024'),
(767, 72, 13, 0, 0, 0, 0, 584.985, 70.198, 83.00, 'B', 8060269, '2024'),
(768, 73, 13, 0, 0, 0, 0, 595.566, 71.468, 80.89, 'B', 8345771, '2024'),
(769, 74, 13, 0, 0, 0, 0, 633.831, 76.060, 73.23, 'B', 7109136, '2024'),
(770, 75, 13, 0, 0, 0, 0, 630.339, 75.641, 73.93, 'B', 8995573, '2024'),
(771, 76, 13, 0, 0, 0, 0, 632.349, 75.882, 73.53, 'A', 8186941, '2024'),
(772, 77, 13, 0, 0, 0, 0, 587.276, 70.473, 82.54, 'B', 5921454, '2024'),
(773, 78, 13, 0, 0, 0, 0, 579.273, 69.513, 84.15, 'B', 8072493, '2024'),
(774, 79, 13, 0, 0, 0, 0, 604.450, 72.534, 79.11, 'A', 8095132, '2024'),
(775, 80, 13, 0, 0, 0, 0, 632.416, 75.890, 73.52, 'B', 7174003, '2024'),
(776, 81, 13, 0, 0, 0, 0, 606.455, 72.775, 78.71, 'A', 7296633, '2024'),
(785, 50, 14, 0, 0, 0, 0, 582.450, 69.894, 83.51, 'B', 6838909, '2024'),
(786, 52, 14, 0, 0, 0, 0, 609.804, 73.176, 78.04, 'A', 5425822, '2024'),
(787, 53, 14, 0, 0, 0, 0, 622.332, 74.680, 75.53, 'A', 8610724, '2024'),
(788, 54, 14, 0, 0, 0, 0, 613.460, 73.615, 77.31, 'A', 7701803, '2024'),
(789, 55, 14, 0, 0, 0, 0, 562.597, 67.512, 85.00, 'B', 6432921, '2024'),
(790, 57, 14, 0, 0, 0, 0, 602.452, 72.294, 79.51, 'A', 8026686, '2024'),
(791, 58, 14, 0, 0, 0, 0, 612.559, 73.507, 77.49, 'A', 6510878, '2024'),
(792, 59, 14, 0, 0, 0, 0, 583.954, 70.074, 83.21, 'B', 7903235, '2024'),
(793, 60, 14, 0, 0, 0, 0, 595.253, 71.430, 80.95, 'B', 6612192, '2024'),
(794, 62, 14, 0, 0, 0, 0, 585.783, 70.294, 82.84, 'B', 5694466, '2024'),
(795, 63, 14, 0, 0, 0, 0, 606.986, 72.838, 78.60, 'A', 7874734, '2024'),
(796, 64, 14, 0, 0, 0, 0, 600.384, 72.046, 79.92, 'A', 6440519, '2024'),
(797, 66, 14, 0, 0, 0, 0, 591.498, 70.980, 81.70, 'B', 6812053, '2024'),
(798, 67, 14, 0, 0, 0, 0, 568.146, 68.177, 85.00, 'B', 5850003, '2024'),
(799, 72, 14, 0, 0, 0, 0, 613.528, 73.623, 77.29, 'A', 6463536, '2024'),
(800, 73, 14, 0, 0, 0, 0, 615.822, 73.899, 76.84, 'B', 7040147, '2024'),
(801, 74, 14, 0, 0, 0, 0, 564.143, 67.697, 85.00, 'B', 6421858, '2024'),
(802, 75, 14, 0, 0, 0, 0, 574.660, 68.959, 85.00, 'B', 8947774, '2024'),
(803, 76, 14, 0, 0, 0, 0, 560.280, 67.234, 85.00, 'B', 8380426, '2024'),
(804, 77, 14, 0, 0, 0, 0, 634.131, 76.096, 73.17, 'A', 7805724, '2024'),
(805, 78, 14, 0, 0, 0, 0, 613.516, 73.622, 77.30, 'A', 6878672, '2024'),
(806, 79, 14, 0, 0, 0, 0, 561.041, 67.325, 85.00, 'B', 5677065, '2024'),
(807, 80, 14, 0, 0, 0, 0, 638.178, 76.581, 72.36, 'B', 8083207, '2024'),
(808, 81, 14, 0, 0, 0, 0, 590.079, 70.810, 81.98, 'B', 6762035, '2024'),
(817, 50, 15, 0, 0, 0, 0, 571.882, 68.626, 85.00, 'B', 6587214, '2024'),
(818, 52, 15, 0, 0, 0, 0, 600.314, 72.038, 79.94, 'A', 6980278, '2024'),
(819, 53, 15, 0, 0, 0, 0, 638.447, 76.614, 72.31, 'A', 6993696, '2024'),
(820, 54, 15, 0, 0, 0, 0, 601.443, 72.173, 79.71, 'A', 6171738, '2024'),
(821, 55, 15, 0, 0, 0, 0, 622.723, 74.727, 75.46, 'A', 5628022, '2024'),
(822, 57, 15, 0, 0, 0, 0, 601.882, 72.226, 79.62, 'A', 8426397, '2024'),
(823, 58, 15, 0, 0, 0, 0, 597.595, 71.711, 80.48, 'B', 7987185, '2024'),
(824, 59, 15, 0, 0, 0, 0, 639.439, 76.733, 72.11, 'A', 5603031, '2024'),
(825, 60, 15, 0, 0, 0, 0, 589.288, 70.715, 82.14, 'B', 5263978, '2024');
INSERT INTO `universitas_jurusan` (`id`, `universitas_id`, `jurusan_id`, `daya_tampung_snbp`, `daya_tampung_snbt`, `peminat_snbp`, `peminat_snbt`, `passing_grade_snbt`, `passing_grade_snbp`, `peluang_masuk`, `akreditasi_prodi`, `biaya_kuliah`, `tahun_data`) VALUES
(826, 62, 15, 0, 0, 0, 0, 572.238, 68.669, 85.00, 'B', 8792566, '2024'),
(827, 63, 15, 0, 0, 0, 0, 598.989, 71.879, 80.20, 'B', 6186841, '2024'),
(828, 64, 15, 0, 0, 0, 0, 597.606, 71.713, 80.48, 'B', 8034898, '2024'),
(829, 66, 15, 0, 0, 0, 0, 565.055, 67.807, 85.00, 'B', 8434021, '2024'),
(830, 67, 15, 0, 0, 0, 0, 601.895, 72.227, 79.62, 'A', 7118414, '2024'),
(831, 72, 15, 0, 0, 0, 0, 631.855, 75.823, 73.63, 'A', 8131940, '2024'),
(832, 73, 15, 0, 0, 0, 0, 598.263, 71.792, 80.35, 'B', 8599010, '2024'),
(833, 74, 15, 0, 0, 0, 0, 598.851, 71.862, 80.23, 'B', 8732800, '2024'),
(834, 75, 15, 0, 0, 0, 0, 638.254, 76.590, 72.35, 'B', 6523133, '2024'),
(835, 76, 15, 0, 0, 0, 0, 633.613, 76.034, 73.28, 'A', 5476639, '2024'),
(836, 77, 15, 0, 0, 0, 0, 574.343, 68.921, 85.00, 'B', 8811992, '2024'),
(837, 78, 15, 0, 0, 0, 0, 605.052, 72.606, 78.99, 'A', 8678194, '2024'),
(838, 79, 15, 0, 0, 0, 0, 624.460, 74.935, 75.11, 'A', 5861732, '2024'),
(839, 80, 15, 0, 0, 0, 0, 568.202, 68.184, 85.00, 'B', 8239065, '2024'),
(840, 81, 15, 0, 0, 0, 0, 605.794, 72.695, 78.84, 'A', 6637073, '2024'),
(849, 50, 16, 0, 0, 0, 0, 621.655, 74.599, 75.67, 'A', 8741439, '2024'),
(850, 52, 16, 0, 0, 0, 0, 610.498, 73.260, 77.90, 'A', 8035993, '2024'),
(851, 53, 16, 0, 0, 0, 0, 612.779, 73.533, 77.44, 'A', 5325226, '2024'),
(852, 54, 16, 0, 0, 0, 0, 615.524, 73.863, 76.90, 'A', 8816158, '2024'),
(853, 55, 16, 0, 0, 0, 0, 607.681, 72.922, 78.46, 'A', 7871763, '2024'),
(854, 57, 16, 0, 0, 0, 0, 565.528, 67.863, 85.00, 'B', 5905956, '2024'),
(855, 58, 16, 0, 0, 0, 0, 567.168, 68.060, 85.00, 'B', 5966563, '2024'),
(856, 59, 16, 0, 0, 0, 0, 604.016, 72.482, 79.20, 'A', 7809764, '2024'),
(857, 60, 16, 0, 0, 0, 0, 595.340, 71.441, 80.93, 'B', 5767683, '2024'),
(858, 62, 16, 0, 0, 0, 0, 585.127, 70.215, 82.97, 'B', 7019781, '2024'),
(859, 63, 16, 0, 0, 0, 0, 568.903, 68.268, 85.00, 'B', 7408615, '2024'),
(860, 64, 16, 0, 0, 0, 0, 623.050, 74.766, 75.39, 'A', 7664073, '2024'),
(861, 66, 16, 0, 0, 0, 0, 593.053, 71.166, 81.39, 'B', 6352455, '2024'),
(862, 67, 16, 0, 0, 0, 0, 563.973, 67.677, 85.00, 'B', 6145502, '2024'),
(863, 72, 16, 0, 0, 0, 0, 591.220, 70.946, 81.76, 'B', 8769719, '2024'),
(864, 73, 16, 0, 0, 0, 0, 598.848, 71.862, 80.23, 'B', 8470867, '2024'),
(865, 74, 16, 0, 0, 0, 0, 603.520, 72.422, 79.30, 'B', 7797363, '2024'),
(866, 75, 16, 0, 0, 0, 0, 604.053, 72.486, 79.19, 'B', 8317271, '2024'),
(867, 76, 16, 0, 0, 0, 0, 618.877, 74.265, 76.22, 'A', 8446697, '2024'),
(868, 77, 16, 0, 0, 0, 0, 628.375, 75.405, 74.32, 'A', 8355350, '2024'),
(869, 78, 16, 0, 0, 0, 0, 638.334, 76.600, 72.33, 'A', 5963063, '2024'),
(870, 79, 16, 0, 0, 0, 0, 598.566, 71.828, 80.29, 'B', 7946688, '2024'),
(871, 80, 16, 0, 0, 0, 0, 562.141, 67.457, 85.00, 'B', 7600271, '2024'),
(872, 81, 16, 0, 0, 0, 0, 592.037, 71.044, 81.59, 'B', 7055374, '2024'),
(881, 50, 17, 0, 0, 0, 0, 597.495, 71.699, 80.50, 'B', 8202615, '2024'),
(882, 52, 17, 0, 0, 0, 0, 605.360, 72.643, 78.93, 'A', 8383047, '2024'),
(883, 53, 17, 0, 0, 0, 0, 616.469, 73.976, 76.71, 'A', 6100967, '2024'),
(884, 54, 17, 0, 0, 0, 0, 613.940, 73.673, 77.21, 'A', 5686020, '2024'),
(885, 55, 17, 0, 0, 0, 0, 613.489, 73.619, 77.30, 'A', 6684592, '2024'),
(886, 57, 17, 0, 0, 0, 0, 633.229, 75.987, 73.35, 'A', 8965190, '2024'),
(887, 58, 17, 0, 0, 0, 0, 581.951, 69.834, 83.61, 'B', 8065056, '2024'),
(888, 59, 17, 0, 0, 0, 0, 632.807, 75.937, 73.44, 'A', 7550739, '2024'),
(889, 60, 17, 0, 0, 0, 0, 628.823, 75.459, 74.24, 'A', 5046372, '2024'),
(890, 62, 17, 0, 0, 0, 0, 603.851, 72.462, 79.23, 'A', 6400137, '2024'),
(891, 63, 17, 0, 0, 0, 0, 617.283, 74.074, 76.54, 'A', 5622196, '2024'),
(892, 64, 17, 0, 0, 0, 0, 594.700, 71.364, 81.06, 'B', 7708653, '2024'),
(893, 66, 17, 0, 0, 0, 0, 581.904, 69.828, 83.62, 'B', 8801243, '2024'),
(894, 67, 17, 0, 0, 0, 0, 580.565, 69.668, 83.89, 'B', 8418946, '2024'),
(895, 72, 17, 0, 0, 0, 0, 564.914, 67.790, 85.00, 'B', 5395811, '2024'),
(896, 73, 17, 0, 0, 0, 0, 586.897, 70.428, 82.62, 'B', 5891584, '2024'),
(897, 74, 17, 0, 0, 0, 0, 629.659, 75.559, 74.07, 'B', 6193459, '2024'),
(898, 75, 17, 0, 0, 0, 0, 574.758, 68.971, 85.00, 'B', 6345973, '2024'),
(899, 76, 17, 0, 0, 0, 0, 617.450, 74.094, 76.51, 'A', 8842036, '2024'),
(900, 77, 17, 0, 0, 0, 0, 627.864, 75.344, 74.43, 'A', 7153871, '2024'),
(901, 78, 17, 0, 0, 0, 0, 566.573, 67.989, 85.00, 'B', 5813419, '2024'),
(902, 79, 17, 0, 0, 0, 0, 634.802, 76.176, 73.04, 'A', 6665277, '2024'),
(903, 80, 17, 0, 0, 0, 0, 607.313, 72.878, 78.54, 'B', 6971469, '2024'),
(904, 81, 17, 0, 0, 0, 0, 616.240, 73.949, 76.75, 'A', 7969172, '2024'),
(913, 50, 18, 0, 0, 0, 0, 602.320, 72.278, 79.54, 'A', 5756784, '2024'),
(914, 52, 18, 0, 0, 0, 0, 620.925, 74.511, 75.81, 'A', 7294181, '2024'),
(915, 53, 18, 0, 0, 0, 0, 586.755, 70.411, 82.65, 'B', 5459392, '2024'),
(916, 54, 18, 0, 0, 0, 0, 597.353, 71.682, 80.53, 'B', 8197759, '2024'),
(917, 55, 18, 0, 0, 0, 0, 636.172, 76.341, 72.77, 'A', 7237590, '2024'),
(918, 57, 18, 0, 0, 0, 0, 563.655, 67.639, 85.00, 'B', 7602215, '2024'),
(919, 58, 18, 0, 0, 0, 0, 561.528, 67.383, 85.00, 'B', 5807153, '2024'),
(920, 59, 18, 0, 0, 0, 0, 561.173, 67.341, 85.00, 'B', 5734367, '2024'),
(921, 60, 18, 0, 0, 0, 0, 607.969, 72.956, 78.41, 'A', 6439371, '2024'),
(922, 62, 18, 0, 0, 0, 0, 594.455, 71.335, 81.11, 'B', 5471576, '2024'),
(923, 63, 18, 0, 0, 0, 0, 636.410, 76.369, 72.72, 'A', 6083794, '2024'),
(924, 64, 18, 0, 0, 0, 0, 568.593, 68.231, 85.00, 'B', 5945973, '2024'),
(925, 66, 18, 0, 0, 0, 0, 603.647, 72.438, 79.27, 'A', 6196156, '2024'),
(926, 67, 18, 0, 0, 0, 0, 592.319, 71.078, 81.54, 'B', 7955967, '2024'),
(927, 72, 18, 0, 0, 0, 0, 610.764, 73.292, 77.85, 'A', 6468848, '2024'),
(928, 73, 18, 0, 0, 0, 0, 587.599, 70.512, 82.48, 'B', 8009376, '2024'),
(929, 74, 18, 0, 0, 0, 0, 617.221, 74.067, 76.56, 'B', 5446510, '2024'),
(930, 75, 18, 0, 0, 0, 0, 635.342, 76.241, 72.93, 'B', 6170190, '2024'),
(931, 76, 18, 0, 0, 0, 0, 608.058, 72.967, 78.39, 'A', 6919418, '2024'),
(932, 77, 18, 0, 0, 0, 0, 638.124, 76.575, 72.38, 'A', 7353604, '2024'),
(933, 78, 18, 0, 0, 0, 0, 594.230, 71.308, 81.15, 'B', 8022536, '2024'),
(934, 79, 18, 0, 0, 0, 0, 574.694, 68.963, 85.00, 'B', 8311636, '2024'),
(935, 80, 18, 0, 0, 0, 0, 561.192, 67.343, 85.00, 'B', 6128247, '2024'),
(936, 81, 18, 0, 0, 0, 0, 617.319, 74.078, 76.54, 'A', 5274304, '2024'),
(945, 50, 19, 0, 0, 0, 0, 598.741, 71.849, 80.25, 'B', 6199997, '2024'),
(946, 52, 19, 0, 0, 0, 0, 586.738, 70.409, 82.65, 'B', 7649807, '2024'),
(947, 53, 19, 0, 0, 0, 0, 589.476, 70.737, 82.10, 'B', 7306797, '2024'),
(948, 54, 19, 0, 0, 0, 0, 591.356, 70.963, 81.73, 'B', 5258290, '2024'),
(949, 55, 19, 0, 0, 0, 0, 594.257, 71.311, 81.15, 'B', 8477475, '2024'),
(950, 57, 19, 0, 0, 0, 0, 596.813, 71.618, 80.64, 'B', 5438438, '2024'),
(951, 58, 19, 0, 0, 0, 0, 626.893, 75.227, 74.62, 'A', 8750794, '2024'),
(952, 59, 19, 0, 0, 0, 0, 607.216, 72.866, 78.56, 'A', 7275159, '2024'),
(953, 60, 19, 0, 0, 0, 0, 616.325, 73.959, 76.73, 'A', 5827264, '2024'),
(954, 62, 19, 0, 0, 0, 0, 572.129, 68.656, 85.00, 'B', 7410955, '2024'),
(955, 63, 19, 0, 0, 0, 0, 572.787, 68.734, 85.00, 'B', 7348692, '2024'),
(956, 64, 19, 0, 0, 0, 0, 561.316, 67.358, 85.00, 'B', 7675100, '2024'),
(957, 66, 19, 0, 0, 0, 0, 598.453, 71.814, 80.31, 'B', 5259851, '2024'),
(958, 67, 19, 0, 0, 0, 0, 576.837, 69.220, 84.63, 'B', 7428324, '2024'),
(959, 72, 19, 0, 0, 0, 0, 633.632, 76.036, 73.27, 'A', 6012163, '2024'),
(960, 73, 19, 0, 0, 0, 0, 584.505, 70.141, 83.10, 'B', 6009813, '2024'),
(961, 74, 19, 0, 0, 0, 0, 577.099, 69.252, 84.58, 'B', 8154448, '2024'),
(962, 75, 19, 0, 0, 0, 0, 595.547, 71.466, 80.89, 'B', 7021040, '2024'),
(963, 76, 19, 0, 0, 0, 0, 629.275, 75.513, 74.14, 'A', 5765923, '2024'),
(964, 77, 19, 0, 0, 0, 0, 620.646, 74.478, 75.87, 'A', 8653577, '2024'),
(965, 78, 19, 0, 0, 0, 0, 579.526, 69.543, 84.09, 'B', 7221824, '2024'),
(966, 79, 19, 0, 0, 0, 0, 572.350, 68.682, 85.00, 'B', 6865693, '2024'),
(967, 80, 19, 0, 0, 0, 0, 573.917, 68.870, 85.00, 'B', 5942143, '2024'),
(968, 81, 19, 0, 0, 0, 0, 628.184, 75.382, 74.36, 'A', 6935209, '2024'),
(977, 50, 20, 0, 0, 0, 0, 591.559, 70.987, 81.69, 'B', 8463316, '2024'),
(978, 52, 20, 0, 0, 0, 0, 628.363, 75.404, 74.33, 'A', 8063000, '2024'),
(979, 53, 20, 0, 0, 0, 0, 601.569, 72.188, 79.69, 'A', 5442302, '2024'),
(980, 54, 20, 0, 0, 0, 0, 628.964, 75.476, 74.21, 'A', 7390005, '2024'),
(981, 55, 20, 0, 0, 0, 0, 591.280, 70.954, 81.74, 'B', 5251599, '2024'),
(982, 57, 20, 0, 0, 0, 0, 636.538, 76.385, 72.69, 'A', 7843556, '2024'),
(983, 58, 20, 0, 0, 0, 0, 562.952, 67.554, 85.00, 'B', 6612363, '2024'),
(984, 59, 20, 0, 0, 0, 0, 634.275, 76.113, 73.15, 'A', 5218734, '2024'),
(985, 60, 20, 0, 0, 0, 0, 605.259, 72.631, 78.95, 'A', 5756897, '2024'),
(986, 62, 20, 0, 0, 0, 0, 583.711, 70.045, 83.26, 'B', 8159046, '2024'),
(987, 63, 20, 0, 0, 0, 0, 609.640, 73.157, 78.07, 'A', 7396246, '2024'),
(988, 64, 20, 0, 0, 0, 0, 612.509, 73.501, 77.50, 'A', 5841826, '2024'),
(989, 66, 20, 0, 0, 0, 0, 637.124, 76.455, 72.58, 'A', 7146869, '2024'),
(990, 67, 20, 0, 0, 0, 0, 611.182, 73.342, 77.76, 'A', 6287320, '2024'),
(991, 72, 20, 0, 0, 0, 0, 567.920, 68.150, 85.00, 'B', 5738783, '2024'),
(992, 73, 20, 0, 0, 0, 0, 582.030, 69.844, 83.59, 'B', 7240820, '2024'),
(993, 74, 20, 0, 0, 0, 0, 613.629, 73.635, 77.27, 'B', 8112713, '2024'),
(994, 75, 20, 0, 0, 0, 0, 612.388, 73.487, 77.52, 'B', 5924808, '2024'),
(995, 76, 20, 0, 0, 0, 0, 562.026, 67.443, 85.00, 'B', 5423958, '2024'),
(996, 77, 20, 0, 0, 0, 0, 575.599, 69.072, 84.88, 'B', 8430799, '2024'),
(997, 78, 20, 0, 0, 0, 0, 616.933, 74.032, 76.61, 'A', 7005826, '2024'),
(998, 79, 20, 0, 0, 0, 0, 596.962, 71.635, 80.61, 'B', 8118166, '2024'),
(999, 80, 20, 0, 0, 0, 0, 630.788, 75.695, 73.84, 'B', 6505640, '2024'),
(1000, 81, 20, 0, 0, 0, 0, 598.230, 71.788, 80.35, 'B', 5109930, '2024'),
(1009, 50, 21, 0, 0, 0, 0, 570.269, 68.432, 85.00, 'B', 7737812, '2024'),
(1010, 52, 21, 0, 0, 0, 0, 569.888, 68.387, 85.00, 'B', 7004502, '2024'),
(1011, 53, 21, 0, 0, 0, 0, 613.569, 73.628, 77.29, 'A', 5682846, '2024'),
(1012, 54, 21, 0, 0, 0, 0, 573.121, 68.775, 85.00, 'B', 6855776, '2024'),
(1013, 55, 21, 0, 0, 0, 0, 618.277, 74.193, 76.34, 'A', 6432946, '2024'),
(1014, 57, 21, 0, 0, 0, 0, 633.004, 75.960, 73.40, 'A', 6060559, '2024'),
(1015, 58, 21, 0, 0, 0, 0, 561.023, 67.323, 85.00, 'B', 7391693, '2024'),
(1016, 59, 21, 0, 0, 0, 0, 613.429, 73.611, 77.31, 'A', 8866810, '2024'),
(1017, 60, 21, 0, 0, 0, 0, 576.455, 69.175, 84.71, 'B', 5566374, '2024'),
(1018, 62, 21, 0, 0, 0, 0, 610.181, 73.222, 77.96, 'A', 8545541, '2024'),
(1019, 63, 21, 0, 0, 0, 0, 629.420, 75.530, 74.12, 'A', 5754883, '2024'),
(1020, 64, 21, 0, 0, 0, 0, 607.362, 72.883, 78.53, 'A', 5163674, '2024'),
(1021, 66, 21, 0, 0, 0, 0, 623.588, 74.831, 75.28, 'A', 5568637, '2024'),
(1022, 67, 21, 0, 0, 0, 0, 614.372, 73.725, 77.13, 'A', 8888799, '2024'),
(1023, 72, 21, 0, 0, 0, 0, 560.061, 67.207, 85.00, 'B', 5176434, '2024'),
(1024, 73, 21, 0, 0, 0, 0, 587.953, 70.554, 82.41, 'B', 7337673, '2024'),
(1025, 74, 21, 0, 0, 0, 0, 627.090, 75.251, 74.58, 'B', 8439256, '2024'),
(1026, 75, 21, 0, 0, 0, 0, 612.648, 73.518, 77.47, 'B', 5827942, '2024'),
(1027, 76, 21, 0, 0, 0, 0, 632.081, 75.850, 73.58, 'A', 5016249, '2024'),
(1028, 77, 21, 0, 0, 0, 0, 604.327, 72.519, 79.13, 'A', 5865254, '2024'),
(1029, 78, 21, 0, 0, 0, 0, 579.680, 69.562, 84.06, 'B', 6057221, '2024'),
(1030, 79, 21, 0, 0, 0, 0, 575.304, 69.036, 84.94, 'B', 5795566, '2024'),
(1031, 80, 21, 0, 0, 0, 0, 574.764, 68.972, 85.00, 'B', 5257170, '2024'),
(1032, 81, 21, 0, 0, 0, 0, 615.845, 73.901, 76.83, 'A', 8275114, '2024'),
(1041, 50, 22, 0, 0, 0, 0, 611.559, 73.387, 77.69, 'A', 5599565, '2024'),
(1042, 52, 22, 0, 0, 0, 0, 636.419, 76.370, 72.72, 'A', 6927720, '2024'),
(1043, 53, 22, 0, 0, 0, 0, 572.109, 68.653, 85.00, 'B', 7988193, '2024'),
(1044, 54, 22, 0, 0, 0, 0, 579.198, 69.504, 84.16, 'B', 5783953, '2024'),
(1045, 55, 22, 0, 0, 0, 0, 576.293, 69.155, 84.74, 'B', 5045139, '2024'),
(1046, 57, 22, 0, 0, 0, 0, 565.211, 67.825, 85.00, 'B', 5330726, '2024'),
(1047, 58, 22, 0, 0, 0, 0, 601.332, 72.160, 79.73, 'A', 7497064, '2024'),
(1048, 59, 22, 0, 0, 0, 0, 605.997, 72.720, 78.80, 'A', 7902212, '2024'),
(1049, 60, 22, 0, 0, 0, 0, 604.412, 72.529, 79.12, 'A', 7510061, '2024'),
(1050, 62, 22, 0, 0, 0, 0, 629.168, 75.500, 74.17, 'A', 6414044, '2024'),
(1051, 63, 22, 0, 0, 0, 0, 636.301, 76.356, 72.74, 'A', 5627102, '2024'),
(1052, 64, 22, 0, 0, 0, 0, 579.119, 69.494, 84.18, 'B', 6254894, '2024'),
(1053, 66, 22, 0, 0, 0, 0, 637.407, 76.489, 72.52, 'A', 8918467, '2024'),
(1054, 67, 22, 0, 0, 0, 0, 569.885, 68.386, 85.00, 'B', 8618144, '2024'),
(1055, 72, 22, 0, 0, 0, 0, 591.028, 70.923, 81.79, 'B', 7792540, '2024'),
(1056, 73, 22, 0, 0, 0, 0, 595.919, 71.510, 80.82, 'B', 6348004, '2024'),
(1057, 74, 22, 0, 0, 0, 0, 576.202, 69.144, 84.76, 'B', 7699539, '2024'),
(1058, 75, 22, 0, 0, 0, 0, 595.380, 71.446, 80.92, 'B', 7722059, '2024'),
(1059, 76, 22, 0, 0, 0, 0, 632.563, 75.908, 73.49, 'A', 8870845, '2024'),
(1060, 77, 22, 0, 0, 0, 0, 571.852, 68.622, 85.00, 'B', 6064906, '2024'),
(1061, 78, 22, 0, 0, 0, 0, 622.251, 74.670, 75.55, 'A', 8271823, '2024'),
(1062, 79, 22, 0, 0, 0, 0, 614.235, 73.708, 77.15, 'A', 5685191, '2024'),
(1063, 80, 22, 0, 0, 0, 0, 608.966, 73.076, 78.21, 'B', 7106111, '2024'),
(1064, 81, 22, 0, 0, 0, 0, 570.904, 68.508, 85.00, 'B', 5351013, '2024'),
(1073, 50, 23, 0, 0, 0, 0, 581.808, 69.817, 83.64, 'B', 8134666, '2024'),
(1074, 52, 23, 0, 0, 0, 0, 636.583, 76.390, 72.68, 'A', 6393728, '2024'),
(1075, 53, 23, 0, 0, 0, 0, 562.422, 67.491, 85.00, 'B', 8585928, '2024'),
(1076, 54, 23, 0, 0, 0, 0, 597.493, 71.699, 80.50, 'B', 8293363, '2024'),
(1077, 55, 23, 0, 0, 0, 0, 618.876, 74.265, 76.22, 'A', 8387433, '2024'),
(1078, 57, 23, 0, 0, 0, 0, 583.499, 70.020, 83.30, 'B', 5018630, '2024'),
(1079, 58, 23, 0, 0, 0, 0, 635.521, 76.263, 72.90, 'A', 5870203, '2024'),
(1080, 59, 23, 0, 0, 0, 0, 586.036, 70.324, 82.79, 'B', 8513867, '2024'),
(1081, 60, 23, 0, 0, 0, 0, 618.949, 74.274, 76.21, 'A', 6883580, '2024'),
(1082, 62, 23, 0, 0, 0, 0, 597.444, 71.693, 80.51, 'B', 8747078, '2024'),
(1083, 63, 23, 0, 0, 0, 0, 618.682, 74.242, 76.26, 'A', 8014477, '2024'),
(1084, 64, 23, 0, 0, 0, 0, 611.574, 73.389, 77.69, 'A', 5751465, '2024'),
(1085, 66, 23, 0, 0, 0, 0, 636.270, 76.352, 72.75, 'A', 6396552, '2024'),
(1086, 67, 23, 0, 0, 0, 0, 588.156, 70.579, 82.37, 'B', 8015280, '2024'),
(1087, 72, 23, 0, 0, 0, 0, 578.829, 69.459, 84.23, 'B', 7119220, '2024'),
(1088, 73, 23, 0, 0, 0, 0, 567.935, 68.152, 85.00, 'B', 8856797, '2024'),
(1089, 74, 23, 0, 0, 0, 0, 580.629, 69.676, 83.87, 'B', 6031531, '2024'),
(1090, 75, 23, 0, 0, 0, 0, 614.411, 73.729, 77.12, 'B', 6768940, '2024'),
(1091, 76, 23, 0, 0, 0, 0, 586.439, 70.373, 82.71, 'B', 8646118, '2024'),
(1092, 77, 23, 0, 0, 0, 0, 569.371, 68.324, 85.00, 'B', 8190698, '2024'),
(1093, 78, 23, 0, 0, 0, 0, 562.882, 67.546, 85.00, 'B', 8883579, '2024'),
(1094, 79, 23, 0, 0, 0, 0, 583.195, 69.983, 83.36, 'B', 5760135, '2024'),
(1095, 80, 23, 0, 0, 0, 0, 599.177, 71.901, 80.16, 'B', 6557789, '2024'),
(1096, 81, 23, 0, 0, 0, 0, 617.271, 74.073, 76.55, 'A', 5826479, '2024'),
(1105, 50, 24, 0, 0, 0, 0, 639.588, 76.751, 72.08, 'A', 8064216, '2024'),
(1106, 52, 24, 0, 0, 0, 0, 585.028, 70.203, 82.99, 'B', 8196266, '2024'),
(1107, 53, 24, 0, 0, 0, 0, 590.839, 70.901, 81.83, 'B', 6096459, '2024'),
(1108, 54, 24, 0, 0, 0, 0, 595.729, 71.487, 80.85, 'B', 8618314, '2024'),
(1109, 55, 24, 0, 0, 0, 0, 614.578, 73.749, 77.08, 'A', 8159719, '2024'),
(1110, 57, 24, 0, 0, 0, 0, 592.036, 71.044, 81.59, 'B', 6737131, '2024'),
(1111, 58, 24, 0, 0, 0, 0, 609.139, 73.097, 78.17, 'A', 6111284, '2024'),
(1112, 59, 24, 0, 0, 0, 0, 582.844, 69.941, 83.43, 'B', 5521806, '2024'),
(1113, 60, 24, 0, 0, 0, 0, 587.703, 70.524, 82.46, 'B', 6148817, '2024'),
(1114, 62, 24, 0, 0, 0, 0, 593.427, 71.211, 81.31, 'B', 7791078, '2024'),
(1115, 63, 24, 0, 0, 0, 0, 571.682, 68.602, 85.00, 'B', 5842652, '2024'),
(1116, 64, 24, 0, 0, 0, 0, 581.573, 69.789, 83.69, 'B', 8436097, '2024'),
(1117, 66, 24, 0, 0, 0, 0, 638.497, 76.620, 72.30, 'A', 6134975, '2024'),
(1118, 67, 24, 0, 0, 0, 0, 569.080, 68.290, 85.00, 'B', 6899674, '2024'),
(1119, 72, 24, 0, 0, 0, 0, 561.007, 67.321, 85.00, 'B', 8878293, '2024'),
(1120, 73, 24, 0, 0, 0, 0, 576.874, 69.225, 84.63, 'B', 8111389, '2024'),
(1121, 74, 24, 0, 0, 0, 0, 623.478, 74.817, 75.30, 'B', 7270713, '2024'),
(1122, 75, 24, 0, 0, 0, 0, 600.835, 72.100, 79.83, 'B', 8425534, '2024'),
(1123, 76, 24, 0, 0, 0, 0, 609.028, 73.083, 78.19, 'A', 5455761, '2024'),
(1124, 77, 24, 0, 0, 0, 0, 576.759, 69.211, 84.65, 'B', 5581551, '2024'),
(1125, 78, 24, 0, 0, 0, 0, 626.068, 75.128, 74.79, 'A', 8589573, '2024'),
(1126, 79, 24, 0, 0, 0, 0, 576.707, 69.205, 84.66, 'B', 8304280, '2024'),
(1127, 80, 24, 0, 0, 0, 0, 576.341, 69.161, 84.73, 'B', 5838476, '2024'),
(1128, 81, 24, 0, 0, 0, 0, 595.302, 71.436, 80.94, 'B', 7883970, '2024'),
(1137, 50, 25, 0, 0, 0, 0, 618.165, 74.180, 76.37, 'A', 6869324, '2024'),
(1138, 52, 25, 0, 0, 0, 0, 604.921, 72.591, 79.02, 'A', 7486740, '2024'),
(1139, 53, 25, 0, 0, 0, 0, 607.290, 72.875, 78.54, 'A', 7658015, '2024'),
(1140, 54, 25, 0, 0, 0, 0, 621.557, 74.587, 75.69, 'A', 7233591, '2024'),
(1141, 55, 25, 0, 0, 0, 0, 569.458, 68.335, 85.00, 'B', 6587523, '2024'),
(1142, 57, 25, 0, 0, 0, 0, 631.324, 75.759, 73.74, 'A', 6472384, '2024'),
(1143, 58, 25, 0, 0, 0, 0, 564.148, 67.698, 85.00, 'B', 6603930, '2024'),
(1144, 59, 25, 0, 0, 0, 0, 583.456, 70.015, 83.31, 'B', 6502162, '2024'),
(1145, 60, 25, 0, 0, 0, 0, 618.625, 74.235, 76.27, 'A', 5058915, '2024'),
(1146, 62, 25, 0, 0, 0, 0, 639.682, 76.762, 72.06, 'A', 5181400, '2024'),
(1147, 63, 25, 0, 0, 0, 0, 599.648, 71.958, 80.07, 'B', 6086342, '2024'),
(1148, 64, 25, 0, 0, 0, 0, 621.515, 74.582, 75.70, 'A', 7363223, '2024'),
(1149, 66, 25, 0, 0, 0, 0, 585.538, 70.265, 82.89, 'B', 5322701, '2024'),
(1150, 67, 25, 0, 0, 0, 0, 584.508, 70.141, 83.10, 'B', 6495332, '2024'),
(1151, 72, 25, 0, 0, 0, 0, 637.142, 76.457, 72.57, 'A', 5106272, '2024'),
(1152, 73, 25, 0, 0, 0, 0, 628.973, 75.477, 74.21, 'B', 6146472, '2024'),
(1153, 74, 25, 0, 0, 0, 0, 573.794, 68.855, 85.00, 'B', 5075074, '2024'),
(1154, 75, 25, 0, 0, 0, 0, 585.518, 70.262, 82.90, 'B', 8568953, '2024'),
(1155, 76, 25, 0, 0, 0, 0, 610.497, 73.260, 77.90, 'A', 7993791, '2024'),
(1156, 77, 25, 0, 0, 0, 0, 605.070, 72.608, 78.99, 'A', 5879365, '2024'),
(1157, 78, 25, 0, 0, 0, 0, 585.057, 70.207, 82.99, 'B', 7675023, '2024'),
(1158, 79, 25, 0, 0, 0, 0, 576.879, 69.226, 84.62, 'B', 8037299, '2024'),
(1159, 80, 25, 0, 0, 0, 0, 599.062, 71.887, 80.19, 'B', 5020395, '2024'),
(1160, 81, 25, 0, 0, 0, 0, 574.338, 68.921, 85.00, 'B', 6524547, '2024'),
(1169, 50, 26, 0, 0, 0, 0, 607.993, 72.959, 78.40, 'A', 8916111, '2024'),
(1170, 52, 26, 0, 0, 0, 0, 590.452, 70.854, 81.91, 'B', 7899222, '2024'),
(1171, 53, 26, 0, 0, 0, 0, 607.804, 72.936, 78.44, 'A', 7051649, '2024'),
(1172, 54, 26, 0, 0, 0, 0, 625.055, 75.007, 74.99, 'A', 6979458, '2024'),
(1173, 55, 26, 0, 0, 0, 0, 569.579, 68.349, 85.00, 'B', 6547004, '2024'),
(1174, 57, 26, 0, 0, 0, 0, 620.700, 74.484, 75.86, 'A', 6708832, '2024'),
(1175, 58, 26, 0, 0, 0, 0, 581.421, 69.771, 83.72, 'B', 6640209, '2024'),
(1176, 59, 26, 0, 0, 0, 0, 595.149, 71.418, 80.97, 'B', 7912173, '2024'),
(1177, 60, 26, 0, 0, 0, 0, 582.488, 69.899, 83.50, 'B', 6783277, '2024'),
(1178, 62, 26, 0, 0, 0, 0, 605.417, 72.650, 78.92, 'A', 8067312, '2024'),
(1179, 63, 26, 0, 0, 0, 0, 618.648, 74.238, 76.27, 'A', 7894338, '2024'),
(1180, 64, 26, 0, 0, 0, 0, 621.850, 74.622, 75.63, 'A', 7984010, '2024'),
(1181, 66, 26, 0, 0, 0, 0, 604.473, 72.537, 79.11, 'A', 8330579, '2024'),
(1182, 67, 26, 0, 0, 0, 0, 620.750, 74.490, 75.85, 'A', 5555803, '2024'),
(1183, 72, 26, 0, 0, 0, 0, 591.350, 70.962, 81.73, 'B', 8279370, '2024'),
(1184, 73, 26, 0, 0, 0, 0, 566.333, 67.960, 85.00, 'B', 5909091, '2024'),
(1185, 74, 26, 0, 0, 0, 0, 609.071, 73.088, 78.19, 'B', 5050115, '2024'),
(1186, 75, 26, 0, 0, 0, 0, 601.597, 72.192, 79.68, 'B', 5621478, '2024'),
(1187, 76, 26, 0, 0, 0, 0, 638.360, 76.603, 72.33, 'A', 6659545, '2024'),
(1188, 77, 26, 0, 0, 0, 0, 599.030, 71.884, 80.19, 'B', 7154017, '2024'),
(1189, 78, 26, 0, 0, 0, 0, 584.350, 70.122, 83.13, 'B', 8138281, '2024'),
(1190, 79, 26, 0, 0, 0, 0, 595.901, 71.508, 80.82, 'B', 8239656, '2024'),
(1191, 80, 26, 0, 0, 0, 0, 616.106, 73.933, 76.78, 'B', 8146711, '2024'),
(1192, 81, 26, 0, 0, 0, 0, 595.214, 71.426, 80.96, 'B', 5692875, '2024'),
(1201, 50, 27, 0, 0, 0, 0, 579.223, 69.507, 84.16, 'B', 7580706, '2024'),
(1202, 52, 27, 0, 0, 0, 0, 589.329, 70.719, 82.13, 'B', 5675449, '2024'),
(1203, 53, 27, 0, 0, 0, 0, 577.006, 69.241, 84.60, 'B', 6826235, '2024'),
(1204, 54, 27, 0, 0, 0, 0, 601.227, 72.147, 79.75, 'A', 5186657, '2024'),
(1205, 55, 27, 0, 0, 0, 0, 563.508, 67.621, 85.00, 'B', 5689957, '2024'),
(1206, 57, 27, 0, 0, 0, 0, 580.833, 69.700, 83.83, 'B', 8554211, '2024'),
(1207, 58, 27, 0, 0, 0, 0, 563.241, 67.589, 85.00, 'B', 8545638, '2024'),
(1208, 59, 27, 0, 0, 0, 0, 627.936, 75.352, 74.41, 'A', 7215182, '2024'),
(1209, 60, 27, 0, 0, 0, 0, 615.378, 73.845, 76.92, 'A', 8032568, '2024'),
(1210, 62, 27, 0, 0, 0, 0, 588.988, 70.679, 82.20, 'B', 5237000, '2024'),
(1211, 63, 27, 0, 0, 0, 0, 618.974, 74.277, 76.21, 'A', 8714500, '2024'),
(1212, 64, 27, 0, 0, 0, 0, 613.579, 73.629, 77.28, 'A', 8271350, '2024'),
(1213, 66, 27, 0, 0, 0, 0, 630.512, 75.661, 73.90, 'A', 8836345, '2024'),
(1214, 67, 27, 0, 0, 0, 0, 561.072, 67.329, 85.00, 'B', 8489176, '2024'),
(1215, 72, 27, 0, 0, 0, 0, 634.784, 76.174, 73.04, 'A', 5438495, '2024'),
(1216, 73, 27, 0, 0, 0, 0, 609.418, 73.130, 78.12, 'B', 7383644, '2024'),
(1217, 74, 27, 0, 0, 0, 0, 597.739, 71.729, 80.45, 'B', 6873845, '2024'),
(1218, 75, 27, 0, 0, 0, 0, 586.978, 70.437, 82.60, 'B', 8675555, '2024'),
(1219, 76, 27, 0, 0, 0, 0, 579.982, 69.598, 84.00, 'B', 7682684, '2024'),
(1220, 77, 27, 0, 0, 0, 0, 579.545, 69.545, 84.09, 'B', 6028364, '2024'),
(1221, 78, 27, 0, 0, 0, 0, 602.206, 72.265, 79.56, 'A', 7815834, '2024'),
(1222, 79, 27, 0, 0, 0, 0, 578.544, 69.425, 84.29, 'B', 8877609, '2024'),
(1223, 80, 27, 0, 0, 0, 0, 568.900, 68.268, 85.00, 'B', 5356540, '2024'),
(1224, 81, 27, 0, 0, 0, 0, 572.750, 68.730, 85.00, 'B', 6402222, '2024'),
(1233, 50, 28, 0, 0, 0, 0, 564.197, 67.704, 85.00, 'B', 7374248, '2024'),
(1234, 52, 28, 0, 0, 0, 0, 625.282, 75.034, 74.94, 'A', 5789825, '2024'),
(1235, 53, 28, 0, 0, 0, 0, 590.624, 70.875, 81.88, 'B', 8423470, '2024'),
(1236, 54, 28, 0, 0, 0, 0, 572.504, 68.700, 85.00, 'B', 5801408, '2024'),
(1237, 55, 28, 0, 0, 0, 0, 624.818, 74.978, 75.04, 'A', 6568813, '2024'),
(1238, 57, 28, 0, 0, 0, 0, 616.224, 73.947, 76.76, 'A', 7864546, '2024'),
(1239, 58, 28, 0, 0, 0, 0, 575.804, 69.096, 84.84, 'B', 7804179, '2024'),
(1240, 59, 28, 0, 0, 0, 0, 597.072, 71.649, 80.59, 'B', 7005631, '2024'),
(1241, 60, 28, 0, 0, 0, 0, 581.200, 69.744, 83.76, 'B', 5183709, '2024'),
(1242, 62, 28, 0, 0, 0, 0, 580.395, 69.647, 83.92, 'B', 7242566, '2024'),
(1243, 63, 28, 0, 0, 0, 0, 607.642, 72.917, 78.47, 'A', 6418762, '2024'),
(1244, 64, 28, 0, 0, 0, 0, 590.755, 70.891, 81.85, 'B', 6844865, '2024'),
(1245, 66, 28, 0, 0, 0, 0, 589.493, 70.739, 82.10, 'B', 7949617, '2024'),
(1246, 67, 28, 0, 0, 0, 0, 568.202, 68.184, 85.00, 'B', 5483081, '2024'),
(1247, 72, 28, 0, 0, 0, 0, 628.132, 75.376, 74.37, 'A', 8314256, '2024'),
(1248, 73, 28, 0, 0, 0, 0, 619.558, 74.347, 76.09, 'B', 7716882, '2024'),
(1249, 74, 28, 0, 0, 0, 0, 601.674, 72.201, 79.67, 'B', 6421784, '2024'),
(1250, 75, 28, 0, 0, 0, 0, 606.839, 72.821, 78.63, 'B', 5618796, '2024'),
(1251, 76, 28, 0, 0, 0, 0, 585.074, 70.209, 82.99, 'B', 7248191, '2024'),
(1252, 77, 28, 0, 0, 0, 0, 626.197, 75.144, 74.76, 'A', 5803242, '2024'),
(1253, 78, 28, 0, 0, 0, 0, 633.137, 75.976, 73.37, 'A', 6507304, '2024'),
(1254, 79, 28, 0, 0, 0, 0, 624.664, 74.960, 75.07, 'A', 6728203, '2024'),
(1255, 80, 28, 0, 0, 0, 0, 574.031, 68.884, 85.00, 'B', 7009943, '2024'),
(1256, 81, 28, 0, 0, 0, 0, 598.258, 71.791, 80.35, 'B', 6859459, '2024'),
(1265, 50, 29, 0, 0, 0, 0, 599.947, 71.994, 80.01, 'B', 6067887, '2024'),
(1266, 52, 29, 0, 0, 0, 0, 600.562, 72.067, 79.89, 'A', 6074421, '2024'),
(1267, 53, 29, 0, 0, 0, 0, 597.981, 71.758, 80.40, 'B', 6302872, '2024'),
(1268, 54, 29, 0, 0, 0, 0, 571.244, 68.549, 85.00, 'B', 7510413, '2024'),
(1269, 55, 29, 0, 0, 0, 0, 574.586, 68.950, 85.00, 'B', 6761634, '2024'),
(1270, 57, 29, 0, 0, 0, 0, 577.708, 69.325, 84.46, 'B', 7369155, '2024'),
(1271, 58, 29, 0, 0, 0, 0, 631.715, 75.806, 73.66, 'A', 7183245, '2024'),
(1272, 59, 29, 0, 0, 0, 0, 625.551, 75.066, 74.89, 'A', 7134071, '2024'),
(1273, 60, 29, 0, 0, 0, 0, 574.179, 68.901, 85.00, 'B', 8404884, '2024'),
(1274, 62, 29, 0, 0, 0, 0, 603.784, 72.454, 79.24, 'A', 6525028, '2024'),
(1275, 63, 29, 0, 0, 0, 0, 630.892, 75.707, 73.82, 'A', 5428129, '2024'),
(1276, 64, 29, 0, 0, 0, 0, 572.985, 68.758, 85.00, 'B', 7095906, '2024'),
(1277, 66, 29, 0, 0, 0, 0, 580.387, 69.646, 83.92, 'B', 8336902, '2024'),
(1278, 67, 29, 0, 0, 0, 0, 586.821, 70.419, 82.64, 'B', 8629689, '2024'),
(1279, 72, 29, 0, 0, 0, 0, 584.119, 70.094, 83.18, 'B', 7451955, '2024'),
(1280, 73, 29, 0, 0, 0, 0, 561.448, 67.374, 85.00, 'B', 5342913, '2024'),
(1281, 74, 29, 0, 0, 0, 0, 578.695, 69.443, 84.26, 'B', 5891959, '2024'),
(1282, 75, 29, 0, 0, 0, 0, 596.128, 71.535, 80.77, 'B', 7258978, '2024'),
(1283, 76, 29, 0, 0, 0, 0, 608.199, 72.984, 78.36, 'A', 5621371, '2024'),
(1284, 77, 29, 0, 0, 0, 0, 619.745, 74.369, 76.05, 'A', 8582464, '2024'),
(1285, 78, 29, 0, 0, 0, 0, 630.412, 75.649, 73.92, 'A', 8100449, '2024'),
(1286, 79, 29, 0, 0, 0, 0, 571.702, 68.604, 85.00, 'B', 7504276, '2024'),
(1287, 80, 29, 0, 0, 0, 0, 625.750, 75.090, 74.85, 'B', 5812931, '2024'),
(1288, 81, 29, 0, 0, 0, 0, 602.038, 72.245, 79.59, 'A', 6590060, '2024'),
(1297, 50, 30, 0, 0, 0, 0, 590.701, 70.884, 81.86, 'B', 8817781, '2024'),
(1298, 52, 30, 0, 0, 0, 0, 598.438, 71.813, 80.31, 'B', 5343972, '2024'),
(1299, 53, 30, 0, 0, 0, 0, 604.499, 72.540, 79.10, 'A', 6066783, '2024'),
(1300, 54, 30, 0, 0, 0, 0, 622.232, 74.668, 75.55, 'A', 7865847, '2024'),
(1301, 55, 30, 0, 0, 0, 0, 563.895, 67.667, 85.00, 'B', 8997122, '2024'),
(1302, 57, 30, 0, 0, 0, 0, 604.952, 72.594, 79.01, 'A', 8795646, '2024'),
(1303, 58, 30, 0, 0, 0, 0, 632.493, 75.899, 73.50, 'A', 6858692, '2024'),
(1304, 59, 30, 0, 0, 0, 0, 626.604, 75.192, 74.68, 'A', 8832152, '2024'),
(1305, 60, 30, 0, 0, 0, 0, 621.353, 74.562, 75.73, 'A', 7780535, '2024'),
(1306, 62, 30, 0, 0, 0, 0, 619.946, 74.393, 76.01, 'A', 5539319, '2024'),
(1307, 63, 30, 0, 0, 0, 0, 636.687, 76.402, 72.66, 'A', 6837680, '2024'),
(1308, 64, 30, 0, 0, 0, 0, 587.924, 70.551, 82.42, 'B', 5312551, '2024'),
(1309, 66, 30, 0, 0, 0, 0, 577.791, 69.335, 84.44, 'B', 8346586, '2024'),
(1310, 67, 30, 0, 0, 0, 0, 566.072, 67.929, 85.00, 'B', 8050712, '2024'),
(1311, 72, 30, 0, 0, 0, 0, 610.005, 73.201, 78.00, 'A', 6336593, '2024'),
(1312, 73, 30, 0, 0, 0, 0, 565.644, 67.877, 85.00, 'B', 8108193, '2024'),
(1313, 74, 30, 0, 0, 0, 0, 565.060, 67.807, 85.00, 'B', 7929911, '2024'),
(1314, 75, 30, 0, 0, 0, 0, 582.064, 69.848, 83.59, 'B', 7449045, '2024'),
(1315, 76, 30, 0, 0, 0, 0, 591.266, 70.952, 81.75, 'B', 5272851, '2024'),
(1316, 77, 30, 0, 0, 0, 0, 624.625, 74.955, 75.08, 'A', 7397823, '2024'),
(1317, 78, 30, 0, 0, 0, 0, 608.551, 73.026, 78.29, 'A', 6696160, '2024'),
(1318, 79, 30, 0, 0, 0, 0, 617.177, 74.061, 76.56, 'A', 5979456, '2024'),
(1319, 80, 30, 0, 0, 0, 0, 618.466, 74.216, 76.31, 'B', 6942460, '2024'),
(1320, 81, 30, 0, 0, 0, 0, 575.880, 69.106, 84.82, 'B', 6563106, '2024'),
(1329, 50, 31, 0, 0, 0, 0, 570.378, 68.445, 85.00, 'B', 6290099, '2024'),
(1330, 52, 31, 0, 0, 0, 0, 625.703, 75.084, 74.86, 'A', 7520880, '2024'),
(1331, 53, 31, 0, 0, 0, 0, 614.202, 73.704, 77.16, 'A', 6585367, '2024'),
(1332, 54, 31, 0, 0, 0, 0, 565.180, 67.822, 85.00, 'B', 7410369, '2024'),
(1333, 55, 31, 0, 0, 0, 0, 616.332, 73.960, 76.73, 'A', 7279609, '2024'),
(1334, 57, 31, 0, 0, 0, 0, 628.958, 75.475, 74.21, 'A', 8994918, '2024'),
(1335, 58, 31, 0, 0, 0, 0, 631.064, 75.728, 73.79, 'A', 6539825, '2024'),
(1336, 59, 31, 0, 0, 0, 0, 606.203, 72.744, 78.76, 'A', 6837629, '2024'),
(1337, 60, 31, 0, 0, 0, 0, 625.079, 75.010, 74.98, 'A', 7436365, '2024'),
(1338, 62, 31, 0, 0, 0, 0, 588.818, 70.658, 82.24, 'B', 8879288, '2024'),
(1339, 63, 31, 0, 0, 0, 0, 577.213, 69.266, 84.56, 'B', 6541565, '2024'),
(1340, 64, 31, 0, 0, 0, 0, 573.782, 68.854, 85.00, 'B', 6804608, '2024'),
(1341, 66, 31, 0, 0, 0, 0, 614.437, 73.732, 77.11, 'A', 8133945, '2024'),
(1342, 67, 31, 0, 0, 0, 0, 602.056, 72.247, 79.59, 'A', 8022405, '2024'),
(1343, 72, 31, 0, 0, 0, 0, 610.088, 73.211, 77.98, 'A', 7517870, '2024'),
(1344, 73, 31, 0, 0, 0, 0, 591.069, 70.928, 81.79, 'B', 5396536, '2024'),
(1345, 74, 31, 0, 0, 0, 0, 617.973, 74.157, 76.41, 'B', 5753687, '2024'),
(1346, 75, 31, 0, 0, 0, 0, 586.634, 70.396, 82.67, 'B', 7212107, '2024'),
(1347, 76, 31, 0, 0, 0, 0, 566.033, 67.924, 85.00, 'B', 5003581, '2024'),
(1348, 77, 31, 0, 0, 0, 0, 631.448, 75.774, 73.71, 'A', 6334088, '2024'),
(1349, 78, 31, 0, 0, 0, 0, 585.833, 70.300, 82.83, 'B', 8733462, '2024'),
(1350, 79, 31, 0, 0, 0, 0, 581.195, 69.743, 83.76, 'B', 6113164, '2024'),
(1351, 80, 31, 0, 0, 0, 0, 609.252, 73.110, 78.15, 'B', 6171434, '2024'),
(1352, 81, 31, 0, 0, 0, 0, 635.563, 76.268, 72.89, 'A', 5316277, '2024'),
(1361, 50, 32, 0, 0, 0, 0, 565.087, 67.810, 85.00, 'B', 8786020, '2024'),
(1362, 52, 32, 0, 0, 0, 0, 595.167, 71.420, 80.97, 'B', 5760212, '2024'),
(1363, 53, 32, 0, 0, 0, 0, 572.208, 68.665, 85.00, 'B', 6121608, '2024'),
(1364, 54, 32, 0, 0, 0, 0, 597.224, 71.667, 80.56, 'B', 5802112, '2024'),
(1365, 55, 32, 0, 0, 0, 0, 570.490, 68.459, 85.00, 'B', 5014065, '2024'),
(1366, 57, 32, 0, 0, 0, 0, 594.559, 71.347, 81.09, 'B', 8066161, '2024'),
(1367, 58, 32, 0, 0, 0, 0, 614.270, 73.712, 77.15, 'A', 7594399, '2024'),
(1368, 59, 32, 0, 0, 0, 0, 565.559, 67.867, 85.00, 'B', 5519363, '2024'),
(1369, 60, 32, 0, 0, 0, 0, 625.534, 75.064, 74.89, 'A', 7664337, '2024'),
(1370, 62, 32, 0, 0, 0, 0, 611.983, 73.438, 77.60, 'A', 7402702, '2024'),
(1371, 63, 32, 0, 0, 0, 0, 591.382, 70.966, 81.72, 'B', 5358006, '2024'),
(1372, 64, 32, 0, 0, 0, 0, 601.819, 72.218, 79.64, 'A', 6028612, '2024'),
(1373, 66, 32, 0, 0, 0, 0, 563.682, 67.642, 85.00, 'B', 7786138, '2024'),
(1374, 67, 32, 0, 0, 0, 0, 600.328, 72.039, 79.93, 'A', 7100560, '2024'),
(1375, 72, 32, 0, 0, 0, 0, 628.199, 75.384, 74.36, 'A', 6735180, '2024'),
(1376, 73, 32, 0, 0, 0, 0, 567.510, 68.101, 85.00, 'B', 8254108, '2024'),
(1377, 74, 32, 0, 0, 0, 0, 598.078, 71.769, 80.38, 'B', 5278847, '2024'),
(1378, 75, 32, 0, 0, 0, 0, 609.099, 73.092, 78.18, 'B', 6866441, '2024'),
(1379, 76, 32, 0, 0, 0, 0, 596.606, 71.593, 80.68, 'B', 6069833, '2024'),
(1380, 77, 32, 0, 0, 0, 0, 619.504, 74.340, 76.10, 'A', 8654009, '2024'),
(1381, 78, 32, 0, 0, 0, 0, 606.789, 72.815, 78.64, 'A', 7114519, '2024'),
(1382, 79, 32, 0, 0, 0, 0, 608.944, 73.073, 78.21, 'A', 5175611, '2024'),
(1383, 80, 32, 0, 0, 0, 0, 600.338, 72.041, 79.93, 'B', 7993704, '2024'),
(1384, 81, 32, 0, 0, 0, 0, 603.354, 72.402, 79.33, 'A', 7080860, '2024');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `aturan`
--
ALTER TABLE `aturan`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode_aturan` (`kode_aturan`),
  ADD KEY `jurusan_id` (`jurusan_id`);

--
-- Indexes for table `detail_jawaban`
--
ALTER TABLE `detail_jawaban`
  ADD PRIMARY KEY (`id`),
  ADD KEY `konsultasi_id` (`konsultasi_id`),
  ADD KEY `pertanyaan_id` (`pertanyaan_id`),
  ADD KEY `jawaban_id` (`jawaban_id`);

--
-- Indexes for table `jurusan_kuliah`
--
ALTER TABLE `jurusan_kuliah`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode` (`kode`),
  ADD KEY `kategori_id` (`kategori_id`);

--
-- Indexes for table `kategori_minat`
--
ALTER TABLE `kategori_minat`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode` (`kode`);

--
-- Indexes for table `konselor`
--
ALTER TABLE `konselor`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nip` (`nip`);

--
-- Indexes for table `konsultasi`
--
ALTER TABLE `konsultasi`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `no_konsultasi` (`no_konsultasi`),
  ADD KEY `siswa_id` (`siswa_id`),
  ADD KEY `konselor_id` (`konselor_id`),
  ADD KEY `rekomendasi_utama_id` (`rekomendasi_utama_id`),
  ADD KEY `rekomendasi_alt1_id` (`rekomendasi_alt1_id`),
  ADD KEY `rekomendasi_alt2_id` (`rekomendasi_alt2_id`);

--
-- Indexes for table `log_aktivitas`
--
ALTER TABLE `log_aktivitas`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pengguna`
--
ALTER TABLE `pengguna`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `pertanyaan_psikotest`
--
ALTER TABLE `pertanyaan_psikotest`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode` (`kode`),
  ADD KEY `kategori_id` (`kategori_id`);

--
-- Indexes for table `pilihan_jawaban`
--
ALTER TABLE `pilihan_jawaban`
  ADD PRIMARY KEY (`id`),
  ADD KEY `pertanyaan_id` (`pertanyaan_id`);

--
-- Indexes for table `rekomendasi_univ`
--
ALTER TABLE `rekomendasi_univ`
  ADD PRIMARY KEY (`id`),
  ADD KEY `konsultasi_id` (`konsultasi_id`),
  ADD KEY `universitas_jurusan_id` (`universitas_jurusan_id`);

--
-- Indexes for table `siswa`
--
ALTER TABLE `siswa`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nis` (`nis`);

--
-- Indexes for table `universitas`
--
ALTER TABLE `universitas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode` (`kode`);

--
-- Indexes for table `universitas_jurusan`
--
ALTER TABLE `universitas_jurusan`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `universitas_id` (`universitas_id`,`jurusan_id`),
  ADD KEY `jurusan_id` (`jurusan_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `aturan`
--
ALTER TABLE `aturan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT for table `detail_jawaban`
--
ALTER TABLE `detail_jawaban`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `jurusan_kuliah`
--
ALTER TABLE `jurusan_kuliah`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `kategori_minat`
--
ALTER TABLE `kategori_minat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `konselor`
--
ALTER TABLE `konselor`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `konsultasi`
--
ALTER TABLE `konsultasi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `log_aktivitas`
--
ALTER TABLE `log_aktivitas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `pengguna`
--
ALTER TABLE `pengguna`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `pertanyaan_psikotest`
--
ALTER TABLE `pertanyaan_psikotest`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT for table `pilihan_jawaban`
--
ALTER TABLE `pilihan_jawaban`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=141;

--
-- AUTO_INCREMENT for table `rekomendasi_univ`
--
ALTER TABLE `rekomendasi_univ`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=405;

--
-- AUTO_INCREMENT for table `siswa`
--
ALTER TABLE `siswa`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `universitas`
--
ALTER TABLE `universitas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=82;

--
-- AUTO_INCREMENT for table `universitas_jurusan`
--
ALTER TABLE `universitas_jurusan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1385;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `aturan`
--
ALTER TABLE `aturan`
  ADD CONSTRAINT `aturan_ibfk_1` FOREIGN KEY (`jurusan_id`) REFERENCES `jurusan_kuliah` (`id`);

--
-- Constraints for table `detail_jawaban`
--
ALTER TABLE `detail_jawaban`
  ADD CONSTRAINT `detail_jawaban_ibfk_1` FOREIGN KEY (`konsultasi_id`) REFERENCES `konsultasi` (`id`),
  ADD CONSTRAINT `detail_jawaban_ibfk_2` FOREIGN KEY (`pertanyaan_id`) REFERENCES `pertanyaan_psikotest` (`id`),
  ADD CONSTRAINT `detail_jawaban_ibfk_3` FOREIGN KEY (`jawaban_id`) REFERENCES `pilihan_jawaban` (`id`);

--
-- Constraints for table `jurusan_kuliah`
--
ALTER TABLE `jurusan_kuliah`
  ADD CONSTRAINT `jurusan_kuliah_ibfk_1` FOREIGN KEY (`kategori_id`) REFERENCES `kategori_minat` (`id`);

--
-- Constraints for table `konsultasi`
--
ALTER TABLE `konsultasi`
  ADD CONSTRAINT `konsultasi_ibfk_1` FOREIGN KEY (`siswa_id`) REFERENCES `siswa` (`id`),
  ADD CONSTRAINT `konsultasi_ibfk_2` FOREIGN KEY (`konselor_id`) REFERENCES `konselor` (`id`),
  ADD CONSTRAINT `konsultasi_ibfk_3` FOREIGN KEY (`rekomendasi_utama_id`) REFERENCES `jurusan_kuliah` (`id`),
  ADD CONSTRAINT `konsultasi_ibfk_4` FOREIGN KEY (`rekomendasi_alt1_id`) REFERENCES `jurusan_kuliah` (`id`),
  ADD CONSTRAINT `konsultasi_ibfk_5` FOREIGN KEY (`rekomendasi_alt2_id`) REFERENCES `jurusan_kuliah` (`id`);

--
-- Constraints for table `pertanyaan_psikotest`
--
ALTER TABLE `pertanyaan_psikotest`
  ADD CONSTRAINT `pertanyaan_psikotest_ibfk_1` FOREIGN KEY (`kategori_id`) REFERENCES `kategori_minat` (`id`);

--
-- Constraints for table `pilihan_jawaban`
--
ALTER TABLE `pilihan_jawaban`
  ADD CONSTRAINT `pilihan_jawaban_ibfk_1` FOREIGN KEY (`pertanyaan_id`) REFERENCES `pertanyaan_psikotest` (`id`);

--
-- Constraints for table `rekomendasi_univ`
--
ALTER TABLE `rekomendasi_univ`
  ADD CONSTRAINT `rekomendasi_univ_ibfk_1` FOREIGN KEY (`konsultasi_id`) REFERENCES `konsultasi` (`id`),
  ADD CONSTRAINT `rekomendasi_univ_ibfk_2` FOREIGN KEY (`universitas_jurusan_id`) REFERENCES `universitas_jurusan` (`id`);

--
-- Constraints for table `universitas_jurusan`
--
ALTER TABLE `universitas_jurusan`
  ADD CONSTRAINT `universitas_jurusan_ibfk_1` FOREIGN KEY (`universitas_id`) REFERENCES `universitas` (`id`),
  ADD CONSTRAINT `universitas_jurusan_ibfk_2` FOREIGN KEY (`jurusan_id`) REFERENCES `jurusan_kuliah` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
