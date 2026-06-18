package com.sistempakar.ui;

import com.sistempakar.db.DBConnection;
import com.sistempakar.ui.master.*;
import com.sistempakar.ui.transaksi.FormKonsultasi;
import com.sistempakar.ui.rekap.PanelRekapGuru;
import com.sistempakar.ui.rekap.FormLaporan;
import com.sistempakar.util.Theme;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class MainFrame extends JFrame {

    private final int userId;
    private final String userNama;
    private final String userRole;

    private JPanel contentArea;
    private CardLayout cardLayout;
    private JLabel pageTitle, clockLabel;

    // 1. PENAMBAHAN MENU BARU PADA ARRAY (Indeks ke-10)
    private final String[] ICONS  = {"🏠","👨‍🎓","📚","🏛️","👨‍💼","❓","⚙️","💬","📈","📋","👥"};
    private final String[] LABELS = {"Dashboard","Data Siswa","Data Jurusan","Data Universitas","Data Konselor","Pertanyaan","Aturan/Rules","Konsultasi","Laporan","Rekap Guru","Manajemen User"};
    private final String[] CARDS  = {"dashboard","siswa","jurusan","universitas","konselor","pertanyaan","aturan","konsultasi","laporan","rekap","users"};
    
    // 2. PENGATURAN HAK AKSES (Hanya Admin yang bernilai true pada indeks terakhir)
    private final boolean[] ADMIN_ACC    = {true,true,true,true,true,true,true,true,true,true,true};
    private final boolean[] KONSELOR_ACC = {true,true,false,false,true,false,false,true,true,true,false};
    private final boolean[] GURU_ACC     = {true,true,false,false,false,false,false,true,true,true,false};

    private JButton[] menuBtns;
    private int activeIdx = 0;

    private FormSiswa        formSiswa;
    private FormJurusan      formJurusan;
    private FormUniversitas  formUniversitas;
    private FormKonselor     formKonselor;
    private FormPertanyaan   formPertanyaan;
    private FormAturan       formAturan;
    private FormKonsultasi   formKonsultasi;
    private FormLaporan      formLaporan;
    private PanelRekapGuru   panelRekap;
    
    // 3. DEKLARASI PANEL MANAJEMEN USER
    private UserManagementPanel panelUserManagement;

    public MainFrame(int userId, String nama, String role) {
        super("Sistem Pakar Rekomendasi Jurusan | " + nama);
        this.userId = userId; this.userNama = nama; this.userRole = role;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1440, 860); setMinimumSize(new Dimension(1100,700)); setLocationRelativeTo(null);
        build(); startClock();
    }

    public MainFrame() { this(0, "Administrator", "admin"); }

    private boolean[] access() {
        if ("admin".equals(userRole))    return ADMIN_ACC;
        if ("konselor".equals(userRole)) return KONSELOR_ACC;
        return GURU_ACC;
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Theme.paintDeepSpaceBackground((Graphics2D)g, getWidth(), getHeight());
            }
        };
        root.setOpaque(true);
        setContentPane(root);
        root.add(sidebar(), BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout()); right.setOpaque(false);
        right.add(topBar(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout); contentArea.setOpaque(false);
        contentArea.add(dashboard(), "dashboard");

        right.add(contentArea, BorderLayout.CENTER);
        root.add(right, BorderLayout.CENTER);
    }

    // ── SIDEBAR ──────────────────────────────────────────────────
    private JPanel sidebar() {
        JPanel sb = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setColor(Theme.sidebarBg()); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(Theme.isDarkMode ? Theme.GLASS_BORDER2 : new Color(0,0,0,20)); 
                g2.drawLine(getWidth()-1,0,getWidth()-1,getHeight());
                g2.dispose();
            }
        };
        sb.setLayout(new BorderLayout()); sb.setPreferredSize(new Dimension(240,0)); sb.setOpaque(false);

        // Logo
        JPanel logo = new JPanel(new BorderLayout(10,6));
        logo.setOpaque(false); logo.setBorder(BorderFactory.createEmptyBorder(24,20,16,20));
        JLabel li = new JLabel("🎓"); li.setFont(new Font(Font.DIALOG,Font.PLAIN,32));
        JPanel lt = new JPanel(new GridLayout(2,1)); lt.setOpaque(false);
        JLabel t1 = new JLabel("SISTEM PAKAR"); t1.setFont(new Font(Font.DIALOG,Font.BOLD,12)); t1.setForeground(Theme.textWhite());
        JLabel t2 = new JLabel("Rekomendasi Jurusan"); t2.setFont(new Font(Font.DIALOG,Font.PLAIN,10)); t2.setForeground(Theme.textMuted());
        lt.add(t1); lt.add(t2); logo.add(li,BorderLayout.WEST); logo.add(lt,BorderLayout.CENTER);

        // Menu
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS)); menu.setOpaque(false);
        menu.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
        menuBtns = new JButton[LABELS.length];
        boolean[] acc = access();
        
        // Penambahan kategori "PENGATURAN" untuk menu Manajemen User
        String[] cats = {"","MASTER DATA","","","","","","TRANSAKSI","LAPORAN","","PENGATURAN"};
        
        for (int i = 0; i < LABELS.length; i++) {
            if (!acc[i]) continue;
            if (!cats[i].isEmpty()) {
                JLabel c = new JLabel(cats[i]);
                c.setFont(new Font(Font.DIALOG,Font.BOLD,9)); c.setForeground(Theme.textMuted());
                c.setBorder(BorderFactory.createEmptyBorder(10,10,4,0)); c.setAlignmentX(Component.LEFT_ALIGNMENT);
                menu.add(c);
            }
            menuBtns[i] = mkMenuBtn(ICONS[i], LABELS[i], i);
            menu.add(menuBtns[i]); menu.add(Box.createVerticalStrut(2));
        }

        // User card at bottom
        JPanel userCard = Theme.createGlassCard(12); userCard.setLayout(new BorderLayout(8,4));
        JLabel uIco = new JLabel(roleEmoji()); uIco.setFont(new Font(Font.DIALOG,Font.PLAIN,22));
        JPanel ut = new JPanel(new GridLayout(2,1)); ut.setOpaque(false);
        String shortName = userNama.length()>22 ? userNama.substring(0,19)+"..." : userNama;
        JLabel uN = new JLabel(shortName); uN.setFont(new Font(Font.DIALOG,Font.BOLD,11)); uN.setForeground(Theme.textWhite());
        JLabel uR = Theme.createBadge(userRole.toUpperCase(), roleColor()); uR.setHorizontalAlignment(SwingConstants.LEFT);
        ut.add(uN); ut.add(uR);
        JButton lbtn = new JButton("⏻"); lbtn.setFont(new Font(Font.DIALOG,Font.PLAIN,16));
        lbtn.setForeground(Theme.DANGER); lbtn.setBorderPainted(false); lbtn.setContentAreaFilled(false);
        lbtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); lbtn.setToolTipText("Keluar");
        lbtn.addActionListener(e -> logout());
        userCard.add(uIco,BorderLayout.WEST); userCard.add(ut,BorderLayout.CENTER); userCard.add(lbtn,BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout()); bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(8,12,16,12));
        bottom.add(userCard,BorderLayout.NORTH);

        JPanel topContent = new JPanel(new BorderLayout()); topContent.setOpaque(false);
        topContent.add(logo,BorderLayout.NORTH);
        JSeparator sep = new JSeparator(); sep.setForeground(Theme.GLASS_BORDER2);
        topContent.add(sep,BorderLayout.CENTER);

        sb.add(topContent,BorderLayout.NORTH);
        sb.add(new JScrollPane(menu){{
            setOpaque(false); getViewport().setOpaque(false); setBorder(null);
            setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
            Theme.styleScrollBar(getVerticalScrollBar(), Theme.ACCENT_BLUE);
        }},BorderLayout.CENTER);
        sb.add(bottom,BorderLayout.SOUTH);

        updateActive(0);
        return sb;
    }

    private String roleEmoji() { return "admin".equals(userRole)?"👑":"konselor".equals(userRole)?"👨‍💼":"📋"; }
    private Color roleColor()  { return "admin".equals(userRole)?Theme.ACCENT_ORANGE:"konselor".equals(userRole)?Theme.ACCENT_BLUE:Theme.ACCENT_GREEN; }

    private JButton mkMenuBtn(String icon, String label, int idx) {
        JButton btn = new JButton() {
            boolean hov = false;
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                boolean act=(activeIdx==idx);
                if(act){
                    g2.setColor(Theme.isDarkMode ? Theme.SIDEBAR_ACTIVE : new Color(Theme.ACCENT_BLUE.getRed(), Theme.ACCENT_BLUE.getGreen(), Theme.ACCENT_BLUE.getBlue(), 40));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    g2.setColor(Theme.ACCENT_BLUE);g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);g2.fillRoundRect(0,4,3,getHeight()-8,3,3);
                }
                else if(hov){
                    g2.setColor(Theme.isDarkMode ? Theme.SIDEBAR_HOVER : new Color(0,0,0,10));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                }
                g2.dispose(); super.paintComponent(g);
            }
            { addMouseListener(new MouseAdapter(){public void mouseEntered(MouseEvent e){hov=true;repaint();}public void mouseExited(MouseEvent e){hov=false;repaint();}}); }
        };
        btn.setText("  "+icon+"  "+label); btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(Theme.textSecondary()); btn.setFont(new Font(Font.DIALOG,Font.PLAIN,13));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(0,6,0,6));
        btn.addActionListener(e->nav(idx));
        return btn;
    }

    public void nav(int idx) {
        updateActive(idx); pageTitle.setText(LABELS[idx]); load(idx); cardLayout.show(contentArea,CARDS[idx]);
    }

    private void load(int idx) {
        switch(idx) {
            case 1: if(formSiswa==null){formSiswa=new FormSiswa();contentArea.add(formSiswa,"siswa");} break;
            case 2: if(formJurusan==null){formJurusan=new FormJurusan();contentArea.add(formJurusan,"jurusan");} break;
            case 3: if(formUniversitas==null){formUniversitas=new FormUniversitas();contentArea.add(formUniversitas,"universitas");} break;
            case 4: if(formKonselor==null){formKonselor=new FormKonselor();contentArea.add(formKonselor,"konselor");} break;
            case 5: if(formPertanyaan==null){formPertanyaan=new FormPertanyaan();contentArea.add(formPertanyaan,"pertanyaan");} break;
            case 6: if(formAturan==null){formAturan=new FormAturan();contentArea.add(formAturan,"aturan");} break;
            case 7: if(formKonsultasi==null){formKonsultasi=new FormKonsultasi(this,userId,userNama,userRole);contentArea.add(formKonsultasi,"konsultasi");} break;
            case 8: if(formLaporan==null){formLaporan=new FormLaporan();contentArea.add(formLaporan,"laporan");} else {formLaporan.refresh();} break;
            case 9: if(panelRekap==null){panelRekap=new PanelRekapGuru(userRole,userId);contentArea.add(panelRekap,"rekap");}else{panelRekap.refresh();} break;
            // 4. MENAMPILKAN PANEL MANAJEMEN USER
            case 10: if(panelUserManagement==null){panelUserManagement=new UserManagementPanel();contentArea.add(panelUserManagement,"users");} break;
        }
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void updateActive(int idx) {
        activeIdx = idx;
        for(int i=0;i<menuBtns.length;i++){
            if(menuBtns[i]!=null){
                menuBtns[i].setForeground(i==idx?Theme.ACCENT_BLUE:Theme.TEXT_SECONDARY);
                menuBtns[i].setFont(new Font(Font.DIALOG,i==idx?Font.BOLD:Font.PLAIN,13));
                menuBtns[i].repaint();
            }
        }
    }

    private JPanel topBar() {
        JPanel bar = new JPanel(new BorderLayout(16,0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2=(Graphics2D)g.create();
                g2.setColor(Theme.isDarkMode ? new Color(0,0,0,40) : new Color(255,255,255,180)); 
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(Theme.isDarkMode ? Theme.GLASS_BORDER2 : new Color(0,0,0,20)); 
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1); g2.dispose();
            }
        };
        bar.setOpaque(false); bar.setBorder(BorderFactory.createEmptyBorder(0,24,0,24)); bar.setPreferredSize(new Dimension(0,60));
        pageTitle = new JLabel("Dashboard"); pageTitle.setFont(new Font(Font.DIALOG,Font.BOLD,20)); 
        pageTitle.setForeground(Theme.isDarkMode ? Theme.TEXT_WHITE : Theme.TEXT_DARK);
        
        JPanel rp = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0)); rp.setOpaque(false);
        
        // Theme Toggle Slider
        JButton btnTheme = Theme.createThemeSlider(e -> {
            Theme.isDarkMode = !Theme.isDarkMode;
            try {
                if (Theme.isDarkMode) FlatDarkLaf.setup();
                else FlatLightLaf.setup();
                Theme.updateThemeColors();
            } catch (Exception ex) { ex.printStackTrace(); }
            
            int oldActive = activeIdx;
            this.dispose();
            MainFrame mf = new MainFrame(userId, userNama, userRole);
            mf.nav(oldActive);
            mf.setVisible(true);
        });
        
        clockLabel = new JLabel(); clockLabel.setFont(new Font(Font.DIALOG,Font.PLAIN,13)); 
        clockLabel.setForeground(Theme.isDarkMode ? Theme.TEXT_SECONDARY : Theme.TEXT_DARK_SEC);
        
        Color rc = roleColor();
        JLabel ub = new JLabel("  "+roleEmoji()+" "+userNama+"  "); ub.setFont(new Font(Font.DIALOG,Font.BOLD,12)); ub.setForeground(rc);
        ub.setOpaque(true); ub.setBackground(new Color(rc.getRed(),rc.getGreen(),rc.getBlue(),25));
        ub.setBorder(BorderFactory.createCompoundBorder(new Theme.RoundedLineBorder(new Color(rc.getRed(),rc.getGreen(),rc.getBlue(),80),1,20),BorderFactory.createEmptyBorder(6,4,6,4)));
        
        rp.add(btnTheme);
        rp.add(clockLabel); rp.add(ub);
        bar.add(pageTitle,BorderLayout.WEST); bar.add(rp,BorderLayout.EAST);
        return bar;
    }

    private void refreshTheme() {
        Theme.setupUIManager();
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
        if (pageTitle != null) pageTitle.setForeground(Theme.isDarkMode ? Theme.TEXT_WHITE : Theme.TEXT_DARK);
    }

    private void startClock() {
        Timer t = new Timer(1000, e -> clockLabel.setText(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy  •  HH:mm:ss",new java.util.Locale("id","ID")))));
        t.start(); t.getActionListeners()[0].actionPerformed(null);
    }

    private void logout() {
        if(JOptionPane.showConfirmDialog(this,"Yakin ingin keluar?","Logout",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private JPanel dashboard() {
        JPanel p = new JPanel(new BorderLayout(16, 16)); 
        p.setOpaque(false); 
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // 1. GREETING PANEL (TOP)
        JPanel greetingPanel = new JPanel(new BorderLayout());
        greetingPanel.setOpaque(false);
        int hour = java.time.LocalTime.now().getHour();
        String sapaan = (hour >= 5 && hour < 11) ? "Selamat Pagi ☀️" : (hour >= 11 && hour < 15) ? "Selamat Siang 🌤️" : (hour >= 15 && hour < 18) ? "Selamat Sore 🌅" : "Selamat Malam 🌙";
        JLabel lblGreet = new JLabel(sapaan + ", " + userNama + "!");
        lblGreet.setFont(new Font(Font.DIALOG, Font.BOLD, 22)); // Lebih kecil agar muat
        lblGreet.setForeground(Theme.isDarkMode ? Theme.TEXT_WHITE : Theme.TEXT_DARK);
        JLabel lblSubGreet = new JLabel("Ringkasan analitik sistem pakar hari ini.");
        lblSubGreet.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        lblSubGreet.setForeground(Theme.TEXT_SECONDARY);
        greetingPanel.add(lblGreet, BorderLayout.NORTH);
        greetingPanel.add(lblSubGreet, BorderLayout.SOUTH);
        greetingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 2. STATS PANEL (ROWS)
        int[] s = stats();
        JPanel row = new JPanel(new GridLayout(1,4,12,0)); row.setOpaque(false);
        row.add(Theme.createStatCard("👨‍🎓",String.valueOf(s[0]),"Total Siswa",Theme.ACCENT_BLUE));
        row.add(Theme.createStatCard("💬",String.valueOf(s[1]),"Konsultasi",Theme.ACCENT_PURPLE));
        row.add(Theme.createStatCard("🏛️",String.valueOf(s[2]),"Universitas",Theme.ACCENT_TEAL));
        row.add(Theme.createStatCard("📚",String.valueOf(s[3]),"Jurusan",Theme.ACCENT_ORANGE));

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(greetingPanel, BorderLayout.NORTH);
        topContainer.add(row, BorderLayout.CENTER);

        // 3. MAIN CONTENT (CHART LEFT, OTHERS RIGHT)
        JPanel mainContent = new JPanel(new GridLayout(1,2,16,0)); 
        mainContent.setOpaque(false);
        
        // 3a. Left: Chart Panel
        JPanel chartCard = Theme.createGlassCard(12); 
        chartCard.setLayout(new BorderLayout(0,8));
        JLabel chartTitle = new JLabel("📊 Tren Fakultas Diminati");
        chartTitle.setFont(Theme.FONT_SUBTITLE); 
        chartTitle.setForeground(Theme.isDarkMode ? Theme.TEXT_WHITE : Theme.TEXT_DARK);
        chartCard.add(chartTitle, BorderLayout.NORTH);
        
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT j.fakultas, COUNT(k.id) as total FROM konsultasi k JOIN jurusan_kuliah j ON k.rekomendasi_utama_id=j.id GROUP BY j.fakultas ORDER BY total DESC LIMIT 5");
            while(rs.next()){
                String f = rs.getString("fakultas") != null ? rs.getString("fakultas") : "Umum";
                ds.addValue(rs.getInt("total"), f, f);
            }
            if(ds.getColumnCount()==0) {
                ds.addValue(12, "Teknik", "Teknik");
                ds.addValue(8, "Kesehatan", "Kesehatan");
                ds.addValue(5, "MIPA", "MIPA");
                ds.addValue(3, "Sastra", "Sastra");
            }
        } catch(Exception e) {
            ds.addValue(12, "Teknik", "Teknik");
            ds.addValue(8, "Kesehatan", "Kesehatan");
        }

        JFreeChart barChart = ChartFactory.createBarChart(null, null, null, ds, PlotOrientation.VERTICAL, false, false, false);
        barChart.setBackgroundPaint(new Color(0,0,0,0));
        CategoryPlot cp = barChart.getCategoryPlot();
        cp.setBackgroundPaint(new Color(0,0,0,0));
        cp.setOutlineVisible(false);
        cp.getDomainAxis().setTickLabelPaint(Theme.isDarkMode ? Color.WHITE : Color.DARK_GRAY);
        cp.getRangeAxis().setTickLabelPaint(Theme.isDarkMode ? Color.WHITE : Color.DARK_GRAY);
        cp.getDomainAxis().setTickLabelFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        cp.setDomainGridlinesVisible(false);
        cp.setRangeGridlinePaint(Theme.isDarkMode ? new Color(255,255,255,30) : new Color(0,0,0,30));
        BarRenderer r = (BarRenderer) cp.getRenderer();
        r.setSeriesPaint(0, Theme.ACCENT_BLUE);
        r.setSeriesPaint(1, Theme.ACCENT_PURPLE);
        r.setSeriesPaint(2, Theme.ACCENT_TEAL);
        r.setSeriesPaint(3, Theme.ACCENT_ORANGE);
        r.setSeriesPaint(4, Theme.ACCENT_PINK);
        r.setMaximumBarWidth(0.15);
        r.setItemMargin(0);
        
        ChartPanel cPanel = new ChartPanel(barChart);
        cPanel.setOpaque(false); cPanel.setBackground(new Color(0,0,0,0));
        chartCard.add(cPanel, BorderLayout.CENTER);

        // 3b. Right: Table (Top) + Info/Actions (Bottom)
        JPanel rightPanel = new JPanel(new GridLayout(2,1,0,16)); 
        rightPanel.setOpaque(false);
        
        // Right Top: Recent Table
        JPanel rc = Theme.createGlassCard(12); rc.setLayout(new BorderLayout(0,6));
        JLabel rt = new JLabel("📋 Konsultasi Terbaru"); rt.setFont(new Font(Font.DIALOG, Font.BOLD, 14)); rt.setForeground(Theme.isDarkMode ? Theme.TEXT_WHITE : Theme.TEXT_DARK);
        JTable tbl = new JTable(recentData(), new String[]{"Siswa","Rekomendasi","Tgl","Status"});
        Theme.styleTable(tbl);
        rc.add(rt,BorderLayout.NORTH); rc.add(Theme.createScrollPane(tbl),BorderLayout.CENTER);
        
        // Right Bottom: InfoBox + Actions
        JPanel rb = new JPanel(new GridLayout(1,2,12,0)); rb.setOpaque(false);
        
        // Info Box
        JPanel infoBox = Theme.createGlassCard(12); infoBox.setLayout(new BorderLayout(0,4));
        JLabel iT = new JLabel("ℹ️ Info Sistem"); iT.setFont(new Font(Font.DIALOG, Font.BOLD, 14)); iT.setForeground(Theme.isDarkMode ? Theme.TEXT_WHITE : Theme.TEXT_DARK);
        JTextArea iA = new JTextArea("Sistem pakar Forward Chaining dengan rekomendasi prioritas PTN berdasar probabilitas.");
        iA.setEditable(false); iA.setWrapStyleWord(true); iA.setLineWrap(true); iA.setOpaque(false);
        iA.setFont(new Font(Font.DIALOG,Font.PLAIN,11)); iA.setForeground(Theme.TEXT_SECONDARY); iA.setBorder(null);
        infoBox.add(iT,BorderLayout.NORTH); infoBox.add(iA,BorderLayout.CENTER);

        // Quick Actions
        JPanel ac = Theme.createAccentCard(Theme.ACCENT_PURPLE); ac.setLayout(new BorderLayout(0,6));
        JLabel at = new JLabel("⚡ Aksi Cepat"); at.setFont(new Font(Font.DIALOG, Font.BOLD, 14)); at.setForeground(Theme.isDarkMode ? Theme.TEXT_WHITE : Theme.TEXT_DARK);
        JPanel bg = new JPanel(new GridLayout(2,2,6,6)); bg.setOpaque(false);
        boolean[] acc = access();
        if(acc[7]){JButton b=Theme.createSecondaryButton("💬 Mulai");b.addActionListener(e->nav(7));bg.add(b);}
        if(acc[1]){JButton b=Theme.createSecondaryButton("👨‍🎓 Siswa");b.addActionListener(e->nav(1));bg.add(b);}
        if(acc[9]){JButton b=Theme.createSecondaryButton("📋 Rekap");b.addActionListener(e->nav(9));bg.add(b);}
        if(acc[8]){JButton b2=Theme.createSecondaryButton("📊 Lapor");b2.addActionListener(e->nav(8));bg.add(b2);}
        ac.add(at,BorderLayout.NORTH); ac.add(bg,BorderLayout.CENTER);

        rb.add(infoBox);
        rb.add(ac);

        rightPanel.add(rc);
        rightPanel.add(rb);

        mainContent.add(chartCard);
        mainContent.add(rightPanel);

        // COMBINE ALL
        p.add(topContainer, BorderLayout.NORTH);
        p.add(mainContent, BorderLayout.CENTER);
        return p;
    }

    private int[] stats() {
        int[] c={0,0,0,0};
        String[] q={"SELECT COUNT(*) FROM siswa","SELECT COUNT(*) FROM konsultasi","SELECT COUNT(*) FROM universitas","SELECT COUNT(*) FROM jurusan_kuliah"};
        for(int i=0;i<4;i++){try{ResultSet rs=DBConnection.executeQuery(q[i]);if(rs.next())c[i]=rs.getInt(1);rs.getStatement().close();}catch(Exception ignored){}}
        return c;
    }

    private Object[][] recentData() {
        try{
            ResultSet rs=DBConnection.executeQuery("SELECT k.no_konsultasi,s.nama,j.nama,DATE_FORMAT(k.tanggal_konsultasi,'%d/%m'),k.status FROM konsultasi k JOIN siswa s ON k.siswa_id=s.id LEFT JOIN jurusan_kuliah j ON k.rekomendasi_utama_id=j.id ORDER BY k.tanggal_konsultasi DESC LIMIT 8");
            java.util.List<Object[]> rows=new java.util.ArrayList<>();
            while(rs.next())rows.add(new Object[]{
                rs.getString(2), 
                rs.getString(3)!=null?rs.getString(3):"-", 
                rs.getString(4), 
                rs.getString(5)
            });
            return rows.toArray(new Object[0][0]);
        }catch(Exception e){return new Object[0][0];}
    }

    public int getUserId(){return userId;}
    public String getUserNama(){return userNama;}
    public String getUserRole(){return userRole;}
}