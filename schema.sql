-- ============================================================
-- SISTEM PAKAR REKOMENDASI JURUSAN
-- Forward Chaining Engine | Database Schema + Seed Data
-- ============================================================

CREATE DATABASE IF NOT EXISTS sistempakar_jurusan 
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistempakar_jurusan;

-- ============================================================
-- MASTER TABLES
-- ============================================================

CREATE TABLE kategori_minat (
  id         INT PRIMARY KEY AUTO_INCREMENT,
  kode       VARCHAR(10) UNIQUE NOT NULL,
  nama       VARCHAR(100) NOT NULL,
  warna_hex  VARCHAR(10) DEFAULT '#4ECDC4',
  deskripsi  TEXT,
  icon       VARCHAR(50)
);

CREATE TABLE konselor (
  id           INT PRIMARY KEY AUTO_INCREMENT,
  nip          VARCHAR(25) UNIQUE NOT NULL,
  nama         VARCHAR(120) NOT NULL,
  gelar        VARCHAR(50),
  email        VARCHAR(120),
  telepon      VARCHAR(20),
  spesialisasi VARCHAR(150),
  pengalaman   INT DEFAULT 0 COMMENT 'tahun pengalaman',
  foto_path    VARCHAR(255),
  aktif        BOOLEAN DEFAULT TRUE,
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE siswa (
  id              INT PRIMARY KEY AUTO_INCREMENT,
  nis             VARCHAR(20) UNIQUE NOT NULL,
  nama            VARCHAR(120) NOT NULL,
  jenis_kelamin   ENUM('L','P') NOT NULL,
  tanggal_lahir   DATE,
  tempat_lahir    VARCHAR(100),
  kelas           VARCHAR(10),
  jurusan_sma     ENUM('IPA','IPS','Bahasa','SMK-TI','SMK-Bisnis','SMK-Seni','Lainnya'),
  alamat          TEXT,
  telepon         VARCHAR(20),
  email           VARCHAR(120),
  nama_ortu       VARCHAR(120),
  telepon_ortu    VARCHAR(20),
  hobi            TEXT COMMENT 'dipisah koma',
  prestasi        TEXT,
  organisasi      TEXT,
  nilai_matematika   DECIMAL(5,2) DEFAULT 0,
  nilai_ipa          DECIMAL(5,2) DEFAULT 0,
  nilai_ips          DECIMAL(5,2) DEFAULT 0,
  nilai_bahasa_ind   DECIMAL(5,2) DEFAULT 0,
  nilai_bahasa_ing   DECIMAL(5,2) DEFAULT 0,
  nilai_seni         DECIMAL(5,2) DEFAULT 0,
  nilai_olahraga     DECIMAL(5,2) DEFAULT 0,
  nilai_komputer     DECIMAL(5,2) DEFAULT 0,
  cita_cita          VARCHAR(200),
  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE jurusan_kuliah (
  id            INT PRIMARY KEY AUTO_INCREMENT,
  kode          VARCHAR(20) UNIQUE NOT NULL,
  nama          VARCHAR(200) NOT NULL,
  kategori_id   INT,
  deskripsi     TEXT,
  prospek_kerja TEXT,
  mata_kuliah_utama TEXT,
  syarat_nilai  TEXT COMMENT 'JSON misal: {"mtk":75,"ipa":70}',
  rumpun        VARCHAR(100),
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (kategori_id) REFERENCES kategori_minat(id)
);

CREATE TABLE universitas (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  kode        VARCHAR(20) UNIQUE NOT NULL,
  nama        VARCHAR(250) NOT NULL,
  singkatan   VARCHAR(20),
  kota        VARCHAR(100),
  provinsi    VARCHAR(100),
  wilayah     ENUM('Jawa','Sumatra','Kalimantan','Sulawesi','Bali-Nusa','Maluku-Papua'),
  akreditasi  VARCHAR(5) DEFAULT 'A',
  website     VARCHAR(200),
  tipe        ENUM('PTN-BH','PTN-BLU','PTN') DEFAULT 'PTN',
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE universitas_jurusan (
  id                  INT PRIMARY KEY AUTO_INCREMENT,
  universitas_id      INT NOT NULL,
  jurusan_id          INT NOT NULL,
  daya_tampung_snbp   INT DEFAULT 0,
  daya_tampung_snbt   INT DEFAULT 0,
  peminat_snbp        INT DEFAULT 0,
  peminat_snbt        INT DEFAULT 0,
  passing_grade_snbt  DECIMAL(6,3) DEFAULT 0,
  passing_grade_snbp  DECIMAL(6,3) DEFAULT 0,
  peluang_masuk       DECIMAL(5,2) DEFAULT 0 COMMENT 'persen 0-100',
  akreditasi_prodi    VARCHAR(5) DEFAULT 'A',
  biaya_kuliah        BIGINT DEFAULT 0 COMMENT 'per semester',
  tahun_data          YEAR DEFAULT 2024,
  FOREIGN KEY (universitas_id) REFERENCES universitas(id),
  FOREIGN KEY (jurusan_id)     REFERENCES jurusan_kuliah(id)
);

CREATE TABLE pertanyaan_psikotest (
  id           INT PRIMARY KEY AUTO_INCREMENT,
  kode         VARCHAR(20) UNIQUE NOT NULL,
  pertanyaan   TEXT NOT NULL,
  kategori_id  INT NOT NULL,
  aspek        VARCHAR(100) COMMENT 'minat/bakat/kepribadian',
  bobot        INT DEFAULT 1,
  urutan       INT DEFAULT 1,
  aktif        BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (kategori_id) REFERENCES kategori_minat(id)
);

CREATE TABLE pilihan_jawaban (
  id             INT PRIMARY KEY AUTO_INCREMENT,
  pertanyaan_id  INT NOT NULL,
  huruf          CHAR(1) NOT NULL,
  teks_jawaban   TEXT NOT NULL,
  skor           INT DEFAULT 1,
  FOREIGN KEY (pertanyaan_id) REFERENCES pertanyaan_psikotest(id)
);

CREATE TABLE aturan (
  id           INT PRIMARY KEY AUTO_INCREMENT,
  kode_aturan  VARCHAR(20) UNIQUE NOT NULL,
  nama_aturan  VARCHAR(200),
  kondisi_json TEXT NOT NULL COMMENT 'JSON kondisi forward chaining',
  jurusan_id   INT NOT NULL,
  prioritas    INT DEFAULT 5,
  confidence   DECIMAL(5,2) DEFAULT 90.00,
  aktif        BOOLEAN DEFAULT TRUE,
  keterangan   TEXT,
  FOREIGN KEY (jurusan_id) REFERENCES jurusan_kuliah(id)
);

-- ============================================================
-- TRANSAKSI
-- ============================================================

CREATE TABLE konsultasi (
  id                   INT PRIMARY KEY AUTO_INCREMENT,
  no_konsultasi        VARCHAR(30) UNIQUE NOT NULL,
  siswa_id             INT NOT NULL,
  konselor_id          INT NOT NULL,
  tanggal_konsultasi   DATETIME DEFAULT CURRENT_TIMESTAMP,
  skor_teknologi       INT DEFAULT 0,
  skor_sains           INT DEFAULT 0,
  skor_sosial          INT DEFAULT 0,
  skor_seni            INT DEFAULT 0,
  skor_bisnis          INT DEFAULT 0,
  skor_bahasa          INT DEFAULT 0,
  skor_kesehatan       INT DEFAULT 0,
  rekomendasi_utama_id INT,
  rekomendasi_alt1_id  INT,
  rekomendasi_alt2_id  INT,
  catatan_konselor     TEXT,
  tips_belajar         TEXT,
  rekomendasi_kegiatan TEXT,
  status               ENUM('draft','selesai','follow_up') DEFAULT 'draft',
  created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (siswa_id)              REFERENCES siswa(id),
  FOREIGN KEY (konselor_id)           REFERENCES konselor(id),
  FOREIGN KEY (rekomendasi_utama_id)  REFERENCES jurusan_kuliah(id),
  FOREIGN KEY (rekomendasi_alt1_id)   REFERENCES jurusan_kuliah(id),
  FOREIGN KEY (rekomendasi_alt2_id)   REFERENCES jurusan_kuliah(id)
);

CREATE TABLE detail_jawaban (
  id              INT PRIMARY KEY AUTO_INCREMENT,
  konsultasi_id   INT NOT NULL,
  pertanyaan_id   INT NOT NULL,
  jawaban_id      INT NOT NULL,
  FOREIGN KEY (konsultasi_id)  REFERENCES konsultasi(id),
  FOREIGN KEY (pertanyaan_id)  REFERENCES pertanyaan_psikotest(id),
  FOREIGN KEY (jawaban_id)     REFERENCES pilihan_jawaban(id)
);

CREATE TABLE rekomendasi_univ (
  id                    INT PRIMARY KEY AUTO_INCREMENT,
  konsultasi_id         INT NOT NULL,
  universitas_jurusan_id INT NOT NULL,
  peluang_personal      DECIMAL(5,2),
  rank_urutan           INT,
  catatan               TEXT,
  FOREIGN KEY (konsultasi_id)          REFERENCES konsultasi(id),
  FOREIGN KEY (universitas_jurusan_id) REFERENCES universitas_jurusan(id)
);

-- ============================================================
-- SEED DATA
-- ============================================================

INSERT INTO kategori_minat (kode,nama,warna_hex,deskripsi,icon) VALUES
('TEKNO', 'Teknologi & Komputer', '#4FC3F7', 'Minat di bidang IT, pemrograman, dan teknologi digital', '💻'),
('SAINS', 'Sains & Eksakta',       '#81C784', 'Minat di bidang matematika, fisika, kimia, biologi', '🔬'),
('SOSIAL','Sosial & Humaniora',    '#FFB74D', 'Minat di bidang sosial, psikologi, hukum, komunikasi', '🤝'),
('SENI',  'Seni & Kreatif',        '#CE93D8', 'Minat di bidang desain, musik, film, sastra', '🎨'),
('BISNIS','Bisnis & Ekonomi',      '#F48FB1', 'Minat di bidang manajemen, akuntansi, ekonomi', '📊'),
('BAHASA','Bahasa & Sastra',       '#80DEEA', 'Minat di bidang bahasa, sastra, linguistik', '📚'),
('KESEHATAN','Kesehatan & Medis',  '#A5D6A7', 'Minat di bidang kedokteran, farmasi, kesehatan', '🏥');

INSERT INTO konselor (nip,nama,gelar,email,telepon,spesialisasi,pengalaman) VALUES
('19800101200501001','Budi Santoso','M.Pd','budi.santoso@sman1.sch.id','081234567890','Bimbingan Karir & Konseling Pendidikan',15),
('19850515201001002','Sari Dewi','S.Psi','sari.dewi@sman1.sch.id','081234567891','Psikologi Pendidikan & Minat Bakat',10),
('19750320199801003','Ahmad Fauzi','M.Pd, Kons','ahmad.fauzi@sman1.sch.id','081234567892','Konseling Karir & Pengembangan Diri',20);

INSERT INTO jurusan_kuliah (kode,nama,kategori_id,deskripsi,prospek_kerja,rumpun) VALUES
('TI','Teknik Informatika',1,'Mempelajari algoritma, pemrograman, dan pengembangan sistem software','Software Engineer, Data Scientist, AI Engineer, Cybersecurity Analyst','Teknologi Informasi'),
('SI','Sistem Informasi',1,'Mengintegrasikan teknologi informasi dengan proses bisnis organisasi','Business Analyst, IT Consultant, System Analyst, IT Project Manager','Teknologi Informasi'),
('TK','Teknik Komputer',1,'Merancang hardware komputer dan sistem embedded','Hardware Engineer, IoT Developer, Network Engineer','Teknologi Informasi'),
('DS','Data Science',1,'Menganalisis dan menginterpretasikan data besar untuk pengambilan keputusan','Data Analyst, Machine Learning Engineer, BI Developer','Teknologi Informasi'),
('TM','Teknik Mesin',2,'Merancang dan menganalisis sistem mekanik dan termal','Mechanical Engineer, Automotive Engineer, Manufacturing Engineer','Teknik'),
('TL','Teknik Listrik/Elektro',2,'Mempelajari sistem kelistrikan, elektronika, dan instrumentasi','Electrical Engineer, Control Systems Engineer, Power Engineer','Teknik'),
('TS','Teknik Sipil',2,'Merancang dan membangun infrastruktur fisik','Civil Engineer, Structural Engineer, Project Manager Konstruksi','Teknik'),
('TK2','Teknik Kimia',2,'Mengubah bahan mentah menjadi produk bernilai melalui proses kimia','Chemical Engineer, Process Engineer, Quality Control','Teknik'),
('FK','Fisika',2,'Mempelajari fenomena alam dari skala subatomik hingga kosmik','Fisikawan, Peneliti, Radiolog, Konsultan Energi','MIPA'),
('KI','Kimia',2,'Mempelajari komposisi, sifat, dan transformasi materi','Kimiawan, Peneliti, Analis Laboratorium, Formulator','MIPA'),
('BI','Biologi',2,'Mempelajari kehidupan makhluk hidup dan ekosistem','Biolog, Peneliti, Konsultan Lingkungan, Bioteknik','MIPA'),
('MA','Matematika',2,'Mempelajari logika, struktur, dan hubungan kuantitas','Aktuaris, Data Analyst, Peneliti, Guru Matematika','MIPA'),
('PS','Psikologi',3,'Mempelajari perilaku dan proses mental manusia','Psikolog, HRD Manager, Konselor, Researcher','Sosial'),
('HK','Hukum/Ilmu Hukum',3,'Mempelajari sistem hukum dan perundang-undangan','Lawyer, Notaris, Jaksa, Hakim, Legal Consultant','Sosial'),
('KM','Komunikasi',3,'Mempelajari proses komunikasi massa dan interpersonal','Jurnalis, PR Manager, Content Creator, Broadcasting','Sosial'),
('HI','Hubungan Internasional',3,'Mempelajari politik luar negeri dan diplomasi internasional','Diplomat, Analis Politik, International NGO, Researcher','Sosial'),
('DKV','Desain Komunikasi Visual',4,'Menggabungkan seni visual dengan komunikasi untuk menyampaikan pesan','Graphic Designer, UI/UX Designer, Art Director, Brand Designer','Seni'),
('DI','Desain Interior',4,'Merancang ruang interior yang fungsional dan estetis','Interior Designer, Konsultan Desain, Project Manager Desain','Seni'),
('SN','Seni Rupa',4,'Mengekspresikan ide dan emosi melalui media visual','Seniman, Kurator, Art Teacher, Galeri Manager','Seni'),
('AR','Arsitektur',4,'Merancang bangunan yang memadukan fungsi, estetika, dan teknik','Arsitek, Urban Planner, Project Manager, Interior Arsitek','Seni'),
('MJ','Manajemen',5,'Mempelajari perencanaan, pengorganisasian, dan pengendalian organisasi','Manajer, Entrepreneur, Konsultan Bisnis, Marketing Manager','Bisnis'),
('AK','Akuntansi',5,'Mempelajari pencatatan, pengklasifikasian, dan pelaporan transaksi keuangan','Akuntan, Auditor, Tax Consultant, CFO','Bisnis'),
('EK','Ekonomi',5,'Mempelajari perilaku ekonomi individu, perusahaan, dan negara','Ekonom, Analis Keuangan, Research Ekonomi, Policy Analyst','Bisnis'),
('PE','Pendidikan Ekonomi',5,'Mempersiapkan tenaga pendidik bidang ekonomi','Guru Ekonomi, Dosen, Pengembang Kurikulum','Bisnis'),
('BI2','Bahasa & Sastra Indonesia',6,'Mempelajari linguistik, sastra, dan budaya Indonesia','Editor, Penulis, Jurnalis, Guru Bahasa, EYD Consultant','Bahasa'),
('BE','Bahasa & Sastra Inggris',6,'Mempelajari bahasa, sastra, dan budaya Inggris secara mendalam','Translator, Teacher, Content Writer, Copywriter','Bahasa'),
('SA','Sastra Arab',6,'Mempelajari bahasa, sastra, dan budaya Arab','Translator Arab, Diplomat, Jurnalis Timur Tengah','Bahasa'),
('PK','Pendidikan Kedokteran',7,'Mempersiapkan dokter umum yang kompeten dan profesional','Dokter Umum, Dokter Spesialis, Peneliti Medis','Kesehatan'),
('KG','Kedokteran Gigi',7,'Mempersiapkan dokter gigi yang kompeten','Dokter Gigi, Orthodontist, Oral Surgeon','Kesehatan'),
('FA','Farmasi',7,'Mempelajari obat-obatan, formulasi, dan penggunaannya','Apoteker, Farmasis, Peneliti Obat, Medical Representative','Kesehatan'),
('GZ','Gizi',7,'Mempelajari nutrisi, pangan, dan kesehatan masyarakat','Ahli Gizi, Dietisien, Nutritionist, Food Technologist','Kesehatan'),
('KP','Keperawatan',7,'Mempersiapkan perawat profesional','Perawat, Nurse Manager, Clinical Instructor','Kesehatan');

INSERT INTO universitas (kode,nama,singkatan,kota,provinsi,wilayah,akreditasi,website,tipe) VALUES
('UI','Universitas Indonesia','UI','Depok','Jawa Barat','Jawa','Unggul','https://ui.ac.id','PTN-BH'),
('UGM','Universitas Gadjah Mada','UGM','Yogyakarta','DI Yogyakarta','Jawa','Unggul','https://ugm.ac.id','PTN-BH'),
('ITB','Institut Teknologi Bandung','ITB','Bandung','Jawa Barat','Jawa','Unggul','https://itb.ac.id','PTN-BH'),
('ITS','Institut Teknologi Sepuluh Nopember','ITS','Surabaya','Jawa Timur','Jawa','Unggul','https://its.ac.id','PTN-BH'),
('UNAIR','Universitas Airlangga','UNAIR','Surabaya','Jawa Timur','Jawa','Unggul','https://unair.ac.id','PTN-BH'),
('IPB','Institut Pertanian Bogor','IPB','Bogor','Jawa Barat','Jawa','Unggul','https://ipb.ac.id','PTN-BH'),
('UNDIP','Universitas Diponegoro','UNDIP','Semarang','Jawa Tengah','Jawa','Unggul','https://undip.ac.id','PTN-BH'),
('UNPAD','Universitas Padjadjaran','UNPAD','Bandung','Jawa Barat','Jawa','Unggul','https://unpad.ac.id','PTN-BH'),
('UB','Universitas Brawijaya','UB','Malang','Jawa Timur','Jawa','Unggul','https://ub.ac.id','PTN-BH'),
('UNS','Universitas Sebelas Maret','UNS','Surakarta','Jawa Tengah','Jawa','Unggul','https://uns.ac.id','PTN-BLU'),
('USU','Universitas Sumatera Utara','USU','Medan','Sumatera Utara','Sumatra','A','https://usu.ac.id','PTN-BH'),
('UNAND','Universitas Andalas','UNAND','Padang','Sumatera Barat','Sumatra','A','https://unand.ac.id','PTN-BH'),
('UNSRI','Universitas Sriwijaya','UNSRI','Palembang','Sumatera Selatan','Sumatra','A','https://unsri.ac.id','PTN-BLU'),
('UNHAS','Universitas Hasanuddin','UNHAS','Makassar','Sulawesi Selatan','Sulawesi','Unggul','https://unhas.ac.id','PTN-BH'),
('UNMUL','Universitas Mulawarman','UNMUL','Samarinda','Kalimantan Timur','Kalimantan','A','https://unmul.ac.id','PTN-BLU'),
('UDAYANA','Universitas Udayana','UDAYANA','Denpasar','Bali','Bali-Nusa','A','https://udayana.ac.id','PTN-BLU'),
('UNJ','Universitas Negeri Jakarta','UNJ','Jakarta','DKI Jakarta','Jawa','A','https://unj.ac.id','PTN-BLU'),
('UPI','Universitas Pendidikan Indonesia','UPI','Bandung','Jawa Barat','Jawa','A','https://upi.edu','PTN-BH'),
('UM','Universitas Negeri Malang','UM','Malang','Jawa Timur','Jawa','Unggul','https://um.ac.id','PTN-BLU'),
('UNEJ','Universitas Jember','UNEJ','Jember','Jawa Timur','Jawa','A','https://unej.ac.id','PTN-BLU');

-- Data universitas_jurusan (sample data penting)
INSERT INTO universitas_jurusan (universitas_id,jurusan_id,daya_tampung_snbp,daya_tampung_snbt,peminat_snbp,peminat_snbt,passing_grade_snbt,peluang_masuk,akreditasi_prodi,biaya_kuliah) VALUES
-- UI
(1,4,30,50,1500,6000,670.00,4.20,'Unggul',11000000),
(1,1,35,60,2100,8500,687.50,3.20,'Unggul',12500000),
(1,3,30,50,1800,7200,682.00,3.50,'Unggul',11000000),
(1,13,40,70,1500,5000,620.00,6.50,'Unggul',8000000),
(1,14,30,50,1200,4500,635.00,5.80,'Unggul',8500000),
(1,21,45,80,900,3200,580.00,9.80,'Unggul',7500000),
(1,22,35,60,750,2800,595.00,10.20,'Unggul',7500000),
(1,28,40,70,3500,12000,720.00,2.10,'Unggul',20000000),
-- UGM
(2,4,35,60,1800,7000,680.00,3.50,'Unggul',9500000),
(2,1,40,70,2500,10000,690.00,2.80,'Unggul',10000000),
(2,21,50,90,800,3000,560.00,11.00,'Unggul',6500000),
(2,22,40,70,650,2500,570.00,10.80,'Unggul',6500000),
(2,13,45,80,1300,4800,615.00,7.00,'Unggul',7000000),
(2,28,35,60,4000,15000,725.00,1.80,'Unggul',22000000),
(2,5,45,80,1200,5000,650.00,5.50,'Unggul',9000000),
-- ITB
(3,4,40,70,2000,8500,685.00,3.00,'Unggul',11500000),
(3,1,50,90,3000,12500,695.00,2.40,'Unggul',12000000),
(3,6,45,80,1500,6500,665.00,4.50,'Unggul',11000000),
(3,5,40,70,1300,5500,660.00,4.80,'Unggul',10500000),
(3,17,35,60,1800,7000,658.00,4.10,'Unggul',9500000),
(3,20,40,70,2000,8000,662.00,3.80,'Unggul',10000000),
-- ITS
(4,4,35,60,1400,5800,665.00,4.80,'Unggul',9000000),
(4,1,45,80,2200,9000,680.00,3.10,'Unggul',9000000),
(4,6,40,70,1400,5800,660.00,4.60,'Unggul',8500000),
(4,5,40,70,1200,5000,655.00,5.00,'Unggul',8000000),
(4,3,35,60,1600,6500,665.00,4.20,'Unggul',8500000),
-- UNAIR
(5,28,35,60,3800,14000,715.00,1.90,'Unggul',22000000),
(5,13,40,70,1100,4200,605.00,7.40,'Unggul',7000000),
(5,30,35,60,600,2200,580.00,10.50,'Unggul',7500000),
(5,21,45,80,700,2600,550.00,12.00,'Unggul',6500000),
-- UNDIP
(7,1,40,70,1800,7500,670.00,3.90,'Unggul',8000000),
(7,22,40,70,550,2000,545.00,13.00,'Unggul',5500000),
(7,14,35,60,900,3500,605.00,7.10,'Unggul',6000000),
(7,7,40,70,1000,4000,625.00,6.40,'Unggul',7000000),
-- UNPAD
(8,28,30,50,3200,12500,708.00,2.00,'Unggul',20000000),
(8,13,40,70,1000,3800,600.00,7.80,'Unggul',6500000),
(8,30,30,50,500,1800,570.00,11.00,'Unggul',7000000),
(8,25,35,60,400,1500,540.00,14.00,'A',4500000),
-- UB
(9,1,40,70,1600,6500,665.00,4.30,'Unggul',7500000),
(9,21,45,80,650,2400,540.00,13.20,'Unggul',5500000),
(9,22,40,70,500,1800,535.00,14.00,'Unggul',5500000),
-- UNS
(10,14,35,60,800,3200,590.00,8.60,'A',5000000),
(10,22,40,70,450,1700,530.00,14.80,'A',4500000),
(10,7,35,60,900,3500,605.00,7.00,'A',5500000),
-- USU
(11,28,25,40,2500,9000,695.00,2.30,'A',15000000),
(11,1,35,60,1200,5000,645.00,5.20,'A',6000000),
(11,14,30,50,700,2800,575.00,9.80,'A',5000000),
-- UNHAS
(14,1,35,60,1300,5200,650.00,5.00,'Unggul',6500000),
(14,28,25,40,2800,10500,700.00,2.10,'A',18000000),
(14,7,35,60,800,3200,595.00,8.00,'A',5500000),
-- UNMUL
(15,1,30,50,500,2000,590.00,11.00,'A',5000000),
(15,21,35,60,300,1200,510.00,18.00,'B',3500000),
-- UDAYANA
(16,28,20,35,1800,6500,685.00,2.50,'A',12000000),
(16,13,35,60,600,2400,565.00,11.50,'A',5000000),
(16,21,40,70,300,1200,505.00,19.00,'A',4000000);

-- Pertanyaan Psikotest
INSERT INTO pertanyaan_psikotest (kode,pertanyaan,kategori_id,aspek,bobot,urutan) VALUES
-- Teknologi (kategori 1)
('P001','Apakah kamu suka membuat atau mengoperasikan program/aplikasi komputer?',1,'minat',2,1),
('P002','Seberapa sering kamu bermain game atau mengeksplorasi teknologi terbaru?',1,'minat',1,2),
('P003','Apakah kamu tertarik belajar coding atau pemrograman?',1,'bakat',2,3),
('P004','Apakah kamu senang memecahkan masalah dengan solusi berbasis teknologi?',1,'bakat',2,4),
('P005','Apakah kamu suka merakit atau memperbaiki perangkat elektronik?',1,'minat',1,5),
-- Sains (kategori 2)
('P006','Apakah kamu menyukai pelajaran Matematika dan merasa mudah memahaminya?',2,'bakat',2,6),
('P007','Apakah kamu tertarik dengan eksperimen sains dan laboratorium?',2,'minat',2,7),
('P008','Apakah kamu suka menganalisis data dan mencari pola dari fakta?',2,'bakat',2,8),
('P009','Apakah fisika atau kimia adalah mata pelajaran favoritmu?',2,'minat',1,9),
('P010','Apakah kamu suka mengamati fenomena alam dan mencari penjelasannya?',2,'minat',1,10),
-- Sosial (kategori 3)
('P011','Apakah kamu mudah bergaul dan suka berinteraksi dengan banyak orang?',3,'kepribadian',1,11),
('P012','Apakah kamu tertarik membantu orang lain menyelesaikan masalah mereka?',3,'minat',2,12),
('P013','Apakah kamu suka diskusi tentang isu sosial, politik, atau hukum?',3,'minat',2,13),
('P014','Apakah kamu nyaman berbicara di depan umum atau menjadi pemimpin?',3,'bakat',1,14),
('P015','Apakah kamu peduli dengan keadilan dan hak-hak masyarakat?',3,'minat',2,15),
-- Seni (kategori 4)
('P016','Apakah kamu suka menggambar, melukis, atau membuat karya visual?',4,'minat',2,16),
('P017','Apakah kamu tertarik dengan desain grafis, fashion, atau arsitektur?',4,'minat',2,17),
('P018','Apakah kamu memiliki selera estetika yang kuat dan suka keindahan?',4,'bakat',1,18),
('P019','Apakah kamu suka musik, film, atau pertunjukan seni?',4,'minat',1,19),
('P020','Apakah kamu sering punya ide kreatif dan unik dalam memecahkan masalah?',4,'bakat',2,20),
-- Bisnis (kategori 5)
('P021','Apakah kamu tertarik dengan dunia usaha, berdagang, atau berwirausaha?',5,'minat',2,21),
('P022','Apakah kamu suka mengatur keuangan dan mencatat pemasukan-pengeluaran?',5,'bakat',2,22),
('P023','Apakah kamu senang membuat strategi atau rencana untuk mencapai tujuan?',5,'bakat',2,23),
('P024','Apakah kamu suka negosiasi dan persuasi dalam kehidupan sehari-hari?',5,'minat',1,24),
('P025','Apakah kamu tertarik dengan analisis pasar atau tren ekonomi?',5,'minat',1,25),
-- Bahasa (kategori 6)
('P026','Apakah kamu suka membaca buku, novel, atau karya sastra?',6,'minat',2,26),
('P027','Apakah kamu menikmati menulis cerita, puisi, atau artikel?',6,'bakat',2,27),
('P028','Apakah kamu mudah mempelajari bahasa asing?',6,'bakat',2,28),
('P029','Apakah kamu suka berbicara dalam bahasa Inggris atau bahasa lainnya?',6,'minat',1,29),
('P030','Apakah kamu tertarik dengan jurnalistik atau komunikasi massa?',6,'minat',1,30),
-- Kesehatan (kategori 7)
('P031','Apakah kamu tertarik dengan ilmu kedokteran, kesehatan, atau biologi manusia?',7,'minat',2,31),
('P032','Apakah kamu memiliki empati tinggi dan suka merawat orang yang sakit?',7,'kepribadian',2,32),
('P033','Apakah kamu tidak takut dengan darah, jarum suntik, atau prosedur medis?',7,'kepribadian',1,33),
('P034','Apakah kamu suka belajar tentang tubuh manusia, penyakit, dan obat-obatan?',7,'minat',2,34),
('P035','Apakah kamu disiplin dan teliti dalam mengerjakan sesuatu?',7,'bakat',1,35);

-- Pilihan Jawaban (4 pilihan per soal)
INSERT INTO pilihan_jawaban (pertanyaan_id,huruf,teks_jawaban,skor) VALUES
(1,'A','Sangat suka, itu aktivitas favoritku sehari-hari',4),
(1,'B','Cukup suka, sering kulakukan di waktu luang',3),
(1,'C','Biasa saja, hanya kalau ada tugas',2),
(1,'D','Tidak terlalu suka, lebih suka aktivitas lain',1),
(2,'A','Setiap hari, selalu update teknologi terbaru',4),
(2,'B','Beberapa kali seminggu',3),
(2,'C','Sesekali saja',2),
(2,'D','Jarang sekali',1),
(3,'A','Sangat tertarik, sudah pernah belajar coding',4),
(3,'B','Tertarik dan ingin mencoba',3),
(3,'C','Sedikit tertarik tapi belum pernah coba',2),
(3,'D','Tidak tertarik sama sekali',1),
(4,'A','Selalu mencari solusi teknologi untuk setiap masalah',4),
(4,'B','Sering menggunakan teknologi untuk solusi',3),
(4,'C','Kadang-kadang saja',2),
(4,'D','Lebih suka solusi tradisional/manual',1),
(5,'A','Sangat suka, sudah punya pengalaman merakit',4),
(5,'B','Suka mencoba walaupun belum mahir',3),
(5,'C','Hanya suka menggunakan, tidak merakit',2),
(5,'D','Tidak suka sama sekali',1),
(6,'A','Sangat suka dan selalu dapat nilai bagus',4),
(6,'B','Suka dan nilainya rata-rata bagus',3),
(6,'C','Biasa saja, cukup untuk lulus',2),
(6,'D','Tidak suka, matematika itu sulit',1),
(7,'A','Sangat suka, laboratorium adalah tempat favoritku',4),
(7,'B','Suka eksperimen meskipun tidak terlalu sering',3),
(7,'C','Biasa saja, hanya ikuti pelajaran',2),
(7,'D','Tidak suka, lebih suka teori',1),
(8,'A','Sangat suka menganalisis data dan mencari pola',4),
(8,'B','Suka melakukannya untuk keperluan belajar',3),
(8,'C','Kadang-kadang melakukannya',2),
(8,'D','Tidak terlalu suka analisis data',1),
(9,'A','Fisika DAN kimia keduanya favorit',4),
(9,'B','Salah satunya adalah favorit',3),
(9,'C','Biasa saja untuk keduanya',2),
(9,'D','Tidak suka keduanya',1),
(10,'A','Selalu bertanya kenapa sebuah fenomena terjadi',4),
(10,'B','Sering penasaran dan mencari tahu',3),
(10,'C','Kadang penasaran tapi malas mencari tahu',2),
(10,'D','Tidak pernah terlalu peduli dengan fenomena alam',1),
(11,'A','Sangat mudah bergaul, punya banyak teman',4),
(11,'B','Cukup mudah bergaul',3),
(11,'C','Selektif dalam berteman',2),
(11,'D','Lebih suka sendiri / introvert',1),
(12,'A','Sangat suka, itu panggilan hidupku',4),
(12,'B','Suka membantu kalau diminta',3),
(12,'C','Kadang-kadang saja',2),
(12,'D','Tidak terlalu suka urusan orang lain',1),
(13,'A','Sangat tertarik dan sering berdiskusi',4),
(13,'B','Cukup tertarik, mengikuti perkembangan berita',3),
(13,'C','Kadang mengikuti kalau ada info menarik',2),
(13,'D','Tidak tertarik dengan isu-isu tersebut',1),
(14,'A','Sangat nyaman, suka jadi pemimpin/presenter',4),
(14,'B','Cukup nyaman walau sedikit nervous',3),
(14,'C','Kurang nyaman tapi bisa kalau dipaksa',2),
(14,'D','Tidak nyaman, lebih suka di belakang layar',1),
(15,'A','Sangat peduli dan sering terlibat isu sosial',4),
(15,'B','Cukup peduli dan mengikuti perkembangan',3),
(15,'C','Kadang-kadang peduli',2),
(15,'D','Kurang peduli, fokus urusan sendiri',1),
(16,'A','Sangat suka, menggambar adalah hobinya',4),
(16,'B','Suka menggambar di waktu luang',3),
(16,'C','Pernah mencoba tapi tidak rutin',2),
(16,'D','Tidak suka menggambar',1),
(17,'A','Sangat tertarik dan punya banyak referensi desain',4),
(17,'B','Tertarik dan suka mengamati desain bagus',3),
(17,'C','Biasa saja',2),
(17,'D','Tidak tertarik dengan dunia desain',1),
(18,'A','Sangat kuat, selalu perhatikan detail estetika',4),
(18,'B','Cukup sensitif terhadap keindahan',3),
(18,'C','Biasa saja',2),
(18,'D','Tidak terlalu perhatikan hal estetika',1),
(19,'A','Sangat suka, aktif bermusik atau di dunia seni',4),
(19,'B','Suka menikmati seni walau tidak aktif berkarya',3),
(19,'C','Sesekali saja',2),
(19,'D','Tidak terlalu menikmati',1),
(20,'A','Selalu punya ide kreatif dan tidak konvensional',4),
(20,'B','Sering punya ide baru yang menarik',3),
(20,'C','Kadang-kadang',2),
(20,'D','Lebih suka mengikuti prosedur yang sudah ada',1),
(21,'A','Sangat tertarik, sudah pernah berjualan',4),
(21,'B','Tertarik dan ingin mencoba wirausaha',3),
(21,'C','Agak tertarik tapi masih ragu',2),
(21,'D','Tidak tertarik dengan dunia bisnis',1),
(22,'A','Sangat suka, selalu catat keuangan dengan detail',4),
(22,'B','Suka dan sudah punya kebiasaan mencatat',3),
(22,'C','Sesekali mencatat kalau dibutuhkan',2),
(22,'D','Tidak suka, keuangan itu membosankan',1),
(23,'A','Selalu membuat perencanaan detail sebelum bertindak',4),
(23,'B','Sering merencanakan untuk hal penting',3),
(23,'C','Kadang-kadang merencanakan',2),
(23,'D','Lebih suka spontan tanpa rencana',1),
(24,'A','Sangat suka, jago negosiasi dan persuasi',4),
(24,'B','Cukup suka dan cukup pintar negosiasi',3),
(24,'C','Biasa saja',2),
(24,'D','Tidak suka negosiasi',1),
(25,'A','Sangat tertarik, sering baca analisis ekonomi',4),
(25,'B','Cukup tertarik dengan tren pasar',3),
(25,'C','Sesekali saja',2),
(25,'D','Tidak tertarik sama sekali',1),
(26,'A','Sangat suka, membaca adalah kebutuhan harian',4),
(26,'B','Suka membaca terutama genre favorit',3),
(26,'C','Baca kalau ada keperluan saja',2),
(26,'D','Tidak suka membaca',1),
(27,'A','Sangat suka, sudah punya banyak karya tulis',4),
(27,'B','Suka menulis di waktu luang',3),
(27,'C','Pernah mencoba tapi tidak rutin',2),
(27,'D','Tidak suka menulis',1),
(28,'A','Sangat mudah, bisa 2-3 bahasa asing',4),
(28,'B','Cukup mudah, sudah lancar 1 bahasa asing',3),
(28,'C','Agak kesulitan tapi masih bisa',2),
(28,'D','Sulit sekali belajar bahasa asing',1),
(29,'A','Sangat suka dan sudah lancar berbicara',4),
(29,'B','Suka dan sedang berlatih',3),
(29,'C','Mau tapi masih malu',2),
(29,'D','Tidak tertarik berbicara bahasa asing',1),
(30,'A','Sangat tertarik, suka nulis artikel/reportase',4),
(30,'B','Tertarik dengan dunia jurnalistik',3),
(30,'C','Biasa saja',2),
(30,'D','Tidak tertarik',1),
(31,'A','Sangat tertarik, itu passionku',4),
(31,'B','Tertarik dan sering cari info kesehatan',3),
(31,'C','Cukup tertarik',2),
(31,'D','Tidak tertarik',1),
(32,'A','Sangat empatik, senang merawat dan membantu',4),
(32,'B','Cukup empatik',3),
(32,'C','Biasa saja',2),
(32,'D','Kurang empatik dalam hal merawat orang sakit',1),
(33,'A','Sama sekali tidak takut, sudah terbiasa',4),
(33,'B','Sedikit tidak nyaman tapi bisa menghadapinya',3),
(33,'C','Agak takut tapi bisa dipaksakan',2),
(33,'D','Sangat takut, tidak mau terlibat',1),
(34,'A','Sangat suka, sering baca buku/artikel medis',4),
(34,'B','Suka dan penasaran dengan ilmu medis',3),
(34,'C','Cukup suka',2),
(34,'D','Tidak tertarik dengan topik medis',1),
(35,'A','Sangat disiplin dan sangat teliti dalam segala hal',4),
(35,'B','Cukup disiplin dan teliti untuk hal penting',3),
(35,'C','Kadang disiplin kadang tidak',2),
(35,'D','Kurang disiplin dan sering teledor',1);

-- Aturan Forward Chaining
INSERT INTO aturan (kode_aturan,nama_aturan,kondisi_json,jurusan_id,prioritas,confidence) VALUES
('R001','Teknik Informatika - Teknologi Tinggi','{"skor_teknologi":{"min":14},"skor_sains":{"min":8},"nilai_matematika":{"min":75}}',1,9,92.00),
('R002','Sistem Informasi - Teknologi + Bisnis','{"skor_teknologi":{"min":10},"skor_bisnis":{"min":8},"nilai_matematika":{"min":70}}',2,8,88.00),
('R003','Teknik Komputer - Hardware Focus','{"skor_teknologi":{"min":12},"skor_sains":{"min":10},"nilai_ipa":{"min":75}}',3,8,86.00),
('R004','Data Science - Analitik Kuat','{"skor_teknologi":{"min":10},"skor_sains":{"min":12},"nilai_matematika":{"min":80}}',4,9,90.00),
('R005','Teknik Mesin - Sains + Eksak','{"skor_sains":{"min":14},"nilai_ipa":{"min":78},"nilai_matematika":{"min":75}}',5,8,87.00),
('R006','Teknik Elektro - Fisika + Tekno','{"skor_sains":{"min":12},"skor_teknologi":{"min":8},"nilai_ipa":{"min":80}}',6,8,88.00),
('R007','Teknik Sipil - Sains + Desain','{"skor_sains":{"min":12},"skor_seni":{"min":6},"nilai_matematika":{"min":72}}',7,7,85.00),
('R008','Fisika - Sains Murni','{"skor_sains":{"min":16},"nilai_ipa":{"min":82},"nilai_matematika":{"min":80}}',9,8,88.00),
('R009','Kimia - Sains Lab','{"skor_sains":{"min":14},"nilai_ipa":{"min":80}}',10,7,84.00),
('R010','Biologi - Sains Hayati','{"skor_sains":{"min":12},"skor_kesehatan":{"min":6},"nilai_ipa":{"min":75}}',11,7,83.00),
('R011','Matematika - Logika Tinggi','{"skor_sains":{"min":16},"nilai_matematika":{"min":85}}',12,8,88.00),
('R012','Psikologi - Sosial + Empati','{"skor_sosial":{"min":14},"nilai_ips":{"min":72}}',13,8,87.00),
('R013','Hukum - Sosial + Bahasa','{"skor_sosial":{"min":12},"skor_bahasa":{"min":8},"nilai_ips":{"min":75}}',14,8,86.00),
('R014','Komunikasi - Sosial + Kreativitas','{"skor_sosial":{"min":10},"skor_seni":{"min":8},"skor_bahasa":{"min":6}}',15,7,84.00),
('R015','HI - Sosial + Bahasa Tinggi','{"skor_sosial":{"min":12},"skor_bahasa":{"min":12},"nilai_bahasa_ing":{"min":78}}',16,8,86.00),
('R016','DKV - Seni + Kreativitas Tinggi','{"skor_seni":{"min":16},"nilai_seni":{"min":78}}',17,9,90.00),
('R017','Desain Interior - Seni + Teknis','{"skor_seni":{"min":14},"skor_sains":{"min":6}}',18,8,86.00),
('R018','Arsitektur - Seni + Sains','{"skor_seni":{"min":12},"skor_sains":{"min":10},"nilai_matematika":{"min":72}}',20,8,87.00),
('R019','Manajemen - Bisnis Tinggi','{"skor_bisnis":{"min":14},"nilai_ips":{"min":72}}',21,8,86.00),
('R020','Akuntansi - Bisnis + Matematika','{"skor_bisnis":{"min":12},"nilai_matematika":{"min":75},"nilai_ips":{"min":72}}',22,8,87.00),
('R021','Ekonomi - Bisnis + Analitik','{"skor_bisnis":{"min":12},"skor_sains":{"min":8},"nilai_matematika":{"min":75}}',23,7,85.00),
('R022','Sastra Inggris - Bahasa Tinggi','{"skor_bahasa":{"min":16},"nilai_bahasa_ing":{"min":82}}',26,9,90.00),
('R023','Sastra Indonesia - Bahasa + Tulis','{"skor_bahasa":{"min":14},"nilai_bahasa_ind":{"min":80}}',25,8,86.00),
('R024','Kedokteran - Sains + Kesehatan Tinggi','{"skor_kesehatan":{"min":14},"skor_sains":{"min":14},"nilai_ipa":{"min":88},"nilai_matematika":{"min":82}}',28,10,95.00),
('R025','Farmasi - Kimia + Kesehatan','{"skor_kesehatan":{"min":10},"skor_sains":{"min":12},"nilai_ipa":{"min":78}}',30,8,87.00),
('R026','Gizi - Kesehatan + Biologi','{"skor_kesehatan":{"min":12},"skor_sains":{"min":8},"nilai_ipa":{"min":72}}',31,7,84.00),
('R027','Keperawatan - Kesehatan + Empati','{"skor_kesehatan":{"min":12},"skor_sosial":{"min":8}}',32,8,85.00),
('R028','Kedokteran Gigi - Sains + Teliti','{"skor_kesehatan":{"min":12},"skor_sains":{"min":10},"nilai_ipa":{"min":82}}',29,9,90.00);

-- Sample Siswa Data
INSERT INTO siswa (nis,nama,jenis_kelamin,tanggal_lahir,tempat_lahir,kelas,jurusan_sma,hobi,prestasi,nilai_matematika,nilai_ipa,nilai_ips,nilai_bahasa_ind,nilai_bahasa_ing,nilai_seni,nilai_olahraga,nilai_komputer,cita_cita)
VALUES
('2024001','Andi Pratama','L','2007-03-15','Jakarta','XII IPA 1','IPA','Coding, Gaming, Robotik','Juara 1 Olimpiade Komputer Kota 2023',88,82,75,78,80,70,75,92,'Software Engineer'),
('2024002','Siti Rahayu','P','2007-07-22','Bandung','XII IPS 2','IPS','Menggambar, Melukis, Desain Digital','Juara 2 Lomba Desain Poster Provinsi',72,68,85,88,78,92,80,75,'UI/UX Designer'),
('2024003','Budi Setiawan','L','2007-01-10','Surabaya','XII IPA 3','IPA','Biologi, Berkebun, Memasak','Juara 3 Olimpiade Biologi Regional',78,88,72,80,72,68,75,70,'Dokter'),
('2024004','Dewi Sartika','P','2007-09-05','Yogyakarta','XII IPS 1','IPS','Membaca, Menulis, Debat','Finalis Lomba Debat Bahasa Inggris Nasional',80,72,88,90,92,75,70,78,'Diplomat/HI'),
('2024005','Rizky Firmansyah','L','2007-12-18','Medan','XII IPA 2','IPA','Matematika, Fisika, Chess','Juara 1 Olimpiade Matematika Regional',95,90,68,75,75,65,72,85,'Data Scientist');
