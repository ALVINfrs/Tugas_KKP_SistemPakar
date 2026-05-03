package com.sistempakar.ui.master;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// ════════════════════════════════════════════════════════════════
// FORM JURUSAN KULIAH
// ════════════════════════════════════════════════════════════════
public class FormJurusan extends JPanel {
    private JTable table; private DefaultTableModel model;
    private JTextField tfKode, tfNama, tfRumpun;
    private JTextArea taDeskripsi, taProspek, taMataKuliah;
    private JComboBox<String> cbKategori;
    private int selectedId = -1;
    private JLabel lblStatus;

    public FormJurusan() {
        setOpaque(false); setLayout(new BorderLayout(16,0));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        init(); load();
    }

    private void init() {
        // Form
        JPanel form = Theme.createGlassCard();
        form.setLayout(new BorderLayout(0,12));
        form.setPreferredSize(new Dimension(420,0));

        JLabel title = new JLabel("📚  Form Data Jurusan");
        title.setFont(Theme.FONT_SUBTITLE); title.setForeground(Theme.textWhite());

        tfKode  = Theme.createTextField(); tfKode.putClientProperty("JTextField.placeholderText","Contoh: TI");
        tfNama  = Theme.createTextField(); tfNama.putClientProperty("JTextField.placeholderText","Nama jurusan lengkap");
        tfRumpun= Theme.createTextField(); tfRumpun.putClientProperty("JTextField.placeholderText","Contoh: Teknologi Informasi");
        taDeskripsi  = Theme.createTextArea(); taDeskripsi.setRows(3);
        taProspek    = Theme.createTextArea(); taProspek.setRows(3);
        taMataKuliah = Theme.createTextArea(); taMataKuliah.setRows(3);

        // Load kategori
        java.util.List<String> kats = new java.util.ArrayList<>();
        java.util.List<Integer> katIds = new java.util.ArrayList<>();
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT id, nama FROM kategori_minat ORDER BY id");
            while (rs.next()) { kats.add(rs.getString("nama")); katIds.add(rs.getInt("id")); }
            rs.getStatement().close();
        } catch (Exception e) { kats.add("N/A"); }
        cbKategori = Theme.createComboBox(kats.toArray(new String[0]));

        JPanel fp = new JPanel(new GridBagLayout()); fp.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,4,5,4); g.fill = GridBagConstraints.HORIZONTAL;

        Object[][] flds = {
            {"Kode Jurusan *",tfKode},{"Nama Jurusan *",tfNama},
            {"Kategori Minat",cbKategori},{"Rumpun",tfRumpun}
        };
        JTextArea[][] areas = {{taDeskripsi,taProspek,taMataKuliah}};
        String[] areaLabels = {"Deskripsi","Prospek Kerja","Mata Kuliah Utama"};

        int r=0;
        for (Object[] f : flds) {
            g.gridx=0; g.gridy=r; g.weightx=0;
            JLabel l = Theme.createLabel(f[0].toString()); l.setPreferredSize(new Dimension(130,25)); fp.add(l,g);
            g.gridx=1; g.weightx=1; fp.add((Component)f[1],g); r++;
        }
        JTextArea[] tas = {taDeskripsi,taProspek,taMataKuliah};
        for (int i=0; i<3; i++) {
            g.gridx=0; g.gridy=r; g.weightx=0; fp.add(Theme.createLabel(areaLabels[i]),g);
            g.gridx=1; g.weightx=1; fp.add(Theme.createScrollPane(tas[i]),g); r++;
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); btns.setOpaque(false);
        lblStatus = new JLabel(" "); lblStatus.setFont(Theme.FONT_SMALL); lblStatus.setForeground(Theme.SUCCESS);
        JButton bN=Theme.createSecondaryButton("🆕 Baru"), bS=Theme.createPrimaryButton("💾 Simpan"),
                bD=Theme.createDangerButton("🗑️ Hapus");
        bN.addActionListener(e->clearF()); bS.addActionListener(e->save(katIds)); bD.addActionListener(e->del());
        btns.add(lblStatus); btns.add(bN); btns.add(bD); btns.add(bS);

        form.add(title,BorderLayout.NORTH); form.add(new JScrollPane(fp){{setOpaque(false);getViewport().setOpaque(false);setBorder(null);}},BorderLayout.CENTER); form.add(btns,BorderLayout.SOUTH);

        // Table
        JPanel tp = Theme.createGlassCard(); tp.setLayout(new BorderLayout(0,12));
        JLabel tl = new JLabel("📚  Daftar Jurusan"); tl.setFont(Theme.FONT_SUBTITLE); tl.setForeground(Theme.textWhite());
        model = new DefaultTableModel(new String[]{"ID","Kode","Nama Jurusan","Kategori","Rumpun"},0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model); Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0); table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&table.getSelectedRow()>=0)loadRow(katIds);});
        tp.add(tl,BorderLayout.NORTH); tp.add(Theme.createScrollPane(table),BorderLayout.CENTER);

        add(form,BorderLayout.WEST); add(tp,BorderLayout.CENTER);
    }

    private void load() {
        model.setRowCount(0);
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT j.id,j.kode,j.nama,km.nama,j.rumpun FROM jurusan_kuliah j JOIN kategori_minat km ON j.kategori_id=km.id ORDER BY j.nama");
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)});
            rs.getStatement().close();
        } catch(Exception e){e.printStackTrace();}
    }

    private void loadRow(java.util.List<Integer> katIds) {
        int row = table.getSelectedRow(); if(row<0)return;
        selectedId = (int)model.getValueAt(row,0);
        try {
            ResultSet rs = DBConnection.executeQuery("SELECT * FROM jurusan_kuliah WHERE id=?",selectedId);
            if(rs.next()) {
                tfKode.setText(rs.getString("kode")); tfNama.setText(rs.getString("nama")); tfRumpun.setText(rs.getString("rumpun"));
                taDeskripsi.setText(rs.getString("deskripsi")); taProspek.setText(rs.getString("prospek_kerja")); taMataKuliah.setText(rs.getString("mata_kuliah_utama"));
                int catId=rs.getInt("kategori_id"); for(int i=0;i<katIds.size();i++) if(katIds.get(i)==catId){cbKategori.setSelectedIndex(i);break;}
            }
            rs.getStatement().close();
        } catch(Exception e){e.printStackTrace();}
    }

    private void save(java.util.List<Integer> katIds) {
        if(tfKode.getText().isEmpty()||tfNama.getText().isEmpty()){JOptionPane.showMessageDialog(this,"Kode dan Nama wajib diisi!","Validasi",JOptionPane.WARNING_MESSAGE);return;}
        try {
            int katId = katIds.isEmpty()?1:katIds.get(cbKategori.getSelectedIndex());
            if(selectedId==-1) DBConnection.executeUpdate("INSERT INTO jurusan_kuliah(kode,nama,kategori_id,deskripsi,prospek_kerja,mata_kuliah_utama,rumpun) VALUES(?,?,?,?,?,?,?)",tfKode.getText(),tfNama.getText(),katId,taDeskripsi.getText(),taProspek.getText(),taMataKuliah.getText(),tfRumpun.getText());
            else DBConnection.executeUpdate("UPDATE jurusan_kuliah SET kode=?,nama=?,kategori_id=?,deskripsi=?,prospek_kerja=?,mata_kuliah_utama=?,rumpun=? WHERE id=?",tfKode.getText(),tfNama.getText(),katId,taDeskripsi.getText(),taProspek.getText(),taMataKuliah.getText(),tfRumpun.getText(),selectedId);
            lblStatus.setText("✅ Tersimpan!"); load(); clearF();
        } catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void del() {
        if(selectedId==-1)return;
        if(JOptionPane.showConfirmDialog(this,"Hapus jurusan ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            try{DBConnection.executeUpdate("DELETE FROM jurusan_kuliah WHERE id=?",selectedId);lblStatus.setText("✅ Dihapus!");load();clearF();}
            catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        }
    }

    private void clearF() {
        selectedId=-1; tfKode.setText(""); tfNama.setText(""); tfRumpun.setText("");
        taDeskripsi.setText(""); taProspek.setText(""); taMataKuliah.setText("");
        cbKategori.setSelectedIndex(0); table.clearSelection(); lblStatus.setText(" ");
    }
}
