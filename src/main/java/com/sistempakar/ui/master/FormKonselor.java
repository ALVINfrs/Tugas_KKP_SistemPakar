package com.sistempakar.ui.master;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FormKonselor extends JPanel {
    private JTable table; private DefaultTableModel model;
    private JTextField tfNip, tfNama, tfGelar, tfEmail, tfTelepon, tfSpesialisasi;
    private JSpinner spPengalaman;
    private JCheckBox cbAktif;
    private int selectedId=-1; private JLabel lblStatus;

    public FormKonselor() {
        setOpaque(false); setLayout(new BorderLayout(16,0));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        init(); load();
    }

    private void init() {
        JPanel form = Theme.createGlassCard(); form.setLayout(new BorderLayout(0,12)); form.setPreferredSize(new Dimension(420,0));
        JLabel ftitle = new JLabel("👨‍💼  Form Data Konselor / Guru BK"); ftitle.setFont(Theme.FONT_SUBTITLE); ftitle.setForeground(Theme.textWhite());

        tfNip=Theme.createTextField(); tfNip.putClientProperty("JTextField.placeholderText","NIP Konselor");
        tfNama=Theme.createTextField(); tfNama.putClientProperty("JTextField.placeholderText","Nama lengkap konselor");
        tfGelar=Theme.createTextField(); tfGelar.putClientProperty("JTextField.placeholderText","Contoh: M.Pd, S.Psi");
        tfEmail=Theme.createTextField(); tfEmail.putClientProperty("JTextField.placeholderText","email@sekolah.sch.id");
        tfTelepon=Theme.createTextField(); tfTelepon.putClientProperty("JTextField.placeholderText","08xxxxxxxxxx");
        tfSpesialisasi=Theme.createTextField(); tfSpesialisasi.putClientProperty("JTextField.placeholderText","Contoh: Bimbingan Karir");
        spPengalaman=Theme.createSpinner(new SpinnerNumberModel(1,0,50,1));
        cbAktif=new JCheckBox("Konselor Aktif"); cbAktif.setOpaque(false); cbAktif.setForeground(Theme.TEXT_PRIMARY); cbAktif.setFont(Theme.FONT_BODY); cbAktif.setSelected(true);

        JPanel fp = new JPanel(new GridBagLayout()); fp.setOpaque(false);
        GridBagConstraints g=new GridBagConstraints(); g.insets=new Insets(6,4,6,4); g.fill=GridBagConstraints.HORIZONTAL;

        Object[][] flds={
            {"NIP *",tfNip},{"Nama Lengkap *",tfNama},{"Gelar",tfGelar},
            {"Email",tfEmail},{"Telepon",tfTelepon},
            {"Spesialisasi",tfSpesialisasi},{"Pengalaman (tahun)",spPengalaman}
        };
        int r=0;
        for(Object[] f:flds){
            g.gridx=0;g.gridy=r;g.weightx=0;
            JLabel l=Theme.createLabel(f[0].toString());l.setPreferredSize(new Dimension(155,25));fp.add(l,g);
            g.gridx=1;g.weightx=1;fp.add((Component)f[1],g);r++;
        }
        g.gridx=0;g.gridy=r;g.gridwidth=2;fp.add(cbAktif,g);

        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); btns.setOpaque(false);
        lblStatus=new JLabel(" "); lblStatus.setFont(Theme.FONT_SMALL); lblStatus.setForeground(Theme.SUCCESS);
        JButton bN=Theme.createSecondaryButton("🆕 Baru"),bS=Theme.createPrimaryButton("💾 Simpan"),bD=Theme.createDangerButton("🗑️ Hapus");
        bN.addActionListener(e->clearF()); bS.addActionListener(e->save()); bD.addActionListener(e->del());
        btns.add(lblStatus); btns.add(bN); btns.add(bD); btns.add(bS);

        form.add(ftitle,BorderLayout.NORTH);
        form.add(new JScrollPane(fp){{setOpaque(false);getViewport().setOpaque(false);setBorder(null);}},BorderLayout.CENTER);
        form.add(btns,BorderLayout.SOUTH);

        JPanel tp=Theme.createGlassCard(); tp.setLayout(new BorderLayout(0,12));
        JLabel tt=new JLabel("👨‍💼  Daftar Konselor / Guru BK"); tt.setFont(Theme.FONT_SUBTITLE); tt.setForeground(Theme.textWhite());
        model=new DefaultTableModel(new String[]{"ID","NIP","Nama","Gelar","Spesialisasi","Pengalaman","Aktif"},0){public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(model); Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0); table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&table.getSelectedRow()>=0)loadRow();});
        tp.add(tt,BorderLayout.NORTH); tp.add(Theme.createScrollPane(table),BorderLayout.CENTER);
        add(form,BorderLayout.WEST); add(tp,BorderLayout.CENTER);
    }

    private void load() {
        model.setRowCount(0);
        try{
            ResultSet rs=DBConnection.executeQuery("SELECT id,nip,nama,gelar,spesialisasi,pengalaman,aktif FROM konselor ORDER BY nama");
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getInt(6)+" tahun",rs.getBoolean(7)?"✅ Aktif":"❌ Nonaktif"});
            rs.getStatement().close();
        }catch(Exception e){e.printStackTrace();}
    }

    private void loadRow() {
        int row=table.getSelectedRow(); if(row<0)return; selectedId=(int)model.getValueAt(row,0);
        try{
            ResultSet rs=DBConnection.executeQuery("SELECT * FROM konselor WHERE id=?",selectedId);
            if(rs.next()){tfNip.setText(rs.getString("nip"));tfNama.setText(rs.getString("nama"));tfGelar.setText(rs.getString("gelar"));tfEmail.setText(rs.getString("email"));tfTelepon.setText(rs.getString("telepon"));tfSpesialisasi.setText(rs.getString("spesialisasi"));spPengalaman.setValue(rs.getInt("pengalaman"));cbAktif.setSelected(rs.getBoolean("aktif"));}
            rs.getStatement().close();
        }catch(Exception e){e.printStackTrace();}
    }

    private void save() {
        if(tfNip.getText().isEmpty()||tfNama.getText().isEmpty()){JOptionPane.showMessageDialog(this,"NIP & Nama wajib diisi!","Validasi",JOptionPane.WARNING_MESSAGE);return;}
        try{
            if(selectedId==-1) DBConnection.executeUpdate("INSERT INTO konselor(nip,nama,gelar,email,telepon,spesialisasi,pengalaman,aktif)VALUES(?,?,?,?,?,?,?,?)",tfNip.getText(),tfNama.getText(),tfGelar.getText(),tfEmail.getText(),tfTelepon.getText(),tfSpesialisasi.getText(),spPengalaman.getValue(),cbAktif.isSelected());
            else DBConnection.executeUpdate("UPDATE konselor SET nip=?,nama=?,gelar=?,email=?,telepon=?,spesialisasi=?,pengalaman=?,aktif=? WHERE id=?",tfNip.getText(),tfNama.getText(),tfGelar.getText(),tfEmail.getText(),tfTelepon.getText(),tfSpesialisasi.getText(),spPengalaman.getValue(),cbAktif.isSelected(),selectedId);
            lblStatus.setText("✅ Tersimpan!"); load(); clearF();
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void del() {
        if(selectedId==-1)return;
        if(JOptionPane.showConfirmDialog(this,"Hapus konselor ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{DBConnection.executeUpdate("DELETE FROM konselor WHERE id=?",selectedId);lblStatus.setText("✅ Dihapus!");load();clearF();}
            catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
        }
    }

    private void clearF(){selectedId=-1;tfNip.setText("");tfNama.setText("");tfGelar.setText("");tfEmail.setText("");tfTelepon.setText("");tfSpesialisasi.setText("");spPengalaman.setValue(1);cbAktif.setSelected(true);table.clearSelection();lblStatus.setText(" ");}
}
