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
        public List<RekomendasiUniv> universitas = new ArrayList<>();

        public static class RekomendasiUniv {
            public int univJurusanId;
            public String namaUniv;
            public String kota;
            public String wilayah;
            public double peluangMasuk;
            public double peluangPersonal;
            public String akreditasiProdi;
            public String akreditasiUniv;
        }
    }

    /**
     * Jalankan forward chaining berdasarkan fakta siswa
     * @return List rekomendasi jurusan (max 5, sorted by confidence)
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
                    // Adjust confidence based on how strongly facts match
                    hasil1.confidence = adjustConfidence(hasil1.confidence, kondisiJson, fakta, gson);
                    hasil.add(hasil1);
                }
            }
            rs.getStatement().close();

            // Sort by confidence desc
            hasil.sort((a, b) -> Double.compare(b.confidence, a.confidence));

            // Limit to top 5
            if (hasil.size() > 5) hasil = hasil.subList(0, 5);

            // Enrich with university data
            for (HasilRekomendasi h : hasil) {
                h.universitas = getUniversitasRekomendasi(h.jurusanId, fakta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasil;
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

    private double adjustConfidence(double base, String json, Fakta f, Gson gson) {
        try {
            JsonObject cond = gson.fromJson(json, JsonObject.class);
            double bonus = 0;
            for (Map.Entry<String, JsonElement> entry : cond.entrySet()) {
                String key = entry.getKey();
                JsonObject range = entry.getValue().getAsJsonObject();
                double actual = getFaktaValue(key, f);
                double min = range.has("min") ? range.get("min").getAsDouble() : 0;
                // Extra confidence for exceeding minimum by a lot
                if (actual > min) {
                    bonus += Math.min(2.0, (actual - min) * 0.3);
                }
            }
            return Math.min(99.0, base + bonus);
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

    private List<HasilRekomendasi.RekomendasiUniv> getUniversitasRekomendasi(int jurusanId, Fakta fakta) {
        List<HasilRekomendasi.RekomendasiUniv> list = new ArrayList<>();
        try {
            String sql = "SELECT uj.id, u.nama, u.kota, u.wilayah, u.akreditasi, " +
                         "uj.peluang_masuk, uj.akreditasi_prodi, uj.passing_grade_snbt " +
                         "FROM universitas_jurusan uj " +
                         "JOIN universitas u ON uj.universitas_id = u.id " +
                         "WHERE uj.jurusan_id = ? ORDER BY uj.peluang_masuk DESC";
            ResultSet rs = DBConnection.executeQuery(sql, jurusanId);
            while (rs.next()) {
                HasilRekomendasi.RekomendasiUniv ru = new HasilRekomendasi.RekomendasiUniv();
                ru.univJurusanId   = rs.getInt("id");
                ru.namaUniv        = rs.getString("nama");
                ru.kota            = rs.getString("kota");
                ru.wilayah         = rs.getString("wilayah");
                ru.peluangMasuk    = rs.getDouble("peluang_masuk");
                ru.akreditasiProdi = rs.getString("akreditasi_prodi");
                ru.akreditasiUniv  = rs.getString("akreditasi");
                // Peluang personal disesuaikan nilai siswa vs passing grade
                ru.peluangPersonal = hitungPeluangPersonal(ru.peluangMasuk,
                    rs.getDouble("passing_grade_snbt"), fakta);
                list.add(ru);
            }
            rs.getStatement().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private double hitungPeluangPersonal(double peluangBase, double passingGrade, Fakta f) {
        // Estimasi rata-rata nilai siswa (konversi ke skala 1000)
        double nilaiRata = (f.nilaiMtk + f.nilaiIpa + f.nilaiIps + f.nilaiBahInd + f.nilaiBahIng) / 5.0;
        double nilaiEstimasi = nilaiRata * 7.5; // rough conversion to UTBK scale

        if (passingGrade == 0 || nilaiEstimasi == 0) return peluangBase;

        double ratio = nilaiEstimasi / passingGrade;
        if (ratio >= 1.05)       return Math.min(peluangBase * 1.5, 45.0);
        else if (ratio >= 1.0)   return Math.min(peluangBase * 1.2, 35.0);
        else if (ratio >= 0.95)  return peluangBase * 0.9;
        else if (ratio >= 0.90)  return peluangBase * 0.6;
        else                     return peluangBase * 0.3;
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

    /**
     * Generate tips belajar berdasarkan jurusan rekomendasi
     */
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

    /**
     * Generate kegiatan rekomendasi
     */
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
