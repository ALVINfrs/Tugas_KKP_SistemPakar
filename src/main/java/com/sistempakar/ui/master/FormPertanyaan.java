package com.sistempakar.ui.master;

import com.sistempakar.db.DBConnection;
import com.sistempakar.util.Theme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FormPertanyaan extends JPanel {
    private JTable table; private DefaultTableModel model;
    private JTextField tfKode, tfAspek;
    private JTextArea taPertanyaan;
    private JComboBox<String> cbKategori, cbAktif;
    private JSpinner spBobot, spUrutan;
    private int selectedId=-1; private JLabel lblStatus;
    private java.util.List<Integer> katIds = new java.util.ArrayList<>();

    public FormPertanyaan() {
        setOpaque(false); setLayout(new BorderLayout(16,0));
        setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        init(); load();
    }

    private void init() {
        JPanel form=Theme.createGlassCard(); form.setLayout(new BorderLayout(0,12)); form.setPreferredSize(new Dimension(430,0));
        JLabel ft=new JLabel("❓  Form Pertanyaan Psikotest"); ft.setFont(Theme.FONT_SUBTITLE); ft.setForeground(Theme.textWhite());

        tfKode=Theme.createTextField(); tfKode.putClientProperty("JTextField.placeholderText","Contoh: P036");
        tfAspek=Theme.createTextField(); tfAspek.putClientProperty("JTextField.placeholderText","minat / bakat / kepribadian");
        taPertanyaan=Theme.createTextArea(); taPertanyaan.setRows(4);
        taPertanyaan.putClientProperty("JTextArea.placeholderText","Tulis pertanyaan psikotest di sini...");
        spBobot=Theme.createSpinner(new SpinnerNumberModel(1,1,5,1));
        spUrutan=Theme.createSpinner(new SpinnerNumberModel(1,1,999,1));
        cbAktif=Theme.createComboBox(new String[]{"✅ Aktif","❌ Nonaktif"});

        java.util.List<String> katNama=new java.util.ArrayList<>();
        try{ResultSet rs=DBConnection.executeQuery("SELECT id,nama FROM kategori_minat ORDER BY id");
            while(rs.next()){katNama.add(rs.getString("nama"));katIds.add(rs.getInt("id"));}
            rs.getStatement().close();}catch(Exception e){}
        cbKategori=Theme.createComboBox(katNama.toArray(new String[0]));

        JPanel fp=new JPanel(new GridBagLayout()); fp.setOpaque(false);
        GridBagConstraints g=new GridBagConstraints(); g.insets=new Insets(5,4,5,4); g.fill=GridBagConstraints.HORIZONTAL;
        Object[][] flds={{"Kode *",tfKode},{"Kategori Minat",cbKategori},{"Aspek",tfAspek},{"Bobot (1-5)",spBobot},{"Urutan",spUrutan},{"Status",cbAktif}};
        int r=0;
        for(Object[] f:flds){g.gridx=0;g.gridy=r;g.weightx=0;JLabel l=Theme.createLabel(f[0].toString());l.setPreferredSize(new Dimension(140,25));fp.add(l,g);g.gridx=1;g.weightx=1;fp.add((Component)f[1],g);r++;}
        g.gridx=0;g.gridy=r;g.weightx=0;fp.add(Theme.createLabel("Pertanyaan *"),g);
        g.gridx=1;g.weightx=1;fp.add(Theme.createScrollPane(taPertanyaan),g);

        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); btns.setOpaque(false);
        lblStatus=new JLabel(" "); lblStatus.setFont(Theme.FONT_SMALL); lblStatus.setForeground(Theme.SUCCESS);
        JButton bN=Theme.createSecondaryButton("🆕"),bS=Theme.createPrimaryButton("💾 Simpan"),bD=Theme.createDangerButton("🗑️");
        bN.addActionListener(e->clearF()); bS.addActionListener(e->save()); bD.addActionListener(e->del());
        btns.add(lblStatus); btns.add(bN); btns.add(bD); btns.add(bS);
        form.add(ft,BorderLayout.NORTH);
        form.add(new JScrollPane(fp){{setOpaque(false);getViewport().setOpaque(false);setBorder(null);}},BorderLayout.CENTER);
        form.add(btns,BorderLayout.SOUTH);

        JPanel tp=Theme.createGlassCard(); tp.setLayout(new BorderLayout(0,12));
        JLabel tt=new JLabel("❓  Daftar Pertanyaan Psikotest"); tt.setFont(Theme.FONT_SUBTITLE); tt.setForeground(Theme.textWhite());
        model=new DefaultTableModel(new String[]{"ID","Kode","Pertanyaan","Kategori","Aspek","Bobot","Urutan","Aktif"},0){public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(model); Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0); table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&table.getSelectedRow()>=0)loadRow();});
        tp.add(tt,BorderLayout.NORTH); tp.add(Theme.createScrollPane(table),BorderLayout.CENTER);
        add(form,BorderLayout.WEST); add(tp,BorderLayout.CENTER);
    }

    private void load() {
        model.setRowCount(0);
        try{
            ResultSet rs=DBConnection.executeQuery("SELECT pp.id,pp.kode,pp.pertanyaan,km.nama,pp.aspek,pp.bobot,pp.urutan,pp.aktif FROM pertanyaan_psikotest pp JOIN kategori_minat km ON pp.kategori_id=km.id ORDER BY pp.urutan");
            while(rs.next()) {
                String q=rs.getString("pertanyaan"); if(q!=null&&q.length()>55)q=q.substring(0,52)+"...";
                model.addRow(new Object[]{rs.getInt(1),rs.getString(2),q,rs.getString(4),rs.getString(5),rs.getInt(6),rs.getInt(7),rs.getBoolean(8)?"✅":"❌"});
            }
            rs.getStatement().close();
        }catch(Exception e){e.printStackTrace();}
    }

    private void loadRow() {
        int row=table.getSelectedRow(); if(row<0)return; selectedId=(int)model.getValueAt(row,0);
        try{
            ResultSet rs=DBConnection.executeQuery("SELECT * FROM pertanyaan_psikotest WHERE id=?",selectedId);
            if(rs.next()){tfKode.setText(rs.getString("kode"));taPertanyaan.setText(rs.getString("pertanyaan"));tfAspek.setText(rs.getString("aspek"));spBobot.setValue(rs.getInt("bobot"));spUrutan.setValue(rs.getInt("urutan"));cbAktif.setSelectedIndex(rs.getBoolean("aktif")?0:1);
                int catId=rs.getInt("kategori_id"); for(int i=0;i<katIds.size();i++)if(katIds.get(i)==catId){cbKategori.setSelectedIndex(i);break;}}
            rs.getStatement().close();
        }catch(Exception e){e.printStackTrace();}
    }

    private void save() {
        if(tfKode.getText().isEmpty()||taPertanyaan.getText().isEmpty()){JOptionPane.showMessageDialog(this,"Kode & Pertanyaan wajib!","Validasi",JOptionPane.WARNING_MESSAGE);return;}
        try{
            int katId=katIds.isEmpty()?1:katIds.get(cbKategori.getSelectedIndex());
            boolean aktif=cbAktif.getSelectedIndex()==0;
            if(selectedId==-1) DBConnection.executeUpdate("INSERT INTO pertanyaan_psikotest(kode,pertanyaan,kategori_id,aspek,bobot,urutan,aktif)VALUES(?,?,?,?,?,?,?)",tfKode.getText(),taPertanyaan.getText(),katId,tfAspek.getText(),spBobot.getValue(),spUrutan.getValue(),aktif);
            else DBConnection.executeUpdate("UPDATE pertanyaan_psikotest SET kode=?,pertanyaan=?,kategori_id=?,aspek=?,bobot=?,urutan=?,aktif=? WHERE id=?",tfKode.getText(),taPertanyaan.getText(),katId,tfAspek.getText(),spBobot.getValue(),spUrutan.getValue(),aktif,selectedId);
            lblStatus.setText("✅ Tersimpan!"); load(); clearF();
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void del(){if(selectedId==-1)return;if(JOptionPane.showConfirmDialog(this,"Hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){try{DBConnection.executeUpdate("DELETE FROM pertanyaan_psikotest WHERE id=?",selectedId);load();clearF();}catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}}}
    private void clearF(){selectedId=-1;tfKode.setText("");taPertanyaan.setText("");tfAspek.setText("");spBobot.setValue(1);spUrutan.setValue(1);cbAktif.setSelectedIndex(0);if(!katIds.isEmpty())cbKategori.setSelectedIndex(0);table.clearSelection();lblStatus.setText(" ");}
}
