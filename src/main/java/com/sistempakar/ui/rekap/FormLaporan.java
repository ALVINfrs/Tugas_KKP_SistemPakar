package com.sistempakar.ui.rekap;

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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Form Laporan Konsultasi Terpadu (Enterprise Reporting Module).
 * Dilengkapi dengan Visual Analytics, Filter Lanjutan, Export PDF (Konsultasi & Nilai),
 * Export CSV Excel-Ready, Rapor Visual (Radar Chart), dan Pusat Analitik Lanjutan.
 */
public class FormLaporan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JComboBox<String> cbStatus, cbKelas;
    private JPanel chartContainer;
    private JLabel lblCounter;

    public FormLaporan() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        initComponents();
        loadData();
        refreshCharts();
    }

    private void initComponents() {
        // ── TOP: DASHBOARD ANALYTICS ──────────────────────────
        chartContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        chartContainer.setOpaque(false);
        chartContainer.setPreferredSize(new Dimension(0, 280));
        
        // ── MIDDLE: FILTERS & ACTIONS ─────────────────────────
        JPanel midPanel = new JPanel(new BorderLayout(12, 12));
        midPanel.setOpaque(false);

        JLabel title = new JLabel("📈 Laporan & Analisis Data Terpadu");
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
        title.setForeground(Theme.TEXT_WHITE);

        // -- PANEL FILTER (BARIS 1) --
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);

        tfSearch = Theme.createTextField(15);
        tfSearch.putClientProperty("JTextField.placeholderText", "🔍 Cari Siswa...");
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { loadData(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { loadData(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        cbStatus = Theme.createComboBox(new String[]{"Semua Status", "Selesai", "Draft", "Follow Up"});
        cbStatus.addActionListener(e -> loadData());

        cbKelas = Theme.createComboBox(new String[]{"Semua Kelas"});
        loadKelasFilter();
        cbKelas.addActionListener(e -> loadData());

        JButton btnRefresh = Theme.createSecondaryButton("🔄 Segarkan");
        btnRefresh.addActionListener(e -> { 
            tfSearch.setText(""); 
            loadKelasFilter();
            loadData(); 
            refreshCharts(); 
        });

        filterPanel.add(Theme.createLabel("Filter Data:"));
        filterPanel.add(tfSearch);
        filterPanel.add(cbKelas);
        filterPanel.add(cbStatus);
        filterPanel.add(btnRefresh);

        // -- PANEL EKSPOR & ANALITIK (BARIS 2) --
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnVisualRadar    = Theme.createWarningButton("🕸️ Rapor Visual");
        JButton btnAdvancedRep    = Theme.createDangerButton("🚀 Pusat Analitik");
        JButton btnPdfKonsultasi  = Theme.createPrimaryButton("📄 Lap. Konsul");
        JButton btnPdfNilaiProfil = Theme.createPrimaryButton("📊 Ekspor Nilai"); 
        JButton btnExportCsv      = Theme.createSuccessButton("📑 Excel Data");
        JButton btnPrint          = Theme.createSecondaryButton("🖨️ Cetak Tabel");

        btnVisualRadar.addActionListener(e -> exportRaporVisual());
        btnAdvancedRep.addActionListener(e -> showAdvancedReportDialog());
        btnPdfKonsultasi.addActionListener(e -> exportToPDF("KONSULTASI"));
        btnPdfNilaiProfil.addActionListener(e -> exportNilaiToPDF()); 
        btnExportCsv.addActionListener(e -> exportToCSV());
        btnPrint.addActionListener(e -> printTable());

        // Susun tombol di baris kedua
        actionPanel.add(btnVisualRadar);
        actionPanel.add(btnAdvancedRep);
        actionPanel.add(Box.createHorizontalStrut(10)); // Pemisah grup
        actionPanel.add(btnPdfKonsultasi);
        actionPanel.add(btnPdfNilaiProfil); 
        actionPanel.add(btnExportCsv);
        actionPanel.add(btnPrint);

        // BUNGKUS BARIS 1 & BARIS 2 KE DALAM SATU PANEL VERTIKAL
        JPanel wrapPanels = new JPanel();
        wrapPanels.setLayout(new BoxLayout(wrapPanels, BoxLayout.Y_AXIS));
        wrapPanels.setOpaque(false);
        
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        wrapPanels.add(filterPanel);
        wrapPanels.add(Box.createVerticalStrut(5)); // Jarak antar baris
        wrapPanels.add(actionPanel);

        midPanel.add(title, BorderLayout.NORTH);
        midPanel.add(wrapPanels, BorderLayout.CENTER);

        // ── BOTTOM: TABLE ─────────────────────────────────────
        String[] columns = {"ID", "No. Konsultasi", "Nama Siswa", "Kelas", "Jurusan SMA", "Konselor", "Rekomendasi Utama", "Tanggal", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.setRowHeight(40);
        
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220, 50)); 
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 45, 90));
        header.setForeground(Color.WHITE);
        header.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 8 && value != null) {
                    String status = value.toString().toUpperCase();
                    if (status.equals("SELESAI")) {
                        setForeground(Theme.SUCCESS);
                    } else if (status.equals("DRAFT")) {
                        setForeground(Theme.WARNING);
                    } else if (status.equals("FOLLOW UP")) {
                        setForeground(Theme.INFO);
                    } else {
                        setForeground(Theme.TEXT_PRIMARY);
                    }
                    setFont(new Font(Font.DIALOG, Font.BOLD, 12));
                } else {
                    setForeground(isSelected ? Color.WHITE : Theme.TEXT_PRIMARY);
                    setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                }
                return c;
            }
        });
        
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if(row != -1) {
                        int konsId = (int) table.getValueAt(row, 0);
                        generateAndOpenReport(konsId);
                    }
                }
            }
        });

        JScrollPane sp = Theme.createScrollPane(table);
        
        lblCounter = new JLabel("Menampilkan 0 data", SwingConstants.RIGHT);
        lblCounter.setForeground(Theme.TEXT_MUTED);
        lblCounter.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setOpaque(false);
        centerPanel.add(midPanel, BorderLayout.NORTH);
        centerPanel.add(sp, BorderLayout.CENTER);
        centerPanel.add(lblCounter, BorderLayout.SOUTH);

        add(chartContainer, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void generateAndOpenReport(int konsultasiId) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Laporan Detail Individu");
        fc.setSelectedFile(new java.io.File("Detail_Konsultasi_" + konsultasiId + ".pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ReportGenerator.generatePremiumReport(konsultasiId, fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Laporan individu berhasil dibuat.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(fc.getSelectedFile());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal membuat dokumen: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refresh() {
        loadKelasFilter();
        loadData();
        refreshCharts();
    }

    private void loadKelasFilter() {
        if (cbKelas == null) return;
        cbKelas.removeAllItems();
        cbKelas.addItem("Semua Kelas");
        try (ResultSet rs = DBConnection.executeQuery("SELECT DISTINCT kelas FROM siswa WHERE kelas IS NOT NULL AND kelas != '' ORDER BY kelas")) {
            while (rs.next()) {
                cbKelas.addItem(rs.getString("kelas"));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void loadData() {
        model.setRowCount(0);
        String search = tfSearch.getText().trim();
        String status = cbStatus.getSelectedItem().toString();
        String kelas = cbKelas.getSelectedItem().toString();

        try {
            StringBuilder sb = new StringBuilder(
                "SELECT k.id, k.no_konsultasi, s.nama, s.kelas, s.jurusan_sma, c.nama as konselor, j.nama as rekomendasi, k.tanggal_konsultasi, k.status " +
                "FROM konsultasi k " +
                "JOIN siswa s ON k.siswa_id = s.id " +
                "JOIN konselor c ON k.konselor_id = c.id " +
                "LEFT JOIN jurusan_kuliah j ON k.rekomendasi_utama_id = j.id " +
                "WHERE 1=1 "
            );

            if (!search.isEmpty()) sb.append("AND (k.no_konsultasi LIKE ? OR s.nama LIKE ?) ");
            if (!status.equals("Semua Status")) sb.append("AND k.status = ? ");
            if (!kelas.equals("Semua Kelas")) sb.append("AND s.kelas = ? ");
            sb.append("ORDER BY k.tanggal_konsultasi DESC");

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sb.toString());
            int p = 1;
            if (!search.isEmpty()) {
                ps.setString(p++, "%" + search + "%");
                ps.setString(p++, "%" + search + "%");
            }
            if (!status.equals("Semua Status")) ps.setString(p++, status.toLowerCase().replace(" ", "_"));
            if (!kelas.equals("Semua Kelas")) ps.setString(p++, kelas);

            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("tanggal_konsultasi");
                String tgl = ts != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ts) : "-";
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("no_konsultasi"), rs.getString("nama"),
                    rs.getString("kelas"), rs.getString("jurusan_sma"), rs.getString("konselor"), 
                    rs.getString("rekomendasi") != null ? rs.getString("rekomendasi") : "-",
                    tgl, rs.getString("status").toUpperCase().replace("_", " ")
                });
                count++;
            }
            lblCounter.setText("Menampilkan " + count + " data rekaman.");
            rs.close(); ps.close();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }

    private void refreshCharts() {
        chartContainer.removeAll();
        
        // 1. Pie Chart: Distribusi Jurusan
        DefaultPieDataset pieData = new DefaultPieDataset();
        try (ResultSet rs = DBConnection.executeQuery(
                "SELECT j.nama, COUNT(*) as qty FROM konsultasi k " +
                "JOIN jurusan_kuliah j ON k.rekomendasi_utama_id = j.id " +
                "GROUP BY j.id ORDER BY qty DESC LIMIT 5")) {
            while (rs.next()) pieData.setValue(rs.getString(1), rs.getInt(2));
        } catch (Exception e) { e.printStackTrace(); }

        JFreeChart pieChart = ChartFactory.createPieChart("Top 5 Jurusan Direkomendasikan", pieData, true, true, false);
        styleChart(pieChart);
        ChartPanel cp1 = new ChartPanel(pieChart);
        cp1.setOpaque(false);
        cp1.setBackground(new Color(0,0,0,0));
        chartContainer.add(cp1);

        // 2. Bar Chart: Tren Minat per Kategori
        DefaultCategoryDataset barData = new DefaultCategoryDataset();
        try (ResultSet rs = DBConnection.executeQuery(
                "SELECT AVG(skor_teknologi), AVG(skor_sains), AVG(skor_sosial), AVG(skor_seni), AVG(skor_bisnis) FROM konsultasi")) {
            if (rs.next()) {
                barData.addValue(rs.getDouble(1), "Skor Rata-Rata", "Teknologi");
                barData.addValue(rs.getDouble(2), "Skor Rata-Rata", "Sains");
                barData.addValue(rs.getDouble(3), "Skor Rata-Rata", "Sosial");
                barData.addValue(rs.getDouble(4), "Skor Rata-Rata", "Seni");
                barData.addValue(rs.getDouble(5), "Skor Rata-Rata", "Bisnis");
            }
        } catch (Exception e) { e.printStackTrace(); }

        JFreeChart barChart = ChartFactory.createBarChart("Analisis Skor Minat Angkatan", "Kategori Keilmuan", "Poin Rata-Rata", barData, PlotOrientation.VERTICAL, false, true, false);
        styleChart(barChart);
        ChartPanel cp2 = new ChartPanel(barChart);
        cp2.setOpaque(false);
        cp2.setBackground(new Color(0,0,0,0));
        chartContainer.add(cp2);

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(new Color(0,0,0,0));
        chart.getTitle().setPaint(Theme.TEXT_WHITE);
        chart.getTitle().setFont(Theme.FONT_SUBTITLE);
        
        org.jfree.chart.plot.Plot plot = chart.getPlot();
        plot.setBackgroundPaint(Theme.isDarkMode ? new Color(255,255,255,10) : new Color(0,0,0,10));
        plot.setOutlinePaint(null);

        if (plot instanceof org.jfree.chart.plot.PiePlot) {
            org.jfree.chart.plot.PiePlot pp = (org.jfree.chart.plot.PiePlot) plot;
            pp.setLabelBackgroundPaint(new Color(255,255,255,200));
            pp.setLabelFont(Theme.FONT_SMALL);
            pp.setSectionPaint(0, Theme.ACCENT_BLUE);
            pp.setSectionPaint(1, Theme.ACCENT_PURPLE);
            pp.setSectionPaint(2, Theme.ACCENT_TEAL);
            pp.setSectionPaint(3, Theme.ACCENT_ORANGE);
            pp.setShadowPaint(null);
        } else if (plot instanceof org.jfree.chart.plot.CategoryPlot) {
            org.jfree.chart.plot.CategoryPlot cp = (org.jfree.chart.plot.CategoryPlot) plot;
            org.jfree.chart.renderer.category.BarRenderer renderer = (org.jfree.chart.renderer.category.BarRenderer) cp.getRenderer();
            renderer.setSeriesPaint(0, Theme.ACCENT_BLUE);
            renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
            renderer.setShadowVisible(false);
            
            cp.getDomainAxis().setTickLabelPaint(Theme.TEXT_SECONDARY);
            cp.getDomainAxis().setLabelPaint(Theme.TEXT_SECONDARY);
            cp.getRangeAxis().setTickLabelPaint(Theme.TEXT_SECONDARY);
            cp.getRangeAxis().setLabelPaint(Theme.TEXT_SECONDARY);
        }

        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(new Color(0,0,0,0));
            chart.getLegend().setItemPaint(Theme.TEXT_SECONDARY);
            chart.getLegend().setFrame(org.jfree.chart.block.BlockBorder.NONE);
        }
    }

    private void printTable() {
        try {
            MessageFormat headerFormat = new MessageFormat("Laporan Data Konsultasi Siswa - SMAN 1 Contoh Kota");
            MessageFormat footerFormat = new MessageFormat("- Halaman {0} -");
            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, headerFormat, footerFormat);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Pencetakan berhasil diselesaikan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception pe) {
            JOptionPane.showMessageDialog(this, "Terjadi kegagalan saat mencetak: " + pe.getMessage(), "Kesalahan Pencetakan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToPDF(String reportType) {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data yang tersedia untuk diekspor.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Laporan PDF");
        String fileName = reportType.equals("NILAI") ? "Laporan_Akademik_Siswa_" : "Laporan_Konsultasi_Terpadu_";
        fc.setSelectedFile(new java.io.File(fileName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
                PdfWriter.getInstance(doc, new FileOutputStream(fc.getSelectedFile()));
                doc.open();

                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(30, 45, 90));
                com.itextpdf.text.Font subFont   = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
                com.itextpdf.text.Font headerTbl = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
                com.itextpdf.text.Font cellFont  = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);

                Paragraph kop1 = new Paragraph("PEMERINTAH PROVINSI JAYA\nDINAS PENDIDIKAN DAN KEBUDAYAAN", subFont);
                kop1.setAlignment(Element.ALIGN_CENTER);
                Paragraph kop2 = new Paragraph("SMA NEGERI 1 CONTOH KOTA", titleFont);
                kop2.setAlignment(Element.ALIGN_CENTER);
                Paragraph kop3 = new Paragraph("Jalan Pendidikan No. 123, Telp. (021) 555-1234, Email: info@sman1contoh.sch.id\n", subFont);
                kop3.setAlignment(Element.ALIGN_CENTER);
                
                doc.add(kop1); doc.add(kop2); doc.add(kop3);
                doc.add(new Chunk(new LineSeparator(2, 100, new BaseColor(30, 45, 90), Element.ALIGN_CENTER, -5)));
                doc.add(new Paragraph("\n"));

                Paragraph repTitle = new Paragraph("LAPORAN PELAKSANAAN BIMBINGAN KARIR & KONSULTASI", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK));
                repTitle.setAlignment(Element.ALIGN_CENTER);
                doc.add(repTitle);
                
                Paragraph meta = new Paragraph(
                    "Tanggal Cetak : " + new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID")).format(new Date()) + "\n" +
                    "Filter Kelas  : " + cbKelas.getSelectedItem().toString() + "\n\n",
                    subFont
                );
                doc.add(meta);

                PdfPTable pdfTable = new PdfPTable(8);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{10f, 15f, 6f, 8f, 12f, 18f, 10f, 8f});

                String[] hKons = {"No. Konsul", "Nama Siswa", "Kelas", "Jur. SMA", "Konselor", "Rekomendasi Utama", "Tanggal", "Status"};
                BaseColor bgHeader = new BaseColor(30, 45, 90);
                for(String h : hKons) {
                    PdfPCell c = new PdfPCell(new Phrase(h, headerTbl));
                    c.setBackgroundColor(bgHeader); c.setHorizontalAlignment(Element.ALIGN_CENTER); c.setPadding(6);
                    pdfTable.addCell(c);
                }

                BaseColor bgRow1 = new BaseColor(245, 245, 250);
                BaseColor bgRow2 = BaseColor.WHITE;

                for (int i = 0; i < model.getRowCount(); i++) {
                    BaseColor rowColor = (i % 2 == 0) ? bgRow1 : bgRow2;
                    int[] colsToExport = {1, 2, 3, 4, 5, 6, 7, 8}; 
                    for (int colIdx : colsToExport) {
                        String val = model.getValueAt(i, colIdx).toString().replaceAll("[^\\x00-\\x7F]", ""); 
                        PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
                        cell.setBackgroundColor(rowColor); cell.setPadding(5);
                        pdfTable.addCell(cell);
                    }
                }

                doc.add(pdfTable);
                
                doc.add(new Paragraph("\n\n\n"));
                PdfPTable tableTtd = new PdfPTable(3);
                tableTtd.setWidthPercentage(100);
                
                PdfPCell cellKoor = new PdfPCell(new Phrase("Mengetahui,\nKoordinator Bimbingan Konseling\n\n\n\n\n\n(Dra. Siti Aminah, M.Pd)\nNIP. 197501012000012001", subFont));
                cellKoor.setBorder(com.itextpdf.text.Rectangle.NO_BORDER); cellKoor.setHorizontalAlignment(Element.ALIGN_CENTER);
                
                PdfPCell cellSpace = new PdfPCell(new Phrase(" ", subFont));
                cellSpace.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                
                PdfPCell cellKepsek = new PdfPCell(new Phrase("Kota Contoh, " + new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID")).format(new Date()) + "\nKepala Sekolah\n\n\n\n\n\n(Drs. Budi Santoso, M.Si)\nNIP. 197005151995121001", subFont));
                cellKepsek.setBorder(com.itextpdf.text.Rectangle.NO_BORDER); cellKepsek.setHorizontalAlignment(Element.ALIGN_CENTER);
                
                tableTtd.addCell(cellKoor); tableTtd.addCell(cellSpace); tableTtd.addCell(cellKepsek);
                doc.add(tableTtd);

                doc.close();
                JOptionPane.showMessageDialog(this, "Dokumen PDF berhasil diterbitkan dan disimpan.", "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(fc.getSelectedFile());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengekspor dokumen PDF: " + ex.getMessage(), "Kesalahan Ekspor", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void exportNilaiToPDF() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Laporan Akademik (PDF)");
        fc.setSelectedFile(new java.io.File("Laporan_Nilai_Profil_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document doc = new Document(PageSize.LEGAL.rotate(), 20, 20, 30, 30);
                PdfWriter.getInstance(doc, new FileOutputStream(fc.getSelectedFile()));
                doc.open();

                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(30, 45, 90));
                com.itextpdf.text.Font subFont   = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
                com.itextpdf.text.Font headerTbl = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, BaseColor.WHITE); 
                com.itextpdf.text.Font cellFont  = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.BLACK); 

                Paragraph kop1 = new Paragraph("PEMERINTAH PROVINSI JAYA\nDINAS PENDIDIKAN DAN KEBUDAYAAN", subFont);
                kop1.setAlignment(Element.ALIGN_CENTER);
                Paragraph kop2 = new Paragraph("SMA NEGERI 1 CONTOH KOTA", titleFont);
                kop2.setAlignment(Element.ALIGN_CENTER);
                Paragraph kop3 = new Paragraph("Jalan Pendidikan No. 123, Telp. (021) 555-1234, Email: info@sman1contoh.sch.id\n", subFont);
                kop3.setAlignment(Element.ALIGN_CENTER);
                
                doc.add(kop1); doc.add(kop2); doc.add(kop3);
                doc.add(new Chunk(new LineSeparator(2, 100, new BaseColor(30, 45, 90), Element.ALIGN_CENTER, -5)));
                doc.add(new Paragraph("\n"));

                Paragraph repTitle = new Paragraph("REKAPITULASI NILAI AKADEMIK DAN PROFIL SISWA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK));
                repTitle.setAlignment(Element.ALIGN_CENTER);
                doc.add(repTitle);
                
                Paragraph meta = new Paragraph(
                    "Tanggal Cetak : " + new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID")).format(new Date()) + "\n" +
                    "Filter Kelas  : " + cbKelas.getSelectedItem().toString() + "\n\n",
                    subFont
                );
                doc.add(meta);

                PdfPTable pdfTable = new PdfPTable(15);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{6f, 13f, 3f, 6f, 7f, 4f, 4f, 4f, 4f, 4f, 4f, 5f, 12f, 12f, 12f});

                String[] hNilai = {"NIS", "Nama Siswa", "L/P", "Kelas", "Jur.SMA", "MTK", "IPA", "IPS", "IND", "ING", "SNI", "Rata2", "Hobi", "Prestasi", "Cita-cita"};
                BaseColor bgHeader = new BaseColor(30, 45, 90);
                for(String h : hNilai) {
                    PdfPCell c = new PdfPCell(new Phrase(h, headerTbl));
                    c.setBackgroundColor(bgHeader); c.setHorizontalAlignment(Element.ALIGN_CENTER); c.setPadding(4);
                    pdfTable.addCell(c);
                }

                String kelas = cbKelas.getSelectedItem().toString();
                String query = "SELECT nis, nama, jenis_kelamin, kelas, jurusan_sma, " +
                               "nilai_matematika, nilai_ipa, nilai_ips, nilai_bahasa_ind, nilai_bahasa_ing, nilai_seni, " +
                               "hobi, prestasi, cita_cita FROM siswa ";
                if (!kelas.equals("Semua Kelas")) {
                    query += "WHERE kelas = '" + kelas + "' ";
                }
                query += "ORDER BY kelas ASC, nama ASC";

                BaseColor bgRow1 = new BaseColor(245, 245, 250);
                BaseColor bgRow2 = BaseColor.WHITE;

                try (ResultSet rs = DBConnection.executeQuery(query)) {
                    int r = 0;
                    while(rs.next()) {
                        BaseColor rowColor = (r % 2 == 0) ? bgRow1 : bgRow2;
                        
                        double mt = rs.getDouble("nilai_matematika");
                        double ip = rs.getDouble("nilai_ipa");
                        double is = rs.getDouble("nilai_ips");
                        double bi = rs.getDouble("nilai_bahasa_ind");
                        double en = rs.getDouble("nilai_bahasa_ing");
                        double sn = rs.getDouble("nilai_seni");
                        double avg = (mt + ip + is + bi + en + sn) / 6.0;

                        String[] rowData = {
                            rs.getString("nis"),
                            rs.getString("nama"),
                            rs.getString("jenis_kelamin"),
                            rs.getString("kelas"),
                            rs.getString("jurusan_sma"),
                            String.valueOf(mt), String.valueOf(ip), String.valueOf(is),
                            String.valueOf(bi), String.valueOf(en), String.valueOf(sn),
                            String.format(Locale.US, "%.1f", avg),
                            rs.getString("hobi") != null ? rs.getString("hobi") : "-",
                            rs.getString("prestasi") != null ? rs.getString("prestasi") : "-",
                            rs.getString("cita_cita") != null ? rs.getString("cita_cita") : "-"
                        };

                        for(int i=0; i<rowData.length; i++) {
                            PdfPCell cell = new PdfPCell(new Phrase(rowData[i], cellFont));
                            cell.setBackgroundColor(rowColor); cell.setPadding(4);
                            if(i == 2 || (i >= 5 && i <= 11)) cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pdfTable.addCell(cell);
                        }
                        r++;
                    }
                }
                
                doc.add(pdfTable);
                doc.close();
                JOptionPane.showMessageDialog(this, "Laporan Nilai Akademik & Profil (PDF) berhasil dibuat.", "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(fc.getSelectedFile());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengekspor dokumen PDF: " + ex.getMessage(), "Kesalahan Ekspor", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void exportToCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Rekapitulasi_Konsultasi_Master_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(fc.getSelectedFile()), "UTF-8")) {
                fw.write("\ufeff"); 
                String sep = ";"; 
                
                fw.write("SMAN 1 CONTOH KOTA - REKAPITULASI KONSULTASI LENGKAP\n");
                fw.write("Tanggal Cetak:" + sep + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "\n");
                fw.write("Filter Kelas:" + sep + cbKelas.getSelectedItem() + "\n");
                fw.write("Filter Status:" + sep + cbStatus.getSelectedItem() + "\n");
                fw.write("Total Data:" + sep + model.getRowCount() + "\n\n");
                
                fw.write("No Konsultasi" + sep + "Tanggal" + sep + "NIS" + sep + "Nama Siswa" + sep + "Kelas" + sep + "Jurusan SMA" + sep + "Konselor/Guru BK" + sep +
                         "Skor Teknologi" + sep + "Skor Sains" + sep + "Skor Sosial" + sep + "Skor Seni" + sep + "Skor Bisnis" + sep + "Skor Bahasa" + sep + "Skor Kesehatan" + sep +
                         "Rekomendasi Utama" + sep + "Alternatif 1" + sep + "Alternatif 2" + sep + "Status\n");
                
                String search = tfSearch.getText().trim();
                String status = cbStatus.getSelectedItem().toString();
                String kelas = cbKelas.getSelectedItem().toString();
                
                StringBuilder sb = new StringBuilder(
                    "SELECT k.no_konsultasi, k.tanggal_konsultasi, s.nis, s.nama as siswa, s.kelas, s.jurusan_sma, c.nama as konselor, " +
                    "k.skor_teknologi, k.skor_sains, k.skor_sosial, k.skor_seni, k.skor_bisnis, k.skor_bahasa, k.skor_kesehatan, " +
                    "j1.nama as rek1, j2.nama as rek2, j3.nama as rek3, k.status " +
                    "FROM konsultasi k " +
                    "JOIN siswa s ON k.siswa_id = s.id " +
                    "JOIN konselor c ON k.konselor_id = c.id " +
                    "LEFT JOIN jurusan_kuliah j1 ON k.rekomendasi_utama_id = j1.id " +
                    "LEFT JOIN jurusan_kuliah j2 ON k.rekomendasi_alt1_id = j2.id " +
                    "LEFT JOIN jurusan_kuliah j3 ON k.rekomendasi_alt2_id = j3.id " +
                    "WHERE 1=1 "
                );
                
                if (!search.isEmpty()) sb.append("AND (k.no_konsultasi LIKE '%").append(search).append("%' OR s.nama LIKE '%").append(search).append("%') ");
                if (!status.equals("Semua Status")) sb.append("AND k.status = '").append(status.toLowerCase().replace(" ", "_")).append("' ");
                if (!kelas.equals("Semua Kelas")) sb.append("AND s.kelas = '").append(kelas).append("' ");
                sb.append("ORDER BY k.tanggal_konsultasi DESC");
                
                ResultSet rs = DBConnection.executeQuery(sb.toString());
                int count = 0;
                long sumTekno=0, sumSains=0, sumSosial=0, sumSeni=0, sumBisnis=0, sumBahasa=0, sumKes=0;
                
                while(rs.next()) {
                    fw.write(
                        "\"" + rs.getString("no_konsultasi") + "\"" + sep +
                        "\"" + (rs.getTimestamp("tanggal_konsultasi") != null ? new SimpleDateFormat("dd/MM/yyyy").format(rs.getTimestamp("tanggal_konsultasi")) : "") + "\"" + sep +
                        "\"" + rs.getString("nis") + "\"" + sep +
                        "\"" + rs.getString("siswa") + "\"" + sep +
                        "\"" + rs.getString("kelas") + "\"" + sep +
                        "\"" + rs.getString("jurusan_sma") + "\"" + sep +
                        "\"" + rs.getString("konselor") + "\"" + sep +
                        rs.getInt("skor_teknologi") + sep +
                        rs.getInt("skor_sains") + sep +
                        rs.getInt("skor_sosial") + sep +
                        rs.getInt("skor_seni") + sep +
                        rs.getInt("skor_bisnis") + sep +
                        rs.getInt("skor_bahasa") + sep +
                        rs.getInt("skor_kesehatan") + sep +
                        "\"" + (rs.getString("rek1") != null ? rs.getString("rek1") : "") + "\"" + sep +
                        "\"" + (rs.getString("rek2") != null ? rs.getString("rek2") : "") + "\"" + sep +
                        "\"" + (rs.getString("rek3") != null ? rs.getString("rek3") : "") + "\"" + sep +
                        "\"" + rs.getString("status").toUpperCase().replace("_", " ") + "\"\n"
                    );
                    sumTekno += rs.getInt("skor_teknologi");
                    sumSains += rs.getInt("skor_sains");
                    sumSosial += rs.getInt("skor_sosial");
                    sumSeni += rs.getInt("skor_seni");
                    sumBisnis += rs.getInt("skor_bisnis");
                    sumBahasa += rs.getInt("skor_bahasa");
                    sumKes += rs.getInt("skor_kesehatan");
                    count++;
                }
                
                if(count > 0) {
                    fw.write(sep + sep + sep + sep + sep + sep + "RATA-RATA:" + sep + 
                        (sumTekno/count) + sep + (sumSains/count) + sep + (sumSosial/count) + sep +
                        (sumSeni/count) + sep + (sumBisnis/count) + sep + (sumBahasa/count) + sep +
                        (sumKes/count) + sep + sep + sep + "\n");
                }
                
                rs.getStatement().close();
                JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke Excel (CSV) secara rapi.", "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(fc.getSelectedFile());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal melakukan ekspor: " + e.getMessage(), "Kesalahan Ekspor", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void exportRaporVisual() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu siswa dari tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konsId = (int) table.getValueAt(row, 0);
        String namaSiswa = table.getValueAt(row, 2).toString();

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Rapor Visual Psikotest (PDF)");
        fc.setSelectedFile(new java.io.File("Rapor_Visual_Psikotest_" + namaSiswa.replace(" ", "_") + ".pdf"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ResultSet rs = DBConnection.executeQuery(
                    "SELECT skor_teknologi, skor_sains, skor_sosial, skor_seni, skor_bisnis, skor_bahasa, skor_kesehatan " +
                    "FROM konsultasi WHERE id = ?", konsId
                );

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Data skor tidak ditemukan untuk konsultasi ini.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                dataset.addValue(rs.getDouble("skor_teknologi"), "Skor Minat", "Teknologi");
                dataset.addValue(rs.getDouble("skor_sains"), "Skor Minat", "Sains");
                dataset.addValue(rs.getDouble("skor_sosial"), "Skor Minat", "Sosial");
                dataset.addValue(rs.getDouble("skor_seni"), "Skor Minat", "Seni");
                dataset.addValue(rs.getDouble("skor_bisnis"), "Skor Minat", "Bisnis");
                dataset.addValue(rs.getDouble("skor_bahasa"), "Skor Minat", "Bahasa");
                dataset.addValue(rs.getDouble("skor_kesehatan"), "Skor Minat", "Kesehatan");
                rs.getStatement().close();

                SpiderWebPlot plot = new SpiderWebPlot(dataset);
                plot.setInteriorGap(0.15);
                plot.setOutlinePaint(Color.WHITE);
                plot.setAxisLinePaint(Color.DARK_GRAY);
                plot.setSeriesPaint(0, new Color(79, 195, 247, 180)); 
                plot.setWebFilled(true);

                JFreeChart radarChart = new JFreeChart("Peta Potensi & Minat Bakat", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
                radarChart.setBackgroundPaint(Color.WHITE);

                BufferedImage bufferedImage = radarChart.createBufferedImage(600, 500);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(baos.toByteArray());
                pdfImage.setAlignment(Element.ALIGN_CENTER);

                Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
                PdfWriter.getInstance(doc, new FileOutputStream(fc.getSelectedFile()));
                doc.open();

                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new BaseColor(30, 45, 90));
                com.itextpdf.text.Font subFont   = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);

                Paragraph title = new Paragraph("RAPOR VISUAL PSIKOTEST (MINAT & BAKAT)", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                Paragraph subtitle = new Paragraph("Nama Siswa: " + namaSiswa + "\nNo Konsultasi: " + table.getValueAt(row, 1).toString(), subFont);
                subtitle.setAlignment(Element.ALIGN_CENTER);
                subtitle.setSpacingAfter(20);

                doc.add(title);
                doc.add(subtitle);
                doc.add(new Chunk(new LineSeparator(2, 100, new BaseColor(30, 45, 90), Element.ALIGN_CENTER, -5)));
                doc.add(new Paragraph("\n"));
                
                doc.add(pdfImage); 
                
                Paragraph desc = new Paragraph("\nGrafik di atas merepresentasikan kecenderungan dominasi minat dan bakat siswa berdasarkan hasil kuesioner sistem pakar. Semakin luas area grafik pada suatu sektor, semakin tinggi kecocokan siswa pada rumpun keilmuan tersebut.", subFont);
                desc.setAlignment(Element.ALIGN_JUSTIFIED);
                doc.add(desc);

                doc.close();
                JOptionPane.showMessageDialog(this, "Rapor Visual PDF berhasil diterbitkan.", "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(fc.getSelectedFile());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal membuat rapor visual: " + ex.getMessage(), "Kesalahan Ekspor", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void showAdvancedReportDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pusat Analitik & Laporan Lanjutan", true);
        dlg.setSize(600, 450); 
        dlg.setLocationRelativeTo(this);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        main.setBackground(Theme.mainBg());

        JLabel title = new JLabel("Pilih Jenis Laporan Analitik Eksekutif");
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        title.setForeground(Theme.TEXT_PRIMARY);
        
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        
        JRadioButton[] rbOptions = new JRadioButton[5]; 

        String[][] menuData = {
            {"⚠️ Laporan Early Warning System", "Siswa dengan Peluang PTN sangat rendah (< 10%)."},
            {"📊 Laporan Tren Peminatan", "Agregat dan peringkat jurusan & kampus yang paling diminati angkatan."},
            {"👨‍💼 Evaluasi Kinerja Konselor", "Rekapitulasi jumlah siswa yang ditangani oleh masing-masing Guru BK."},
            {"📉 Gap Analysis Nilai Akademik", "Kesenjangan antara Nilai Rapor siswa terhadap Passing Grade SNBT kampus."},
            {"🔄 Analisis Kesenjangan Lintas Jalur", "Mendeteksi siswa IPA yang mengambil Soshum, atau IPS yang mengambil Saintek."}
        };

        for (int i = 0; i < 5; i++) {
            JPanel card = Theme.createGlassCard(10);
            card.setLayout(new BorderLayout(10, 0));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
            
            rbOptions[i] = new JRadioButton("<html><b>" + menuData[i][0] + "</b><br><span style='font-size:9px; color:gray;'>" + menuData[i][1] + "</span></html>");
            rbOptions[i].setOpaque(false);
            rbOptions[i].setForeground(Theme.TEXT_PRIMARY);
            rbOptions[i].setFocusPainted(false);
            
            bg.add(rbOptions[i]);
            card.add(rbOptions[i], BorderLayout.CENTER);
            radioPanel.add(card);
            radioPanel.add(Box.createVerticalStrut(8));
        }
        rbOptions[0].setSelected(true); 

        JButton btnGenerate = Theme.createPrimaryButton("Mulai Eksekusi Laporan");
        JButton btnClose = Theme.createSecondaryButton("Batal");

        btnClose.addActionListener(e -> dlg.dispose());
        btnGenerate.addActionListener(e -> {
            int selectedIndex = 0;
            for (int i = 0; i < 5; i++) {
                if (rbOptions[i].isSelected()) {
                    selectedIndex = i;
                    break;
                }
            }
            dlg.dispose();
            executeAdvancedReport(selectedIndex);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnGenerate);
        btnPanel.add(btnClose);

        main.add(title, BorderLayout.NORTH);
        main.add(new JScrollPane(radioPanel) {{
            setOpaque(false); getViewport().setOpaque(false); setBorder(null);
            setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        }}, BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);

        dlg.setContentPane(main);
        dlg.setVisible(true);
    }

    private void executeAdvancedReport(int reportType) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Laporan Analitik (PDF)");
        String fileName = "Laporan_Analitik_Lanjutan_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";
        fc.setSelectedFile(new java.io.File(fileName));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
                PdfWriter.getInstance(doc, new FileOutputStream(fc.getSelectedFile()));
                doc.open();

                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(30, 45, 90));
                com.itextpdf.text.Font subFont   = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
                com.itextpdf.text.Font headerTbl = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
                com.itextpdf.text.Font cellFont  = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);

                String reportTitle = "";
                String query = "";
                String[] headers = null;
                float[] widths = null;

                switch (reportType) {
                    case 0: // Early Warning
                        reportTitle = "EARLY WARNING SYSTEM (SISWA BERISIKO PELUANG PTN < 10%)";
                        headers = new String[]{"NIS", "Nama Siswa", "Kelas", "Rekomendasi PTN", "Peluang", "Status Konsultasi"};
                        widths = new float[]{8f, 15f, 6f, 20f, 8f, 10f};
                        query = "SELECT s.nis, s.nama, s.kelas, j.nama as jurusan, uj.peluang_masuk, k.status " +
                                "FROM konsultasi k JOIN siswa s ON k.siswa_id = s.id " +
                                "LEFT JOIN rekomendasi_univ ru ON ru.konsultasi_id = k.id AND ru.rank_urutan = 1 " +
                                "LEFT JOIN universitas_jurusan uj ON ru.universitas_jurusan_id = uj.id " +
                                "LEFT JOIN jurusan_kuliah j ON k.rekomendasi_utama_id = j.id " +
                                "WHERE uj.peluang_masuk < 10 OR uj.peluang_masuk IS NULL ORDER BY uj.peluang_masuk ASC";
                        break;
                    case 1: // Tren Jurusan
                        reportTitle = "LAPORAN TREN PEMINATAN JURUSAN & KAMPUS";
                        headers = new String[]{"Peringkat", "Nama Jurusan / Program Studi", "Rumpun Keilmuan", "Jumlah Direkomendasikan"};
                        widths = new float[]{5f, 20f, 15f, 10f};
                        query = "SELECT @row := @row + 1 as no, j.nama, j.rumpun, COUNT(k.id) as total " +
                                "FROM jurusan_kuliah j LEFT JOIN konsultasi k ON k.rekomendasi_utama_id = j.id " +
                                "CROSS JOIN (SELECT @row := 0) r GROUP BY j.id ORDER BY total DESC";
                        break;
                    case 2: // Kinerja Konselor
                        reportTitle = "EVALUASI KINERJA KONSELOR / GURU BK";
                        headers = new String[]{"No", "Nama Konselor", "Total Siswa Ditangani", "Selesai", "Perlu Follow-Up"};
                        widths = new float[]{5f, 20f, 10f, 10f, 10f};
                        query = "SELECT @row := @row + 1 as no, c.nama, COUNT(k.id) as total, " +
                                "SUM(CASE WHEN k.status='selesai' THEN 1 ELSE 0 END) as selesai, " +
                                "SUM(CASE WHEN k.status='follow_up' THEN 1 ELSE 0 END) as followup " +
                                "FROM konselor c LEFT JOIN konsultasi k ON k.konselor_id = c.id " +
                                "CROSS JOIN (SELECT @row := 0) r GROUP BY c.id ORDER BY total DESC";
                        break;
                    case 3: // Gap Analysis
                        reportTitle = "GAP ANALYSIS (KESENJANGAN NILAI RAPOR VS PASSING GRADE PTN)";
                        headers = new String[]{"NIS", "Nama Siswa", "Rekomendasi PTN", "Rata-rata Nilai Inti", "Minimal Passing Grade", "Indikator"};
                        widths = new float[]{8f, 15f, 20f, 10f, 10f, 10f};
                        query = "SELECT s.nis, s.nama, j.nama, (s.nilai_matematika+s.nilai_ipa)/2 as rata, uj.passing_grade_snbt, " +
                                "CASE WHEN ((s.nilai_matematika+s.nilai_ipa)/2) >= 78 THEN 'Aman' ELSE 'Peringatan' END as status " +
                                "FROM konsultasi k JOIN siswa s ON k.siswa_id = s.id " +
                                "LEFT JOIN rekomendasi_univ ru ON ru.konsultasi_id = k.id AND ru.rank_urutan = 1 " +
                                "LEFT JOIN universitas_jurusan uj ON ru.universitas_jurusan_id = uj.id " +
                                "LEFT JOIN jurusan_kuliah j ON k.rekomendasi_utama_id = j.id";
                        break;
                    case 4: // Lintas Jurusan
                        reportTitle = "ANALISIS KESESUAIAN LINTAS JURUSAN (CROSS-MAJOR ANALYSIS)";
                        headers = new String[]{"NIS", "Nama Siswa", "Jurusan SMA", "Rekomendasi PTN", "Rumpun Ilmu", "Indikator Lintas"};
                        widths = new float[]{8f, 15f, 10f, 20f, 12f, 10f};
                        query = "SELECT s.nis, s.nama, s.jurusan_sma, j.nama as jurusan_ptn, j.rumpun, " +
                                "CASE " +
                                "  WHEN s.jurusan_sma = 'IPA' AND j.rumpun IN ('Sosial', 'Seni', 'Bahasa', 'Bisnis') THEN 'Lintas Jurusan' " +
                                "  WHEN s.jurusan_sma = 'IPS' AND j.rumpun IN ('Sains', 'Teknik', 'Kesehatan', 'Teknologi Informasi', 'MIPA') THEN 'Lintas Jurusan' " +
                                "  ELSE 'Linier / Sesuai' " +
                                "END as indikator " +
                                "FROM konsultasi k JOIN siswa s ON k.siswa_id = s.id " +
                                "LEFT JOIN jurusan_kuliah j ON k.rekomendasi_utama_id = j.id " +
                                "ORDER BY indikator DESC, s.nama ASC";
                        break;
                }

                Paragraph kop = new Paragraph("SMA NEGERI 1 CONTOH KOTA\nLaporan Analitik Berbasis Keputusan (Decision Support System)", titleFont);
                kop.setAlignment(Element.ALIGN_CENTER);
                doc.add(kop);
                doc.add(new Chunk(new LineSeparator(2, 100, new BaseColor(30, 45, 90), Element.ALIGN_CENTER, -5)));
                doc.add(new Paragraph("\n"));

                Paragraph repTitle = new Paragraph(reportTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK));
                repTitle.setAlignment(Element.ALIGN_CENTER);
                repTitle.setSpacingAfter(15);
                doc.add(repTitle);

                PdfPTable pdfTable = new PdfPTable(headers.length);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(widths);

                BaseColor bgHeader = new BaseColor(30, 45, 90);
                for(String h : headers) {
                    PdfPCell c = new PdfPCell(new Phrase(h, headerTbl));
                    c.setBackgroundColor(bgHeader); c.setHorizontalAlignment(Element.ALIGN_CENTER); c.setPadding(6);
                    pdfTable.addCell(c);
                }

                try (ResultSet rs = DBConnection.executeQuery(query)) {
                    int r = 0;
                    BaseColor rowBg1 = new BaseColor(245, 245, 250);
                    BaseColor rowBg2 = BaseColor.WHITE;
                    BaseColor warningBg = new BaseColor(255, 243, 205); 
                    
                    while(rs.next()) {
                        BaseColor rowColor = (r % 2 == 0) ? rowBg1 : rowBg2;
                        
                        if (reportType == 3 && rs.getString(6).equals("Peringatan")) rowColor = warningBg;
                        if (reportType == 4 && rs.getString(6).equals("Lintas Jurusan")) rowColor = warningBg;

                        for(int i=1; i<=headers.length; i++) {
                            String val = rs.getString(i) != null ? rs.getString(i) : "-";
                            PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
                            cell.setBackgroundColor(rowColor); cell.setPadding(5);
                            pdfTable.addCell(cell);
                        }
                        r++;
                    }
                }
                doc.add(pdfTable);
                doc.close();

                JOptionPane.showMessageDialog(this, "Laporan Analitik PDF berhasil diterbitkan.", "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(fc.getSelectedFile());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal membuat laporan analitik: " + ex.getMessage(), "Kesalahan Ekspor", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}