package com.sistempakar;

import com.formdev.flatlaf.FlatDarkLaf;
import com.sistempakar.ui.LoginFrame;
import com.sistempakar.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Entry Point (Kelas Utama)
 * Menjalankan SplashScreen dan menginisialisasi konfigurasi tema.
 */
public class MainApp {

    public static void main(String[] args) {
        // Mengatur tema bawaan aplikasi ke Mode Gelap (Dark Mode) menggunakan FlatLaf
        try {
            FlatDarkLaf.setup();
            Theme.isDarkMode = true;
            Theme.setupUIManager(); // Menginisialisasi komponen khusus dari class Theme
        } catch(Exception e) { 
            System.err.println("Gagal memuat FlatLaf: " + e.getMessage()); 
        }

        // Menjalankan SplashScreen pada thread antarmuka pengguna
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
            
            // Jeda waktu 2.8 detik untuk menampilkan SplashScreen
            new Timer(2800, e -> {
                splash.dispose(); // Menutup SplashScreen
                new LoginFrame().setVisible(true); // Menjalankan jendela Login utama
            }) {{ 
                setRepeats(false); 
                start(); 
            }};
        });
    }

    /**
     * Komponen JWindow untuk membuat layar SplashScreen.
     */
    static class SplashScreen extends JWindow {
        private float alpha = 0f;
        private int progress = 0;
        private JProgressBar pb;
        private JLabel msgLbl;
        private final String[] msgs = {
            "Memuat basis pengetahuan...",
            "Menghubungkan database...",
            "Menyiapkan aturan forward chaining...",
            "Memuat data PTN Indonesia...",
            "Sistem siap!"
        };
        private int mi = 0;

        SplashScreen() {
            setSize(680, 420); 
            setLocationRelativeTo(null);
            
            JPanel p = new JPanel() {
                @Override 
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g.create(); 
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Efek Fade-In saat layar pertama kali muncul
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    
                    // Latar Belakang Gradient (Gaya Deep Space)
                    GradientPaint bg = new GradientPaint(0, 0, new Color(8, 12, 28), getWidth(), getHeight(), new Color(15, 25, 60)); 
                    g2.setPaint(bg); 
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    
                    // Dekorasi Bercahaya (Orb)
                    drawOrb(g2, 120, 100, 200, new Color(79, 195, 247, 30)); 
                    drawOrb(g2, 520, 280, 180, new Color(179, 136, 255, 25)); 
                    drawOrb(g2, 340, 380, 150, new Color(77, 208, 186, 20));
                    
                    // Kartu Transparan (Glassmorphism Effect)
                    g2.setColor(new Color(255, 255, 255, 12)); 
                    g2.fillRoundRect(60, 40, 560, 310, 20, 20);
                    g2.setColor(new Color(255, 255, 255, 40)); 
                    g2.setStroke(new BasicStroke(1f)); 
                    g2.drawRoundRect(60, 40, 560, 310, 20, 20);
                    
                    // Konten Teks
                    g2.setFont(new Font(Font.DIALOG, Font.PLAIN, 52)); 
                    g2.setColor(new Color(79, 195, 247)); 
                    g2.drawString("🎓", 300, 130);
                    
                    g2.setFont(new Font(Font.DIALOG, Font.BOLD, 20)); 
                    g2.setColor(Color.WHITE); 
                    drawCenter(g2, "SISTEM PAKAR REKOMENDASI JURUSAN", getWidth() / 2, 180);
                    
                    g2.setFont(new Font(Font.DIALOG, Font.PLAIN, 13)); 
                    g2.setColor(new Color(160, 175, 210)); 
                    drawCenter(g2, "Metode Forward Chaining  •  v1.0", getWidth() / 2, 205);
                    
                    g2.setColor(new Color(79, 195, 247, 60)); 
                    g2.setStroke(new BasicStroke(1f)); 
                    g2.drawLine(120, 222, 560, 222);
                    
                    g2.setFont(new Font(Font.DIALOG, Font.ITALIC, 12)); 
                    g2.setColor(new Color(100, 120, 165)); 
                    drawCenter(g2, "\"Temukan Jurusan Impianmu Berdasarkan Minat & Bakat\"", getWidth() / 2, 245);
                    
                    g2.setFont(new Font(Font.DIALOG, Font.PLAIN, 11)); 
                    g2.setColor(new Color(80, 100, 145));
                    String[] feats = {
                        "✦  Psikotest Minat & Bakat",
                        "✦  Forward Chaining Engine",
                        "✦  Data Peluang Masuk PTN",
                        "✦  Rekap Konsultasi Siswa"
                    };
                    for(int i = 0; i < feats.length; i++) {
                        drawCenter(g2, feats[i], getWidth() / 2, 275 + i * 22);
                    }
                    g2.dispose();
                }
                
                void drawOrb(Graphics2D g2, float cx, float cy, float r, Color c){
                    RadialGradientPaint p = new RadialGradientPaint(cx, cy, r, new float[]{0f, 1f}, new Color[]{c, new Color(c.getRed(), c.getGreen(), c.getBlue(), 0)});
                    g2.setPaint(p);
                    g2.fillOval((int)(cx - r), (int)(cy - r), (int)(r * 2), (int)(r * 2));
                }
                
                void drawCenter(Graphics2D g2, String s, int cx, int y){
                    int w = g2.getFontMetrics().stringWidth(s);
                    g2.drawString(s, cx - w / 2, y);
                }
            };
            
            p.setLayout(new BorderLayout());
            
            // Komponen Bawah: Status Loading & Progress Bar
            JPanel bottom = new JPanel(new BorderLayout(0, 6)); 
            bottom.setOpaque(false); 
            bottom.setBorder(BorderFactory.createEmptyBorder(0, 80, 22, 80));
            
            msgLbl = new JLabel(msgs[0], SwingConstants.CENTER); 
            msgLbl.setFont(new Font(Font.DIALOG, Font.PLAIN, 11)); 
            msgLbl.setForeground(new Color(100, 150, 200));
            
            pb = new JProgressBar(0, 100); 
            pb.setStringPainted(false); 
            pb.setPreferredSize(new Dimension(0, 4)); 
            pb.setBorderPainted(false); 
            pb.setOpaque(false);
            
            bottom.add(msgLbl, BorderLayout.CENTER); 
            bottom.add(pb, BorderLayout.SOUTH);
            p.add(bottom, BorderLayout.SOUTH);
            
            setContentPane(p);
            
            // Timer untuk Animasi Loading
            Timer t = new Timer(30, null);
            t.addActionListener(e -> {
                alpha = Math.min(1f, alpha + 0.04f);
                progress = Math.min(100, progress + 2);
                pb.setValue(progress);
                
                if(progress % 20 == 0 && mi < msgs.length - 1){
                    mi++;
                    msgLbl.setText(msgs[mi]);
                }
                
                p.repaint();
                
                if(alpha >= 1f && progress >= 100) {
                    t.stop();
                }
            });
            t.start();
        }
    }
}