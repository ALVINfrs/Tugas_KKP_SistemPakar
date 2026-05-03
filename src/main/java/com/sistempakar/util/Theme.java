package com.sistempakar.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Theme & Glassmorphism UI Utility
 * Sistem Pakar Rekomendasi Jurusan
 */
public class Theme {

    // ═══════════════════════════════════════
    // THEME STATE
    // ═══════════════════════════════════════
    public static boolean isDarkMode = true;

    // ═══════════════════════════════════════
    // COLOR PALETTE – Deep Space Glassmorphism
    // ═══════════════════════════════════════
    // Dark Palette
    public static final Color BG_DEEPSPACE    = new Color(8, 12, 28);
    public static final Color BG_DARK         = new Color(13, 20, 45);
    public static final Color BG_MEDIUM       = new Color(20, 30, 65);
    
    // Light Palette
    public static final Color BG_LIGHT        = new Color(240, 244, 250);
    public static final Color BG_LIGHT_SOFT   = new Color(225, 230, 245);
    public static final Color TEXT_DARK       = new Color(30, 35, 60);
    public static final Color TEXT_DARK_SEC   = new Color(80, 90, 120);
    public static final Color TEXT_DARK_MUTED = new Color(130, 140, 170);

    public static Color mainBg()       { return isDarkMode ? BG_DEEPSPACE : BG_LIGHT; }
    public static Color cardBg()       { return isDarkMode ? new Color(255, 255, 255, 18) : new Color(255, 255, 255, 160); }
    public static Color cardHover()    { return isDarkMode ? new Color(255, 255, 255, 28) : new Color(255, 255, 255, 220); }
    public static Color glassBorder()  { return isDarkMode ? new Color(255, 255, 255, 55) : new Color(100, 120, 180, 80); }
    public static Color textPrimary()  { return isDarkMode ? new Color(236, 240, 255) : TEXT_DARK; }
    public static Color textSecondary(){ return isDarkMode ? new Color(160, 175, 210) : TEXT_DARK_SEC; }
    public static Color textWhite()    { return isDarkMode ? Color.WHITE : TEXT_DARK; }
    public static Color textMuted()    { return isDarkMode ? new Color(100, 120, 165) : TEXT_DARK_MUTED; }
    public static Color sidebarBg()    { return isDarkMode ? new Color(10, 15, 38, 230) : new Color(255, 255, 255, 245); }
    public static Color tableHeader()  { return isDarkMode ? new Color(30, 45, 90) : new Color(210, 220, 240); }
    public static Color tableRowEven() { return isDarkMode ? new Color(15, 25, 55) : new Color(255, 255, 255); }
    public static Color tableRowOdd()  { return isDarkMode ? new Color(20, 32, 65) : new Color(245, 248, 255); }
    public static Color tabBg()        { return isDarkMode ? new Color(20, 30, 65) : Color.WHITE; }

    public static Color GLASS_BORDER    = new Color(255, 255, 255, 55);
    public static Color GLASS_BORDER2   = new Color(255, 255, 255, 25);

    public static final Color ACCENT_BLUE     = new Color(79, 195, 247);
    public static final Color ACCENT_PURPLE   = new Color(179, 136, 255);
    public static final Color ACCENT_TEAL     = new Color(77, 208, 186);
    public static final Color ACCENT_PINK     = new Color(255, 128, 171);
    public static final Color ACCENT_ORANGE   = new Color(255, 183, 77);
    public static final Color ACCENT_GREEN    = new Color(129, 199, 132);

    public static Color TEXT_WHITE      = new Color(255, 255, 255);
    public static Color TEXT_PRIMARY    = new Color(236, 240, 255);
    public static Color TEXT_SECONDARY  = new Color(160, 175, 210);
    public static Color TEXT_MUTED      = new Color(100, 120, 165);

    public static Color SIDEBAR_BG      = new Color(10, 15, 38, 230);
    public static Color SIDEBAR_HOVER   = new Color(79, 195, 247, 40);
    public static Color SIDEBAR_ACTIVE  = new Color(79, 195, 247, 70);
    public static Color SIDEBAR_LINE    = new Color(255, 255, 255, 15);

    public static Color TABLE_HEADER    = new Color(30, 45, 90);
    public static Color TABLE_ROW_ODD   = new Color(20, 32, 65);
    public static Color TABLE_ROW_EVEN  = new Color(15, 25, 55);
    public static Color TABLE_SELECT    = new Color(79, 195, 247, 60);

    public static final Color SUCCESS         = new Color(105, 210, 155);
    public static final Color WARNING         = new Color(255, 193, 77);
    public static final Color DANGER          = new Color(255, 100, 130);
    public static final Color INFO            = new Color(100, 190, 255);

    public static void updateThemeColors() {
        if (isDarkMode) {
            GLASS_BORDER    = new Color(255, 255, 255, 55);
            GLASS_BORDER2   = new Color(255, 255, 255, 25);
            TEXT_WHITE      = new Color(255, 255, 255);
            TEXT_PRIMARY    = new Color(236, 240, 255);
            TEXT_SECONDARY  = new Color(160, 175, 210);
            TEXT_MUTED      = new Color(100, 120, 165);
            SIDEBAR_BG      = new Color(10, 15, 38, 230);
            SIDEBAR_HOVER   = new Color(79, 195, 247, 40);
            SIDEBAR_ACTIVE  = new Color(79, 195, 247, 70);
            SIDEBAR_LINE    = new Color(255, 255, 255, 15);
            TABLE_HEADER    = new Color(30, 45, 90);
            TABLE_ROW_ODD   = new Color(20, 32, 65);
            TABLE_ROW_EVEN  = new Color(15, 25, 55);
            TABLE_SELECT    = new Color(79, 195, 247, 60);
        } else {
            GLASS_BORDER    = new Color(100, 120, 180, 80);
            GLASS_BORDER2   = new Color(0, 0, 0, 20);
            TEXT_WHITE      = TEXT_DARK;
            TEXT_PRIMARY    = TEXT_DARK;
            TEXT_SECONDARY  = TEXT_DARK_SEC;
            TEXT_MUTED      = TEXT_DARK_MUTED;
            SIDEBAR_BG      = new Color(255, 255, 255, 245);
            SIDEBAR_HOVER   = new Color(0, 0, 0, 10);
            SIDEBAR_ACTIVE  = new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), 40);
            SIDEBAR_LINE    = new Color(0, 0, 0, 15);
            TABLE_HEADER    = new Color(210, 220, 240);
            TABLE_ROW_ODD   = new Color(245, 248, 255);
            TABLE_ROW_EVEN  = new Color(255, 255, 255);
            TABLE_SELECT    = new Color(79, 195, 247, 60);
        }
        setupUIManager();
    }

    public static JButton createThemeSlider(ActionListener onToggle) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // Draw track
                g2.setColor(isDarkMode ? new Color(40, 50, 80) : new Color(200, 210, 230));
                g2.fillRoundRect(0, 0, w, h, h, h);
                // Draw thumb
                int ts = h - 6;
                int x = isDarkMode ? w - ts - 3 : 3;
                g2.setColor(isDarkMode ? new Color(20, 30, 60) : Color.WHITE);
                g2.fillOval(x, 3, ts, ts);
                // Draw icon
                g2.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
                g2.setColor(isDarkMode ? Color.WHITE : Color.ORANGE);
                String ico = isDarkMode ? "🌙" : "☀️";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ico, x + (ts - fm.stringWidth(ico)) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(56, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.addActionListener(onToggle);
        return btn;
    }

    // ═══════════════════════════════════════
    // FONTS
    // ═══════════════════════════════════════
    public static final Font FONT_TITLE    = new Font(Font.DIALOG, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(Font.DIALOG, Font.BOLD, 16);
    public static final Font FONT_HEADER   = new Font(Font.DIALOG, Font.BOLD, 13);
    public static final Font FONT_BODY     = new Font(Font.DIALOG, Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font(Font.DIALOG, Font.PLAIN, 11);
    public static final Font FONT_MONO     = new Font("JetBrains Mono", Font.PLAIN, 12);
    public static final Font FONT_LABEL    = new Font(Font.DIALOG, Font.PLAIN, 12);
    public static final Font FONT_BOLD     = new Font(Font.DIALOG, Font.BOLD, 13);

    // ═══════════════════════════════════════
    // GRADIENT BACKGROUNDS
    // ═══════════════════════════════════════
    public static void paintDeepSpaceBackground(Graphics2D g2, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (isDarkMode) {
            GradientPaint gradient = new GradientPaint(0, 0, BG_DEEPSPACE, width, height, new Color(15, 25, 60));
            g2.setPaint(gradient);
            g2.fillRect(0, 0, width, height);
            paintGlow(g2, width * 0.15f, height * 0.2f, 350, new Color(79, 195, 247, 20));
            paintGlow(g2, width * 0.75f, height * 0.6f, 300, new Color(179, 136, 255, 18));
            paintGlow(g2, width * 0.5f, height * 0.9f, 250, new Color(77, 208, 186, 15));
        } else {
            GradientPaint gradient = new GradientPaint(0, 0, BG_LIGHT, width, height, new Color(220, 230, 250));
            g2.setPaint(gradient);
            g2.fillRect(0, 0, width, height);
            paintGlow(g2, width * 0.8f, height * 0.2f, 400, new Color(79, 195, 247, 30));
            paintGlow(g2, width * 0.2f, height * 0.8f, 350, new Color(179, 136, 255, 25));
        }
    }

    private static void paintGlow(Graphics2D g2, float cx, float cy, float radius, Color color) {
        RadialGradientPaint glow = new RadialGradientPaint(
            cx, cy, radius,
            new float[]{0f, 1f},
            new Color[]{color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)}
        );
        g2.setPaint(glow);
        g2.fillOval((int)(cx - radius), (int)(cy - radius), (int)(radius * 2), (int)(radius * 2));
    }

    // ═══════════════════════════════════════
    // STYLED COMPONENTS
    // ═══════════════════════════════════════
    
    public static void setupUIManager() {
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 8);
        UIManager.put("Table.rowHeight", 36);
        UIManager.put("TableHeader.background", isDarkMode ? new Color(30, 45, 90) : new Color(210, 220, 240));
        UIManager.put("TableHeader.foreground", isDarkMode ? Color.WHITE : TEXT_DARK);
        UIManager.put("Table.selectionBackground", TABLE_SELECT);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("Separator.foreground", isDarkMode ? new Color(255, 255, 255, 25) : new Color(0, 0, 0, 20));
        
        UIManager.put("TabbedPane.tabHeight", 38);
        UIManager.put("TabbedPane.selectedBackground", new Color(79,195,247,30));
        UIManager.put("TabbedPane.underlineColor", ACCENT_BLUE);
    }

    public static JLabel createTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(textWhite());
        return lbl;
    }

    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(textSecondary());
        return lbl;
    }

    public static JLabel createBoldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(textPrimary());
        return lbl;
    }

    public static JTextField createTextField() {
        JTextField tf = new JTextField() {
            @Override public void updateUI() {
                super.updateUI();
                styleTextField(this);
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        styleTextField(tf);
        return tf;
    }

    public static JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols) {
            @Override public void updateUI() {
                super.updateUI();
                styleTextField(this);
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        styleTextField(tf);
        return tf;
    }

    public static void styleTextField(JTextField tf) {
        tf.setBackground(isDarkMode ? new Color(255, 255, 255, 20) : new Color(0, 0, 0, 10));
        tf.setForeground(textPrimary());
        tf.setCaretColor(ACCENT_BLUE);
        tf.setFont(FONT_BODY);
        tf.setBorder(createFieldBorder());
        tf.putClientProperty("JTextField.placeholderForeground", textMuted());
    }

    public static JTextArea createTextArea() {
        JTextArea ta = new JTextArea() {
            @Override public void updateUI() {
                super.updateUI();
                setBackground(isDarkMode ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 8));
                setForeground(textPrimary());
                setCaretColor(ACCENT_BLUE);
                setBorder(BorderFactory.createCompoundBorder(
                    createFieldBorder(), BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        ta.setOpaque(false);
        ta.setBackground(isDarkMode ? new Color(255, 255, 255, 15) : new Color(0, 0, 0, 8));
        ta.setForeground(textPrimary());
        ta.setCaretColor(ACCENT_BLUE);
        ta.setFont(FONT_BODY);
        ta.setBorder(BorderFactory.createCompoundBorder(
            createFieldBorder(), BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        return ta;
    }

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items) {
            @Override public void updateUI() {
                super.updateUI();
                setBackground(isDarkMode ? new Color(20, 32, 70) : Color.WHITE);
                setForeground(textPrimary());
                setBorder(createFieldBorder());
            }
        };
        cb.setBackground(isDarkMode ? new Color(20, 32, 70) : Color.WHITE);
        cb.setForeground(textPrimary());
        cb.setFont(FONT_BODY);
        cb.setBorder(createFieldBorder());
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? TABLE_SELECT : (isDarkMode ? new Color(15, 25, 55) : Color.WHITE));
                setForeground(textPrimary());
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        return cb;
    }

    public static JSpinner createSpinner(SpinnerModel model) {
        JSpinner sp = new JSpinner(model) {
            @Override public void updateUI() {
                super.updateUI();
                setBackground(isDarkMode ? new Color(20, 32, 70) : Color.WHITE);
                setForeground(textPrimary());
                setBorder(createFieldBorder());
                JComponent editor = getEditor();
                if (editor instanceof JSpinner.DefaultEditor) {
                    JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
                    tf.setBackground(isDarkMode ? new Color(20, 32, 70) : Color.WHITE);
                    tf.setForeground(textPrimary());
                }
            }
        };
        sp.setBackground(isDarkMode ? new Color(20, 32, 70) : Color.WHITE);
        sp.setForeground(textPrimary());
        sp.setFont(FONT_BODY);
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(isDarkMode ? new Color(20, 32, 70) : Color.WHITE);
            tf.setForeground(textPrimary());
            tf.setFont(FONT_BODY);
        }
        sp.setBorder(createFieldBorder());
        return sp;
    }

    public static Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
            new RoundedLineBorder(glassBorder(), 1, 10),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, ACCENT_BLUE, BG_DARK, true);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS, BG_DARK, true);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER, BG_DARK, true);
    }

    public static JButton createWarningButton(String text) {
        return createButton(text, WARNING, BG_DARK, true);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, glassBorder(), new Color(255,255,255,5), false);
    }

    private static JButton createButton(String text, Color accent, Color bg, boolean filled) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                if (hovered) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }

                Color base = filled ? accent : (isDarkMode ? new Color(255,255,255, hovered?20:10) : new Color(0,0,0, hovered?20:10));
                if (hovered && filled) {
                    base = accent.brighter();
                }
                
                g2.setColor(base);
                if (filled) {
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(isDarkMode || !filled ? TEXT_WHITE : Color.WHITE);
                } else {
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                    g2.setColor(accent);
                }
                
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                });
            }
        };
        btn.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }

    public static JScrollPane createScrollPane(Component content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        styleScrollBar(sp.getVerticalScrollBar(), ACCENT_BLUE);
        styleScrollBar(sp.getHorizontalScrollBar(), ACCENT_BLUE);
        return sp;
    }

    public static void styleScrollBar(JScrollBar bar, Color color) {
        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
                this.trackColor = new Color(0,0,0,0);
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createEmptyButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createEmptyButton(); }
            private JButton createEmptyButton() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
                g2.dispose();
            }
        });
        bar.setPreferredSize(new Dimension(8, 8));
        bar.setOpaque(false);
    }

    public static JTable styleTable(JTable table) {
        table.setOpaque(false);
        table.setBackground(new Color(0,0,0,0));
        table.setForeground(textPrimary());
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setSelectionBackground(TABLE_SELECT);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));

        JTableHeader header = table.getTableHeader();
        header.setOpaque(false);
        header.setBackground(tableHeader());
        header.setForeground(isDarkMode ? TEXT_WHITE : TEXT_DARK);
        header.setFont(FONT_HEADER);
        header.setPreferredSize(new Dimension(0, 40));
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isSel, hasF, r, c);
                comp.setBackground(isSel ? TABLE_SELECT : (r % 2 == 0 ? tableRowEven() : tableRowOdd()));
                comp.setForeground(textPrimary());
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                }
                return comp;
            }
        });
        return table;
    }

    public static JProgressBar createProgressBar(Color color) {
        JProgressBar pb = new JProgressBar();
        pb.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = c.getWidth();
                int h = c.getHeight();
                double amount = pb.getPercentComplete();
                
                g2.setColor(isDarkMode ? new Color(255,255,255,20) : new Color(0,0,0,15));
                g2.fillRoundRect(0, 0, w, h, h, h);
                
                if (amount > 0) {
                    g2.setColor(color);
                    g2.fillRoundRect(0, 0, (int)(w * amount), h, h, h);
                }
                g2.dispose();
            }
        });
        pb.setOpaque(false);
        pb.setPreferredSize(new Dimension(0, 6));
        return pb;
    }

    public static JPanel createGlassCard() {
        return createGlassCard(24);
    }

    public static JPanel createGlassCard(int radius) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setColor(glassBorder());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return panel;
    }

    /** Colored accent line at top of card */
    public static JPanel createAccentCard(Color accentColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.setColor(glassBorder());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return panel;
    }
    
    public static JPanel createStatCard(String icon, String value, String label, Color color) {
        JPanel p = createGlassCard(16);
        p.setLayout(new BorderLayout(12, 0));
        
        JLabel i = new JLabel(icon);
        i.setFont(new Font(Font.DIALOG, Font.PLAIN, 32));
        i.setForeground(color);
        
        JPanel c = new JPanel(new GridLayout(2, 1, 0, 2));
        c.setOpaque(false);
        
        JLabel v = new JLabel(value);
        v.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        v.setForeground(textWhite());
        
        JLabel l = new JLabel(label);
        l.setFont(FONT_SMALL);
        l.setForeground(textSecondary());
        
        c.add(v); c.add(l);
        p.add(i, BorderLayout.WEST);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    /** Badge label */
    public static JLabel createBadge(String text, Color color) {
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), isDarkMode ? 40 : 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
        badge.setForeground(color);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        badge.setOpaque(false);
        return badge;
    }

    /** Separator line */
    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(isDarkMode ? new Color(255, 255, 255, 25) : new Color(0, 0, 0, 15));
        sep.setBackground(new Color(0,0,0,0));
        return sep;
    }

    public static JLabel createIcon(String emoji, int size) {
        JLabel lbl = new JLabel(emoji);
        lbl.setFont(new Font(Font.DIALOG, Font.PLAIN, size));
        return lbl;
    }

    /** Custom Border for Rounded Edges */
    public static class RoundedLineBorder extends AbstractBorder {
        private Color color;
        private int thickness;
        private int radius;
        public RoundedLineBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }
}
