package com.sistempakar.ui;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;

/**
 * Panel Manajemen Pengguna untuk Administrator.
 * Menyediakan fungsionalitas CRUD, pencarian, dan filter data pengguna.
 */
public class UserManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbFilterRole;

    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadData();
    }

    private void initComponents() {
        // --- Bagian Atas: Judul dan Filter Pencarian ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = Theme.createTitle("Manajemen Pengguna");
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);

        txtSearch = Theme.createTextField(15);
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari nama/username...");
        txtSearch.addActionListener(e -> loadData()); // Menjalankan pencarian saat tombol Enter ditekan

        cbFilterRole = Theme.createComboBox(new String[]{"Semua Role", "admin", "konselor", "guru"});
        cbFilterRole.addActionListener(e -> loadData());

        JButton btnCari = Theme.createSecondaryButton("Cari");
        btnCari.addActionListener(e -> loadData());

        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(cbFilterRole);
        filterPanel.add(txtSearch);
        filterPanel.add(btnCari);
        topPanel.add(filterPanel, BorderLayout.EAST);

        // --- Bagian Tengah: Tabel Data Pengguna ---
        String[] columns = {"ID", "Nama Lengkap", "Username", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Mencegah sel tabel diedit secara langsung
            }
        };
        table = new JTable(tableModel);
        Theme.styleTable(table); 
        
        JScrollPane scrollPane = Theme.createScrollPane(table);

        // --- Bagian Bawah: Tombol Aksi ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton btnAdd = Theme.createSuccessButton("Tambah Pengguna");
        JButton btnEdit = Theme.createPrimaryButton("Edit Data");
        JButton btnToggleStatus = Theme.createWarningButton("Ubah Status Aktif");
        JButton btnResetPass = Theme.createDangerButton("Reset Password");

        btnAdd.addActionListener(e -> showUserForm(false, 0));
        
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                showUserForm(true, (int) table.getValueAt(row, 0));
            } else {
                showWarning("Silakan pilih pengguna terlebih dahulu dari tabel.");
            }
        });
        
        btnToggleStatus.addActionListener(e -> toggleUserStatus());
        btnResetPass.addActionListener(e -> resetPassword());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnToggleStatus);
        bottomPanel.add(btnResetPass);

        // --- Menyusun seluruh komponen ke dalam panel utama ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ─────────────────────────────────────
    // LOGIKA DATABASE & CRUD
    // ─────────────────────────────────────

    /**
     * Memuat data pengguna dari database dan menampilkannya ke dalam tabel.
     */
    private void loadData() {
        tableModel.setRowCount(0); // Mengosongkan data tabel sebelumnya
        String search = txtSearch.getText().trim();
        String role = cbFilterRole.getSelectedIndex() == 0 ? "%" : cbFilterRole.getSelectedItem().toString();

        try {
            String query = "SELECT id, nama, username, role, aktif FROM pengguna WHERE role LIKE ? AND (nama LIKE ? OR username LIKE ?)";
            ResultSet rs = DBConnection.executeQuery(query, role, "%" + search + "%", "%" + search + "%");
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getBoolean("aktif") ? "Aktif" : "Nonaktif"
                });
            }
            rs.getStatement().close();
        } catch (Exception e) {
            showWarning("Terjadi kesalahan saat memuat data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Menampilkan form input untuk menambah atau mengubah data pengguna.
     * @param isEdit true jika mode edit, false jika mode tambah.
     * @param userId ID pengguna yang akan diedit (abaikan jika isEdit = false).
     */
    private void showUserForm(boolean isEdit, int userId) {
        JTextField txtNama = new JTextField(20);
        JTextField txtUser = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JPasswordField txtPass = new JPasswordField(20);
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"guru", "konselor", "admin"});

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Nama Lengkap:")); panel.add(txtNama);
        panel.add(new JLabel("Username:")); panel.add(txtUser);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Role:")); panel.add(cbRole);

        if (!isEdit) {
            panel.add(new JLabel("Password:")); panel.add(txtPass);
        } else {
            // Memuat data lama jika berada dalam mode edit
            try {
                ResultSet rs = DBConnection.executeQuery("SELECT nama, username, email, role FROM pengguna WHERE id=?", userId);
                if (rs.next()) {
                    txtNama.setText(rs.getString("nama"));
                    txtUser.setText(rs.getString("username"));
                    txtEmail.setText(rs.getString("email"));
                    cbRole.setSelectedItem(rs.getString("role"));
                }
                rs.getStatement().close();
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        }

        String dialogTitle = isEdit ? "Ubah Data Pengguna" : "Tambah Pengguna Baru";
        int result = JOptionPane.showConfirmDialog(this, panel, dialogTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String nama = txtNama.getText().trim();
            String user = txtUser.getText().trim();
            String email = txtEmail.getText().trim();
            String role = cbRole.getSelectedItem().toString();

            if (nama.isEmpty() || user.isEmpty()) {
                showWarning("Kolom Nama dan Username wajib diisi.");
                return;
            }

            try {
                if (isEdit) {
                    // Proses pembaruan data
                    DBConnection.executeUpdate("UPDATE pengguna SET nama=?, username=?, email=?, role=? WHERE id=?", 
                            nama, user, email, role, userId);
                    JOptionPane.showMessageDialog(this, "Data pengguna berhasil diperbarui.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Proses penambahan data baru
                    String pass = new String(txtPass.getPassword());
                    if (pass.isEmpty()) { 
                        showWarning("Kolom Password wajib diisi."); 
                        return; 
                    }
                    if (pass.length() < 6) {
                        showWarning("Password harus terdiri dari minimal 6 karakter.");
                        return;
                    }
                    
                    // Memeriksa apakah username sudah digunakan
                    ResultSet rsCek = DBConnection.executeQuery("SELECT COUNT(*) FROM pengguna WHERE username=?", user);
                    if (rsCek.next() && rsCek.getInt(1) > 0) {
                        showWarning("Username tersebut sudah digunakan. Silakan gunakan username lain.");
                        rsCek.getStatement().close();
                        return;
                    }
                    rsCek.getStatement().close();

                    DBConnection.executeUpdate("INSERT INTO pengguna (nama, username, email, password, role) VALUES (?, ?, ?, ?, ?)", 
                            nama, user, email, LoginFrame.hash(pass), role);
                    JOptionPane.showMessageDialog(this, "Pengguna baru berhasil ditambahkan.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
                loadData(); 
            } catch (Exception e) {
                showWarning("Terjadi kesalahan pada database: " + e.getMessage());
            }
        }
    }

    /**
     * Mengubah status pengguna dari Aktif menjadi Nonaktif, dan sebaliknya (Soft Delete).
     */
    private void toggleUserStatus() {
        int row = table.getSelectedRow();
        if (row == -1) { 
            showWarning("Silakan pilih pengguna terlebih dahulu dari tabel."); 
            return; 
        }
        
        int id = (int) table.getValueAt(row, 0);
        String statusSaatIni = (String) table.getValueAt(row, 4);
        boolean targetStatus = statusSaatIni.equals("Nonaktif"); // Jika saat ini nonaktif, ubah menjadi true (aktif)

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin mengubah status pengguna ini?", 
                "Konfirmasi Perubahan Status", 
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DBConnection.executeUpdate("UPDATE pengguna SET aktif=? WHERE id=?", targetStatus, id);
                loadData();
            } catch (Exception e) {
                showWarning("Gagal mengubah status: " + e.getMessage());
            }
        }
    }

    /**
     * Mereset kata sandi pengguna yang dipilih.
     */
    private void resetPassword() {
        int row = table.getSelectedRow();
        if (row == -1) { 
            showWarning("Silakan pilih pengguna terlebih dahulu dari tabel."); 
            return; 
        }
        
        int id = (int) table.getValueAt(row, 0);
        String username = (String) table.getValueAt(row, 2);

        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(this, pf, 
                "Masukkan kata sandi baru untuk pengguna '" + username + "':", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            String passwordBaru = new String(pf.getPassword());
            if(passwordBaru.length() < 6) {
                showWarning("Kata sandi harus terdiri dari minimal 6 karakter.");
                return;
            }
            try {
                DBConnection.executeUpdate("UPDATE pengguna SET password=? WHERE id=?", LoginFrame.hash(passwordBaru), id);
                JOptionPane.showMessageDialog(this, "Kata sandi berhasil direset.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showWarning("Gagal mereset kata sandi: " + e.getMessage());
            }
        }
    }

    /**
     * Menampilkan pesan peringatan standar.
     * @param msg Pesan yang akan ditampilkan.
     */
    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }
}