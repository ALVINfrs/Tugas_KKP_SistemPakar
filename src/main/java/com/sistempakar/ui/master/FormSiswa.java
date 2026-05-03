package com.sistempakar.ui.master;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

/**
 * Form Master Data Siswa
 * CRUD lengkap dengan data akademik dan non-akademik
 */
public class FormSiswa extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    // Input fields
    private JTextField tfNis, tfNama, tfTempatLahir, tfKelas, tfTelepon, tfEmail;
    private JTextField tfNamaOrtu, tfTeleponOrtu, tfCitaCita;
    private JTextArea taAlamat, taHobi, taPrestasi, taOrganisasi;
    private JComboBox<String> cbJK, cbJurusanSma;
    private JSpinner spTanggalLahir;
    private JSpinner spMtk, spIpa, spIps, spBahInd, spBahIng, spSeni, spOlahraga, spKomputer;

    private int selectedId = -1;
    private JLabel lblStatus;

    public FormSiswa() {
        setOpaque(false);
        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        initComponents();
        loadData();
    }

    private void initComponents() {
        // ── LEFT: FORM INPUT ──────────────────────────────────
        JPanel formPanel = Theme.createGlassCard();
        formPanel.setLayout(new BorderLayout(0, 12));
        formPanel.setPreferredSize(new Dimension(480, 0));

        JLabel formTitle = new JLabel("📝  Form Data Siswa");
        formTitle.setFont(Theme.FONT_SUBTITLE);
        formTitle.setForeground(Theme.textWhite());

        // Tabs for form sections
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_BODY);
        tabs.setForeground(Theme.textPrimary());
        tabs.setBackground(Theme.tabBg());
        
        // Wrap each tab in a scroll pane to ensure all fields are accessible
        tabs.addTab("Data Pribadi", Theme.createScrollPane(createPersonalTab()));
        tabs.addTab("Akademik & Nilai", Theme.createScrollPane(createAkademikTab()));
        tabs.addTab("Hobi & Prestasi", Theme.createScrollPane(createHobiTab()));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(Theme.FONT_SMALL);
        lblStatus.setForeground(Theme.SUCCESS);

        JButton btnNew   = Theme.createSecondaryButton("🆕 Baru");
        JButton btnSave  = Theme.createPrimaryButton("💾 Simpan");
        JButton btnEdit  = Theme.createWarningButton("✏️ Edit");
        JButton btnDel   = Theme.createDangerButton("🗑️ Hapus");

        btnNew.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> saveData());
        btnEdit.addActionListener(e -> editSelected());
        btnDel.addActionListener(e -> deleteSelected());

        btnPanel.add(lblStatus);
        btnPanel.add(btnNew);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDel);
        btnPanel.add(btnSave);

        formPanel.add(formTitle, BorderLayout.NORTH);
        formPanel.add(tabs, BorderLayout.CENTER);
        formPanel.add(btnPanel, BorderLayout.SOUTH);

        // ── RIGHT: TABLE ──────────────────────────────────────
        JPanel tablePanel = Theme.createGlassCard();
        tablePanel.setLayout(new BorderLayout(0, 12));

        JLabel tblTitle = new JLabel("👨‍🎓  Daftar Siswa");
        tblTitle.setFont(Theme.FONT_SUBTITLE);
        tblTitle.setForeground(Theme.textWhite());

        // Search bar
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        JTextField tfSearch = Theme.createTextField();
        tfSearch.putClientProperty("JTextField.placeholderText", "🔍 Cari NIS / Nama...");
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(tfSearch.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(tfSearch.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });
        JButton btnRefresh = Theme.createSecondaryButton("🔄");
        btnRefresh.addActionListener(e -> { tfSearch.setText(""); loadData(); });

        searchRow.add(tfSearch, BorderLayout.CENTER);
        searchRow.add(btnRefresh, BorderLayout.EAST);

        String[] cols = {"ID", "NIS", "Nama Siswa", "L/P", "Kelas", "Jur. SMA", "No. HP", "Cita-cita"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        Theme.styleTable(table);
        table.setColumnSelectionAllowed(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) loadFormFromTable();
        });
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane sp = Theme.createScrollPane(table);

        tablePanel.add(tblTitle, BorderLayout.NORTH);

        JPanel tblContent = new JPanel(new BorderLayout(0, 8));
        tblContent.setOpaque(false);
        tblContent.add(searchRow, BorderLayout.NORTH);
        tblContent.add(sp, BorderLayout.CENTER);

        tablePanel.add(tblTitle, BorderLayout.NORTH);
        tablePanel.add(tblContent, BorderLayout.CENTER);

        // Wrap formPanel in a scroll pane to prevent clipping
        JScrollPane formScroll = Theme.createScrollPane(formPanel);
        formScroll.setPreferredSize(new Dimension(500, 0));
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Theme.styleScrollBar(formScroll.getVerticalScrollBar(), Theme.ACCENT_BLUE);

        add(formScroll, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel createPersonalTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfNis        = Theme.createTextField();  tfNis.putClientProperty("JTextField.placeholderText", "Contoh: 2024001");
        tfNama       = Theme.createTextField();  tfNama.putClientProperty("JTextField.placeholderText", "Nama lengkap siswa");
        tfTempatLahir= Theme.createTextField();  tfTempatLahir.putClientProperty("JTextField.placeholderText", "Kota kelahiran");
        tfKelas      = Theme.createTextField();  tfKelas.putClientProperty("JTextField.placeholderText", "Contoh: XII IPA 1");
        tfTelepon    = Theme.createTextField();  tfTelepon.putClientProperty("JTextField.placeholderText", "08xxxxxxxxxx");
        tfEmail      = Theme.createTextField();  tfEmail.putClientProperty("JTextField.placeholderText", "email@example.com");
        tfNamaOrtu   = Theme.createTextField();  tfNamaOrtu.putClientProperty("JTextField.placeholderText", "Nama orang tua");
        tfTeleponOrtu= Theme.createTextField();  tfTeleponOrtu.putClientProperty("JTextField.placeholderText", "Telepon orang tua");
        tfCitaCita   = Theme.createTextField();  tfCitaCita.putClientProperty("JTextField.placeholderText", "Cita-cita / impian karir");
        taAlamat     = Theme.createTextArea();   taAlamat.setRows(3);

        cbJK = Theme.createComboBox(new String[]{"L - Laki-laki", "P - Perempuan"});
        cbJurusanSma = Theme.createComboBox(new String[]{"IPA","IPS","Bahasa","SMK-TI","SMK-Bisnis","SMK-Seni","Lainnya"});

        SpinnerDateModel dateModel = new SpinnerDateModel();
        spTanggalLahir = Theme.createSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spTanggalLahir, "dd/MM/yyyy");
        spTanggalLahir.setEditor(dateEditor);

        Object[][] fields = {
            {"NIS *", tfNis}, {"Nama Lengkap *", tfNama},
            {"Jenis Kelamin", cbJK}, {"Tempat Lahir", tfTempatLahir},
            {"Tanggal Lahir", spTanggalLahir}, {"Kelas", tfKelas},
            {"Jurusan SMA", cbJurusanSma}, {"No. Telepon", tfTelepon},
            {"Email", tfEmail}, {"Nama Orang Tua", tfNamaOrtu},
            {"Telepon Ortu", tfTeleponOrtu}, {"Cita-cita", tfCitaCita}
        };

        int row = 0;
        for (Object[] field : fields) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
            JLabel lbl = Theme.createLabel(field[0].toString());
            lbl.setPreferredSize(new Dimension(130, 25));
            p.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            p.add((Component) field[1], gbc);
            row++;
        }

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(Theme.createLabel("Alamat"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(new JScrollPane(taAlamat) {{ setBorder(BorderFactory.createEmptyBorder()); }}, gbc);
        row++;

        // UX Reminder
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.insets = new Insets(12, 4, 4, 4);
        JPanel reminder = Theme.createGlassCard(8); reminder.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JLabel remLbl = new JLabel("ℹ️  Jangan lupa mengisi nilai rapor pada tab 'Akademik & Nilai'");
        remLbl.setFont(Theme.FONT_SMALL); remLbl.setForeground(Theme.ACCENT_ORANGE);
        reminder.add(remLbl); p.add(reminder, gbc);

        return p;
    }

    private JPanel createAkademikTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Info card
        JPanel info = Theme.createGlassCard(8);
        info.setLayout(new BorderLayout(8, 0));
        JLabel infoLbl = new JLabel("ℹ️  Isikan nilai rata-rata rapor semester terakhir (0-100)");
        infoLbl.setFont(Theme.FONT_SMALL);
        infoLbl.setForeground(Theme.ACCENT_BLUE);
        info.add(infoLbl, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        p.add(info, gbc);

        String[] subjects = {"Matematika", "IPA (Fisika/Kimia/Bio)", "IPS (Geo/Sej/Eko)",
                             "Bahasa Indonesia", "Bahasa Inggris", "Seni Budaya",
                             "Penjasorkes", "Informatika/TIK"};
        JSpinner[] spinners = new JSpinner[8];
        for (int i = 0; i < 8; i++) {
            spinners[i] = Theme.createSpinner(new SpinnerNumberModel(75.0, 0, 100, 0.5));
        }
        spMtk = spinners[0]; spIpa = spinners[1]; spIps = spinners[2];
        spBahInd = spinners[3]; spBahIng = spinners[4]; spSeni = spinners[5];
        spOlahraga = spinners[6]; spKomputer = spinners[7];

        gbc.gridwidth = 1;
        for (int i = 0; i < subjects.length; i++) {
            gbc.gridx = 0; gbc.gridy = i+1; gbc.weightx = 0;
            JLabel lbl = Theme.createLabel(subjects[i]);
            lbl.setPreferredSize(new Dimension(160, 28));
            p.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.add(spinners[i], BorderLayout.WEST);
            // Visual progress bar
            JProgressBar pb = Theme.createProgressBar(Theme.ACCENT_BLUE);
            pb.setMaximum(100);
            final JSpinner sp2 = spinners[i];
            sp2.addChangeListener(e -> {
                double val = (double) sp2.getValue();
                pb.setValue((int) val);
                pb.repaint();
            });
            pb.setValue(75);
            row.add(pb, BorderLayout.CENTER);
            p.add(row, gbc);
        }

        return p;
    }

    private JPanel createHobiTab() {
        JPanel p = new JPanel(new GridLayout(3, 1, 0, 12));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));

        taHobi      = Theme.createTextArea(); taHobi.setRows(4);
        taPrestasi  = Theme.createTextArea(); taPrestasi.setRows(4);
        taOrganisasi= Theme.createTextArea(); taOrganisasi.setRows(4);

        taHobi.putClientProperty("JTextArea.placeholderText",
            "Contoh: Membaca, Coding, Menggambar, Bermain musik...");
        taPrestasi.putClientProperty("JTextArea.placeholderText",
            "Contoh: Juara 1 Olimpiade Matematika, Juara 3 Debat Provinsi...");
        taOrganisasi.putClientProperty("JTextArea.placeholderText",
            "Contoh: Ketua OSIS, Anggota Pramuka, Club Robotik...");

        String[] labels = {"🎮  Hobi & Minat (pisahkan dengan koma)",
                           "🏆  Prestasi & Penghargaan",
                           "👥  Organisasi & Kegiatan Ekstrakurikuler"};
        JTextArea[] areas = {taHobi, taPrestasi, taOrganisasi};

        for (int i = 0; i < 3; i++) {
            JPanel section = Theme.createGlassCard(10);
            section.setLayout(new BorderLayout(0, 8));
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(Theme.FONT_BOLD);
            lbl.setForeground(Theme.ACCENT_TEAL);
            section.add(lbl, BorderLayout.NORTH);
            section.add(Theme.createScrollPane(areas[i]), BorderLayout.CENTER);
            p.add(section);
        }
        return p;
    }

    void loadData() {
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT id, nis, nama, jenis_kelamin, kelas, jurusan_sma, telepon, cita_cita FROM siswa ORDER BY nama";
            ResultSet rs = DBConnection.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("nis"), rs.getString("nama"),
                    rs.getString("jenis_kelamin"), rs.getString("kelas"),
                    rs.getString("jurusan_sma"), rs.getString("telepon"),
                    rs.getString("cita_cita")
                });
            }
            rs.getStatement().close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable(String keyword) {
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT id, nis, nama, jenis_kelamin, kelas, jurusan_sma, telepon, cita_cita " +
                         "FROM siswa WHERE nis LIKE ? OR nama LIKE ? ORDER BY nama";
            String kw = "%" + keyword + "%";
            ResultSet rs = DBConnection.executeQuery(sql, kw, kw);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    rs.getString(7), rs.getString(8)
                });
            }
            rs.getStatement().close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        selectedId = id;
        try {
            String sql = "SELECT * FROM siswa WHERE id = ?";
            ResultSet rs = DBConnection.executeQuery(sql, id);
            if (rs.next()) {
                tfNis.setText(rs.getString("nis"));
                tfNama.setText(rs.getString("nama"));
                cbJK.setSelectedIndex("L".equals(rs.getString("jenis_kelamin")) ? 0 : 1);
                tfTempatLahir.setText(rs.getString("tempat_lahir"));
                tfKelas.setText(rs.getString("kelas"));
                tfTelepon.setText(rs.getString("telepon"));
                tfEmail.setText(rs.getString("email"));
                tfNamaOrtu.setText(rs.getString("nama_ortu"));
                tfTeleponOrtu.setText(rs.getString("telepon_ortu"));
                tfCitaCita.setText(rs.getString("cita_cita"));
                taAlamat.setText(rs.getString("alamat"));
                taHobi.setText(rs.getString("hobi"));
                taPrestasi.setText(rs.getString("prestasi"));
                taOrganisasi.setText(rs.getString("organisasi"));

                String jSma = rs.getString("jurusan_sma");
                for (int i = 0; i < cbJurusanSma.getItemCount(); i++) {
                    if (cbJurusanSma.getItemAt(i).equals(jSma)) { cbJurusanSma.setSelectedIndex(i); break; }
                }

                setSpinnerVal(spMtk, rs.getDouble("nilai_matematika"));
                setSpinnerVal(spIpa, rs.getDouble("nilai_ipa"));
                setSpinnerVal(spIps, rs.getDouble("nilai_ips"));
                setSpinnerVal(spBahInd, rs.getDouble("nilai_bahasa_ind"));
                setSpinnerVal(spBahIng, rs.getDouble("nilai_bahasa_ing"));
                setSpinnerVal(spSeni, rs.getDouble("nilai_seni"));
                setSpinnerVal(spOlahraga, rs.getDouble("nilai_olahraga"));
                setSpinnerVal(spKomputer, rs.getDouble("nilai_komputer"));

                java.util.Date tl = rs.getDate("tanggal_lahir");
                if (tl != null) spTanggalLahir.setValue(tl);
            }
            rs.getStatement().close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void setSpinnerVal(JSpinner sp, double val) {
        sp.setValue(val == 0 ? 75.0 : val);
    }

    private void saveData() {
        if (tfNis.getText().isEmpty() || tfNama.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIS dan Nama wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String jk = cbJK.getSelectedIndex() == 0 ? "L" : "P";
            String jSma = (String) cbJurusanSma.getSelectedItem();
            java.sql.Date tl = new java.sql.Date(((java.util.Date) spTanggalLahir.getValue()).getTime());

            if (selectedId == -1) {
                String sql = "INSERT INTO siswa (nis,nama,jenis_kelamin,tanggal_lahir,tempat_lahir,kelas," +
                    "jurusan_sma,alamat,telepon,email,nama_ortu,telepon_ortu,hobi,prestasi,organisasi," +
                    "nilai_matematika,nilai_ipa,nilai_ips,nilai_bahasa_ind,nilai_bahasa_ing,nilai_seni," +
                    "nilai_olahraga,nilai_komputer,cita_cita) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                DBConnection.executeUpdate(sql, tfNis.getText(), tfNama.getText(), jk, tl,
                    tfTempatLahir.getText(), tfKelas.getText(), jSma, taAlamat.getText(),
                    tfTelepon.getText(), tfEmail.getText(), tfNamaOrtu.getText(), tfTeleponOrtu.getText(),
                    taHobi.getText(), taPrestasi.getText(), taOrganisasi.getText(),
                    spMtk.getValue(), spIpa.getValue(), spIps.getValue(),
                    spBahInd.getValue(), spBahIng.getValue(), spSeni.getValue(),
                    spOlahraga.getValue(), spKomputer.getValue(), tfCitaCita.getText());
                lblStatus.setText("✅ Data siswa berhasil disimpan!");
            } else {
                String sql = "UPDATE siswa SET nis=?,nama=?,jenis_kelamin=?,tanggal_lahir=?,tempat_lahir=?,kelas=?," +
                    "jurusan_sma=?,alamat=?,telepon=?,email=?,nama_ortu=?,telepon_ortu=?,hobi=?,prestasi=?," +
                    "organisasi=?,nilai_matematika=?,nilai_ipa=?,nilai_ips=?,nilai_bahasa_ind=?,nilai_bahasa_ing=?," +
                    "nilai_seni=?,nilai_olahraga=?,nilai_komputer=?,cita_cita=? WHERE id=?";
                DBConnection.executeUpdate(sql, tfNis.getText(), tfNama.getText(), jk, tl,
                    tfTempatLahir.getText(), tfKelas.getText(), jSma, taAlamat.getText(),
                    tfTelepon.getText(), tfEmail.getText(), tfNamaOrtu.getText(), tfTeleponOrtu.getText(),
                    taHobi.getText(), taPrestasi.getText(), taOrganisasi.getText(),
                    spMtk.getValue(), spIpa.getValue(), spIps.getValue(),
                    spBahInd.getValue(), spBahIng.getValue(), spSeni.getValue(),
                    spOlahraga.getValue(), spKomputer.getValue(), tfCitaCita.getText(), selectedId);
                lblStatus.setText("✅ Data siswa berhasil diperbarui!");
            }
            loadData(); clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data siswa dari tabel!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelected() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data siswa yang akan dihapus!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus data siswa ini? Semua data konsultasi terkait juga akan berpengaruh.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DBConnection.executeUpdate("DELETE FROM siswa WHERE id = ?", selectedId);
                lblStatus.setText("✅ Data berhasil dihapus!");
                loadData(); clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedId = -1;
        tfNis.setText(""); tfNama.setText(""); tfTempatLahir.setText("");
        tfKelas.setText(""); tfTelepon.setText(""); tfEmail.setText("");
        tfNamaOrtu.setText(""); tfTeleponOrtu.setText(""); tfCitaCita.setText("");
        taAlamat.setText(""); taHobi.setText(""); taPrestasi.setText(""); taOrganisasi.setText("");
        cbJK.setSelectedIndex(0); cbJurusanSma.setSelectedIndex(0);
        spTanggalLahir.setValue(new java.util.Date());
        for (JSpinner sp : new JSpinner[]{spMtk,spIpa,spIps,spBahInd,spBahIng,spSeni,spOlahraga,spKomputer})
            if (sp != null) sp.setValue(75.0);
        table.clearSelection();
        lblStatus.setText(" ");
    }

    // Public accessor for ConsultasiForm
    public String getSelectedNis() { return tfNis.getText(); }
    public int getSelectedId() { return selectedId; }
}
