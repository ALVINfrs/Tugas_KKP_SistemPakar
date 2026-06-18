package com.sistempakar.engine;

import com.google.gson.*;
import com.sistempakar.db.DBConnection;
import java.sql.*;
import java.util.*;

/**
 * Forward Chaining Expert System Engine
 * Mengolah fakta minat/bakat siswa → Merekomendasikan jurusan
 */
public class ForwardChaining {

    public static class Fakta {
        public int skorTeknologi;
        public int skorSains;
        public int skorSosial;
        public int skorSeni;
        public int skorBisnis;
        public int skorBahasa;
        public int skorKesehatan;
        public double nilaiMtk;
        public double nilaiIpa;
        public double nilaiIps;
        public double nilaiBahInd;
        public double nilaiBahIng;
        public double nilaiSeni;
        public String jurusanSma;

        @Override
        public String toString() {
            return String.format(
                "Teknologi=%d | Sains=%d | Sosial=%d | Seni=%d | Bisnis=%d | Bahasa=%d | Kesehatan=%d",
                skorTeknologi, skorSains, skorSosial, skorSeni, skorBisnis, skorBahasa, skorKesehatan
            );
        }
    }

    public static class HasilRekomendasi {
        public int jurusanId;
        public String namaJurusan;
        public String kategori;
        public double confidence;
        public int prioritas;
        public String kodeAturan;
        public String namaAturan;
        
        // --- TAMBAHAN FITUR BARU: ALASAN & STRATEGI ---
        public String alasanPakar; 
        public String strategiTembus;
        
        public List<RekomendasiUniv> universitas = new ArrayList<>();

        public static class RekomendasiUniv {
            public int univJurusanId;
            public String namaUniv;
            public String kota;
            public String provinsi;     // TAMBAHAN: Provinsi
            public String wilayah;
            public double peluangMasuk;
            public double peluangPersonal;
            public double fitScore;
            public String akreditasiProdi;
            public String akreditasiUniv;
            public double passingGradeSnbt; // TAMBAHAN: Passing Grade SNBT
            public double passingGradeSnbp; // TAMBAHAN: Passing Grade SNBP
            public long biayaKuliah;    // TAMBAHAN: Biaya Kuliah (UKT)
        }
    }

    /**
     * Jalankan forward chaining berdasarkan fakta siswa
     * @return List rekomendasi jurusan (max 3 untuk Utama, Alt1, Alt2)
     */
    public List<HasilRekomendasi> rekomendasikan(Fakta fakta) {
        List<HasilRekomendasi> hasil = new ArrayList<>();

        try {
            String sql = "SELECT a.id, a.kode_aturan, a.nama_aturan, a.kondisi_json, " +
                         "a.prioritas, a.confidence, j.id as jid, j.nama as jnama, km.nama as knama " +
                         "FROM aturan a " +
                         "JOIN jurusan_kuliah j ON a.jurusan_id = j.id " +
                         "JOIN kategori_minat km ON j.kategori_id = km.id " +
                         "WHERE a.aktif = true ORDER BY a.prioritas DESC, a.confidence DESC";

            ResultSet rs = DBConnection.executeQuery(sql);
            Gson gson = new Gson();

            while (rs.next()) {
                String kondisiJson = rs.getString("kondisi_json");
                if (evaluasiKondisi(kondisiJson, fakta, gson)) {
                    HasilRekomendasi hasil1 = new HasilRekomendasi();
                    hasil1.jurusanId  = rs.getInt("jid");
                    hasil1.namaJurusan = rs.getString("jnama");
                    hasil1.kategori   = rs.getString("knama");
                    hasil1.confidence = rs.getDouble("confidence");
                    hasil1.prioritas  = rs.getInt("prioritas");
                    hasil1.kodeAturan = rs.getString("kode_aturan");
                    hasil1.namaAturan = rs.getString("nama_aturan");
                    // Penyesuaian skor confidence berdasarkan seberapa kuat faktanya
                    hasil1.confidence = adjustConfidence(hasil1.confidence, kondisiJson, fakta, gson, hasil1.kategori);
                    hasil.add(hasil1);
                }
            }
            rs.getStatement().close();

            // Urutkan jurusan berdasarkan confidence tertinggi
            hasil.sort((a, b) -> Double.compare(b.confidence, a.confidence));

            // FALLBACK SYSTEM
            if (hasil.isEmpty()) {
                String fbSql = "SELECT j.id, j.nama, km.nama as knama FROM jurusan_kuliah j JOIN kategori_minat km ON j.kategori_id = km.id WHERE j.nama LIKE '%Manajemen%' OR j.nama LIKE '%Komunikasi%' OR j.nama LIKE '%Informatika%' LIMIT 3";
                ResultSet fbRs = DBConnection.executeQuery(fbSql);
                while(fbRs.next()) {
                    HasilRekomendasi fb = new HasilRekomendasi();
                    fb.jurusanId = fbRs.getInt("id");
                    fb.namaJurusan = fbRs.getString("nama") + " (Saran Adaptif)";
                    fb.kategori = fbRs.getString("knama");
                    fb.confidence = 45.0;
                    fb.prioritas = 1;
                    fb.kodeAturan = "FB-00";
                    fb.namaAturan = "Sistem Fallback (Skor Belum Memenuhi Standard)";
                    hasil.add(fb);
                }
                if (fbRs.getStatement() != null) fbRs.getStatement().close();
            }

            // FIX: Batasi rekomendasi maksimal 3 Jurusan (Utama, Alternatif 1, Alternatif 2)
            if (hasil.size() > 3) hasil = hasil.subList(0, 3);

            // FIX: Loop untuk menarik daftar Universitas di KETIGA jurusan tersebut
            for (HasilRekomendasi h : hasil) {
                h.universitas = getUniversitasRekomendasi(h.jurusanId, fakta);
                h.alasanPakar = generateAlasanPakar(h, fakta);
                h.strategiTembus = generateStrategiTembus(h, fakta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasil;
    }

    // =========================================================================
    // FITUR BARU: GENERATOR ALASAN LOGIS (MENGAPA COCOK?)
    // =========================================================================
    private String generateAlasanPakar(HasilRekomendasi h, Fakta f) {
        double avgMtkIpa = (f.nilaiMtk + f.nilaiIpa) / 2.0;
        double avgSosBhs = (f.nilaiIps + f.nilaiBahInd + f.nilaiBahIng) / 3.0;
        
        String mapelKuat;
        double skorDominan;

        if (h.kategori.contains("Sains") || h.kategori.contains("Teknologi") || h.kategori.contains("Kesehatan")) {
            mapelKuat = "Matematika & Sains";
            skorDominan = avgMtkIpa;
        } else if (h.kategori.contains("Sosial") || h.kategori.contains("Bisnis") || h.kategori.contains("Bahasa")) {
            mapelKuat = "Sosial & Bahasa";
            skorDominan = avgSosBhs;
        } else {
            mapelKuat = "Seni & Kreativitas";
            skorDominan = f.nilaiSeni > 0 ? f.nilaiSeni : avgSosBhs;
        }

        String analisis;
        if (skorDominan >= 85) {
            analisis = "Hal ini ditopang oleh fondasi akademik Anda yang sangat cemerlang di bidang " + mapelKuat + " (Rata-rata: " + String.format(Locale.US, "%.1f", skorDominan) + ").";
        } else if (skorDominan >= 75) {
            analisis = "Hal ini didukung oleh nilai " + mapelKuat + " Anda yang memadai (Rata-rata: " + String.format(Locale.US, "%.1f", skorDominan) + ") sebagai syarat dasar perkuliahan.";
        } else {
            analisis = "Namun, Anda perlu meningkatkan intensitas belajar pada mata pelajaran " + mapelKuat + " untuk mengimbangi standar akademik jurusan ini.";
        }

        // Lintas Jurusan Warning
        if (f.jurusanSma != null && !f.jurusanSma.isEmpty()) {
            String sma = f.jurusanSma.toUpperCase();
            boolean isIpa = sma.contains("IPA") || sma.contains("MIPA");
            boolean isIps = sma.contains("IPS") || sma.contains("IIS") || sma.contains("SOSIAL");

            if (isIps && (h.kategori.contains("Sains") || h.kategori.contains("Teknologi") || h.kategori.contains("Kesehatan"))) {
                analisis += " ⚠️ PERINGATAN: Anda dari jurusan IPS yang akan masuk Saintek. Peluang seleksi SNBP/SNBT mungkin jauh lebih sulit. Pastikan usaha ekstra.";
            } else if (isIpa && (h.kategori.contains("Sosial") || h.kategori.contains("Bisnis") || h.kategori.contains("Bahasa"))) {
                analisis += " ⚠️ INFO: Anda mengambil lintas jurusan ke Soshum. Bersiaplah menguasai materi Soshum UTBK dengan baik.";
            }
        }

        return "Berdasarkan hasil assemen psikologi, profil kognitif Anda menunjukkan kecenderungan minat yang dominan pada rumpun " + h.kategori + ". " + analisis;
    }

    // =========================================================================
    // FITUR BARU: GENERATOR STRATEGI SNBP & SNBT
    // =========================================================================
    private String generateStrategiTembus(HasilRekomendasi h, Fakta f) {
        if (h.universitas.isEmpty()) {
            return "Terus tingkatkan nilai rapor dan persiapan UTBK secara mandiri.";
        }

        HasilRekomendasi.RekomendasiUniv topUniv = h.universitas.get(0);
        double estUtbk = ((f.nilaiMtk + f.nilaiIpa + f.nilaiIps + f.nilaiBahInd + f.nilaiBahIng) / 5.0) * 8.5;
        
        StringBuilder strat = new StringBuilder();
        
        strat.append("Untuk jalur SNBT (UTBK), PTN unggulan di jurusan ini (seperti ").append(topUniv.namaUniv).append(") mematok Passing Grade sekitar ")
             .append(String.format(Locale.US, "%.0f", topUniv.passingGradeSnbt)).append(". ");
             
        if (estUtbk >= topUniv.passingGradeSnbt) {
            strat.append("Estimasi skor UTBK Anda saat ini (").append(String.format(Locale.US, "%.0f", estUtbk))
                 .append(") sudah SANGAT AMAN. Pertahankan konsistensi belajar agar tidak turun.");
        } else {
            double gap = topUniv.passingGradeSnbt - estUtbk;
            strat.append("Estimasi skor UTBK Anda (").append(String.format(Locale.US, "%.0f", estUtbk))
                 .append(") MASIH KURANG ").append(String.format(Locale.US, "%.0f", gap))
                 .append(" poin. Wajib tambah porsi TryOut UTBK berkala.");
        }

        if (topUniv.passingGradeSnbp > 0) {
            strat.append("\nUntuk jalur SNBP (Undangan), target indeks rapor yang dibutuhkan minimal ")
                 .append(topUniv.passingGradeSnbp).append(". Jaga grafik nilai selalu naik dan kumpulkan sertifikat lomba.");
        }

        return strat.toString();
    }

    private boolean evaluasiKondisi(String json, Fakta f, Gson gson) {
        try {
            JsonObject cond = gson.fromJson(json, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : cond.entrySet()) {
                String key = entry.getKey();
                JsonObject range = entry.getValue().getAsJsonObject();
                double actual = getFaktaValue(key, f);
                if (range.has("min") && actual < range.get("min").getAsDouble()) return false;
                if (range.has("max") && actual > range.get("max").getAsDouble()) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private double adjustConfidence(double base, String json, Fakta f, Gson gson, String kategoriJurusan) {
        try {
            JsonObject cond = gson.fromJson(json, JsonObject.class);
            double bonus = 0;
            for (Map.Entry<String, JsonElement> entry : cond.entrySet()) {
                String key = entry.getKey();
                JsonObject range = entry.getValue().getAsJsonObject();
                double actual = getFaktaValue(key, f);
                double min = range.has("min") ? range.get("min").getAsDouble() : 0;
                // Ekstra confidence jika nilai melampaui batas minimum secara signifikan
                if (actual > min) {
                    bonus += Math.min(2.0, (actual - min) * 0.3);
                }
            }
            
            // Penalty lintas jurusan
            if (f.jurusanSma != null && !f.jurusanSma.isEmpty() && kategoriJurusan != null) {
                String sma = f.jurusanSma.toUpperCase();
                boolean isIpa = sma.contains("IPA") || sma.contains("MIPA");
                boolean isIps = sma.contains("IPS") || sma.contains("IIS") || sma.contains("SOSIAL");

                if (isIps && (kategoriJurusan.contains("Sains") || kategoriJurusan.contains("Teknologi") || kategoriJurusan.contains("Kesehatan"))) {
                    bonus -= 30.0; // Penalty besar untuk anak IPS masuk Saintek/Medis
                } else if (isIpa && (kategoriJurusan.contains("Sosial") || kategoriJurusan.contains("Bisnis") || kategoriJurusan.contains("Bahasa"))) {
                    bonus -= 10.0; // Penalty ringan untuk anak IPA lintas jurusan
                }
            }
            
            return Math.max(0.1, Math.min(99.0, base + bonus));
        } catch (Exception e) {
            return base;
        }
    }

    private double getFaktaValue(String key, Fakta f) {
        switch (key) {
            case "skor_teknologi":  return f.skorTeknologi;
            case "skor_sains":      return f.skorSains;
            case "skor_sosial":     return f.skorSosial;
            case "skor_seni":       return f.skorSeni;
            case "skor_bisnis":     return f.skorBisnis;
            case "skor_bahasa":     return f.skorBahasa;
            case "skor_kesehatan":  return f.skorKesehatan;
            case "nilai_matematika":return f.nilaiMtk;
            case "nilai_ipa":       return f.nilaiIpa;
            case "nilai_ips":       return f.nilaiIps;
            case "nilai_bahasa_ind":return f.nilaiBahInd;
            case "nilai_bahasa_ing":return f.nilaiBahIng;
            case "nilai_seni":      return f.nilaiSeni;
            default: return 0;
        }
    }

    // ── FIX LOGIKA PENCARIAN KAMPUS (DINAMIS SESUAI NILAI SISWA) ──
    private List<HasilRekomendasi.RekomendasiUniv> getUniversitasRekomendasi(int jurusanId, Fakta fakta) {
        List<HasilRekomendasi.RekomendasiUniv> list = new ArrayList<>();
        try {
            // Mengambil SEMUA Kampus untuk jurusan ini
            String sql = "SELECT uj.id, u.nama, u.kota, u.provinsi, u.wilayah, u.akreditasi, " +
                         "uj.peluang_masuk, uj.akreditasi_prodi, uj.passing_grade_snbt, uj.passing_grade_snbp, uj.biaya_kuliah " +
                         "FROM universitas_jurusan uj " +
                         "JOIN universitas u ON uj.universitas_id = u.id " +
                         "WHERE uj.jurusan_id = ?";
            
            ResultSet rs = DBConnection.executeQuery(sql, jurusanId);
            while (rs.next()) {
                HasilRekomendasi.RekomendasiUniv ru = new HasilRekomendasi.RekomendasiUniv();
                ru.univJurusanId   = rs.getInt("id");
                ru.namaUniv        = rs.getString("nama");
                ru.kota            = rs.getString("kota");
                ru.provinsi        = rs.getString("provinsi");
                ru.wilayah         = rs.getString("wilayah");
                ru.peluangMasuk    = rs.getDouble("peluang_masuk");
                ru.akreditasiProdi = rs.getString("akreditasi_prodi");
                ru.akreditasiUniv  = rs.getString("akreditasi");
                ru.passingGradeSnbt = rs.getDouble("passing_grade_snbt");
                ru.passingGradeSnbp = rs.getDouble("passing_grade_snbp");
                ru.biayaKuliah     = rs.getLong("biaya_kuliah");
                
                // Peluang dihitung matematis murni sesuai dengan rapor asli
                ru.peluangPersonal = hitungPeluangPersonal(ru.peluangMasuk, ru.passingGradeSnbt, fakta);
                list.add(ru);
            }
            if(rs.getStatement() != null) rs.getStatement().close();
            
            // --- PIRAMIDA REKOMENDASI (SAFE vs ASPIRATIONAL) ---
            list.sort((a, b) -> Double.compare(b.peluangPersonal, a.peluangPersonal));
            
            List<HasilRekomendasi.RekomendasiUniv> finalList = new ArrayList<>();
            for (int k = 0; k < Math.min(3, list.size()); k++) {
                finalList.add(list.get(k));
            }
            
            List<HasilRekomendasi.RekomendasiUniv> remaining = new ArrayList<>(list);
            remaining.removeAll(finalList);
            remaining.removeIf(u -> u.peluangPersonal < 40.0);
            remaining.sort((a, b) -> Double.compare(b.passingGradeSnbt, a.passingGradeSnbt));
            
            for (int k = 0; k < Math.min(2, remaining.size()); k++) {
                finalList.add(remaining.get(k));
            }
            
            if (finalList.size() < 5) {
                List<HasilRekomendasi.RekomendasiUniv> safetyFill = new ArrayList<>(list);
                safetyFill.removeAll(finalList);
                for (int k = 0; k < safetyFill.size() && finalList.size() < 5; k++) {
                    finalList.add(safetyFill.get(k));
                }
            }
            
            list = finalList;
            
            // Batasi 5 hasil PTN yang paling relevan dengan kapasitas siswa
            if (list.size() > 5) {
                list = list.subList(0, 5);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── ALGORITMA ESTIMASI PELUANG MATEMATIS (TANPA RANDOM) ──
    private double hitungPeluangPersonal(double peluangBase, double passingGrade, Fakta f) {
        double nilaiRata = (f.nilaiMtk + f.nilaiIpa + f.nilaiIps + f.nilaiBahInd + f.nilaiBahIng) / 5.0;
        double nilaiEstimasi = nilaiRata * 8.5; 

        if (passingGrade == 0 || nilaiEstimasi == 0) return peluangBase;

        double selisih = nilaiEstimasi - passingGrade;

        if (selisih >= 20) {
            return Math.min(95.0, 80.0 + (selisih * 0.15)); 
        } else if (selisih >= 0) {
            return Math.min(79.9, 60.0 + (selisih * 0.5));
        } else if (selisih >= -30) {
            return Math.max(35.0, 60.0 + selisih);
        } else {
            return Math.max(2.0, 35.0 + (selisih * 0.3));
        }
    }

    /**
     * Hitung skor per kategori dari jawaban psikotest
     */
    public static Fakta hitungSkorDariJawaban(Map<Integer, Integer> jawabanMap) {
        Fakta fakta = new Fakta();
        try {
            for (Map.Entry<Integer, Integer> entry : jawabanMap.entrySet()) {
                int pertanyaanId = entry.getKey();
                int jawabanId = entry.getValue();

                String sql = "SELECT pj.skor, pp.kategori_id, pp.bobot " +
                             "FROM pilihan_jawaban pj " +
                             "JOIN pertanyaan_psikotest pp ON pj.pertanyaan_id = pp.id " +
                             "WHERE pj.id = ? AND pj.pertanyaan_id = ?";
                ResultSet rs = DBConnection.executeQuery(sql, jawabanId, pertanyaanId);
                if (rs.next()) {
                    int skor = rs.getInt("skor") * rs.getInt("bobot");
                    int kategori = rs.getInt("kategori_id");
                    switch (kategori) {
                        case 1: fakta.skorTeknologi  += skor; break;
                        case 2: fakta.skorSains      += skor; break;
                        case 3: fakta.skorSosial     += skor; break;
                        case 4: fakta.skorSeni       += skor; break;
                        case 5: fakta.skorBisnis     += skor; break;
                        case 6: fakta.skorBahasa     += skor; break;
                        case 7: fakta.skorKesehatan  += skor; break;
                    }
                }
                rs.getStatement().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fakta;
    }

    // =========================================================================
    // FITUR LAMA: TETAP ADA 100% SESUAI REQUEST LU (TIDAK DIHAPUS)
    // =========================================================================
    public static String generateTipsBelajar(HasilRekomendasi rekomendasi) {
        StringBuilder tips = new StringBuilder();
        String jurusan = rekomendasi.namaJurusan;

        if (jurusan.contains("Informatika") || jurusan.contains("Komputer") || jurusan.contains("Data Science")) {
            tips.append("📌 Tips Menuju ").append(jurusan).append(":\n\n");
            tips.append("• Perkuat Matematika: pelajari aljabar linier, statistik, dan logika matematika\n");
            tips.append("• Mulai belajar programming: Python (data/AI) atau Java/C++ (software)\n");
            tips.append("• Ikuti kelas online Dicoding, Coursera, atau freeCodeCamp\n");
            tips.append("• Bangun portfolio proyek di GitHub\n");
            tips.append("• Pelajari struktur data dan algoritma\n");
            tips.append("• Persiapkan UTBK: fokus TPS & TKA Saintek (Matematika, Fisika)");
        } else if (jurusan.contains("Kedokteran")) {
            tips.append("📌 Tips Menuju Kedokteran:\n\n");
            tips.append("• Tingkatkan nilai Biologi, Kimia, dan Fisika ke angka 90+\n");
            tips.append("• Latihan soal UTBK intensif, terutama TKA Saintek\n");
            tips.append("• Ikuti bimbel khusus kedokteran (Primagama, Ganesha, dll)\n");
            tips.append("• Perkuat pemahaman anatomi dan fisiologi dasar\n");
            tips.append("• Magang/volunteer di klinik atau puskesmas\n");
            tips.append("• Target nilai UTBK minimal 700+ untuk PTN favorit");
        } else if (jurusan.contains("Hukum")) {
            tips.append("📌 Tips Menuju Hukum:\n\n");
            tips.append("• Banyak membaca berita dan kasus hukum terkini\n");
            tips.append("• Perkuat kemampuan membaca dan analisis teks\n");
            tips.append("• Ikuti ekskul debat atau moot court di sekolah\n");
            tips.append("• Pelajari Pancasila, UUD 1945, dan sistem hukum Indonesia\n");
            tips.append("• Latihan menulis argumentasi yang terstruktur\n");
            tips.append("• Persiapkan TPS UTBK terutama pemahaman bacaan");
        } else if (jurusan.contains("Desain") || jurusan.contains("Seni") || jurusan.contains("Arsitektur")) {
            tips.append("📌 Tips Menuju ").append(jurusan).append(":\n\n");
            tips.append("• Bangun portofolio karya seni/desain yang beragam\n");
            tips.append("• Kuasai software: Adobe Illustrator, Photoshop, CorelDRAW\n");
            tips.append("• Ikuti lomba desain dan seni untuk pengalaman\n");
            tips.append("• Belajar prinsip desain: komposisi, warna, tipografi\n");
            tips.append("• Aktif di komunitas kreatif online (Behance, Dribbble)\n");
            tips.append("• Persiapkan tes portfolio/kemampuan khusus PTN seni");
        } else if (jurusan.contains("Manajemen") || jurusan.contains("Akuntansi") || jurusan.contains("Ekonomi")) {
            tips.append("📌 Tips Menuju ").append(jurusan).append(":\n\n");
            tips.append("• Pelajari dasar-dasar akuntansi dan pembukuan\n");
            tips.append("• Ikuti simulasi bisnis atau Business Plan Competition\n");
            tips.append("• Baca buku bisnis: Rich Dad Poor Dad, Zero to One\n");
            tips.append("• Perkuat Matematika dan kemampuan analisis data\n");
            tips.append("• Mulai belajar investasi saham atau pasar modal sederhana\n");
            tips.append("• Persiapkan TPS UTBK: penalaran kuantitatif");
        } else if (jurusan.contains("Psikologi")) {
            tips.append("📌 Tips Menuju Psikologi:\n\n");
            tips.append("• Banyak membaca buku psikologi populer\n");
            tips.append("• Aktif dalam kegiatan sosial dan komunitas\n");
            tips.append("• Kembangkan empati dan kemampuan mendengarkan\n");
            tips.append("• Ikuti seminar atau webinar psikologi\n");
            tips.append("• Pelajari statistik dasar (penting untuk riset psikologi)\n");
            tips.append("• Persiapkan TPS dan TKA Soshum UTBK");
        } else if (jurusan.contains("Bahasa") || jurusan.contains("Sastra") || jurusan.contains("Komunikasi")) {
            tips.append("📌 Tips Menuju ").append(jurusan).append(":\n\n");
            tips.append("• Perbanyak membaca literatur dalam dan luar negeri\n");
            tips.append("• Mulai ngeblog atau nulis di platform online\n");
            tips.append("• Tingkatkan kemampuan bahasa Inggris (TOEFL/IELTS)\n");
            tips.append("• Ikuti lomba menulis, debat, atau jurnalistik\n");
            tips.append("• Tonton film/series berbahasa asing untuk melatih pendengaran\n");
            tips.append("• Persiapkan TKA Soshum UTBK");
        } else {
            tips.append("📌 Tips Umum Menuju ").append(jurusan).append(":\n\n");
            tips.append("• Perkuat mata pelajaran yang relevan dengan jurusan\n");
            tips.append("• Ikuti bimbingan belajar atau les intensif\n");
            tips.append("• Latihan soal UTBK dari tahun-tahun sebelumnya\n");
            tips.append("• Bangun portofolio atau prestasi yang relevan\n");
            tips.append("• Konsultasikan target PTN dengan guru BK\n");
            tips.append("• Jaga kesehatan dan manajemen waktu belajar");
        }

        return tips.toString();
    }

    public static String generateRekomendasiKegiatan(HasilRekomendasi rekomendasi) {
        String jurusan = rekomendasi.namaJurusan;
        StringBuilder sb = new StringBuilder("🎯 Rekomendasi Kegiatan:\n");

        if (jurusan.contains("Informatika") || jurusan.contains("Data") || jurusan.contains("Sistem Informasi")) {
            sb.append("• Ikuti hackathon atau programming contest\n");
            sb.append("• Bergabung di komunitas developer (GitHub, Stack Overflow)\n");
            sb.append("• Buat aplikasi sederhana sebagai proyek latihan\n");
            sb.append("• Ikuti program Google Developer Student Club di PTN tujuan");
        } else if (jurusan.contains("Kedokteran") || jurusan.contains("Farmasi") || jurusan.contains("Kesehatan")) {
            sb.append("• Volunteer PMI atau kegiatan kesehatan masyarakat\n");
            sb.append("• Ikuti olimpiade biologi atau kimia\n");
            sb.append("• Magang di klinik atau apotek terdekat\n");
            sb.append("• Gabung komunitas first aid / pertolongan pertama");
        } else if (jurusan.contains("Seni") || jurusan.contains("Desain")) {
            sb.append("• Ikut pameran seni atau kompetisi desain\n");
            sb.append("• Buat akun portofolio di Behance/Instagram\n");
            sb.append("• Bergabung komunitas seni atau club desain sekolah\n");
            sb.append("• Freelance desain untuk menambah pengalaman");
        } else if (jurusan.contains("Bisnis") || jurusan.contains("Ekonomi") || jurusan.contains("Manajemen")) {
            sb.append("• Ikuti Young Entrepreneur Competition\n");
            sb.append("• Coba berjualan online sebagai latihan bisnis\n");
            sb.append("• Bergabung OSIS divisi kewirausahaan\n");
            sb.append("• Baca laporan keuangan perusahaan publik");
        } else {
            sb.append("• Aktif dalam organisasi siswa yang relevan\n");
            sb.append("• Ikuti olimpiade sesuai bidang minat\n");
            sb.append("• Magang atau shadow profesional di bidang tersebut\n");
            sb.append("• Bangun jaringan dengan mahasiswa PTN tujuan");
        }
        return sb.toString();
    }
}