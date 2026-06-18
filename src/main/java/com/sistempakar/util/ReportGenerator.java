package com.sistempakar.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sistempakar.db.DBConnection;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility for generating complex PDF and Excel reports (Enterprise Expert Edition)
 */
public class ReportGenerator {

    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
    private static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.GRAY);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font FONT_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    private static final Font FONT_SMALL = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY);
    private static final BaseColor COLOR_PRIMARY = new BaseColor(30, 45, 90);
    private static final BaseColor COLOR_SUCCESS = new BaseColor(40, 167, 69);
    private static final BaseColor COLOR_WARNING = new BaseColor(255, 193, 7);
    private static final BaseColor COLOR_DANGER = new BaseColor(220, 53, 69);

    /**
     * Generates a premium individual consultation report
     */
    public static void generatePremiumReport(int konsultasiId, String savePath) throws Exception {
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(savePath));
        
        // Add Header/Footer Event
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                Phrase footer = new Phrase("Sistem Pakar Rekomendasi Jurusan v1.0 | SMAN 1 CONTOH KOTA | Halaman " + writer.getPageNumber(), FONT_SMALL);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, 
                    (document.right() - document.left()) / 2 + document.leftMargin(), 
                    document.bottom() - 20, 0);
            }
        });

        document.open();

        // 1. Fetch Data Lengkap (Termasuk Alternatif 1 & 2 dari JOIN yang dimodifikasi)
        ResultSet rs = DBConnection.executeQuery(
            "SELECT k.*, s.nis, s.nama as s_nama, s.kelas, s.jurusan_sma, s.jenis_kelamin, s.tempat_lahir, s.tanggal_lahir, " +
            "s.email as s_email, s.telepon as s_telepon, s.nama_ortu, s.hobi, s.prestasi, s.cita_cita, " +
            "j1.id as r1_id, j1.nama as r1_nama, j1.rumpun as r1_rumpun, j1.deskripsi as r1_desk, j1.prospek_kerja as r1_prospek, " +
            "j2.id as r2_id, j2.nama as r2_nama, j2.rumpun as r2_rumpun, j2.deskripsi as r2_desk, j2.prospek_kerja as r2_prospek, " +
            "j3.id as r3_id, j3.nama as r3_nama, j3.rumpun as r3_rumpun, j3.deskripsi as r3_desk, j3.prospek_kerja as r3_prospek, " +
            "c.nama as c_nama, " +
            "s.nilai_matematika, s.nilai_ipa, s.nilai_ips, s.nilai_bahasa_ind, s.nilai_bahasa_ing, s.nilai_seni, s.nilai_olahraga, s.nilai_komputer " +
            "FROM konsultasi k " +
            "JOIN siswa s ON k.siswa_id = s.id " +
            "JOIN konselor c ON k.konselor_id = c.id " +
            "LEFT JOIN jurusan_kuliah j1 ON k.rekomendasi_utama_id = j1.id " +
            "LEFT JOIN jurusan_kuliah j2 ON k.rekomendasi_alt1_id = j2.id " +
            "LEFT JOIN jurusan_kuliah j3 ON k.rekomendasi_alt2_id = j3.id " +
            "WHERE k.id = ?", konsultasiId
        );

        if (!rs.next()) throw new Exception("Data konsultasi tidak ditemukan");

        // --- KOP SURAT ---
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        PdfPCell kopCell = new PdfPCell();
        kopCell.setBorder(Rectangle.NO_BORDER);
        kopCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Paragraph pTitle = new Paragraph("SMAN 1 CONTOH KOTA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, COLOR_PRIMARY));
        pTitle.setAlignment(Element.ALIGN_CENTER);
        Paragraph pAddr = new Paragraph("Jl. Pendidikan No. 1, Kota Contoh, Provinsi Jaya 12345", FONT_NORMAL);
        pAddr.setAlignment(Element.ALIGN_CENTER);
        Paragraph pContact = new Paragraph("Telp: (021) 1234567 | Email: info@sman1contoh.sch.id", FONT_NORMAL);
        pContact.setAlignment(Element.ALIGN_CENTER);
        
        kopCell.addElement(pTitle);
        kopCell.addElement(pAddr);
        kopCell.addElement(pContact);
        headerTable.addCell(kopCell);
        document.add(headerTable);
        
        LineSeparator line = new LineSeparator(2, 100, COLOR_PRIMARY, Element.ALIGN_CENTER, -5);
        document.add(new Chunk(line));
        document.add(new Chunk("\n"));
        
        Paragraph docTitle = new Paragraph("LAPORAN HASIL KONSULTASI KARIR & REKOMENDASI JURUSAN", FONT_TITLE);
        docTitle.setAlignment(Element.ALIGN_CENTER);
        docTitle.setSpacingAfter(20);
        document.add(docTitle);

        // --- SECTION I: PROFIL SISWA ---
        addSectionTitle(document, "I. PROFIL SISWA");
        PdfPTable tableSiswa = new PdfPTable(4);
        tableSiswa.setWidthPercentage(100);
        tableSiswa.setWidths(new float[]{1.5f, 3.5f, 1.5f, 3.5f});
        tableSiswa.setSpacingBefore(10);
        tableSiswa.setSpacingAfter(20);
        
        addInfoRow(tableSiswa, "Nama Lengkap", rs.getString("s_nama"));
        addInfoRow(tableSiswa, "NIS", rs.getString("nis"));
        
        String ttl = rs.getString("tempat_lahir") + ", " + (rs.getDate("tanggal_lahir") != null ? new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_lahir")) : "-");
        addInfoRow(tableSiswa, "Tempat, Tgl Lahir", ttl);
        addInfoRow(tableSiswa, "Jenis Kelamin", "L".equals(rs.getString("jenis_kelamin")) ? "Laki-laki" : "Perempuan");
        
        addInfoRow(tableSiswa, "Kelas / Jurusan", rs.getString("kelas") + " / " + rs.getString("jurusan_sma"));
        addInfoRow(tableSiswa, "Nama Orang Tua", rs.getString("nama_ortu") != null ? rs.getString("nama_ortu") : "-");
        
        addInfoRow(tableSiswa, "Cita-cita", rs.getString("cita_cita") != null ? rs.getString("cita_cita") : "-");
        addInfoRow(tableSiswa, "Tgl Konsultasi", new SimpleDateFormat("dd MMMM yyyy HH:mm").format(rs.getTimestamp("tanggal_konsultasi")));
        
        PdfPCell cSpan1 = new PdfPCell(new Phrase("Hobi / Prestasi", FONT_BOLD)); cSpan1.setBorder(Rectangle.NO_BORDER);
        PdfPCell cSpan2 = new PdfPCell(new Phrase(": " + (rs.getString("hobi") != null ? rs.getString("hobi") : "-") + " / " + (rs.getString("prestasi") != null ? rs.getString("prestasi") : "-"), FONT_NORMAL));
        cSpan2.setBorder(Rectangle.NO_BORDER); cSpan2.setColspan(3);
        tableSiswa.addCell(cSpan1); tableSiswa.addCell(cSpan2);
        
        document.add(tableSiswa);

        // --- SECTION II: NILAI RAPOR (FITUR LAMA TETAP ADA UTUH) ---
        addSectionTitle(document, "II. ANALISIS NILAI RAPOR");
        document.add(new Paragraph("Berikut adalah rincian nilai rata-rata rapor siswa:", FONT_NORMAL));
        
        PdfPTable tableRapor = new PdfPTable(4);
        tableRapor.setWidthPercentage(100);
        tableRapor.setWidths(new float[]{3, 2, 2, 3});
        tableRapor.setSpacingBefore(10);
        tableRapor.setSpacingAfter(20);
        
        addCell(tableRapor, "Mata Pelajaran", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableRapor, "Nilai Rata-rata", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableRapor, "Predikat", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableRapor, "Kategori", FONT_HEADER, COLOR_PRIMARY);
        
        String[] mapel = {"Matematika", "IPA", "IPS", "Bahasa Indonesia", "Bahasa Inggris", "Seni"};
        String[] dbFields = {"nilai_matematika", "nilai_ipa", "nilai_ips", "nilai_bahasa_ind", "nilai_bahasa_ing", "nilai_seni"};
        
        double valMtk = rs.getDouble("nilai_matematika");
        double valIpa = rs.getDouble("nilai_ipa");
        double valIps = rs.getDouble("nilai_ips");
        double valBind = rs.getDouble("nilai_bahasa_ind");
        double valBing = rs.getDouble("nilai_bahasa_ing");
        double valSeni = rs.getDouble("nilai_seni");
        
        // Kalkulasi UTBK untuk peluang PTN nanti
        double avgNilai = (valMtk + valIpa + valIps + valBind + valBing) / 5.0;
        double estUtbk = avgNilai * 8.5; 
        
        for (int i=0; i<mapel.length; i++) {
            double val = rs.getDouble(dbFields[i]);
            String predikat = val >= 85 ? "A (Sangat Baik)" : val >= 75 ? "B (Baik)" : val >= 65 ? "C (Cukup)" : "D (Kurang)";
            String kategori = val >= 80 ? "Sangat Memadai" : val >= 75 ? "Memadai" : "Perlu Peningkatan";
            
            addCell(tableRapor, mapel[i], FONT_NORMAL, BaseColor.WHITE);
            addCell(tableRapor, String.valueOf(val), FONT_BOLD, BaseColor.WHITE);
            addCell(tableRapor, predikat, FONT_NORMAL, BaseColor.WHITE);
            addCell(tableRapor, kategori, val >= 75 ? FONT_NORMAL : FONT_BOLD, val >= 75 ? BaseColor.WHITE : new BaseColor(255, 230, 230));
        }
        document.add(tableRapor);
        
        String raporDesc = String.format(Locale.US, "Berdasarkan hasil nilai rapor, siswa memiliki performa yang baik dengan nilai Matematika sebesar %.1f dan IPA sebesar %.1f. Hasil ini dapat menjadi dasar yang kuat untuk melanjutkan studi ke rumpun yang relevan.", valMtk, valIpa);
        Paragraph pRaporDesc = new Paragraph(raporDesc, FONT_NORMAL);
        pRaporDesc.setSpacingAfter(20);
        document.add(pRaporDesc);

        // --- SECTION III: HASIL PSIKOTEST (FIX: MAKS 32, BUKAN 20 LAGI) ---
        addSectionTitle(document, "III. HASIL ASESMEN MINAT & BAKAT");
        document.add(new Paragraph("Distribusi skor dari 35 pertanyaan psikotest berdasarkan 7 kategori minat utama:", FONT_NORMAL));
        
        PdfPTable tableSkor = new PdfPTable(4);
        tableSkor.setWidthPercentage(100);
        tableSkor.setWidths(new float[]{3, 1, 4, 2});
        tableSkor.setSpacingBefore(10);
        tableSkor.setSpacingAfter(20);
        
        addCell(tableSkor, "Kategori", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableSkor, "Skor", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableSkor, "Visualisasi (Maks 32)", FONT_HEADER, COLOR_PRIMARY); // FIXED TO 32
        addCell(tableSkor, "Level", FONT_HEADER, COLOR_PRIMARY);
        
        String[] cats = {"Teknologi", "Sains", "Sosial", "Seni", "Bisnis", "Bahasa", "Kesehatan"};
        String[] scoreFields = {
            "skor_teknologi", "skor_sains", "skor_sosial",
            "skor_seni", "skor_bisnis", "skor_bahasa", "skor_kesehatan"
        };
        
        for (int i=0; i<cats.length; i++) {
            int skor = rs.getInt(scoreFields[i]);
            
            // FIX: Rumus perhitungan persentase berdasarkan maksimal skor 32
            double pct = (skor / 32.0) * 100;
            String level = skor >= 24 ? "Tinggi" : skor >= 16 ? "Sedang" : "Rendah"; 
            
            // Text based progress bar
            int blocks = (int)Math.round((skor / 32.0) * 10);
            StringBuilder bar = new StringBuilder();
            for(int b=0; b<10; b++) bar.append(b < blocks ? "█" : "░");
            
            addCell(tableSkor, cats[i], FONT_NORMAL, BaseColor.WHITE);
            addCell(tableSkor, skor + "/32", FONT_BOLD, BaseColor.WHITE); // FIXED TO 32
            addCell(tableSkor, bar.toString() + " " + String.format(Locale.US, "%.1f%%", pct), FONT_NORMAL, BaseColor.WHITE);
            
            BaseColor lvlColor = skor >= 24 ? COLOR_SUCCESS : skor >= 16 ? COLOR_WARNING : COLOR_DANGER;
            PdfPCell cLvl = new PdfPCell(new Phrase(level, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, lvlColor)));
            cLvl.setHorizontalAlignment(Element.ALIGN_CENTER);
            cLvl.setPadding(5);
            tableSkor.addCell(cLvl);
        }
        document.add(tableSkor);

        // --- SECTION IV: REKOMENDASI 3 JURUSAN & ANALISIS PTN ---
        // (Ini gabungan dari Rekomendasi Jurusan dan Daftar PTN yang di-loop 3x)
        document.newPage();
        Paragraph pIntro = new Paragraph("IV. STRATEGI PEMILIHAN JURUSAN & TARGET PTN\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, COLOR_PRIMARY));
        document.add(pIntro);
        document.add(new Chunk(new LineSeparator(1, 100, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -2)));
        document.add(new Paragraph("Berdasarkan korelasi antara profil akademik dan hasil psikotes, berikut adalah 3 rekomendasi jurusan terbaik beserta analisis dan strategi lolos PTN:", FONT_NORMAL));

        String[] rLabels = {"PRIORITAS UTAMA", "ALTERNATIF 1", "ALTERNATIF 2"};
        String[] rIds = {"r1_id", "r2_id", "r3_id"};
        String[] rNames = {"r1_nama", "r2_nama", "r3_nama"};
        String[] rRumpuns = {"r1_rumpun", "r2_rumpun", "r3_rumpun"};
        String[] rDescs = {"r1_desk", "r2_desk", "r3_desk"};
        String[] rProspeks = {"r1_prospek", "r2_prospek", "r3_prospek"};

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        for (int i = 0; i < 3; i++) {
            int jurId = rs.getInt(rIds[i]);
            if (jurId == 0 || rs.getString(rNames[i]) == null) continue; // Skip jika alternatif kosong

            String jurusanNama = rs.getString(rNames[i]);
            String rumpun = rs.getString(rRumpuns[i]);

            // KOTAK JURUSAN
            PdfPTable tableRek = new PdfPTable(1);
            tableRek.setWidthPercentage(100);
            tableRek.setSpacingBefore(15);
            
            PdfPCell rekCell = new PdfPCell();
            rekCell.setPadding(15);
            rekCell.setBackgroundColor(new BaseColor(240, 244, 255)); // Light Blue
            rekCell.setBorderColor(COLOR_PRIMARY);
            
            Paragraph pRek = new Paragraph("⭐ " + rLabels[i] + " : " + jurusanNama, FONT_SUBTITLE);
            pRek.setSpacingAfter(5);
            rekCell.addElement(pRek);
            
            rekCell.addElement(new Paragraph("Rumpun Keilmuan: " + rumpun, FONT_BOLD));
            
            Paragraph pDesk = new Paragraph("Deskripsi: " + rs.getString(rDescs[i]), FONT_NORMAL);
            pDesk.setSpacingBefore(5);
            rekCell.addElement(pDesk);
            
            Paragraph pProspek = new Paragraph("Prospek Kerja: " + (rs.getString(rProspeks[i]) != null ? rs.getString(rProspeks[i]) : "-"), FONT_NORMAL);
            pProspek.setSpacingBefore(5);
            rekCell.addElement(pProspek);

            // FITUR BARU: ANALISIS PAKAR
            String analisis = generateAnalisisPakar(rumpun, valMtk, valIpa, valIps, valBind, valBing, valSeni);
            Paragraph pAnalisis = new Paragraph("\n🧠 Analisis Pakar: " + analisis, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.DARK_GRAY));
            rekCell.addElement(pAnalisis);

            tableRek.addCell(rekCell);
            document.add(tableRek);

            // TABEL UNIVERSITAS SPESIFIK JURUSAN INI (DENGAN SNBP & SNBT)
            PdfPTable tableUniv = new PdfPTable(7);
            tableUniv.setWidthPercentage(100);
            tableUniv.setSpacingBefore(10);
            tableUniv.setWidths(new float[]{3.5f, 2f, 1.5f, 1.5f, 1.5f, 2.5f, 1.5f});
            
            addCell(tableUniv, "PTN Target", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "Provinsi", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "Akreditasi", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "PG SNBP", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "PG SNBT", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "UKT / SMT", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "Peluang", FONT_HEADER, COLOR_PRIMARY);

            // FIX: QUERY BYPASS LANGSUNG KE MASTER DATA. JADI KAMPUS PASTI ADA (LIMIT 5)!
            ResultSet rsU = DBConnection.executeQuery(
                "SELECT u.nama as u_nama, u.provinsi, uj.akreditasi_prodi, uj.passing_grade_snbp, uj.passing_grade_snbt, uj.biaya_kuliah " +
                "FROM universitas_jurusan uj " +
                "JOIN universitas u ON uj.universitas_id = u.id " +
                "WHERE uj.jurusan_id = ? ORDER BY uj.passing_grade_snbt DESC LIMIT 5", 
                jurId
            );
            
            boolean hasUniv = false;
            double topPgSnbt = 0;
            double topPgSnbp = 0;
            String topUnivName = "";

            while (rsU.next()) {
                hasUniv = true;
                String univName = rsU.getString("u_nama");
                double pgSnbp = rsU.getDouble("passing_grade_snbp");
                double pgSnbt = rsU.getDouble("passing_grade_snbt");
                
                if(topPgSnbt == 0) {
                    topPgSnbt = pgSnbt; topPgSnbp = pgSnbp; topUnivName = univName;
                }

                // Kalkulasi matematis untuk peluang (tanpa random/gacha)
                double diff = estUtbk - pgSnbt;
                double peluang; String status; BaseColor col;
                
                if (diff >= 20) { peluang = Math.min(95.0, 80.0 + (diff * 0.15)); status = "Aman"; col = COLOR_SUCCESS; }
                else if (diff >= 0) { peluang = Math.min(79.9, 60.0 + (diff * 0.5)); status = "Cukup"; col = COLOR_SUCCESS; }
                else if (diff >= -30) { peluang = Math.max(35.0, 60.0 + diff); status = "Berisiko"; col = COLOR_WARNING; }
                else { peluang = Math.max(2.0, 35.0 + (diff * 0.3)); status = "Sulit"; col = COLOR_DANGER; }
                
                addCell(tableUniv, univName, FONT_NORMAL, BaseColor.WHITE);
                addCell(tableUniv, rsU.getString("provinsi"), FONT_NORMAL, BaseColor.WHITE);
                addCell(tableUniv, rsU.getString("akreditasi_prodi"), FONT_NORMAL, BaseColor.WHITE);
                addCell(tableUniv, String.valueOf(pgSnbp), FONT_NORMAL, BaseColor.WHITE);
                addCell(tableUniv, String.valueOf(pgSnbt), FONT_NORMAL, BaseColor.WHITE);
                addCell(tableUniv, nf.format(rsU.getLong("biaya_kuliah")).replace(",00", ""), FONT_NORMAL, BaseColor.WHITE);
                
                PdfPCell cStat = new PdfPCell(new Phrase(String.format(Locale.US, "%.1f%%\n(%s)", peluang, status), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, col)));
                cStat.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableUniv.addCell(cStat);
            }
            rsU.close();
            
            if(hasUniv) {
                document.add(tableUniv);
                // FITUR BARU: STRATEGI TEMBUS PTN
                String strategi = generateStrategiTembus(estUtbk, topPgSnbt, topPgSnbp, topUnivName);
                Paragraph pStrat = new Paragraph("🎯 Taktik Tembus PTN: " + strategi, FONT_BOLD);
                pStrat.setSpacingBefore(5); pStrat.setSpacingAfter(10);
                document.add(pStrat);
            } else {
                document.add(new Paragraph("Tidak ada data rekomendasi PTN di dalam sistem untuk jurusan ini.", FONT_NORMAL));
            }
        }

        // --- SECTION V: CATATAN & TIPS BELAJAR (FITUR LAMA TETAP ADA UTUH 100%) ---
        document.newPage(); 
        addSectionTitle(document, "V. CATATAN KONSELOR & STRATEGI UMUM");
        
        PdfPTable tableCatatan = new PdfPTable(1);
        tableCatatan.setWidthPercentage(100);
        tableCatatan.setSpacingBefore(10);
        
        PdfPCell catCell = new PdfPCell();
        catCell.setPadding(15);
        
        catCell.addElement(new Paragraph("📌 Catatan Khusus Guru BK:", FONT_BOLD));
        Paragraph pCat = new Paragraph(rs.getString("catatan_konselor") != null ? rs.getString("catatan_konselor") : "-", FONT_NORMAL);
        pCat.setSpacingBefore(5); pCat.setSpacingAfter(15);
        catCell.addElement(pCat);
        
        catCell.addElement(new Paragraph("💡 Tips Belajar Menuju Kampus Impian:", FONT_BOLD));
        Paragraph pTips = new Paragraph(rs.getString("tips_belajar") != null ? rs.getString("tips_belajar") : "-", FONT_NORMAL);
        pTips.setSpacingBefore(5); pTips.setSpacingAfter(15);
        catCell.addElement(pTips);
        
        catCell.addElement(new Paragraph("🎯 Rekomendasi Kegiatan Tambahan:", FONT_BOLD));
        Paragraph pKeg = new Paragraph(rs.getString("rekomendasi_kegiatan") != null ? rs.getString("rekomendasi_kegiatan") : "-", FONT_NORMAL);
        pKeg.setSpacingBefore(5);
        catCell.addElement(pKeg);
        
        tableCatatan.addCell(catCell);
        document.add(tableCatatan);

        // --- SECTION VI: SIGNATURE SECTION (FITUR LAMA TETAP ADA UTUH 100%) ---
        document.add(new Paragraph("\n\n\n"));
        PdfPTable tableTtd = new PdfPTable(3);
        tableTtd.setWidthPercentage(100);
        
        PdfPCell tSiswa = new PdfPCell(new Phrase("Siswa Bersangkutan,\n\n\n\n\n\n(" + rs.getString("s_nama") + ")\nNIS. " + rs.getString("nis"), FONT_NORMAL));
        tSiswa.setBorder(Rectangle.NO_BORDER); tSiswa.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell tSpace = new PdfPCell(new Phrase(" ", FONT_NORMAL));
        tSpace.setBorder(Rectangle.NO_BORDER);
        
        PdfPCell tKonselor = new PdfPCell(new Phrase("Mengetahui,\nKonselor / Guru BK\n\n\n\n\n\n(" + rs.getString("c_nama") + ")", FONT_NORMAL));
        tKonselor.setBorder(Rectangle.NO_BORDER); tKonselor.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        tableTtd.addCell(tSiswa);
        tableTtd.addCell(tSpace);
        tableTtd.addCell(tKonselor);
        
        document.add(tableTtd);

        document.close();
        rs.close(); 
    }

    // --- HELPER UNTUK MENGHASILKAN TEKS PAKAR (FITUR BARU) ---
    private static String generateAnalisisPakar(String rumpun, double mtk, double ipa, double ips, double bind, double bing, double seni) {
        if(rumpun == null) return "Data sesuai dengan profil Anda.";
        double avgMtkIpa = (mtk + ipa) / 2.0;
        double avgSosBhs = (ips + bind + bing) / 3.0;
        
        double skorDominan; String mapelKuat;
        if (rumpun.contains("Sains") || rumpun.contains("Teknologi") || rumpun.contains("Kesehatan")) {
            skorDominan = avgMtkIpa; mapelKuat = "Matematika & Sains";
        } else if (rumpun.contains("Sosial") || rumpun.contains("Bisnis") || rumpun.contains("Bahasa")) {
            skorDominan = avgSosBhs; mapelKuat = "Sosial & Bahasa";
        } else {
            skorDominan = seni > 0 ? seni : avgSosBhs; mapelKuat = "Seni & Kreativitas";
        }

        if (skorDominan >= 85) return "Pilihan ini sangat direkomendasikan karena ditopang oleh fondasi akademik Anda yang cemerlang di bidang " + mapelKuat + " (Rata-rata: " + String.format(Locale.US, "%.1f", skorDominan) + ").";
        if (skorDominan >= 75) return "Pilihan ini direkomendasikan dan didukung oleh nilai " + mapelKuat + " Anda yang memadai (Rata-rata: " + String.format(Locale.US, "%.1f", skorDominan) + ").";
        return "Pilihan ini cocok dengan minat Anda, namun Anda perlu meningkatkan nilai pada mata pelajaran " + mapelKuat + " untuk mengimbangi standar kompetensi jurusan ini.";
    }

    private static String generateStrategiTembus(double estUtbk, double pgSnbt, double pgSnbp, String topUniv) {
        if (pgSnbt == 0) return "Terus perkuat persiapan ujian mandiri secara konsisten.";
        StringBuilder sb = new StringBuilder();
        sb.append("Untuk jalur SNBT, target Passing Grade di ").append(topUniv).append(" adalah ").append(String.format(Locale.US, "%.0f", pgSnbt)).append(". ");
        
        if (estUtbk >= pgSnbt) {
            sb.append("Estimasi skor Anda (").append(String.format(Locale.US, "%.0f", estUtbk)).append(") sudah AMAN. Jangan kendor latihan TPS!");
        } else {
            double gap = pgSnbt - estUtbk;
            sb.append("Estimasi skor UTBK Anda masih KURANG ").append(String.format(Locale.US, "%.0f", gap)).append(" poin. Perbanyak TryOut dan bedah soal TKA!");
        }
        
        if (pgSnbp > 0) {
            sb.append(" Untuk SNBP, usahakan grafik nilai rapor Anda naik dan targetkan rata-rata minimal di angka ").append(String.format(Locale.US, "%.0f", pgSnbp)).append(".");
        }
        return sb.toString();
    }

    // --- HELPER METHODS UNTUK ITEXTPDF ---
    private static void addSectionTitle(Document doc, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, COLOR_PRIMARY));
        p.setSpacingBefore(15);
        p.setSpacingAfter(5);
        doc.add(p);
        LineSeparator line = new LineSeparator(1, 100, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -2);
        doc.add(new Chunk(line));
    }

    private static void addInfoRow(PdfPTable table, String key, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key, FONT_BOLD));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setPadding(4);
        table.addCell(cell1);
        
        PdfPCell cell2 = new PdfPCell(new Phrase(": " + value, FONT_NORMAL));
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setPadding(4);
        table.addCell(cell2);
    }

    private static void addCell(PdfPTable table, String text, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}