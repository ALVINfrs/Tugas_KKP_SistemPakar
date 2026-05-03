package com.sistempakar.ui.rekap;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;
import com.sistempakar.util.ReportGenerator;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Panel Rekap Guru – Ringkasan Siswa & Peluang Masuk PTN
 * Menampilkan: status minat PTN/non-PTN, peluang, rekomendasi jurusan,
 * catatan konselor, dan chart distribusi
 */
public class PanelRekapGuru extends JPanel {

    private final String userRole;
    private final int userId;

    private JTable tblRekap;
    private DefaultTableModel modelRekap;
    private JLabel lblTotalSiswa, lblSudahKonsultasi, lblMinatPTN, lblAvgPeluang;
    private JComboBox<String> cbFilter, cbFilterJurusan;
    private JTextField tfSearch;
    private JPanel chartPanel;

    // Filter state
    private List<Object[]> allData = new ArrayList<>();

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
        pageTitle.setFont(new Font(Font.DIALOG,Font.BOLD,20));
        pageTitle.setForeground(Theme.textWhite());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        btnRow.setOpaque(false);
        JButton btnRefresh = Theme.createSecondaryButton("🔄 Refresh");
        JButton btnExport  = Theme.createPrimaryButton("📄 Export PDF");
        JButton btnCetak   = Theme.createSuccessButton("🖨️ Print Rekap");
        btnRefresh.addActionListener(e -> refresh());
        btnExport.addActionListener(e -> exportPDF());
        btnCetak.addActionListener(e -> cetakRekap());
        btnRow.add(btnRefresh); btnRow.add(btnExport); btnRow.add(btnCetak);

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
            public void insertUpdate(javax.swing.event.DocumentEvent e){applyFilter();}
            public void removeUpdate(javax.swing.event.DocumentEvent e){applyFilter();}
            public void changedUpdate(javax.swing.event.DocumentEvent e){}
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
        legend.add(mkLegend("🟢","Peluang Tinggi ≥ 15%", Theme.SUCCESS));
        legend.add(mkLegend("🟡","Peluang Sedang 8-15%",Theme.WARNING));
        legend.add(mkLegend("🔴","Peluang Rendah < 8%",Theme.DANGER));
        legend.add(mkLegend("⚫","Belum Konsultasi",Theme.TEXT_MUTED));

        add(topSection, BorderLayout.NORTH);
        add(midSection, BorderLayout.CENTER);
        add(legend, BorderLayout.SOUTH);
    }

    private JLabel statValue(String v) {
        JLabel l = new JLabel(v); l.setFont(new Font(Font.DIALOG,Font.BOLD,26)); return l;
    }

    private JPanel mkStatCard(String icon, String label, JLabel valLabel, Color color) {
        JPanel card = Theme.createAccentCard(color);
        card.setLayout(new BorderLayout(0,6));
        JLabel ico = new JLabel(icon,SwingConstants.CENTER); ico.setFont(new Font(Font.DIALOG,Font.PLAIN,26));
        valLabel.setForeground(color); valLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbl = new JLabel(label,SwingConstants.CENTER); lbl.setFont(Theme.FONT_SMALL); lbl.setForeground(Theme.TEXT_SECONDARY);
        card.add(ico,BorderLayout.NORTH); card.add(valLabel,BorderLayout.CENTER); card.add(lbl,BorderLayout.SOUTH);
        return card;
    }

    private JPanel mkLegend(String icon, String label, Color c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,4,0)); p.setOpaque(false);
        JLabel i = new JLabel(icon); i.setFont(new Font(Font.DIALOG,Font.PLAIN,13));
        JLabel l = new JLabel(label); l.setFont(Theme.FONT_SMALL); l.setForeground(c);
        p.add(i); p.add(l); return p;
    }

    private void buildTable() {
        String[] cols = {
            "NIS","Nama Siswa","Kelas","Jur.SMA","Status Konsultasi",
            "Rekomendasi Jurusan","Univ. Terbaik","Peluang Masuk","Kategori Peluang",
            "Catatan Konselor","Tips Belajar","Tgl Konsultasi"
        };
        modelRekap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };
        tblRekap = new JTable(modelRekap);
        Theme.styleTable(tblRekap);
        tblRekap.setRowHeight(42);
        tblRekap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Column widths
        int[] widths = {80,160,80,90,140,200,200,100,120,200,160,120};
        for (int i = 0; i < widths.length; i++)
            tblRekap.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Custom cell renderer for peluang coloring
        tblRekap.setDefaultRenderer(String.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                String v = val != null ? val.toString() : "";

                if (sel) { setBackground(Theme.TABLE_SELECT); setForeground(Theme.TEXT_WHITE); return this; }

                setBackground(row%2==0?Theme.TABLE_ROW_EVEN:Theme.TABLE_ROW_ODD);
                setForeground(Theme.TEXT_PRIMARY);

                if (col == 4) { // status
                    if (v.contains("Sudah")) setForeground(Theme.SUCCESS);
                    else setForeground(Theme.TEXT_MUTED);
                } else if (col == 8) { // kategori peluang
                    if (v.contains("Tinggi"))  setForeground(Theme.SUCCESS);
                    else if (v.contains("Sedang")) setForeground(Theme.WARNING);
                    else if (v.contains("Rendah")) setForeground(Theme.DANGER);
                    else setForeground(Theme.TEXT_MUTED);
                } else if (col == 7) { // peluang %
                    try {
                        double pct = Double.parseDouble(v.replace("%","").trim());
                        if (pct >= 15) setForeground(Theme.SUCCESS);
                        else if (pct >= 8) setForeground(Theme.WARNING);
                        else setForeground(Theme.DANGER);
                    } catch (Exception ignored) {}
                }
                return this;
            }
        });

        // Double-click = detail dialog
        tblRekap.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2 && tblRekap.getSelectedRow()>=0)
                    showDetailDialog(tblRekap.getSelectedRow());
            }
        });
    }

    public void refresh() {
        allData.clear();
        modelRekap.setRowCount(0);
        int total=0, konsultasi=0; double sumPeluang=0; int ptnCount=0;

        try {
            // Get all students with their consultation status
            String sql =
                "SELECT s.nis, s.nama, s.kelas, s.jurusan_sma, " +
                "  k.no_konsultasi, k.tanggal_konsultasi, k.status, " +
                "  j.nama AS jur_nama, j.rumpun, " +
                "  j2.nama AS alt1, j3.nama AS alt2, " +
                "  k.catatan_konselor, k.tips_belajar, k.rekomendasi_kegiatan, " +
                "  u.nama AS univ_nama, uj.peluang_masuk, uj.akreditasi_prodi, " +
                "  km.nama AS kategori " +
                "FROM siswa s " +
                "LEFT JOIN konsultasi k ON k.id = (" +
                "  SELECT id FROM konsultasi WHERE siswa_id=s.id ORDER BY tanggal_konsultasi DESC LIMIT 1) " +
                "LEFT JOIN jurusan_kuliah j  ON k.rekomendasi_utama_id = j.id " +
                "LEFT JOIN jurusan_kuliah j2 ON k.rekomendasi_alt1_id  = j2.id " +
                "LEFT JOIN jurusan_kuliah j3 ON k.rekomendasi_alt2_id  = j3.id " +
                "LEFT JOIN kategori_minat km ON j.kategori_id = km.id " +
                "LEFT JOIN rekomendasi_univ ru ON ru.konsultasi_id=k.id AND ru.rank_urutan=1 " +
                "LEFT JOIN universitas_jurusan uj ON ru.universitas_jurusan_id=uj.id " +
                "LEFT JOIN universitas u ON uj.universitas_id=u.id " +
                "ORDER BY s.nama ASC";

            ResultSet rs = DBConnection.executeQuery(sql);
            while (rs.next()) {
                total++;
                String nokons   = rs.getString("no_konsultasi");
                boolean sudah   = nokons != null;
                if (sudah) konsultasi++;

                double peluang  = rs.getDouble("peluang_masuk");
                String univName = rs.getString("univ_nama");
                String jurNama  = rs.getString("jur_nama");
                String kategori = rs.getString("kategori");
                String catKons  = rs.getString("catatan_konselor");
                String tips     = rs.getString("tips_belajar");
                String tglKons  = "";
                Timestamp ts = rs.getTimestamp("tanggal_konsultasi");
                if (ts != null) tglKons = new SimpleDateFormat("dd/MM/yyyy").format(ts);

                // Determine peluang category
                String katPeluang;
                if (!sudah) katPeluang = "⚫ Belum Konsultasi";
                else if (peluang >= 15) katPeluang = "🟢 Tinggi";
                else if (peluang >= 8)  katPeluang = "🟡 Sedang";
                else                    katPeluang = "🔴 Rendah";

                if (sudah && peluang >= 8) ptnCount++;
                if (sudah) sumPeluang += peluang;

                Object[] row = {
                    rs.getString("nis"),
                    rs.getString("nama"),
                    rs.getString("kelas"),
                    rs.getString("jurusan_sma"),
                    sudah ? "✅ Sudah Konsultasi" : "⏳ Belum Konsultasi",
                    jurNama != null ? jurNama : "-",
                    univName != null ? univName : "-",
                    sudah ? String.format("%.1f%%", peluang) : "-",
                    katPeluang,
                    catKons != null ? catKons : "-",
                    tips != null ? tips.substring(0, Math.min(60, tips.length()))+"..." : "-",
                    tglKons
                };
                allData.add(row);
                modelRekap.addRow(row);
            }
            rs.getStatement().close();

            // Update stat labels
            lblTotalSiswa.setText(String.valueOf(total));
            lblSudahKonsultasi.setText(String.valueOf(konsultasi));
            lblMinatPTN.setText(konsultasi > 0 ? String.format("%.0f%%", (double)ptnCount/konsultasi*100) : "0%");
            lblAvgPeluang.setText(konsultasi > 0 ? String.format("%.1f%%", sumPeluang/konsultasi) : "0%");

            // Refresh chart
            rebuildChart(total, konsultasi, ptnCount);

        } catch (Exception e) {
            e.printStackTrace();
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
                // Will be filled by rebuildChart
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
        ct.setFont(Theme.FONT_SUBTITLE); ct.setForeground(Theme.textWhite());

        // Donut chart panel
        JPanel donut = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w=getWidth(), h=getHeight(), r=Math.min(w,h)-40;
                int x=(w-r)/2, y=(h-r)/2;

                if (total == 0) {
                    g2.setColor(Theme.TEXT_MUTED); g2.drawString("Tidak ada data",w/2-40,h/2); g2.dispose(); return;
                }

                double belum = total - konsultasi;
                double nonPtn = konsultasi - ptnMinat;

                // Angles
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

                // Inner circle (donut hole)
                int ir = r/3;
                int ix = (w-ir)/2, iy=(h-ir)/2;
                g2.setColor(Theme.isDarkMode ? new Color(20, 30, 60) : new Color(255,255,255));
                g2.fillOval(ix,iy,ir,ir);

                // Center text
                g2.setFont(new Font(Font.DIALOG,Font.BOLD,18)); g2.setColor(Theme.textWhite());
                String ct2 = String.valueOf(total);
                int tw=g2.getFontMetrics().stringWidth(ct2);
                g2.drawString(ct2,(w-tw)/2,h/2+2);
                g2.setFont(Theme.FONT_SMALL); g2.setColor(Theme.TEXT_MUTED);
                String sl="siswa"; int sl2=g2.getFontMetrics().stringWidth(sl);
                g2.drawString(sl,(w-sl2)/2,h/2+18);

                g2.dispose();
            }
        };
        donut.setOpaque(false); donut.setPreferredSize(new Dimension(200,200));

        // Bar chart: peluang categories
        JPanel bars = new JPanel(new GridLayout(3,1,0,10)); bars.setOpaque(false);
        int blmKons = total - konsultasi;
        int rendah  = Math.max(0, konsultasi - ptnMinat);
        addBar(bars, "Belum Konsultasi", blmKons, total, Theme.TEXT_MUTED);
        addBar(bars, "Peluang ≥ 8%", ptnMinat, total, Theme.SUCCESS);
        addBar(bars, "Peluang < 8%", rendah, total, Theme.DANGER);

        // Legend
        JPanel leg = new JPanel(new GridLayout(3,1,0,4)); leg.setOpaque(false);
        String[][] legs = {{"⚫","Belum Konsultasi"},{"🟢","Peluang PTN ≥ 8%"},{"🔴","Peluang PTN < 8%"}};
        Color[] lc = {Theme.TEXT_MUTED, Theme.SUCCESS, Theme.DANGER};
        int[] lv = {blmKons, ptnMinat, rendah};
        for (int i=0;i<3;i++) {
            JPanel lr=new JPanel(new BorderLayout(6,0)); lr.setOpaque(false);
            JLabel li=new JLabel(legs[i][0]); li.setFont(new Font(Font.DIALOG,Font.PLAIN,12));
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

    // ── DETAIL DIALOG ──────────────────────────────────────────
    private void showDetailDialog(int row) {
        Object[] data = new Object[modelRekap.getColumnCount()];
        for (int i=0;i<data.length;i++) data[i]=modelRekap.getValueAt(row,i);

        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Detail Rekap Siswa", true);
        dlg.setSize(760,620); dlg.setLocationRelativeTo(this);

        JPanel main = new JPanel(new BorderLayout(16,16)){
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Theme.paintDeepSpaceBackground((Graphics2D)g,getWidth(),getHeight());
            }
        };
        main.setOpaque(true); main.setBorder(BorderFactory.createEmptyBorder(24,28,24,28));

        // Header
        JPanel header = Theme.createGlassCard(12); header.setLayout(new BorderLayout(16,0));
        JLabel hIcon = new JLabel("👨‍🎓"); hIcon.setFont(new Font(Font.DIALOG,Font.PLAIN,40));
        JPanel hText = new JPanel(new GridLayout(3,1,0,2)); hText.setOpaque(false);
        JLabel hName = new JLabel(data[1].toString()); hName.setFont(new Font(Font.DIALOG,Font.BOLD,20)); hName.setForeground(Theme.textWhite());
        JLabel hSub  = new JLabel("NIS: "+data[0]+" │ Kelas: "+data[2]+" │ "+data[3]); hSub.setFont(Theme.FONT_BODY); hSub.setForeground(Theme.TEXT_SECONDARY);
        String peluangStr = data[7].toString(); Color peluangColor = Theme.TEXT_MUTED;
        try { double p=Double.parseDouble(peluangStr.replace("%","").trim()); peluangColor = p>=15?Theme.SUCCESS:p>=8?Theme.WARNING:Theme.DANGER; } catch(Exception ignored){}
        JLabel hPeluang = new JLabel("Peluang Masuk PTN: "+peluangStr+"  "+data[8]); hPeluang.setFont(new Font(Font.DIALOG,Font.BOLD,14)); hPeluang.setForeground(peluangColor);
        hText.add(hName); hText.add(hSub); hText.add(hPeluang);
        header.add(hIcon,BorderLayout.WEST); header.add(hText,BorderLayout.CENTER);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_BODY); tabs.setForeground(Theme.TEXT_PRIMARY);

        // Tab 1: Rekomendasi
        JPanel tabRek = new JPanel(new GridLayout(2,1,0,12)); tabRek.setOpaque(false); tabRek.setBorder(BorderFactory.createEmptyBorder(12,8,8,8));
        JPanel rekCard = Theme.createAccentCard(Theme.ACCENT_BLUE); rekCard.setLayout(new GridLayout(3,1,0,6));
        rekCard.add(mkDetailRow("🎓 Rekomendasi Utama:", data[5].toString(), Theme.ACCENT_BLUE));
        rekCard.add(mkDetailRow("🏛️ Universitas Terbaik:", data[6].toString(), Theme.ACCENT_PURPLE));
        rekCard.add(mkDetailRow("📊 Peluang Masuk:", peluangStr+"  "+data[8],peluangColor));

        JPanel ptnCard = Theme.createGlassCard(10); ptnCard.setLayout(new BorderLayout(0,8));
        JLabel ptnTitle = new JLabel("🏛️  Cara Meningkatkan Peluang Masuk PTN"); ptnTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 13)); ptnTitle.setForeground(Theme.ACCENT_TEAL);
        boolean sudah = data[4].toString().contains("Sudah");
        JTextArea ptnText = new JTextArea(generatePeluangTips(peluangStr, sudah));
        ptnText.setEditable(false); ptnText.setWrapStyleWord(true); ptnText.setLineWrap(true);
        ptnText.setOpaque(false); ptnText.setFont(new Font(Font.DIALOG,Font.PLAIN,12)); ptnText.setForeground(Theme.TEXT_SECONDARY);
        ptnCard.add(ptnTitle,BorderLayout.NORTH); ptnCard.add(ptnText,BorderLayout.CENTER);

        tabRek.add(rekCard); tabRek.add(ptnCard);
        tabs.addTab("🎓 Rekomendasi",tabRek);

        // Tab 2: Catatan Konselor
        JPanel tabCat = new JPanel(new BorderLayout(0,12)); tabCat.setOpaque(false); tabCat.setBorder(BorderFactory.createEmptyBorder(12,8,8,8));
        JPanel catCard = Theme.createAccentCard(Theme.ACCENT_PURPLE); catCard.setLayout(new BorderLayout(0,8));
        JLabel catTitle = new JLabel("📝  Catatan Konselor / Guru BK"); catTitle.setFont(Theme.FONT_BOLD); catTitle.setForeground(Theme.ACCENT_PURPLE);
        JTextArea catText = new JTextArea(data[9].toString().equals("-")?"Belum ada catatan dari konselor.":data[9].toString());
        catText.setEditable(false); catText.setWrapStyleWord(true); catText.setLineWrap(true);
        catText.setOpaque(false); catText.setFont(new Font(Font.DIALOG,Font.PLAIN,13)); catText.setForeground(Theme.TEXT_PRIMARY);
        catCard.add(catTitle,BorderLayout.NORTH); catCard.add(Theme.createScrollPane(catText),BorderLayout.CENTER);

        JPanel tglCard=Theme.createGlassCard(10); tglCard.setLayout(new BorderLayout(8,0));
        tglCard.add(mkDetailRow("📅 Tanggal Konsultasi:",data[11].toString(),Theme.ACCENT_TEAL),BorderLayout.CENTER);
        tglCard.setPreferredSize(new Dimension(0,52));

        tabCat.add(catCard,BorderLayout.CENTER); tabCat.add(tglCard,BorderLayout.SOUTH);
        tabs.addTab("📝 Catatan",tabCat);

        // Tab 3: Tips Belajar
        JPanel tabTips = new JPanel(new BorderLayout(0,12)); tabTips.setOpaque(false); tabTips.setBorder(BorderFactory.createEmptyBorder(12,8,8,8));
        JPanel tipsCard=Theme.createAccentCard(Theme.ACCENT_ORANGE); tipsCard.setLayout(new BorderLayout(0,8));
        JLabel tipsTitle=new JLabel("💡  Tips Belajar & Kegiatan"); tipsTitle.setFont(Theme.FONT_BOLD); tipsTitle.setForeground(Theme.ACCENT_ORANGE);
        JTextArea tipsText=new JTextArea(data[10].toString().equals("-...")?"Belum ada tips belajar.\nLakukan konsultasi terlebih dahulu.":data[10].toString());
        tipsText.setEditable(false); tipsText.setWrapStyleWord(true); tipsText.setLineWrap(true);
        tipsText.setOpaque(false); tipsText.setFont(new Font(Font.DIALOG,Font.PLAIN,13)); tipsText.setForeground(Theme.TEXT_PRIMARY);
        tipsCard.add(tipsTitle,BorderLayout.NORTH); tipsCard.add(Theme.createScrollPane(tipsText),BorderLayout.CENTER);
        tabTips.add(tipsCard,BorderLayout.CENTER);
        tabs.addTab("💡 Tips Belajar",tabTips);

        main.add(header,BorderLayout.NORTH);
        main.add(tabs,BorderLayout.CENTER);

        JButton btnFullPDF = Theme.createSuccessButton("💾  Laporan Lengkap (PDF)");
        JButton close=Theme.createSecondaryButton("Tutup");

        btnFullPDF.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new java.io.File("Laporan_Profil_" + data[1].toString().replace(" ","_") + ".pdf"));
            if (fc.showSaveDialog(dlg) == JFileChooser.APPROVE_OPTION) {
                try {
                    // Get the consultation ID from the table row (first element of allData list for this student)
                    // We need to find the student's index in the original allData list
                    int sid = -1;
                    for (Object[] r : allData) {
                        if (r[0].toString().equals(data[0].toString())) { // match NIS
                            // We need to query the ID again since NIS is unique
                            ResultSet rs = DBConnection.executeQuery("SELECT k.id FROM konsultasi k JOIN siswa s ON k.siswa_id=s.id WHERE s.nis=? ORDER BY k.tanggal_konsultasi DESC LIMIT 1", data[0].toString());
                            if (rs.next()) sid = rs.getInt(1);
                            rs.close();
                            break;
                        }
                    }
                    
                    if (sid != -1) {
                        ReportGenerator.generatePremiumReport(sid, fc.getSelectedFile().getAbsolutePath());
                        JOptionPane.showMessageDialog(dlg, "Laporan lengkap berhasil dibuat!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(dlg, "Siswa ini belum melakukan konsultasi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dlg, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        close.addActionListener(e->dlg.dispose());
        JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0)); btnRow.setOpaque(false);
        btnRow.add(btnFullPDF); btnRow.add(close); 
        main.add(btnRow,BorderLayout.SOUTH);

        dlg.setContentPane(main); dlg.setVisible(true);
    }

    private JPanel mkDetailRow(String label, String value, Color valColor) {
        JPanel p=new JPanel(new BorderLayout(12,0)); p.setOpaque(false);
        JLabel l=Theme.createBoldLabel(label); l.setPreferredSize(new Dimension(180,22));
        JLabel v=new JLabel(value); v.setFont(Theme.FONT_BODY); v.setForeground(valColor);
        p.add(l,BorderLayout.WEST); p.add(v,BorderLayout.CENTER);
        return p;
    }

    private String generatePeluangTips(String peluangStr, boolean sudah) {
        if (!sudah) return "Siswa belum melakukan konsultasi. Segera ajak siswa untuk konsultasi dengan guru BK untuk mendapatkan analisis peluang masuk PTN.";
        double p=0; try{p=Double.parseDouble(peluangStr.replace("%","").trim());}catch(Exception e){}
        if (p>=15) return "✅ Peluang bagus! Pertahankan prestasi akademik dan lanjutkan persiapan:\n• Ikuti tryout UTBK secara rutin\n• Latihan soal dari bank soal 3 tahun terakhir\n• Daftarkan di SNBP jika nilai rapor konsisten tinggi\n• Persiapkan jalur mandiri sebagai backup";
        if (p>=8) return "⚠️ Peluang sedang, butuh usaha ekstra:\n• Tingkatkan intensitas belajar minimal 3 jam/hari\n• Ikut bimbingan belajar intensif\n• Fokus pada mata pelajaran yang kurang\n• Pertimbangkan PTN alternatif dengan passing grade lebih rendah\n• Coba jalur SNBP jika nilai rapor memenuhi syarat";
        return "🔴 Peluang masih rendah, butuh kerja keras:\n• Evaluasi strategi belajar dan tambah jam belajar\n• Ikut program bimbingan belajar intensif\n• Pertimbangkan jurusan/PTN dengan persaingan lebih rendah\n• Manfaatkan jalur mandiri atau PTS bereputasi baik\n• Konsultasikan kembali dengan guru BK untuk strategi yang lebih tepat";
    }

    // ── EXPORT & PRINT ─────────────────────────────────────────
    private void exportPDF() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Rekap sebagai PDF");
        fc.setSelectedFile(new java.io.File("Rekap_Siswa_PTN_"+new SimpleDateFormat("yyyyMMdd").format(new java.util.Date())+".pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            new SwingWorker<Void,Void>(){
                @Override protected Void doInBackground() throws Exception { generatePDF(fc.getSelectedFile().getAbsolutePath()); return null; }
                @Override protected void done() { JOptionPane.showMessageDialog(PanelRekapGuru.this,"PDF berhasil disimpan!\n"+fc.getSelectedFile().getAbsolutePath(),"Sukses",JOptionPane.INFORMATION_MESSAGE); }
            }.execute();
        }
    }

    private void generatePDF(String path) {
        try {
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new java.io.FileOutputStream(path));
            doc.open();

            // Fonts
            com.itextpdf.text.Font titleFont  = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 18);
            com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 10);
            com.itextpdf.text.Font cellFont   = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA, 9);
            com.itextpdf.text.Font subFont    = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA, 11);

            // Header block
            com.itextpdf.text.Paragraph h1 = new com.itextpdf.text.Paragraph("REKAP GURU – STATUS & PELUANG MASUK PTN SISWA", titleFont);
            h1.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            com.itextpdf.text.Paragraph h2 = new com.itextpdf.text.Paragraph("Sistem Pakar Rekomendasi Jurusan | Metode Forward Chaining", subFont);
            h2.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            com.itextpdf.text.Paragraph h3 = new com.itextpdf.text.Paragraph("Dicetak: "+new SimpleDateFormat("dd MMMM yyyy HH:mm",new Locale("id","ID")).format(new java.util.Date()), subFont);
            h3.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            doc.add(h1); doc.add(h2); doc.add(h3);
            doc.add(new com.itextpdf.text.Paragraph(" "));

            // Summary
            com.itextpdf.text.Paragraph summary = new com.itextpdf.text.Paragraph(
                "Total Siswa: "+lblTotalSiswa.getText()+"  |  " +
                "Sudah Konsultasi: "+lblSudahKonsultasi.getText()+"  |  " +
                "Minat PTN: "+lblMinatPTN.getText()+"  |  " +
                "Rata-rata Peluang: "+lblAvgPeluang.getText(),
                com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.HELVETICA_BOLD, 10)
            );
            doc.add(summary); doc.add(new com.itextpdf.text.Paragraph(" "));

            // Table
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(10);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f,10f,5f,5f,9f,12f,12f,6f,8f,10f});

            String[] headers={"NIS","Nama Siswa","Kelas","Jur.SMA","Status","Rekomendasi Jurusan","Univ. Terbaik","Peluang","Kategori","Catatan Konselor"};
            com.itextpdf.text.BaseColor headerBg = new com.itextpdf.text.BaseColor(13,20,45);
            for(String h:headers){
                com.itextpdf.text.pdf.PdfPCell c=new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(h,headerFont));
                c.setBackgroundColor(headerBg); c.setPadding(7); c.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                table.addCell(c);
            }

            com.itextpdf.text.BaseColor rowBg1 = new com.itextpdf.text.BaseColor(240,244,255);
            com.itextpdf.text.BaseColor rowBg2 = new com.itextpdf.text.BaseColor(255,255,255);
            for(int r=0;r<modelRekap.getRowCount();r++){
                com.itextpdf.text.BaseColor rbg = r%2==0?rowBg1:rowBg2;
                for(int c=0;c<10;c++){
                    String val=modelRekap.getValueAt(r,c)!=null?modelRekap.getValueAt(r,c).toString():"";
                    // clean emoji for PDF
                    val=val.replaceAll("[^\\x00-\\x7F]","");
                    com.itextpdf.text.pdf.PdfPCell cell=new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(val,cellFont));
                    cell.setBackgroundColor(rbg); cell.setPadding(5); table.addCell(cell);
                }
            }
            doc.add(table);
            doc.close();
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Gagal export PDF: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }

    private void cetakRekap() {
        try {
            tblRekap.print(JTable.PrintMode.FIT_WIDTH, new java.text.MessageFormat("Rekap Guru – Status & Peluang Masuk PTN"), new java.text.MessageFormat("Halaman {0}"));
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Gagal print: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }
}
