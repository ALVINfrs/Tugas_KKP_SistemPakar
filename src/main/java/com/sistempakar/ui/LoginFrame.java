package com.sistempakar.ui;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.security.MessageDigest;
import java.sql.*;

/**
 * Login & Register Screen – Glassmorphism Design
 * Roles: admin, konselor, guru
 */
public class LoginFrame extends JFrame {

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private static final String SALT = "SP_JURUSAN_2024";

    // Login fields
    private JTextField loginUser;
    private JPasswordField loginPass;
    private JLabel loginStatus;

    // Register fields
    private JTextField regNama, regUsername, regEmail;
    private JPasswordField regPass, regPassConfirm;
    private JComboBox<String> regRole;
    private JLabel regStatus;

    public LoginFrame() {
        super("Sistem Pakar Rekomendasi Jurusan – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1040, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        ensureUserTable();
        initComponents();
    }

    private void ensureUserTable() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) return;
            Statement st = conn.createStatement();
            st.execute(
                "CREATE TABLE IF NOT EXISTS pengguna (" +
                "  id         INT PRIMARY KEY AUTO_INCREMENT," +
                "  username   VARCHAR(50) UNIQUE NOT NULL," +
                "  password   VARCHAR(255) NOT NULL," +
                "  nama       VARCHAR(120) NOT NULL," +
                "  email      VARCHAR(120)," +
                "  role       ENUM('admin','konselor','guru') DEFAULT 'guru'," +
                "  aktif      BOOLEAN DEFAULT TRUE," +
                "  last_login DATETIME," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            // Seed default admin
            ResultSet rs = DBConnection.executeQuery("SELECT COUNT(*) FROM pengguna WHERE username='admin'");
            if (rs.next() && rs.getInt(1) == 0) {
                DBConnection.executeUpdate(
                    "INSERT INTO pengguna(username,password,nama,email,role) VALUES(?,?,?,?,?)",
                    "admin", hash("admin123"), "Administrator", "admin@sekolah.sch.id", "admin"
                );
                DBConnection.executeUpdate(
                    "INSERT INTO pengguna(username,password,nama,email,role) VALUES(?,?,?,?,?)",
                    "bk1", hash("bk1234"), "Budi Santoso, M.Pd", "budi@sekolah.sch.id", "konselor"
                );
                DBConnection.executeUpdate(
                    "INSERT INTO pengguna(username,password,nama,email,role) VALUES(?,?,?,?,?)",
                    "guru1", hash("guru123"), "Sari Dewi, S.Psi", "sari@sekolah.sch.id", "guru"
                );
            }
            rs.getStatement().close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Theme.paintDeepSpaceBackground((Graphics2D)g, getWidth(), getHeight());
            }
        };
        root.setOpaque(true);

        // Left illustration panel
        JPanel leftPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean isDark = Theme.isDarkMode;
                
                Color colorStart = isDark ? new Color(10,20,55,200) : new Color(245, 248, 255, 220); 
                Color colorEnd = isDark ? new Color(5,12,40,180) : new Color(225, 230, 245, 200);   
                
                GradientPaint gp=new GradientPaint(0,0,colorStart,getWidth(),getHeight(),colorEnd);
                g2.setPaint(gp); 
                g2.fillRect(0,0,getWidth(),getHeight());
                
                Color circle1 = isDark ? new Color(79,195,247,25) : new Color(79,195,247,40);
                Color circle2 = isDark ? new Color(179,136,255,20) : new Color(179,136,255,35);
                Color circle3 = isDark ? new Color(77,208,186,18) : new Color(77,208,186,30);

                paintCircle(g2,80,120,160, circle1);
                paintCircle(g2,350,450,120, circle2);
                paintCircle(g2,200,550,80, circle3);
                
                g2.setFont(new Font(Font.DIALOG,Font.PLAIN,72)); 
                g2.setColor(isDark ? new Color(79,195,247,200) : new Color(79,195,247,255));
                drawCentered(g2, "🎓", getWidth()/2, 260);
                
                g2.setFont(new Font(Font.DIALOG,Font.BOLD,22)); 
                g2.setColor(UIManager.getColor("Label.foreground")); 
                drawCentered(g2,"SISTEM PAKAR",getWidth()/2,310);
                drawCentered(g2,"REKOMENDASI JURUSAN",getWidth()/2,340);
                
                g2.setFont(new Font(Font.DIALOG,Font.PLAIN,13)); 
                Color fg = UIManager.getColor("Label.foreground");
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 180));
                drawCentered(g2,"Metode Forward Chaining",getWidth()/2,370);
                
                g2.setFont(new Font(Font.DIALOG,Font.PLAIN,12));
                String[] features={"✦  Psikotest Minat & Bakat","✦  Rekomendasi 5 Jurusan Terbaik","✦  Peluang Masuk PTN Indonesia","✦  Catatan & Tips Belajar Konselor"};
                for(int i=0;i<features.length;i++) drawCentered(g2,features[i],getWidth()/2,420+i*28);
                g2.dispose();
            }
            
            void paintCircle(Graphics2D g2,float cx,float cy,float r,Color c){
                RadialGradientPaint p=new RadialGradientPaint(cx,cy,r,new float[]{0f,1f},new Color[]{c,new Color(c.getRed(),c.getGreen(),c.getBlue(),0)});
                g2.setPaint(p); g2.fillOval((int)(cx-r),(int)(cy-r),(int)(r*2),(int)(r*2));
            }
            void drawCentered(Graphics2D g2,String s,int cx,int y){
                int w=g2.getFontMetrics().stringWidth(s); g2.drawString(s,cx-w/2,y);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(380,0));

        JSeparator divider = new JSeparator(SwingConstants.VERTICAL);
        divider.setForeground(Theme.GLASS_BORDER);
        divider.setPreferredSize(new Dimension(1,0));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.add(buildLoginPanel(), "login");
        cardPanel.add(buildRegisterPanel(), "register");
        cardLayout.show(cardPanel, "login");

        root.add(leftPanel, BorderLayout.WEST);
        root.add(divider, BorderLayout.CENTER);
        root.add(cardPanel, BorderLayout.EAST);
        cardPanel.setPreferredSize(new Dimension(620, 0));

        setContentPane(root);
    }

    // ─────────────────────────────────────
    // LOGIN PANEL
    // ─────────────────────────────────────
    private JPanel buildLoginPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bg = UIManager.getColor("Panel.background");
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 150)); 
                g2.fillRoundRect(0,0,getWidth(),getHeight(),24,24);
                
                g2.setColor(Theme.GLASS_BORDER); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,24,24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40,50,40,50));
        card.setPreferredSize(new Dimension(480, 600));

        // Title
        JLabel icon = new JLabel("🔐", SwingConstants.CENTER); 
        icon.setFont(new Font(Font.DIALOG,Font.PLAIN,40)); 
        icon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); 
        icon.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel title = new JLabel("Selamat Datang", SwingConstants.CENTER); 
        title.setFont(new Font(Font.DIALOG,Font.BOLD,26)); 
        title.setForeground(UIManager.getColor("Label.foreground")); 
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 
        title.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel sub = new JLabel("Masuk ke Sistem Pakar Rekomendasi Jurusan", SwingConstants.CENTER); 
        sub.setFont(Theme.FONT_SMALL); 
        sub.setForeground(Theme.TEXT_MUTED); 
        sub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20)); 
        sub.setAlignmentX(CENTER_ALIGNMENT);

        // Fields - FIX RATA KIRI
        JLabel userLbl = Theme.createLabel("Username"); 
        userLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        userLbl.setHorizontalAlignment(SwingConstants.LEFT);
        userLbl.setAlignmentX(CENTER_ALIGNMENT);
        
        loginUser = Theme.createTextField(); 
        loginUser.putClientProperty("JTextField.placeholderText","Masukkan username..."); 
        loginUser.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        loginUser.setAlignmentX(CENTER_ALIGNMENT);

        JLabel passLbl = Theme.createLabel("Password"); 
        passLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        passLbl.setHorizontalAlignment(SwingConstants.LEFT);
        passLbl.setAlignmentX(CENTER_ALIGNMENT);
        
        loginPass = new JPasswordField(); Theme.styleTextField(loginPass); 
        loginPass.putClientProperty("JTextField.placeholderText","Masukkan password..."); 
        loginPass.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        loginPass.setAlignmentX(CENTER_ALIGNMENT);

        loginStatus = new JLabel(" ", SwingConstants.CENTER); 
        loginStatus.setFont(Theme.FONT_SMALL); 
        loginStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        loginStatus.setAlignmentX(CENTER_ALIGNMENT);

        JButton btnLogin = Theme.createPrimaryButton("  🚀  Masuk ke Sistem  ");
        btnLogin.setAlignmentX(CENTER_ALIGNMENT); btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        btnLogin.addActionListener(e -> doLogin());
        loginPass.addActionListener(e -> doLogin());

        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.CENTER,4,0)); regRow.setOpaque(false);
        JLabel noAcc = Theme.createLabel("Belum punya akun?");
        JButton toReg = new JButton("Daftar di sini"); toReg.setBorderPainted(false); toReg.setContentAreaFilled(false); toReg.setForeground(Theme.ACCENT_BLUE); toReg.setFont(new Font(Font.DIALOG,Font.BOLD,12)); toReg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); toReg.addActionListener(e->cardLayout.show(cardPanel,"register"));
        regRow.add(noAcc); regRow.add(toReg);

        // Demo accounts info
        JPanel demoCard = Theme.createGlassCard(10); demoCard.setLayout(new GridLayout(4,1,0,2));
        demoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE,90));
        JLabel demoTitle=new JLabel("💡 Akun Demo Bawaan"); demoTitle.setFont(new Font(Font.DIALOG,Font.BOLD,11)); demoTitle.setForeground(Theme.ACCENT_ORANGE);
        JLabel d1=new JLabel("  👑 Admin: admin / admin123"); d1.setFont(Theme.FONT_SMALL); d1.setForeground(Theme.TEXT_SECONDARY);
        JLabel d2=new JLabel("  👨‍💼 Konselor: bk1 / bk1234"); d2.setFont(Theme.FONT_SMALL); d2.setForeground(Theme.TEXT_SECONDARY);
        JLabel d3=new JLabel("  📋 Guru: guru1 / guru123"); d3.setFont(Theme.FONT_SMALL); d3.setForeground(Theme.TEXT_SECONDARY);
        demoCard.add(demoTitle); demoCard.add(d1); demoCard.add(d2); demoCard.add(d3);
        demoCard.setAlignmentX(CENTER_ALIGNMENT); // Samain alignment

        card.add(icon); card.add(Box.createVerticalStrut(8));
        card.add(title); card.add(Box.createVerticalStrut(4));
        card.add(sub); card.add(Box.createVerticalStrut(28));
        card.add(userLbl); card.add(Box.createVerticalStrut(6));
        card.add(loginUser); card.add(Box.createVerticalStrut(14));
        card.add(passLbl); card.add(Box.createVerticalStrut(6));
        card.add(loginPass); card.add(Box.createVerticalStrut(8));
        card.add(loginStatus); card.add(Box.createVerticalStrut(16));
        card.add(btnLogin); card.add(Box.createVerticalStrut(20));
        card.add(regRow); card.add(Box.createVerticalStrut(16));
        card.add(demoCard);

        GridBagConstraints gbc=new GridBagConstraints(); gbc.anchor=GridBagConstraints.CENTER;
        wrapper.add(card,gbc);
        return wrapper;
    }

    // ─────────────────────────────────────
    // REGISTER PANEL 
    // ─────────────────────────────────────
    private JPanel buildRegisterPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bg = UIManager.getColor("Panel.background");
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 150)); 
                g2.fillRoundRect(0,0,getWidth(),getHeight(),24,24);
                
                g2.setColor(Theme.GLASS_BORDER); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,24,24); g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20,50,32,50)); 

        // Tombol Kembali
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JButton btnBack = new JButton("<html><b>&larr; Kembali</b></html>");
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setForeground(Theme.TEXT_MUTED);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            cardLayout.show(cardPanel, "login");
            clearReg();
        });
        topRow.add(btnBack);

        JLabel icon = new JLabel("📝", SwingConstants.CENTER); 
        icon.setFont(new Font(Font.DIALOG, Font.PLAIN, 36)); 
        icon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); 
        icon.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel title = new JLabel("Buat Akun Baru", SwingConstants.CENTER); 
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 24)); 
        title.setForeground(UIManager.getColor("Label.foreground")); 
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 
        title.setAlignmentX(CENTER_ALIGNMENT);

        // Fields - FIX RATA KIRI
        JLabel namaLbl=Theme.createLabel("Nama Lengkap *"); 
        namaLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        namaLbl.setHorizontalAlignment(SwingConstants.LEFT);
        namaLbl.setAlignmentX(CENTER_ALIGNMENT);
        
        regNama=Theme.createTextField(); 
        regNama.putClientProperty("JTextField.placeholderText","Nama lengkap Anda"); 
        regNama.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        regNama.setAlignmentX(CENTER_ALIGNMENT);

        JLabel userLbl=Theme.createLabel("Username *"); 
        userLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        userLbl.setHorizontalAlignment(SwingConstants.LEFT);
        userLbl.setAlignmentX(CENTER_ALIGNMENT);
        
        regUsername=Theme.createTextField(); 
        regUsername.putClientProperty("JTextField.placeholderText","Buat username unik"); 
        regUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        regUsername.setAlignmentX(CENTER_ALIGNMENT);

        JLabel emailLbl=Theme.createLabel("Email"); 
        emailLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        emailLbl.setHorizontalAlignment(SwingConstants.LEFT);
        emailLbl.setAlignmentX(CENTER_ALIGNMENT);
        
        regEmail=Theme.createTextField(); 
        regEmail.putClientProperty("JTextField.placeholderText","email@sekolah.sch.id"); 
        regEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        regEmail.setAlignmentX(CENTER_ALIGNMENT);

        JLabel roleLbl=Theme.createLabel("Role / Jabatan *"); 
        roleLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        roleLbl.setHorizontalAlignment(SwingConstants.LEFT);
        roleLbl.setAlignmentX(CENTER_ALIGNMENT);
        
        regRole=Theme.createComboBox(new String[]{"guru – Guru Wali Kelas","konselor – Guru BK / Konselor","admin – Administrator"}); 
        regRole.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        regRole.setAlignmentX(CENTER_ALIGNMENT);

        JLabel passLbl=Theme.createLabel("Password * (min 6 karakter)"); 
        passLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        passLbl.setHorizontalAlignment(SwingConstants.LEFT);
        passLbl.setAlignmentX(CENTER_ALIGNMENT);
        
        regPass=new JPasswordField(); Theme.styleTextField(regPass); 
        regPass.putClientProperty("JTextField.placeholderText","Buat password"); 
        regPass.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        regPass.setAlignmentX(CENTER_ALIGNMENT);

        JLabel passCLbl=Theme.createLabel("Konfirmasi Password *"); 
        passCLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        passCLbl.setHorizontalAlignment(SwingConstants.LEFT);
        passCLbl.setAlignmentX(CENTER_ALIGNMENT);
        
        regPassConfirm=new JPasswordField(); Theme.styleTextField(regPassConfirm); 
        regPassConfirm.putClientProperty("JTextField.placeholderText","Ulangi password"); 
        regPassConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        regPassConfirm.setAlignmentX(CENTER_ALIGNMENT);

        regStatus=new JLabel(" ",SwingConstants.CENTER); 
        regStatus.setFont(Theme.FONT_SMALL); 
        regStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        regStatus.setAlignmentX(CENTER_ALIGNMENT);

        JButton btnReg=Theme.createSuccessButton("  ✅  Daftar Sekarang  ");
        btnReg.setAlignmentX(CENTER_ALIGNMENT); btnReg.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        btnReg.addActionListener(e->doRegister());

        JPanel loginRow=new JPanel(new FlowLayout(FlowLayout.CENTER,4,0)); loginRow.setOpaque(false);
        JLabel hasAcc=Theme.createLabel("Sudah punya akun?");
        JButton toLog=new JButton("Masuk di sini"); toLog.setBorderPainted(false); toLog.setContentAreaFilled(false); toLog.setForeground(Theme.ACCENT_BLUE); toLog.setFont(new Font(Font.DIALOG,Font.BOLD,12)); toLog.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); toLog.addActionListener(e->cardLayout.show(cardPanel,"login"));
        loginRow.add(hasAcc); loginRow.add(toLog);

        card.add(topRow); card.add(Box.createVerticalStrut(5));
        card.add(icon); card.add(Box.createVerticalStrut(6));
        card.add(title); card.add(Box.createVerticalStrut(20));
        card.add(namaLbl); card.add(Box.createVerticalStrut(5)); card.add(regNama); card.add(Box.createVerticalStrut(10));
        card.add(userLbl); card.add(Box.createVerticalStrut(5)); card.add(regUsername); card.add(Box.createVerticalStrut(10));
        card.add(emailLbl); card.add(Box.createVerticalStrut(5)); card.add(regEmail); card.add(Box.createVerticalStrut(10));
        card.add(roleLbl); card.add(Box.createVerticalStrut(5)); card.add(regRole); card.add(Box.createVerticalStrut(10));
        card.add(passLbl); card.add(Box.createVerticalStrut(5)); card.add(regPass); card.add(Box.createVerticalStrut(10));
        card.add(passCLbl); card.add(Box.createVerticalStrut(5)); card.add(regPassConfirm); card.add(Box.createVerticalStrut(8));
        card.add(regStatus); card.add(Box.createVerticalStrut(12));
        card.add(btnReg); card.add(Box.createVerticalStrut(14));
        card.add(loginRow);

        // ScrollPane untuk Register Form
        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false); 
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(480, 580)); 

        GridBagConstraints gbc=new GridBagConstraints(); gbc.anchor=GridBagConstraints.CENTER;
        wrapper.add(scrollPane,gbc); 
        return wrapper;
    }

    // ─────────────────────────────────────
    // AUTH LOGIC
    // ─────────────────────────────────────
    private void doLogin() {
        String u = loginUser.getText().trim();
        String p = new String(loginPass.getPassword());
        if (u.isEmpty()||p.isEmpty()) { setStatus(loginStatus,"⚠️ Username dan password wajib diisi!",Theme.WARNING); return; }
        try {
            ResultSet rs = DBConnection.executeQuery(
                "SELECT id,nama,role,aktif FROM pengguna WHERE username=? AND password=?", u, hash(p));
            if (rs.next()) {
                if (!rs.getBoolean("aktif")) { setStatus(loginStatus,"❌ Akun Anda dinonaktifkan!",Theme.DANGER); rs.getStatement().close(); return; }
                int id = rs.getInt("id"); String nama = rs.getString("nama"); String role = rs.getString("role");
                rs.getStatement().close();
                DBConnection.executeUpdate("UPDATE pengguna SET last_login=NOW() WHERE id=?", id);
                setStatus(loginStatus,"✅ Login berhasil! Membuka sistem...", Theme.SUCCESS);
                Timer t = new Timer(900, evt -> {
                    dispose();
                    new MainFrame(id, nama, role).setVisible(true);
                });
                t.setRepeats(false); t.start();
            } else {
                rs.getStatement().close();
                setStatus(loginStatus,"❌ Username atau password salah!",Theme.DANGER);
                loginPass.setText("");
            }
        } catch (Exception e) {
            setStatus(loginStatus,"⚠️ Gagal: " + e.getMessage(), Theme.WARNING);
        }
    }

    private void doRegister() {
        String nama=regNama.getText().trim(), user=regUsername.getText().trim(), email=regEmail.getText().trim();
        String p=new String(regPass.getPassword()), pc=new String(regPassConfirm.getPassword());
        String roleVal=((String)regRole.getSelectedItem()).split(" ")[0];

        if(nama.isEmpty()||user.isEmpty()||p.isEmpty()){setStatus(regStatus,"⚠️ Nama, Username, dan Password wajib diisi!",Theme.WARNING);return;}
        if(p.length()<6){setStatus(regStatus,"⚠️ Password minimal 6 karakter!",Theme.WARNING);return;}
        if(!p.equals(pc)){setStatus(regStatus,"❌ Password tidak cocok!",Theme.DANGER);return;}
        try {
            ResultSet rs=DBConnection.executeQuery("SELECT COUNT(*) FROM pengguna WHERE username=?",user);
            boolean exists=rs.next()&&rs.getInt(1)>0; rs.getStatement().close();
            if(exists){setStatus(regStatus,"❌ Username sudah digunakan!",Theme.DANGER);return;}
            DBConnection.executeUpdate("INSERT INTO pengguna(username,password,nama,email,role)VALUES(?,?,?,?,?)",user,hash(p),nama,email,roleVal);
            setStatus(regStatus,"✅ Akun berhasil dibuat! Silakan login.",Theme.SUCCESS);
            Timer t=new Timer(1500,e->{ cardLayout.show(cardPanel,"login"); loginUser.setText(user); clearReg(); });
            t.setRepeats(false); t.start();
        }catch(Exception e){setStatus(regStatus,"❌ Gagal: "+e.getMessage(),Theme.DANGER);}
    }

    private void setStatus(JLabel lbl, String msg, Color c){lbl.setText(msg);lbl.setForeground(c);}
    private void clearReg(){regNama.setText("");regUsername.setText("");regEmail.setText("");regPass.setText("");regPassConfirm.setText("");regRole.setSelectedIndex(0); regStatus.setText(" ");}

    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest((input + SALT).getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(byte by:b) sb.append(String.format("%02x",by));
            return sb.toString();
        } catch(Exception e){ return input; }
    }
}