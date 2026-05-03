package com.sistempakar.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sistempakar.db.DBConnection;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for generating complex PDF and Excel reports
 */
public class ReportGenerator {

    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.DARK_GRAY);
    private static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.GRAY);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
    private static final Font FONT_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
    private static final Font FONT_SMALL = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY);

    /**
     * Generates a premium individual consultation report
     */
    public static void generatePremiumReport(int konsultasiId, String savePath) throws Exception {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(savePath));
        document.open();

        // 1. Fetch Data
        ResultSet rs = DBConnection.executeQuery(
            "SELECT k.*, s.nis, s.nama as s_nama, s.kelas, s.jurusan_sma, " +
            "j1.nama as rek_utama, j1.rumpun as rumpun_utama, j1.deskripsi as desk_utama, " +
            "c.nama as c_nama, " +
            "s.nilai_matematika, s.nilai_ipa, s.nilai_ips, s.nilai_bahasa_ind, s.nilai_bahasa_ing " +
            "FROM konsultasi k " +
            "JOIN siswa s ON k.siswa_id = s.id " +
            "JOIN konselor c ON k.konselor_id = c.id " +
            "LEFT JOIN jurusan_kuliah j1 ON k.rekomendasi_utama_id = j1.id " +
            "WHERE k.id = ?", konsultasiId
        );

        if (!rs.next()) throw new Exception("Data konsultasi tidak ditemukan");

        // Header
        Paragraph pTitle = new Paragraph("LAPORAN HASIL KONSULTASI", FONT_TITLE);
        pTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitle);
        
        Paragraph pSub = new Paragraph("Sistem Pakar Rekomendasi Jurusan v1.0", FONT_SMALL);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(20);
        document.add(pSub);

        // Section: Profil Siswa
        addSectionTitle(document, "I. PROFIL SISWA");
        PdfPTable tableSiswa = new PdfPTable(2);
        tableSiswa.setWidthPercentage(100);
        tableSiswa.setSpacingBefore(10);
        tableSiswa.setSpacingAfter(20);
        
        addInfoRow(tableSiswa, "Nama Lengkap", rs.getString("s_nama"));
        addInfoRow(tableSiswa, "NIS", rs.getString("nis"));
        addInfoRow(tableSiswa, "Kelas / Jurusan SMA", rs.getString("kelas") + " / " + rs.getString("jurusan_sma"));
        addInfoRow(tableSiswa, "Tanggal Konsultasi", new SimpleDateFormat("dd MMMM yyyy HH:mm").format(rs.getTimestamp("tanggal_konsultasi")));
        addInfoRow(tableSiswa, "Guru BK / Konselor", rs.getString("c_nama"));
        
        document.add(tableSiswa);

        // Section: Hasil Analisis Psikotes
        addSectionTitle(document, "II. ANALISIS MINAT & BAKAT");
        Paragraph pSkor = new Paragraph("Berdasarkan hasil psikotes, berikut adalah distribusi skor minat Anda pada 7 kategori utama:", FONT_NORMAL);
        pSkor.setSpacingAfter(10);
        document.add(pSkor);

        PdfPTable tableSkor = new PdfPTable(7);
        tableSkor.setWidthPercentage(100);
        String[] cats = {"Tekno", "Sains", "Sosial", "Seni", "Bisnis", "Bahasa", "Medis"};
        String[] scores = {
            rs.getString("skor_teknologi"), rs.getString("skor_sains"), rs.getString("skor_sosial"),
            rs.getString("skor_seni"), rs.getString("skor_bisnis"), rs.getString("skor_bahasa"),
            rs.getString("skor_kesehatan")
        };
        for (String c : cats) addCell(tableSkor, c, FONT_HEADER, new BaseColor(79, 195, 247));
        for (String s : scores) addCell(tableSkor, s, FONT_BOLD, BaseColor.WHITE);
        document.add(tableSkor);

        // Section: Rekomendasi Utama
        document.add(Chunk.NEWLINE);
        addSectionTitle(document, "III. REKOMENDASI JURUSAN UTAMA");
        Paragraph pRek = new Paragraph(rs.getString("rek_utama"), FONT_SUBTITLE);
        pRek.setSpacingBefore(5);
        document.add(pRek);
        
        Paragraph pRumpun = new Paragraph("Rumpun: " + rs.getString("rumpun_utama"), FONT_BOLD);
        document.add(pRumpun);
        
        Paragraph pDesk = new Paragraph(rs.getString("desk_utama"), FONT_NORMAL);
        pDesk.setSpacingBefore(8);
        pDesk.setSpacingAfter(15);
        pDesk.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(pDesk);

        // Section: Rekomendasi PTN
        addSectionTitle(document, "IV. DAFTAR PTN PILIHAN & PELUANG");
        PdfPTable tableUniv = new PdfPTable(4);
        tableUniv.setWidthPercentage(100);
        tableUniv.setSpacingBefore(10);
        tableUniv.setWidths(new float[]{4, 2, 2, 2});
        
        addCell(tableUniv, "Universitas", FONT_HEADER, new BaseColor(30, 45, 90));
        addCell(tableUniv, "Akreditasi", FONT_HEADER, new BaseColor(30, 45, 90));
        addCell(tableUniv, "Passing Grade", FONT_HEADER, new BaseColor(30, 45, 90));
        addCell(tableUniv, "Peluang Anda", FONT_HEADER, new BaseColor(30, 45, 90));

        ResultSet rsU = DBConnection.executeQuery(
            "SELECT u.nama as u_nama, uj.akreditasi_prodi, uj.passing_grade_snbt, ru.peluang_personal " +
            "FROM rekomendasi_univ ru " +
            "JOIN universitas_jurusan uj ON ru.universitas_jurusan_id = uj.id " +
            "JOIN universitas u ON uj.universitas_id = u.id " +
            "WHERE ru.konsultasi_id = ? ORDER BY ru.rank_urutan ASC", konsultasiId
        );
        while (rsU.next()) {
            addCell(tableUniv, rsU.getString("u_nama"), FONT_NORMAL, BaseColor.WHITE);
            addCell(tableUniv, rsU.getString("akreditasi_prodi"), FONT_NORMAL, BaseColor.WHITE);
            addCell(tableUniv, rsU.getString("passing_grade_snbt"), FONT_NORMAL, BaseColor.WHITE);
            addCell(tableUniv, rsU.getString("peluang_personal") + "%", FONT_BOLD, BaseColor.WHITE);
        }
        document.add(tableUniv);

        // Section: Catatan Konselor
        document.add(Chunk.NEWLINE);
        addSectionTitle(document, "V. CATATAN & TIPS STRATEGIS");
        document.add(new Paragraph("Catatan Konselor:", FONT_BOLD));
        document.add(new Paragraph(rs.getString("catatan_konselor"), FONT_NORMAL));
        document.add(new Paragraph("\nTips Belajar:", FONT_BOLD));
        document.add(new Paragraph(rs.getString("tips_belajar"), FONT_NORMAL));

        // Footer
        document.add(Chunk.NEWLINE);
        Paragraph pFooter = new Paragraph("\nDicetak pada: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), FONT_SMALL);
        pFooter.setAlignment(Element.ALIGN_RIGHT);
        document.add(pFooter);

        document.close();
        rs.close(); rsU.close();
    }

    private static void addSectionTitle(Document doc, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, FONT_BOLD);
        p.setSpacingBefore(10);
        p.setSpacingAfter(5);
        p.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(30, 45, 90)));
        doc.add(p);
        LineSeparator line = new LineSeparator(1, 100, new BaseColor(200, 200, 200), Element.ALIGN_CENTER, -2);
        doc.add(new Chunk(line));
    }

    private static void addInfoRow(PdfPTable table, String key, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key, FONT_BOLD));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setPadding(5);
        table.addCell(cell1);
        
        PdfPCell cell2 = new PdfPCell(new Phrase(": " + value, FONT_NORMAL));
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setPadding(5);
        table.addCell(cell2);
    }

    private static void addCell(PdfPTable table, String text, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}
