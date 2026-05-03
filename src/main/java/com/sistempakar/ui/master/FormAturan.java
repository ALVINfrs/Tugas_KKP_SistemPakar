package com.sistempakar.ui.master;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FormAturan extends JPanel {
    private JTable table; private DefaultTableModel model;
    private JTextField tfKode, tfNama;
    private JTextArea taKondisi, taKeterangan;
    private JComboBox<String> cbJurusan, cbAktif;
    private JSpinner spPrioritas, spConfidence;
    private int selectedId=-1; private JLabel lblStatus;
    private java.util.List<Integer> jIds=new java.util.ArrayList<>();

    public FormAturan() {
        setOpaque(false); setLayout(new BorderLayout(16,0));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        init(); load();
    }

    private void init() {
        JPanel form=Theme.createGlassCard(); form.setLayout(new BorderLayout(0,12)); form.setPreferredSize(new Dimension(480,0));
        JLabel ft=new JLabel("⚙️  Form Aturan Forward Chaining"); ft.setFont(Theme.FONT_SUBTITLE); ft.setForeground(Theme.textWhite());

        tfKode=Theme.createTextField(); tfKode.putClientProperty("JTextField.placeholderText","Contoh: R029");
        tfNama=Theme.createTextField(); tfNama.putClientProperty("JTextField.placeholderText","Nama aturan deskriptif");
        taKondisi=Theme.createTextArea(); taKondisi.setRows(5);
        taKondisi.setText("{\n  \"skor_teknologi\": {\"min\": 10},\n  \"nilai_matematika\": {\"min\": 75}\n}");
        taKeterangan=Theme.createTextArea(); taKeterangan.setRows(3);
        spPrioritas=Theme.createSpinner(new SpinnerNumberModel(5,1,10,1));
        spConfidence=Theme.createSpinner(new SpinnerNumberModel(85.0,0.0,100.0,0.5));
        cbAktif=Theme.createComboBox(new String[]{"✅ Aktif","❌ Nonaktif"});

        java.util.List<String> jNama=new java.util.ArrayList<>();
        try{ResultSet rs=DBConnection.executeQuery("SELECT id,nama FROM jurusan_kuliah ORDER BY nama");
            while(rs.next()){jNama.add(rs.getString("nama"));jIds.add(rs.getInt("id"));}
            rs.getStatement().close();}catch(Exception e){}
        cbJurusan=Theme.createComboBox(jNama.toArray(new String[0]));

        // Help panel
        JPanel helpPanel=Theme.createGlassCard(8); helpPanel.setLayout(new BorderLayout());
        JLabel helpTitle=new JLabel("💡 Format Kondisi JSON"); helpTitle.setFont(Theme.FONT_BOLD); helpTitle.setForeground(Theme.ACCENT_BLUE);
        JTextArea helpText=new JTextArea(
            "Kunci yang tersedia:\n"+
            "• skor_teknologi, skor_sains, skor_sosial\n"+
            "• skor_seni, skor_bisnis, skor_bahasa, skor_kesehatan\n"+
            "• nilai_matematika, nilai_ipa, nilai_ips\n"+
            "• nilai_bahasa_ind, nilai_bahasa_ing, nilai_seni\n\n"+
            "Format: {\"kunci\": {\"min\": nilai, \"max\": nilai}}"
        );
        helpText.setEditable(false); helpText.setOpaque(false); helpText.setFont(Theme.FONT_SMALL); helpText.setForeground(Theme.TEXT_SECONDARY);
        helpPanel.add(helpTitle,BorderLayout.NORTH); helpPanel.add(helpText,BorderLayout.CENTER);

        JPanel fp=new JPanel(new GridBagLayout()); fp.setOpaque(false);
        GridBagConstraints g=new GridBagConstraints(); g.insets=new Insets(5,4,5,4); g.fill=GridBagConstraints.HORIZONTAL;
        Object[][] flds={{"Kode Aturan *",tfKode},{"Nama Aturan *",tfNama},{"Jurusan Target",cbJurusan},{"Prioritas (1-10)",spPrioritas},{"Confidence (%)",spConfidence},{"Status",cbAktif}};
        int r=0;
        for(Object[] f:flds){g.gridx=0;g.gridy=r;g.weightx=0;JLabel l=Theme.createLabel(f[0].toString());l.setPreferredSize(new Dimension(145,25));fp.add(l,g);g.gridx=1;g.weightx=1;fp.add((Component)f[1],g);r++;}
        g.gridx=0;g.gridy=r;g.weightx=0;fp.add(Theme.createLabel("Kondisi (JSON) *"),g);
        g.gridx=1;g.weightx=1;fp.add(Theme.createScrollPane(taKondisi),g); r++;
        g.gridx=0;g.gridy=r;g.weightx=0;fp.add(Theme.createLabel("Keterangan"),g);
        g.gridx=1;g.weightx=1;fp.add(Theme.createScrollPane(taKeterangan),g); r++;
        g.gridx=0;g.gridy=r;g.gridwidth=2;fp.add(helpPanel,g);

        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); btns.setOpaque(false);
        lblStatus=new JLabel(" "); lblStatus.setFont(Theme.FONT_SMALL); lblStatus.setForeground(Theme.SUCCESS);
        JButton bN=Theme.createSecondaryButton("🆕"),bS=Theme.createPrimaryButton("💾 Simpan"),bD=Theme.createDangerButton("🗑️");
        bN.addActionListener(e->clearF()); bS.addActionListener(e->save()); bD.addActionListener(e->del());
        btns.add(lblStatus); btns.add(bN); btns.add(bD); btns.add(bS);
        form.add(ft,BorderLayout.NORTH);
        form.add(new JScrollPane(fp){{setOpaque(false);getViewport().setOpaque(false);setBorder(null);}},BorderLayout.CENTER);
        form.add(btns,BorderLayout.SOUTH);

        JPanel tp=Theme.createGlassCard(); tp.setLayout(new BorderLayout(0,12));
        JLabel tt=new JLabel("⚙️  Daftar Aturan / Rules"); tt.setFont(Theme.FONT_SUBTITLE); tt.setForeground(Theme.textWhite());
        model=new DefaultTableModel(new String[]{"ID","Kode","Nama Aturan","Jurusan Target","Prioritas","Confidence","Aktif"},0){public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(model); Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0); table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&table.getSelectedRow()>=0)loadRow();});
        tp.add(tt,BorderLayout.NORTH); tp.add(Theme.createScrollPane(table),BorderLayout.CENTER);
        add(form,BorderLayout.WEST); add(tp,BorderLayout.CENTER);
    }

    private void load() {
        model.setRowCount(0);
        try{
            ResultSet rs=DBConnection.executeQuery("SELECT a.id,a.kode_aturan,a.nama_aturan,j.nama,a.prioritas,a.confidence,a.aktif FROM aturan a JOIN jurusan_kuliah j ON a.jurusan_id=j.id ORDER BY a.prioritas DESC");
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5),rs.getDouble(6)+"%",rs.getBoolean(7)?"✅":"❌"});
            rs.getStatement().close();
        }catch(Exception e){e.printStackTrace();}
    }

    private void loadRow() {
        int row=table.getSelectedRow(); if(row<0)return; selectedId=(int)model.getValueAt(row,0);
        try{
            ResultSet rs=DBConnection.executeQuery("SELECT * FROM aturan WHERE id=?",selectedId);
            if(rs.next()){tfKode.setText(rs.getString("kode_aturan"));tfNama.setText(rs.getString("nama_aturan"));taKondisi.setText(rs.getString("kondisi_json"));taKeterangan.setText(rs.getString("keterangan"));spPrioritas.setValue(rs.getInt("prioritas"));spConfidence.setValue(rs.getDouble("confidence"));cbAktif.setSelectedIndex(rs.getBoolean("aktif")?0:1);
                int jId=rs.getInt("jurusan_id"); for(int i=0;i<jIds.size();i++)if(jIds.get(i)==jId){cbJurusan.setSelectedIndex(i);break;}}
            rs.getStatement().close();
        }catch(Exception e){e.printStackTrace();}
    }

    private void save() {
        if(tfKode.getText().isEmpty()||taKondisi.getText().isEmpty()){JOptionPane.showMessageDialog(this,"Kode & Kondisi wajib!","Validasi",JOptionPane.WARNING_MESSAGE);return;}
        try{
            int jId=jIds.isEmpty()?1:jIds.get(cbJurusan.getSelectedIndex());
            boolean aktif=cbAktif.getSelectedIndex()==0;
            if(selectedId==-1) DBConnection.executeUpdate("INSERT INTO aturan(kode_aturan,nama_aturan,kondisi_json,jurusan_id,prioritas,confidence,keterangan,aktif)VALUES(?,?,?,?,?,?,?,?)",tfKode.getText(),tfNama.getText(),taKondisi.getText(),jId,spPrioritas.getValue(),spConfidence.getValue(),taKeterangan.getText(),aktif);
            else DBConnection.executeUpdate("UPDATE aturan SET kode_aturan=?,nama_aturan=?,kondisi_json=?,jurusan_id=?,prioritas=?,confidence=?,keterangan=?,aktif=? WHERE id=?",tfKode.getText(),tfNama.getText(),taKondisi.getText(),jId,spPrioritas.getValue(),spConfidence.getValue(),taKeterangan.getText(),aktif,selectedId);
            lblStatus.setText("✅ Tersimpan!"); load(); clearF();
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void del(){if(selectedId==-1)return;if(JOptionPane.showConfirmDialog(this,"Hapus aturan ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){try{DBConnection.executeUpdate("DELETE FROM aturan WHERE id=?",selectedId);load();clearF();}catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}}}
    private void clearF(){selectedId=-1;tfKode.setText("");tfNama.setText("");taKondisi.setText("{\n  \"skor_teknologi\": {\"min\": 10}\n}");taKeterangan.setText("");spPrioritas.setValue(5);spConfidence.setValue(85.0);cbAktif.setSelectedIndex(0);if(!jIds.isEmpty())cbJurusan.setSelectedIndex(0);table.clearSelection();lblStatus.setText(" ");}
}
