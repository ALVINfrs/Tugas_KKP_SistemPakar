package com.sistempakar.ui.rekap;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Form Laporan Konsultasi Canggih
 * Termasuk Visual Analytics, Filter Kelas, dan Export Excel/CSV
 */
public class FormLaporan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JComboBox<String> cbStatus, cbKelas;
    private JPanel chartContainer;

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

        JLabel title = new JLabel("📈  Laporan & Analisis Konsultasi");
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
        title.setForeground(Theme.textWhite());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);

        tfSearch = Theme.createTextField(15);
        tfSearch.putClientProperty("JTextField.placeholderText", "🔍 Cari Siswa...");
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        cbStatus = Theme.createComboBox(new String[]{"Semua Status", "Selesai", "Draft", "Follow Up"});
        cbStatus.addActionListener(e -> loadData());

        cbKelas = Theme.createComboBox(new String[]{"Semua Kelas"});
        loadKelasFilter();
        cbKelas.addActionListener(e -> loadData());

        JButton btnRefresh = Theme.createSecondaryButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> { 
            tfSearch.setText(""); 
            loadKelasFilter();
            loadData(); 
            refreshCharts(); 
        });

        filterPanel.add(Theme.createLabel("Filter:"));
        filterPanel.add(tfSearch);
        filterPanel.add(cbKelas);
        filterPanel.add(cbStatus);
        filterPanel.add(btnRefresh);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnExportCsv = Theme.createSuccessButton("📊  Export CSV");
        btnExportCsv.addActionListener(e -> exportToCSV());

        actionPanel.add(btnExportCsv);

        midPanel.add(title, BorderLayout.NORTH);
        midPanel.add(filterPanel, BorderLayout.WEST);
        midPanel.add(actionPanel, BorderLayout.EAST);

        // ── BOTTOM: TABLE ─────────────────────────────────────
        String[] columns = {"ID", "No. Konsultasi", "Siswa", "Kelas", "Rekomendasi Utama", "Tanggal", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.setRowHeight(40);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane sp = Theme.createScrollPane(table);
        
        // Main Layout Assembly
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);
        centerPanel.add(midPanel, BorderLayout.NORTH);
        centerPanel.add(sp, BorderLayout.CENTER);

        add(chartContainer, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
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
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT DISTINCT kelas FROM siswa WHERE kelas IS NOT NULL AND kelas != '' ORDER BY kelas");
            while (rs.next()) {
                cbKelas.addItem(rs.getString("kelas"));
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadData() {
        model.setRowCount(0);
        String search = tfSearch.getText().trim();
        String status = cbStatus.getSelectedItem().toString();
        String kelas = cbKelas.getSelectedItem().toString();

        try {
            StringBuilder sb = new StringBuilder(
                "SELECT k.id, k.no_konsultasi, s.nama, s.kelas, j.nama as rekomendasi, k.tanggal_konsultasi, k.status " +
                "FROM konsultasi k " +
                "JOIN siswa s ON k.siswa_id = s.id " +
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
            if (!status.equals("Semua Status")) ps.setString(p++, status.toLowerCase());
            if (!kelas.equals("Semua Kelas")) ps.setString(p++, kelas);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("tanggal_konsultasi");
                String tgl = ts != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(ts) : "-";
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("no_konsultasi"), rs.getString("nama"),
                    rs.getString("kelas"), rs.getString("rekomendasi") != null ? rs.getString("rekomendasi") : "-",
                    tgl, rs.getString("status").toUpperCase()
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void refreshCharts() {
        chartContainer.removeAll();
        
        // 1. Pie Chart: Distribusi Jurusan
        DefaultPieDataset pieData = new DefaultPieDataset();
        try {
            ResultSet rs = DBConnection.executeQuery(
                "SELECT j.nama, COUNT(*) as qty FROM konsultasi k " +
                "JOIN jurusan_kuliah j ON k.rekomendasi_utama_id = j.id " +
                "GROUP BY j.id ORDER BY qty DESC LIMIT 5"
            );
            while (rs.next()) pieData.setValue(rs.getString(1), rs.getInt(2));
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }

        JFreeChart pieChart = ChartFactory.createPieChart("Top 5 Jurusan Direkomendasikan", pieData, true, true, false);
        styleChart(pieChart);
        ChartPanel cp1 = new ChartPanel(pieChart);
        cp1.setOpaque(false);
        cp1.setBackground(new Color(0,0,0,0));
        chartContainer.add(cp1);

        // 2. Bar Chart: Tren Minat per Kategori
        DefaultCategoryDataset barData = new DefaultCategoryDataset();
        try {
            ResultSet rs = DBConnection.executeQuery(
                "SELECT AVG(skor_teknologi), AVG(skor_sains), AVG(skor_sosial), AVG(skor_seni), AVG(skor_bisnis) FROM konsultasi"
            );
            if (rs.next()) {
                barData.addValue(rs.getDouble(1), "Skor", "Tekno");
                barData.addValue(rs.getDouble(2), "Skor", "Sains");
                barData.addValue(rs.getDouble(3), "Skor", "Sosial");
                barData.addValue(rs.getDouble(4), "Skor", "Seni");
                barData.addValue(rs.getDouble(5), "Skor", "Bisnis");
            }
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }

        JFreeChart barChart = ChartFactory.createBarChart("Rata-rata Skor Minat Angkatan", "Kategori", "Skor", barData, PlotOrientation.VERTICAL, false, true, false);
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
        chart.getTitle().setPaint(Theme.textWhite());
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
            
            cp.getDomainAxis().setTickLabelPaint(Theme.isDarkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            cp.getDomainAxis().setLabelPaint(Theme.isDarkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            cp.getRangeAxis().setTickLabelPaint(Theme.isDarkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            cp.getRangeAxis().setLabelPaint(Theme.isDarkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        }

        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(new Color(0,0,0,0));
            chart.getLegend().setItemPaint(Theme.isDarkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            chart.getLegend().setFrame(org.jfree.chart.block.BlockBorder.NONE);
        }
    }

    private void exportToCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Rekap_Konsultasi_" + new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                for (int i = 1; i < model.getColumnCount(); i++) {
                    fw.write(model.getColumnName(i) + (i == model.getColumnCount() - 1 ? "" : ","));
                }
                fw.write("\n");
                for (int r = 0; r < model.getRowCount(); r++) {
                    for (int c = 1; c < model.getColumnCount(); c++) {
                        fw.write(model.getValueAt(r, c).toString() + (c == model.getColumnCount() - 1 ? "" : ","));
                    }
                    fw.write("\n");
                }
                JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke CSV!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal ekspor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
