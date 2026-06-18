package com.sistempakar.ui.rekap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sistempakar.db.DBConnection;
import com.sistempakar.util.ReportGenerator;
import com.sistempakar.util.Theme;

/**
 * Panel Rekap Guru – Ringkasan Siswa & Peluang Masuk PTN
 * Menampilkan: status minat PTN/non-PTN, peluang, rekomendasi jurusan,
 * catatan konselor, dan chart distribusi
 */
public class PanelRekapGuru extends JPanel {

    @SuppressWarnings("unused") private final String userRole;
    @SuppressWarnings("unused") private final int userId;

    private JTable tblRekap;
    private DefaultTableModel modelRekap;
    private JLabel lblTotalSiswa, lblSudahKonsultasi, lblMinatPTN, lblAvgPeluang;
    private JComboBox<String> cbFilter, cbFilterJurusan;
    private JTextField tfSearch;
    private JPanel chartPanel;

    // Filter state
    private final List<Object[]> allData = new ArrayList<>();

    public PanelRekapGuru(String role, int userId) {
        this.userRole = role; this.userId = userId;
        setOpaque(false);
        setLayout(new BorderLayout(16,16));
        setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
        buildUI();
        refresh();
    }

    private void buildUI() {
        // ── TOP: Title + Stats ────────────────────────────────
        JPanel topSection = new JPanel(new BorderLayout(16,12));
        topSection.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout(12,0));
        titleRow.setOpaque(false);
        JLabel pageTitle = new JLabel("📋  Rekap Guru – Status & Peluang Masuk PTN Siswa");
        pageTitle.setFont(new java.awt.Font(java.awt.Font.DIALOG,java.awt.Font.BOLD,20));
        pageTitle.setForeground(Theme.TEXT_WHITE); // Fixed

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        btnRow.setOpaque(false);
        JButton btnRefresh = Theme.createSecondaryButton("🔄 Refresh");
        JButton btnExportCsv = Theme.createSuccessButton("📊 Export CSV");
        JButton btnExportPdf = Theme.createPrimaryButton("📄 Export PDF");
        
        btnRefresh.addActionListener(e -> refresh());
        btnExportCsv.addActionListener(e -> exportCSV());
        btnExportPdf.addActionListener(e -> exportPDF());
        
        btnRow.add(btnRefresh); btnRow.add(btnExportCsv); btnRow.add(btnExportPdf);

        titleRow.add(pageTitle, BorderLayout.WEST);
        titleRow.add(btnRow, BorderLayout.EAST);

        // Stats cards
        JPanel statsRow = new JPanel(new GridLayout(1,4,16,0));
        statsRow.setOpaque(false);
        lblTotalSiswa      = statValue("0");
        lblSudahKonsultasi = statValue("0");
        lblMinatPTN        = statValue("0%");
        lblAvgPeluang      = statValue("0%");
        statsRow.add(mkStatCard("👨‍🎓","Total Siswa Terdaftar",lblTotalSiswa,Theme.ACCENT_BLUE));
        statsRow.add(mkStatCard("💬","Sudah Konsultasi",lblSudahKonsultasi,Theme.ACCENT_PURPLE));
        statsRow.add(mkStatCard("🏛️","Minat Masuk PTN",lblMinatPTN,Theme.ACCENT_TEAL));
        statsRow.add(mkStatCard("📈","Rata-rata Peluang",lblAvgPeluang,Theme.ACCENT_ORANGE));

        topSection.add(titleRow, BorderLayout.NORTH);
        topSection.add(statsRow, BorderLayout.CENTER);

        // ── MIDDLE: Filter + Chart ────────────────────────────
        JPanel midSection = new JPanel(new BorderLayout(16,0));
        midSection.setOpaque(false);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        filterBar.setOpaque(false);
        tfSearch = Theme.createTextField(20);
        tfSearch.putClientProperty("JTextField.placeholderText","🔍 Cari nama / NIS...");
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e){applyFilter();}
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e){applyFilter();}
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e){}
        });

        cbFilter = Theme.createComboBox(new String[]{"Semua Status","✅ Sudah Konsultasi","⏳ Belum Konsultasi"});
        cbFilter.addActionListener(e -> applyFilter());

        cbFilterJurusan = Theme.createComboBox(new String[]{"Semua Jurusan SMA","IPA","IPS","Bahasa","SMK-TI","SMK-Bisnis","SMK-Seni"});
        cbFilterJurusan.addActionListener(e -> applyFilter());

        filterBar.add(Theme.createLabel("Filter:")); filterBar.add(tfSearch);
        filterBar.add(cbFilter); filterBar.add(cbFilterJurusan);

        // Chart panel
        chartPanel = buildChartPanel();
        chartPanel.setPreferredSize(new Dimension(320,0));

        JPanel midLeft = new JPanel(new BorderLayout(0,8)); midLeft.setOpaque(false);
        midLeft.add(filterBar, BorderLayout.NORTH);

        // Main table
        buildTable();
        midLeft.add(Theme.createScrollPane(tblRekap), BorderLayout.CENTER);

        midSection.add(midLeft, BorderLayout.CENTER);
        midSection.add(chartPanel, BorderLayout.EAST);

        // ── BOTTOM: Legend ────────────────────────────────────
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT,20,0));
        legend.setOpaque(false);
        legend.add(mkLegend("🟢","Peluang Tinggi ≥ 60%", Theme.SUCCESS));
        legend.add(mkLegend("🟡","Peluang Sedang 35-59%",Theme.WARNING));
        legend.add(mkLegend("🔴","Peluang Rendah < 35%",Theme.DANGER));
        legend.add(mkLegend("⚫","Belum Konsultasi",Theme.TEXT_MUTED));

        add(topSection, BorderLayout.NORTH);
        add(midSection, BorderLayout.CENTER);
        add(legend, BorderLayout.SOUTH);
    }

    private JLabel statValue(String v) {
        JLabel l = new JLabel(v); l.setFont(new java.awt.Font(java.awt.Font.DIALOG,java.awt.Font.BOLD,26)); return l;
    }

    private JPanel mkStatCard(String icon, String label, JLabel valLabel, Color color) {
        JPanel card = Theme.createAccentCard(color);
        card.setLayout(new BorderLayout(0,6));
        JLabel ico = new JLabel(icon,SwingConstants.CENTER); ico.setFont(new java.awt.Font(java.awt.Font.DIALOG,java.awt.Font.PLAIN,26));
        valLabel.setForeground(color); valLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbl = new JLabel(label,SwingConstants.CENTER); lbl.setFont(Theme.FONT_SMALL); lbl.setForeground(Theme.TEXT_SECONDARY);
        card.add(ico,BorderLayout.NORTH); card.add(valLabel,BorderLayout.CENTER); card.add(lbl,BorderLayout.SOUTH);
        return card;
    }

    private JPanel mkLegend(String icon, String label, Color c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,4,0)); p.setOpaque(false);
        JLabel i = new JLabel(icon); i.setFont(new java.awt.Font(java.awt.Font.DIALOG,java.awt.Font.PLAIN,13));
        JLabel l = new JLabel(label); l.setFont(Theme.FONT_SMALL); l.setForeground(c);
        p.add(i); p.add(l); return p;
    }

    private void buildTable() {
        String[] cols = {
            "NIS","Nama Siswa","Kelas","Jur.SMA","Status Konsultasi",
            "Rekomendasi Utama","Alt 1","Alt 2","PTN Terbaik","Passing Grade","Peluang Masuk","Kategori",
            "Status Rapor","Catatan Konselor","Tgl Konsultasi"
        };
        modelRekap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRekap = new JTable(modelRekap);
        Theme.styleTable(tblRekap);
        tblRekap.setRowHeight(42);
        tblRekap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Column widths
        int[] widths = {80,160,80,90,140,160,120,120,180,100,100,100,120,200,120};
        for (int i = 0; i < widths.length; i++)
            tblRekap.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Custom cell renderer for table styling
        tblRekap.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                String v = val != null ? val.toString() : "";

                if (sel) { setBackground(Theme.TABLE_SELECT); setForeground(Theme.TEXT_WHITE); return this; }

                setBackground(row%2==0?Theme.TABLE_ROW_EVEN:Theme.TABLE_ROW_ODD);
                setForeground(Theme.TEXT_PRIMARY);

                switch (col) {
                    case 4: // status
                        if (v.contains("Sudah")) setForeground(Theme.SUCCESS);
                        else setForeground(Theme.TEXT_MUTED);
                        break;
                    case 11: // kategori peluang
                        if (v.contains("Tinggi"))  setForeground(Theme.SUCCESS);
                        else if (v.contains("Sedang")) setForeground(Theme.WARNING);
                        else if (v.contains("Rendah")) setForeground(Theme.DANGER);
                        else setForeground(Theme.TEXT_MUTED);
                        break;
                    case 10: // peluang %
                        try {
                            double pct = Double.parseDouble(v.replace("%","").trim());
                            if (pct >= 15) setForeground(Theme.SUCCESS);
                            else if (pct >= 8) setForeground(Theme.WARNING);
                            else setForeground(Theme.DANGER);
                        } catch (NumberFormatException ignored) {}
                        break;
                    case 12: // status rapor
                        if (v.contains("Layak")) setForeground(Theme.SUCCESS);
                        else if (v.contains("Kurang")) setForeground(Theme.WARNING);
                        else setForeground(Theme.TEXT_MUTED);
                        break;
                }
                return this;
            }
        });

        // Double-click untuk menampilkan modal detail
        tblRekap.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblRekap.getSelectedRow() >= 0) {
                    showDetailDialog(tblRekap.getSelectedRow());
                }
            }
        });
    }

    // ── MODAL DETAIL & PRINT ──────────────────────────────────────────
    private void showDetailDialog(int row) {
        String nis = modelRekap.getValueAt(row, 0).toString();
        String nama = modelRekap.getValueAt(row, 1).toString();
        String kelas = modelRekap.getValueAt(row, 2).toString();
        String jurSma = modelRekap.getValueAt(row, 3).toString();
        String statusKons = modelRekap.getValueAt(row, 4).toString();
        String rekUtama = modelRekap.getValueAt(row, 5).toString();
        String alt1 = modelRekap.getValueAt(row, 6).toString();
        String alt2 = modelRekap.getValueAt(row, 7).toString();
        String ptn = modelRekap.getValueAt(row, 8).toString();
        String pg = modelRekap.getValueAt(row, 9).toString();
        String peluangStr = modelRekap.getValueAt(row, 10).toString();
        String kategori = modelRekap.getValueAt(row, 11).toString();
        String statRapor = modelRekap.getValueAt(row, 12).toString();
        String catatan = modelRekap.getValueAt(row, 13).toString();
        String tgl = modelRekap.getValueAt(row, 14).toString();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detail Analisis Siswa - " + nama, true);
        dlg.setSize(800, 650);
        dlg.setLocationRelativeTo(this);

        JPanel main = new JPanel(new BorderLayout(16, 16)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Theme.paintDeepSpaceBackground((Graphics2D) g, getWidth(), getHeight());
            }
        };
        main.setOpaque(true);
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // -- Header Modal --
        JPanel header = Theme.createGlassCard(15);
        header.setLayout(new BorderLayout(15, 0));
        JLabel hIcon = new JLabel("👨‍🎓");
        hIcon.setFont(new java.awt.Font(java.awt.Font.DIALOG, java.awt.Font.PLAIN, 48));
        JPanel hText = new JPanel(new GridLayout(2, 1, 0, 5));
        hText.setOpaque(false);
        JLabel hName = new JLabel(nama);
        hName.setFont(new java.awt.Font(java.awt.Font.DIALOG, java.awt.Font.BOLD, 22));
        hName.setForeground(Theme.TEXT_WHITE); // Fixed
        JLabel hSub = new JLabel("NIS: " + nis + "  |  Kelas: " + kelas + " (" + jurSma + ")");
        hSub.setFont(Theme.FONT_BODY);
        hSub.setForeground(Theme.TEXT_SECONDARY); // Fixed
        hText.add(hName); hText.add(hSub);
        header.add(hIcon, BorderLayout.WEST);
        header.add(hText, BorderLayout.CENTER);

        // -- Body Modal (Menggunakan Tab untuk Informasi Kompleks) --
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_BODY);

        // Tab 1: Rekomendasi
        JPanel pnlRek = new JPanel(new GridLayout(3, 1, 0, 15));
        pnlRek.setOpaque(false);
        pnlRek.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        pnlRek.add(Theme.createStatCard("🎯", rekUtama, "Rekomendasi Jurusan Utama", Theme.SUCCESS));
        pnlRek.add(Theme.createStatCard("✨", alt1, "Alternatif Jurusan Pertama", Theme.ACCENT_BLUE));
        pnlRek.add(Theme.createStatCard("✨", alt2, "Alternatif Jurusan Kedua", Theme.ACCENT_PURPLE));
        
        // Tab 2: PTN Target
        JPanel pnlPtn = new JPanel(new BorderLayout(10, 10));
        pnlPtn.setOpaque(false);
        pnlPtn.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel ptnCard = Theme.createAccentCard(Theme.ACCENT_TEAL);
        ptnCard.setLayout(new GridLayout(4, 1, 0, 12));
        ptnCard.add(mkDetailRow("Universitas & Jurusan Target:", ptn, Theme.TEXT_WHITE)); // Fixed
        ptnCard.add(mkDetailRow("Passing Grade (SNBT):", pg, Theme.WARNING));
        ptnCard.add(mkDetailRow("Peluang Diterima:", peluangStr + " (" + kategori + ")", Theme.SUCCESS));
        ptnCard.add(mkDetailRow("Status Kesiapan Rapor:", statRapor, Theme.ACCENT_BLUE));
        pnlPtn.add(ptnCard, BorderLayout.NORTH);

        // Tab 3: Catatan & Status
        JPanel pnlCat = new JPanel(new BorderLayout(0, 10));
        pnlCat.setOpaque(false);
        pnlCat.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel statusCard = Theme.createGlassCard(10);
        statusCard.setLayout(new GridLayout(1, 2));
        statusCard.add(mkDetailRow("Status:", statusKons, Theme.TEXT_WHITE)); // Fixed
        statusCard.add(mkDetailRow("Tanggal:", tgl.isEmpty() ? "-" : tgl, Theme.TEXT_SECONDARY)); // Fixed
        
        JTextArea txtCatatan = new JTextArea(catatan);
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        txtCatatan.setEditable(false);
        txtCatatan.setFont(new java.awt.Font(java.awt.Font.DIALOG, java.awt.Font.ITALIC, 14));
        txtCatatan.setBackground(new Color(0, 0, 0, 40));
        txtCatatan.setForeground(Theme.TEXT_WHITE); // Fixed
        txtCatatan.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        pnlCat.add(statusCard, BorderLayout.NORTH);
        pnlCat.add(Theme.createScrollPane(txtCatatan), BorderLayout.CENTER);

        tabs.addTab("🎓 Hasil Rekomendasi", pnlRek);
        tabs.addTab("🏛️ Analisis PTN Target", pnlPtn);
        tabs.addTab("📝 Catatan & Status", pnlCat);

        // -- Footer / Tombol Aksi --
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        JButton btnPrint = Theme.createPrimaryButton("🖨️ Cetak Laporan (PDF)");
        JButton btnClose = Theme.createSecondaryButton("Tutup");

        btnClose.addActionListener(e -> dlg.dispose());
        
        // Logika untuk Generate PDF
        btnPrint.addActionListener(e -> {
            try (ResultSet rs = DBConnection.executeQuery("SELECT k.id FROM konsultasi k JOIN siswa s ON k.siswa_id=s.id WHERE s.nis=? ORDER BY k.tanggal_konsultasi DESC LIMIT 1", nis)) {
                if(rs.next()) {
                    int sid = rs.getInt(1);
                    JFileChooser fc = new JFileChooser();
                    fc.setSelectedFile(new java.io.File("Laporan_Profil_" + nis + ".pdf"));
                    if (fc.showSaveDialog(dlg) == JFileChooser.APPROVE_OPTION) {
                        ReportGenerator.generatePremiumReport(sid, fc.getSelectedFile().getAbsolutePath());
                        JOptionPane.showMessageDialog(dlg, "Laporan lengkap berhasil dibuat dan disimpan!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        Desktop.getDesktop().open(fc.getSelectedFile());
                    }
                } else {
                    JOptionPane.showMessageDialog(dlg, "Siswa ini belum melakukan konsultasi. Tidak ada laporan yang dapat dicetak.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException | java.io.IOException ex) {
                JOptionPane.showMessageDialog(dlg, "Terjadi kesalahan sistem: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Terjadi kesalahan tidak terduga: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        });

        footer.add(btnPrint);
        footer.add(btnClose);

        main.add(header, BorderLayout.NORTH);
        main.add(tabs, BorderLayout.CENTER);
        main.add(footer, BorderLayout.SOUTH);

        dlg.setContentPane(main);
        dlg.setVisible(true);
    }

    /**
     * Membuat baris informasi detail dengan label dan nilai.
     */
    private JPanel mkDetailRow(String labelText, String valueText, Color valueColor) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.FONT_BOLD);
        lbl.setForeground(Theme.TEXT_SECONDARY); // Fixed
        lbl.setPreferredSize(new Dimension(170, 24));
        
        JLabel val = new JLabel(valueText);
        val.setFont(Theme.FONT_BODY);
        val.setForeground(valueColor);
        
        p.add(lbl, BorderLayout.WEST);
        p.add(val, BorderLayout.CENTER);
        return p;
    }


    public final void refresh() {
        allData.clear();
        modelRekap.setRowCount(0);
        int total=0, konsultasi=0; double sumPeluang=0; int ptnCount=0;

        // Get all students with their consultation status
        // Fix N+1 correlated subquery issue by using derived table for latest konsultasi
        String sql =
            "SELECT s.nis, s.nama, s.kelas, s.jurusan_sma, " +
            "  k.no_konsultasi, k.tanggal_konsultasi, k.status, " +
            "  j.nama AS jur_nama, j.rumpun, " +
            "  j2.nama AS alt1, j3.nama AS alt2, " +
            "  k.catatan_konselor, k.tips_belajar, k.rekomendasi_kegiatan, " +
            "  u.nama AS univ_nama, ru.peluang_personal as peluang_masuk, uj.passing_grade_snbt, uj.akreditasi_prodi, " +
            "  km.nama AS kategori, s.nilai_matematika, s.nilai_ipa " +
            "FROM siswa s " +
            "LEFT JOIN (" +
            "  SELECT k1.* FROM konsultasi k1 " +
            "  INNER JOIN (SELECT MAX(id) as max_id FROM konsultasi GROUP BY siswa_id) k2 ON k1.id = k2.max_id" +
            ") k ON k.siswa_id = s.id " +
            "LEFT JOIN jurusan_kuliah j  ON k.rekomendasi_utama_id = j.id " +
            "LEFT JOIN jurusan_kuliah j2 ON k.rekomendasi_alt1_id  = j2.id " +
            "LEFT JOIN jurusan_kuliah j3 ON k.rekomendasi_alt2_id  = j3.id " +
            "LEFT JOIN kategori_minat km ON j.kategori_id = km.id " +
            "LEFT JOIN rekomendasi_univ ru ON ru.konsultasi_id=k.id AND ru.rank_urutan=1 " +
            "  AND ru.universitas_jurusan_id IN (SELECT id FROM universitas_jurusan WHERE jurusan_id = k.rekomendasi_utama_id) " +
            "LEFT JOIN universitas_jurusan uj ON ru.universitas_jurusan_id=uj.id " +
            "LEFT JOIN universitas u ON uj.universitas_id=u.id " +
            "ORDER BY ru.peluang_personal DESC, s.nama ASC";

        try (ResultSet rs = DBConnection.executeQuery(sql)) {
            while (rs.next()) {
                total++;
                String nokons   = rs.getString("no_konsultasi");
                boolean sudah   = nokons != null;
                if (sudah) konsultasi++;

                double peluang  = rs.getDouble("peluang_masuk");
                String univName = rs.getString("univ_nama");
                String jurNama  = rs.getString("jur_nama");
                String alt1     = rs.getString("alt1");
                String alt2     = rs.getString("alt2");
                String pg       = rs.getString("passing_grade_snbt");
                String catKons  = rs.getString("catatan_konselor");
                
                double avgRap   = (rs.getDouble("nilai_matematika") + rs.getDouble("nilai_ipa")) / 2;
                String statusRap = sudah ? (avgRap >= 78 ? "✅ Layak" : "⚠️ Kurang") : "-";

                String tglKons  = "";
                Timestamp ts = rs.getTimestamp("tanggal_konsultasi");
                if (ts != null) tglKons = new SimpleDateFormat("dd/MM/yyyy").format(ts);

                // Determine peluang category
                String katPeluang;
                if (!sudah) katPeluang = "⚫ Belum";
                else if (peluang >= 60.0) katPeluang = "🟢 Tinggi";
                else if (peluang >= 35.0) katPeluang = "🟡 Sedang";
                else                      katPeluang = "🔴 Rendah";

                if (sudah && peluang >= 60.0) ptnCount++;
                if (sudah) sumPeluang += peluang;
                
                String shortCat = "-";
                if(catKons != null && !catKons.isEmpty()) {
                    shortCat = catKons.length() > 50 ? catKons.substring(0, 47) + "..." : catKons;
                }

                Object[] row = {
                    rs.getString("nis"), rs.getString("nama"), rs.getString("kelas"), rs.getString("jurusan_sma"),
                    sudah ? "✅ Sudah" : "⏳ Belum",
                    jurNama != null ? jurNama : "-",
                    alt1 != null ? alt1 : "-", alt2 != null ? alt2 : "-",
                    univName != null ? univName : "-", pg != null ? pg : "-",
                    sudah ? String.format("%.1f%%", peluang) : "-",
                    katPeluang, statusRap, shortCat, tglKons
                };
                allData.add(row);
                modelRekap.addRow(row);
            }
            if (rs.getStatement() != null) {
                rs.getStatement().close();
            }

            // Update stat labels
            lblTotalSiswa.setText(String.valueOf(total));
            lblSudahKonsultasi.setText(String.valueOf(konsultasi));
            lblMinatPTN.setText(konsultasi > 0 ? String.format("%.0f%%", (double)ptnCount/konsultasi*100) : "0%");
            lblAvgPeluang.setText(konsultasi > 0 ? String.format("%.1f%%", sumPeluang/konsultasi) : "0%");

            // Refresh chart
            rebuildChart(total, konsultasi, ptnCount);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String keyword = tfSearch.getText().toLowerCase().trim();
        String statusFilter = (String) cbFilter.getSelectedItem();
        String jurusanFilter = (String) cbFilterJurusan.getSelectedItem();

        modelRekap.setRowCount(0);
        for (Object[] row : allData) {
            String nama = row[1].toString().toLowerCase();
            String nis  = row[0].toString().toLowerCase();
            String jSma = row[3].toString();
            String status = row[4].toString();

            boolean matchKw  = keyword.isEmpty() || nama.contains(keyword) || nis.contains(keyword);
            boolean matchSt  = statusFilter.startsWith("Semua") || status.contains(statusFilter.contains("Sudah")?"Sudah":"Belum");
            boolean matchJur = jurusanFilter.startsWith("Semua") || jSma.equals(jurusanFilter);

            if (matchKw && matchSt && matchJur) modelRekap.addRow(row);
        }
    }

    // ── CHART PANEL ────────────────────────────────────────────
    private JPanel buildChartPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
    }

    private void rebuildChart(int total, int konsultasi, int ptnMinat) {
        chartPanel.removeAll();
        chartPanel.setLayout(new BorderLayout(0,12));
        chartPanel.setOpaque(false);

        JPanel inner = Theme.createGlassCard();
        inner.setLayout(new BorderLayout(0,16));

        JLabel ct = new JLabel("📊  Distribusi Siswa", SwingConstants.CENTER);
        ct.setFont(Theme.FONT_SUBTITLE); ct.setForeground(Theme.TEXT_WHITE); // Fixed

        // Donut chart panel
        JPanel donut = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w=getWidth(), h=getHeight(), r=Math.min(w,h)-40;
                int x=(w-r)/2, y=(h-r)/2;

                if (total == 0) return;

                double belum = total - konsultasi;
                double nonPtn = konsultasi - ptnMinat;

                double a1 = (belum/total)*360;
                double a2 = (nonPtn/total)*360;
                double a3 = (ptnMinat/total)*360;

                Color[] cols = {Theme.TEXT_MUTED, Theme.WARNING, Theme.SUCCESS};
                double[] angles = {a1, a2, a3};
                double start = -90;

                for (int i=0;i<3;i++) {
                    g2.setColor(cols[i]);
                    g2.fillArc(x,y,r,r,(int)start,(int)angles[i]);
                    start += angles[i];
                }

                int ir = r/3;
                int ix = (w-ir)/2, iy=(h-ir)/2;
                
                // Fixed: Menggunakan UIManager.getColor sebagai ganti Theme.isDarkMode
                g2.setColor(UIManager.getColor("Panel.background"));
                g2.fillOval(ix,iy,ir,ir);

                g2.setFont(new java.awt.Font(java.awt.Font.DIALOG,java.awt.Font.BOLD,18)); g2.setColor(Theme.TEXT_WHITE); // Fixed
                String ct2 = String.valueOf(total);
                int tw=g2.getFontMetrics().stringWidth(ct2);
                g2.drawString(ct2,(w-tw)/2,h/2+2);
                
                g2.dispose();
            }
        };
        donut.setOpaque(false); donut.setPreferredSize(new Dimension(200,200));

        JPanel bars = new JPanel(new GridLayout(3,1,0,10)); bars.setOpaque(false);
        int blmKons = total - konsultasi;
        int rendah  = Math.max(0, konsultasi - ptnMinat);
        addBar(bars, "Belum Konsultasi", blmKons, total, Theme.TEXT_MUTED);
        addBar(bars, "Peluang ≥ 60%", ptnMinat, total, Theme.SUCCESS);
        addBar(bars, "Peluang < 60%", rendah, total, Theme.DANGER);

        JPanel leg = new JPanel(new GridLayout(3,1,0,4)); leg.setOpaque(false);
        String[][] legs = {{"⚫","Belum Konsultasi"},{"🟢","Peluang PTN ≥ 60%"},{"🔴","Peluang PTN < 60%"}};
        Color[] lc = {Theme.TEXT_MUTED, Theme.SUCCESS, Theme.DANGER};
        int[] lv = {blmKons, ptnMinat, rendah};
        for (int i=0;i<3;i++) {
            JPanel lr=new JPanel(new BorderLayout(6,0)); lr.setOpaque(false);
            JLabel li=new JLabel(legs[i][0]); li.setFont(new java.awt.Font(java.awt.Font.DIALOG,java.awt.Font.PLAIN,12));
            JLabel ll=new JLabel(legs[i][1]+" ("+lv[i]+")"); ll.setFont(Theme.FONT_SMALL); ll.setForeground(lc[i]);
            lr.add(li,BorderLayout.WEST); lr.add(ll,BorderLayout.CENTER);
            leg.add(lr);
        }

        JPanel btm=new JPanel(new BorderLayout(0,8)); btm.setOpaque(false);
        btm.add(bars,BorderLayout.NORTH); btm.add(leg,BorderLayout.CENTER);
        inner.add(ct,BorderLayout.NORTH); inner.add(donut,BorderLayout.CENTER);
        inner.add(btm, BorderLayout.SOUTH);

        chartPanel.add(inner,BorderLayout.CENTER);
        chartPanel.revalidate(); chartPanel.repaint();
    }

    private void addBar(JPanel parent, String label, int val, int total, Color c) {
        JPanel row=new JPanel(new BorderLayout(6,0)); row.setOpaque(false);
        JLabel lbl=new JLabel(label); lbl.setFont(Theme.FONT_SMALL); lbl.setForeground(Theme.TEXT_SECONDARY); lbl.setPreferredSize(new Dimension(130,16));
        JProgressBar pb=Theme.createProgressBar(c); pb.setMaximum(Math.max(1,total)); pb.setValue(val);
        JLabel pct=new JLabel(String.valueOf(val)); pct.setFont(Theme.FONT_SMALL); pct.setForeground(c); pct.setPreferredSize(new Dimension(28,16));
        row.add(lbl,BorderLayout.WEST); row.add(pb,BorderLayout.CENTER); row.add(pct,BorderLayout.EAST);
        parent.add(row);
    }
    
    // ── EXPORT CSV ──────────────────────────────────────────────
    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Rekap_Guru_" + new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(fc.getSelectedFile()), "UTF-8")) {
                fw.write("\ufeff");
                fw.write("SMAN 1 CONTOH KOTA - REKAPITULASI GURU BK\n");
                fw.write("Tanggal Cetak:," + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) + "\n");
                fw.write("Total Siswa Terdaftar:," + lblTotalSiswa.getText() + "\n");
                fw.write("Sudah Konsultasi:," + lblSudahKonsultasi.getText() + "\n");
                fw.write("Rata-rata Peluang:," + lblAvgPeluang.getText() + "\n\n");
                
                for (int i = 0; i < modelRekap.getColumnCount(); i++) {
                    fw.write(modelRekap.getColumnName(i) + (i == modelRekap.getColumnCount() - 1 ? "" : ","));
                }
                fw.write("\n");
                for (int r = 0; r < modelRekap.getRowCount(); r++) {
                    for (int c = 0; c < modelRekap.getColumnCount(); c++) {
                        String val = modelRekap.getValueAt(r, c) != null ? modelRekap.getValueAt(r, c).toString() : "";
                        fw.write("\"" + val.replace("\"", "\"\"") + "\"" + (c == modelRekap.getColumnCount() - 1 ? "" : ","));
                    }
                    fw.write("\n");
                }
                JOptionPane.showMessageDialog(this, "Data Rekap Guru berhasil diekspor ke CSV!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal ekspor CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── EXPORT PDF ─────────────────────────────────────────────
    private void exportPDF() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Rekap sebagai PDF");
        fc.setSelectedFile(new java.io.File("Rekap_Guru_PTN_"+new SimpleDateFormat("yyyyMMdd").format(new java.util.Date())+".pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                generatePDF(fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this,"PDF berhasil disimpan!\n"+fc.getSelectedFile().getAbsolutePath(),"Sukses",JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(fc.getSelectedFile());
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this,"Gagal export PDF: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generatePDF(String path) throws Exception {
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
        PdfWriter.getInstance(doc, new java.io.FileOutputStream(path));
        doc.open();

        com.itextpdf.text.Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(30, 45, 90));
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        com.itextpdf.text.Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA, 8);
        com.itextpdf.text.Font subFont    = FontFactory.getFont(FontFactory.HELVETICA, 10);

        // KOP SURAT
        Paragraph h1 = new Paragraph("SMAN 1 CONTOH KOTA", titleFont);
        h1.setAlignment(Element.ALIGN_CENTER);
        Paragraph h2 = new Paragraph("Jl. Pendidikan No. 1, Kota Contoh, Provinsi Jaya 12345 | Telp: (021) 1234567", subFont);
        h2.setAlignment(Element.ALIGN_CENTER);
        doc.add(h1); doc.add(h2);
        
        LineSeparator line = new LineSeparator(2, 100, new BaseColor(30, 45, 90), Element.ALIGN_CENTER, -5);
        doc.add(new Chunk(line));
        doc.add(new Paragraph("\n"));
        
        Paragraph hTitle = new Paragraph("REKAPITULASI DATA SISWA - STATUS BIMBINGAN KARIR & PELUANG PTN", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        hTitle.setAlignment(Element.ALIGN_CENTER);
        hTitle.setSpacingAfter(15);
        doc.add(hTitle);

        // STATISTIK RINGKAS
        Paragraph summary = new Paragraph(
            "Dari total " + lblTotalSiswa.getText() + " siswa terdaftar, sebanyak " + lblSudahKonsultasi.getText() + " siswa telah melakukan konsultasi. " +
            "Tingkat minat masuk PTN mencapai " + lblMinatPTN.getText() + " dengan rata-rata peluang masuk sebesar " + lblAvgPeluang.getText() + ".\n\n",
            subFont
        );
        doc.add(summary);

        // TABEL
        PdfPTable table = new PdfPTable(13);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f,10f,4f,6f,5f,8f,7f,7f,9f,4f,5f,6f,6f});

        String[] headers={"NIS","Nama Siswa","Kls","Jurusan","Status","Rek. Utama","Alt 1","Alt 2","Univ Terbaik","PG","Peluang","Stat Rpr","Tanggal"};
        BaseColor headerBg = new BaseColor(30, 45, 90);
        
        for(String h:headers){
            PdfPCell c=new PdfPCell(new Phrase(h,headerFont));
            c.setBackgroundColor(headerBg); c.setPadding(6); c.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c);
        }

        BaseColor rowBg1 = new BaseColor(245,245,250);
        BaseColor rowBg2 = new BaseColor(255,255,255);
        
        BaseColor bgGreen = new BaseColor(212,237,218);
        BaseColor bgYellow = new BaseColor(255,243,205);
        BaseColor bgRed = new BaseColor(248,215,218);

        for(int r=0;r<modelRekap.getRowCount();r++){
            BaseColor rbg = r%2==0?rowBg1:rowBg2;
            
            String peluangStr = modelRekap.getValueAt(r,10) != null ? modelRekap.getValueAt(r,10).toString() : "";
            if(!peluangStr.equals("-") && !peluangStr.isEmpty()) {
                try {
                    double p = Double.parseDouble(peluangStr.replace("%", "").trim());
                    if(p >= 15) rbg = bgGreen;
                    else if(p >= 8) rbg = bgYellow;
                    else rbg = bgRed;
                } catch(NumberFormatException e){}
            }

            // Map columns: 0:NIS, 1:Nama, 2:Kelas, 3:JurSMA, 4:Status, 5:RekUtama, 6:Alt1, 7:Alt2, 8:Univ, 9:PG, 10:Peluang, 12:StatRapor, 14:Tgl
            int[] colMap = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14};
            for(int c : colMap){
                String val = modelRekap.getValueAt(r,c)!=null?modelRekap.getValueAt(r,c).toString():"";
                val=val.replaceAll("[^\\x00-\\x7F]",""); // Clean emoji
                PdfPCell cell=new PdfPCell(new Phrase(val,cellFont));
                cell.setBackgroundColor(rbg); cell.setPadding(4); 
                table.addCell(cell);
            }
        }
        doc.add(table);
        
        // HALAMAN TANDA TANGAN
        doc.add(new Paragraph("\n\n\n"));
        PdfPTable tableTtd = new PdfPTable(3);
        tableTtd.setWidthPercentage(100);
        
        PdfPCell cellKoor = new PdfPCell(new Phrase("Mengetahui,\nKoordinator Bimbingan Konseling\n\n\n\n\n\n(Dra. Siti Aminah, M.Pd)\nNIP. 197501012000012001", subFont));
        cellKoor.setBorder(com.itextpdf.text.Rectangle.NO_BORDER); cellKoor.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell cellSpace = new PdfPCell(new Phrase(" ", subFont));
        cellSpace.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        
        PdfPCell cellKepsek = new PdfPCell(new Phrase("Kota Contoh, " + new SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID")).format(new java.util.Date()) + "\nKepala Sekolah\n\n\n\n\n\n(Drs. Budi Santoso, M.Si)\nNIP. 197005151995121001", subFont));
        cellKepsek.setBorder(com.itextpdf.text.Rectangle.NO_BORDER); cellKepsek.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        tableTtd.addCell(cellKoor); tableTtd.addCell(cellSpace); tableTtd.addCell(cellKepsek);
        doc.add(tableTtd);

        doc.close();
    }
}