package com.sistempakar.ui.master;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// ════════════════════════════════════════════════════════════════
// FORM UNIVERSITAS
// ════════════════════════════════════════════════════════════════
class FormUniversitasPanel extends JPanel {
    private JTable table; private DefaultTableModel model;
    private JTextField tfKode, tfNama, tfSingkatan, tfKota, tfWebsite;
    private JComboBox<String> cbProvinsi, cbWilayah, cbAkreditasi, cbTipe;
    private int selectedId=-1; private JLabel lblStatus;

    FormUniversitasPanel() {
        setOpaque(false); setLayout(new BorderLayout(16,0));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));

        String[] provs = {"DKI Jakarta","Jawa Barat","Jawa Tengah","DI Yogyakarta","Jawa Timur","Banten","Sumatera Utara","Sumatera Barat","Sumatera Selatan","Riau","Kalimantan Timur","Kalimantan Barat","Sulawesi Selatan","Sulawesi Utara","Bali","NTB","NTT","Maluku","Papua","Aceh"};
        String[] wils = {"Jawa","Sumatra","Kalimantan","Sulawesi","Bali-Nusa","Maluku-Papua"};
        String[] akreds = {"Unggul","A","B","C"};
        String[] tipes = {"PTN-BH","PTN-BLU","PTN"};

        JPanel form = Theme.createGlassCard(); form.setLayout(new BorderLayout(0,12)); form.setPreferredSize(new Dimension(400,0));
        JLabel ftitle = new JLabel("🏛️  Form Data Universitas"); ftitle.setFont(Theme.FONT_SUBTITLE); ftitle.setForeground(Theme.textWhite());

        tfKode=Theme.createTextField(); tfNama=Theme.createTextField(); tfSingkatan=Theme.createTextField();
        tfKota=Theme.createTextField(); tfWebsite=Theme.createTextField();
        cbProvinsi=Theme.createComboBox(provs); cbWilayah=Theme.createComboBox(wils);
        cbAkreditasi=Theme.createComboBox(akreds); cbTipe=Theme.createComboBox(tipes);

        JPanel fp = new JPanel(new GridBagLayout()); fp.setOpaque(false);
        GridBagConstraints g=new GridBagConstraints(); g.insets=new Insets(5,4,5,4); g.fill=GridBagConstraints.HORIZONTAL;
        Object[][] flds={{"Kode *",tfKode},{"Nama Universitas *",tfNama},{"Singkatan",tfSingkatan},
            {"Kota",tfKota},{"Provinsi",cbProvinsi},{"Wilayah",cbWilayah},
            {"Akreditasi",cbAkreditasi},{"Tipe PTN",cbTipe},{"Website",tfWebsite}};
        int r=0;
        for(Object[] f:flds){g.gridx=0;g.gridy=r;g.weightx=0;JLabel l=Theme.createLabel(f[0].toString());l.setPreferredSize(new Dimension(130,25));fp.add(l,g);g.gridx=1;g.weightx=1;fp.add((Component)f[1],g);r++;}

        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); btns.setOpaque(false);
        lblStatus=new JLabel(" "); lblStatus.setFont(Theme.FONT_SMALL); lblStatus.setForeground(Theme.SUCCESS);
        JButton bN=Theme.createSecondaryButton("🆕"),bS=Theme.createPrimaryButton("💾 Simpan"),bD=Theme.createDangerButton("🗑️");
        bN.addActionListener(e->clearF()); bS.addActionListener(e->save(provs,wils,akreds,tipes)); bD.addActionListener(e->del());
        btns.add(lblStatus); btns.add(bN); btns.add(bD); btns.add(bS);

        form.add(ftitle,BorderLayout.NORTH); form.add(new JScrollPane(fp){{setOpaque(false);getViewport().setOpaque(false);setBorder(null);}},BorderLayout.CENTER); form.add(btns,BorderLayout.SOUTH);

        JPanel tp=Theme.createGlassCard(); tp.setLayout(new BorderLayout(0,12));
        JLabel tt=new JLabel("🏛️  Daftar Universitas"); tt.setFont(Theme.FONT_SUBTITLE); tt.setForeground(Theme.textWhite());
        model=new DefaultTableModel(new String[]{"ID","Kode","Nama","Singkatan","Kota","Provinsi","Akreditasi","Tipe"},0){public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(model); Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0); table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&table.getSelectedRow()>=0)loadRow(provs,wils,akreds,tipes);});
        tp.add(tt,BorderLayout.NORTH); tp.add(Theme.createScrollPane(table),BorderLayout.CENTER);
        add(form,BorderLayout.WEST); add(tp,BorderLayout.CENTER); load();
    }

    private void load() {
        model.setRowCount(0);
        try{ResultSet rs=DBConnection.executeQuery("SELECT id,kode,nama,singkatan,kota,provinsi,akreditasi,tipe FROM universitas ORDER BY nama");
        while(rs.next())model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8)});
        rs.getStatement().close();}catch(Exception e){e.printStackTrace();}
    }

    private void loadRow(String[] provs, String[] wils, String[] akreds, String[] tipes) {
        int row=table.getSelectedRow(); if(row<0)return; selectedId=(int)model.getValueAt(row,0);
        try{ResultSet rs=DBConnection.executeQuery("SELECT * FROM universitas WHERE id=?",selectedId);
        if(rs.next()){tfKode.setText(rs.getString("kode"));tfNama.setText(rs.getString("nama"));tfSingkatan.setText(rs.getString("singkatan"));tfKota.setText(rs.getString("kota"));tfWebsite.setText(rs.getString("website"));
        selCombo(cbProvinsi,provs,rs.getString("provinsi"));selCombo(cbWilayah,wils,rs.getString("wilayah"));selCombo(cbAkreditasi,akreds,rs.getString("akreditasi"));selCombo(cbTipe,tipes,rs.getString("tipe"));}
        rs.getStatement().close();}catch(Exception e){e.printStackTrace();}
    }

    private void selCombo(JComboBox<String> cb, String[] arr, String val) {
        for(int i=0;i<arr.length;i++)if(arr[i].equals(val)){cb.setSelectedIndex(i);return;}
    }

    private void save(String[] provs, String[] wils, String[] akreds, String[] tipes) {
        if(tfKode.getText().isEmpty()||tfNama.getText().isEmpty()){JOptionPane.showMessageDialog(this,"Kode & Nama wajib!","Validasi",JOptionPane.WARNING_MESSAGE);return;}
        try{if(selectedId==-1)DBConnection.executeUpdate("INSERT INTO universitas(kode,nama,singkatan,kota,provinsi,wilayah,akreditasi,tipe,website)VALUES(?,?,?,?,?,?,?,?,?)",tfKode.getText(),tfNama.getText(),tfSingkatan.getText(),tfKota.getText(),cbProvinsi.getSelectedItem(),cbWilayah.getSelectedItem(),cbAkreditasi.getSelectedItem(),cbTipe.getSelectedItem(),tfWebsite.getText());
        else DBConnection.executeUpdate("UPDATE universitas SET kode=?,nama=?,singkatan=?,kota=?,provinsi=?,wilayah=?,akreditasi=?,tipe=?,website=? WHERE id=?",tfKode.getText(),tfNama.getText(),tfSingkatan.getText(),tfKota.getText(),cbProvinsi.getSelectedItem(),cbWilayah.getSelectedItem(),cbAkreditasi.getSelectedItem(),cbTipe.getSelectedItem(),tfWebsite.getText(),selectedId);
        lblStatus.setText("✅ Tersimpan!"); load(); clearF();}catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void del() {
        if(selectedId==-1)return;
        if(JOptionPane.showConfirmDialog(this,"Hapus universitas ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{DBConnection.executeUpdate("DELETE FROM universitas WHERE id=?",selectedId);lblStatus.setText("✅ Dihapus!");load();clearF();}
            catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        }
    }

    private void clearF(){selectedId=-1;tfKode.setText("");tfNama.setText("");tfSingkatan.setText("");tfKota.setText("");tfWebsite.setText("");cbProvinsi.setSelectedIndex(0);cbWilayah.setSelectedIndex(0);cbAkreditasi.setSelectedIndex(0);cbTipe.setSelectedIndex(0);table.clearSelection();lblStatus.setText(" ");}
}

// ════════════════════════════════════════════════════════════════
// PUBLIC WRAPPERS – each class is in its own named public class
// ════════════════════════════════════════════════════════════════

public class FormUniversitas extends JPanel {
    public FormUniversitas() { setOpaque(false); setLayout(new BorderLayout()); add(new FormUniversitasPanel()); }
}
