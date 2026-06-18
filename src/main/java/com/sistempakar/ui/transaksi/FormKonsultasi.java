package com.sistempakar.ui.transaksi;

import com.sistempakar.db.DBConnection;
import com.sistempakar.engine.ForwardChaining;
import com.sistempakar.ui.MainFrame;
import com.sistempakar.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import com.sistempakar.util.ReportGenerator;

/**
 * Form Konsultasi – Transaksi Utama
 * Step 1: Pilih siswa & konselor
 * Step 2: Psikotest (35 pertanyaan)
 * Step 3: Hasil Forward Chaining + Catatan Konselor
 * Step 4: Nota / Laporan Hasil
 */
public class FormKonsultasi extends JPanel {

    private final MainFrame parent;
    private final int loginUserId;
    private final String loginNama;
    private final String loginRole;

    // Step navigation
    private CardLayout stepLayout;
    private JPanel stepContainer;
    private int currentStep = 0;
    private JButton btnNext;
    private JPanel questionsPanel;

    // Step 1 – Pilih Siswa & Konselor
    private JComboBox<String> cbSiswa, cbKonselor;
    private JTextArea taSiswaProfil;
    private List<Integer> siswaIds = new ArrayList<>(), konselorIds = new ArrayList<>();

    // Step 2 – Psikotest
    private Map<Integer, ButtonGroup>  answerGroups  = new LinkedHashMap<>();
    private Map<Integer, Integer>      pertanyaanIds = new LinkedHashMap<>();
    private Map<Integer, Map<String,Integer>> jawabanIdMap = new LinkedHashMap<>();
    private JLabel lblPsikStep;

    // Step 3 – Hasil
    private JTextArea taCatatanKonselor, taTipsBelajar, taKegiatanRek;
    private JPanel hasilPanel;
    private List<ForwardChaining.HasilRekomendasi> hasilRekList = new ArrayList<>();
    private ForwardChaining.Fakta faktaResult;

    // Step 4 – Nota
    private JPanel notaPanel;
    private int savedKonsultasiId = -1;
    private JLabel lblStatus;

    public FormKonsultasi(MainFrame parent, int uid, String uNama, String uRole) {
        this.parent = parent; this.loginUserId = uid; this.loginNama = uNama; this.loginRole = uRole;
        setOpaque(false); setLayout(new BorderLayout(0,16));
        setBorder(BorderFactory.createEmptyBorder(16,24,16,24));
        buildUI();
    }

    private void buildUI() {
        // Progress steps bar
        JPanel progressBar = buildProgressBar();

        // Step container
        stepLayout = new CardLayout();
        stepContainer = new JPanel(stepLayout); stepContainer.setOpaque(false);
        stepContainer.add(buildStep1(), "step1");
        
        // Step 2 container setup
        questionsPanel = new JPanel(); 
        questionsPanel.setLayout(new BoxLayout(questionsPanel,BoxLayout.Y_AXIS)); 
        questionsPanel.setOpaque(false);
        
        stepContainer.add(buildStep2(), "step2");
        stepContainer.add(buildStep3(), "step3");
        stepContainer.add(buildStep4(), "step4");

        // Nav buttons
        JPanel navRow = new JPanel(new BorderLayout()); navRow.setOpaque(false);
        lblStatus = new JLabel(" ", SwingConstants.LEFT); lblStatus.setFont(Theme.FONT_SMALL); lblStatus.setForeground(Theme.ACCENT_BLUE);
        JPanel btnRight = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btnRight.setOpaque(false);
        JButton btnBack = Theme.createSecondaryButton("← Kembali");
        btnNext = Theme.createPrimaryButton("Lanjut →");
        btnBack.addActionListener(e -> prevStep());
        btnNext.addActionListener(e -> nextStep(btnNext));
        btnRight.add(btnBack); btnRight.add(btnNext);
        navRow.add(lblStatus, BorderLayout.WEST); navRow.add(btnRight, BorderLayout.EAST);

        add(progressBar, BorderLayout.NORTH);
        add(stepContainer, BorderLayout.CENTER);
        add(navRow, BorderLayout.SOUTH);
    }

    private JPanel buildProgressBar() {
        JPanel p = new JPanel(new GridLayout(1,4,8,0)); p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        String[] steps = {"1  Pilih Siswa","2  Psikotest","3  Analisis & Catatan","4  Hasil & Nota"};
        Color[] colors = {Theme.ACCENT_BLUE, Theme.ACCENT_PURPLE, Theme.ACCENT_TEAL, Theme.ACCENT_ORANGE};
        for (int i=0;i<4;i++) {
            final int idx=i;
            JPanel step = new JPanel(){
                @Override protected void paintComponent(Graphics g){
                    super.paintComponent(g);
                    Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    Color c = idx<=currentStep ? colors[idx] : new Color(255,255,255,20);
                    g2.setColor(c); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    if(idx==currentStep){g2.setColor(new Color(255,255,255,30));g2.setStroke(new BasicStroke(2f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);}
                    g2.dispose();
                }
            };
            step.setOpaque(false); step.setLayout(new FlowLayout(FlowLayout.CENTER,6,8));
            JLabel lbl=new JLabel(steps[i]); lbl.setFont(new Font(Font.DIALOG,i==currentStep?Font.BOLD:Font.PLAIN,12));
            lbl.setForeground(i<=currentStep?Color.WHITE:Theme.TEXT_MUTED);
            step.add(lbl); p.add(step);
        }
        return p;
    }

    // ── STEP 1: SISWA & KONSELOR ──────────────────────────────
    private JPanel buildStep1() {
        JPanel p = new JPanel(new BorderLayout(16,16)); p.setOpaque(false);
        JPanel left = Theme.createGlassCard(); left.setLayout(new BorderLayout(0,16)); left.setPreferredSize(new Dimension(420,0));
        JLabel title = new JLabel("👨‍🎓  Pilih Siswa & Konselor"); title.setFont(Theme.FONT_SUBTITLE); title.setForeground(Theme.TEXT_WHITE);

        loadSiswaKonselor();
        JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false);
        GridBagConstraints g=new GridBagConstraints(); g.insets=new Insets(8,4,8,4); g.fill=GridBagConstraints.HORIZONTAL;

        g.gridx=0;g.gridy=0;g.weightx=0; JLabel sl=Theme.createLabel("Pilih Siswa *"); sl.setPreferredSize(new Dimension(120,25)); form.add(sl,g);
        g.gridx=1;g.weightx=1; form.add(cbSiswa,g);
        
        JButton btnRefreshData = Theme.createSecondaryButton("🔄");
        btnRefreshData.setToolTipText("Muat ulang daftar siswa & konselor");
        btnRefreshData.addActionListener(e -> {
            loadSiswaKonselor();
            lblStatus.setText("✅ Daftar siswa & konselor telah diperbarui.");
        });
        g.gridx=2;g.weightx=0; form.add(btnRefreshData,g);

        g.gridx=0;g.gridy=1;g.weightx=0; form.add(Theme.createLabel("Konselor / Guru BK *"),g);
        g.gridx=1;g.weightx=1; form.add(cbKonselor,g);

        cbSiswa.addActionListener(e -> loadSiswaProfil());

        taSiswaProfil = Theme.createTextArea(); taSiswaProfil.setRows(8); taSiswaProfil.setEditable(false);
        JScrollPane sp=Theme.createScrollPane(taSiswaProfil);

        left.add(title,BorderLayout.NORTH); left.add(form,BorderLayout.NORTH);

        JPanel fp=new JPanel(new BorderLayout(0,8)); fp.setOpaque(false);
        JLabel fl=new JLabel("👤  Profil Singkat Siswa"); fl.setFont(Theme.FONT_BOLD); fl.setForeground(Theme.ACCENT_BLUE);
        fp.add(fl,BorderLayout.NORTH); fp.add(sp,BorderLayout.CENTER);

        JPanel leftContent=new JPanel(new BorderLayout(0,12)); leftContent.setOpaque(false);
        leftContent.add(form,BorderLayout.NORTH); leftContent.add(fp,BorderLayout.CENTER);
        left.add(leftContent,BorderLayout.CENTER);

        // Info card
        JPanel info = Theme.createAccentCard(Theme.ACCENT_ORANGE);
        info.setLayout(new BorderLayout(0,12));
        JLabel it = new JLabel("ℹ️  Petunjuk Konsultasi"); it.setFont(Theme.FONT_BOLD); it.setForeground(Theme.ACCENT_ORANGE);
        JTextArea ia=new JTextArea("Langkah Konsultasi:\n\n1️⃣ Pilih siswa yang akan dikonsultasikan\n2️⃣ Pilih konselor / guru BK pendamping\n3️⃣ Siswa mengerjakan 35 pertanyaan psikotest\n4️⃣ Sistem menganalisis dengan Forward Chaining\n5️⃣ Konselor memberikan catatan & rekomendasi\n6️⃣ Cetak nota hasil konsultasi untuk siswa\n\n💡 Pastikan siswa sudah mengisi data nilai rapor di Form Data Siswa sebelum konsultasi.");
        ia.setEditable(false);ia.setWrapStyleWord(true);ia.setLineWrap(true);ia.setOpaque(false);ia.setFont(new Font(Font.DIALOG,Font.PLAIN,12));ia.setForeground(Theme.TEXT_SECONDARY);ia.setBorder(null);
        info.add(it,BorderLayout.NORTH); info.add(ia,BorderLayout.CENTER);

        p.add(left,BorderLayout.WEST); p.add(info,BorderLayout.CENTER);
        return p;
    }

    private void loadSiswaKonselor() {
        siswaIds.clear(); konselorIds.clear();
        List<String> sNama=new ArrayList<>(), kNama=new ArrayList<>();
        try{
            ResultSet rs=DBConnection.executeQuery("SELECT id,nis,nama,kelas FROM siswa ORDER BY nama");
            while(rs.next()){siswaIds.add(rs.getInt("id"));sNama.add("["+rs.getString("nis")+"] "+rs.getString("nama")+" – "+rs.getString("kelas"));}
            rs.getStatement().close();
            rs=DBConnection.executeQuery("SELECT id,nama,gelar,spesialisasi FROM konselor WHERE aktif=true ORDER BY nama");
            while(rs.next()){konselorIds.add(rs.getInt("id"));kNama.add(rs.getString("nama")+(rs.getString("gelar")!=null?", "+rs.getString("gelar"):"")+" ("+rs.getString("spesialisasi")+")");}
            rs.getStatement().close();
        }catch(Exception e){e.printStackTrace();}
        
        if (cbSiswa == null) {
            cbSiswa=Theme.createComboBox(sNama.toArray(new String[0]));
            cbKonselor=Theme.createComboBox(kNama.toArray(new String[0]));
        } else {
            cbSiswa.setModel(new DefaultComboBoxModel<>(sNama.toArray(new String[0])));
            cbKonselor.setModel(new DefaultComboBoxModel<>(kNama.toArray(new String[0])));
        }
    }

    private void loadSiswaProfil() {
        if (cbSiswa==null||siswaIds.isEmpty()) return;
        int idx=cbSiswa.getSelectedIndex(); if(idx<0)return;
        int sid=siswaIds.get(idx);
        try{
            ResultSet rs=DBConnection.executeQuery("SELECT * FROM siswa WHERE id=?",sid);
            if(rs.next()){
                StringBuilder sb=new StringBuilder();
                sb.append("Nama       : ").append(rs.getString("nama")).append("\n");
                sb.append("NIS        : ").append(rs.getString("nis")).append("\n");
                sb.append("Kelas      : ").append(rs.getString("kelas")).append("\n");
                sb.append("Jur. SMA   : ").append(rs.getString("jurusan_sma")).append("\n");
                sb.append("Cita-cita  : ").append(rs.getString("cita_cita")).append("\n\n");
                sb.append("📊 Nilai Rata-rata:\n");
                sb.append("  Matematika  : ").append(rs.getDouble("nilai_matematika")).append("\n");
                sb.append("  IPA         : ").append(rs.getDouble("nilai_ipa")).append("\n");
                sb.append("  IPS         : ").append(rs.getDouble("nilai_ips")).append("\n");
                sb.append("  B. Indonesia: ").append(rs.getDouble("nilai_bahasa_ind")).append("\n");
                sb.append("  B. Inggris  : ").append(rs.getDouble("nilai_bahasa_ing")).append("\n\n");
                sb.append("🎮 Hobi: ").append(rs.getString("hobi")!=null?rs.getString("hobi"):"-").append("\n");
                sb.append("🏆 Prestasi: ").append(rs.getString("prestasi")!=null?rs.getString("prestasi"):"-").append("\n");
                taSiswaProfil.setText(sb.toString());
            }
            rs.getStatement().close();
        }catch(Exception e){e.printStackTrace();}
    }

    // ── STEP 2: PSIKOTEST ─────────────────────────────────────
    private JPanel buildStep2() {
        JPanel outer = new JPanel(new BorderLayout(0,12)); outer.setOpaque(false);
        JLabel title=new JLabel("🧠  Mini Psikotest – Minat & Bakat"); title.setFont(Theme.FONT_SUBTITLE); title.setForeground(Theme.TEXT_WHITE);
        lblPsikStep=new JLabel("Memuat pertanyaan..."); lblPsikStep.setFont(Theme.FONT_SMALL); lblPsikStep.setForeground(Theme.TEXT_MUTED);

        JPanel hdr=new JPanel(new BorderLayout()); hdr.setOpaque(false);
        hdr.add(title,BorderLayout.WEST); hdr.add(lblPsikStep,BorderLayout.EAST);

        loadQuestions(questionsPanel);

        JScrollPane sp=Theme.createScrollPane(questionsPanel); sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outer.add(hdr,BorderLayout.NORTH); outer.add(sp,BorderLayout.CENTER);
        return outer;
    }

    private void loadQuestions(JPanel container) {
        answerGroups.clear(); pertanyaanIds.clear(); jawabanIdMap.clear();
        container.removeAll();
        try {
            ResultSet rs=DBConnection.executeQuery("SELECT pp.id,pp.kode,pp.pertanyaan,km.nama as knama,km.warna_hex FROM pertanyaan_psikotest pp JOIN kategori_minat km ON pp.kategori_id=km.id WHERE pp.aktif=true ORDER BY pp.urutan LIMIT 35");
            int no=1; String lastCat="";
            while(rs.next()){
                int pid=rs.getInt("id"); String kat=rs.getString("knama"); String pertanyaan=rs.getString("pertanyaan");
                if(!kat.equals(lastCat)){
                    JPanel catHeader=new JPanel(new FlowLayout(FlowLayout.LEFT,10,4)); catHeader.setOpaque(false); catHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
                    JLabel catLbl=new JLabel("📌  "+kat); catLbl.setFont(new Font(Font.DIALOG,Font.BOLD,13)); catLbl.setForeground(Theme.ACCENT_BLUE);
                    catHeader.add(catLbl); container.add(catHeader); lastCat=kat;
                }

                JPanel qCard=Theme.createGlassCard(10); qCard.setLayout(new BorderLayout(0,8)); qCard.setMaximumSize(new Dimension(Integer.MAX_VALUE,160));
                JLabel qLabel=new JLabel("<html><b>"+no+". </b>"+pertanyaan+"</html>"); qLabel.setFont(Theme.FONT_BODY); qLabel.setForeground(Theme.TEXT_PRIMARY);

                JPanel opts=new JPanel(new GridLayout(2,2,8,4)); opts.setOpaque(false);
                ButtonGroup bg=new ButtonGroup();
                Map<String,Integer> jawMap=new LinkedHashMap<>();

                ResultSet pj=DBConnection.executeQuery("SELECT id,huruf,teks_jawaban FROM pilihan_jawaban WHERE pertanyaan_id=? ORDER BY huruf",pid);
                while(pj.next()){
                    int jid=pj.getInt("id"); String huruf=pj.getString("huruf"); String teks=pj.getString("teks_jawaban");
                    jawMap.put(huruf,jid);
                    JRadioButton rb=new JRadioButton("<html><b>"+huruf+".</b> "+teks+"</html>"); rb.setActionCommand(huruf);
                    rb.setOpaque(false); rb.setForeground(Theme.TEXT_PRIMARY); rb.setFont(new Font(Font.DIALOG,Font.PLAIN,12));
                    rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); bg.add(rb); opts.add(rb);
                }
                pj.getStatement().close();

                pertanyaanIds.put(pid,pid); answerGroups.put(pid,bg); jawabanIdMap.put(pid,jawMap);
                qCard.add(qLabel,BorderLayout.NORTH); qCard.add(opts,BorderLayout.CENTER);
                container.add(qCard); container.add(Box.createVerticalStrut(8)); no++;
            }
            rs.getStatement().close();
            lblPsikStep.setText("Total "+answerGroups.size()+" pertanyaan | Jawab semua untuk melanjutkan");
        }catch(Exception e){e.printStackTrace();}
        container.revalidate(); container.repaint();
    }

    // ── STEP 3: HASIL FC + CATATAN ────────────────────────────
    private JPanel buildStep3() {
        JPanel outer=new JPanel(new BorderLayout(16,0)); outer.setOpaque(false);

        hasilPanel=new JPanel(); hasilPanel.setLayout(new BoxLayout(hasilPanel,BoxLayout.Y_AXIS)); hasilPanel.setOpaque(false);
        JScrollPane spLeft=Theme.createScrollPane(hasilPanel); spLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel right=Theme.createGlassCard(); right.setLayout(new BorderLayout(0,12)); right.setPreferredSize(new Dimension(380,0));
        JLabel rt=new JLabel("📝  Catatan Konselor"); rt.setFont(Theme.FONT_SUBTITLE); rt.setForeground(Theme.TEXT_WHITE);

        taCatatanKonselor=Theme.createTextArea(); taCatatanKonselor.setRows(5);
        taCatatanKonselor.putClientProperty("JTextArea.placeholderText","Tulis catatan/rekomendasi untuk siswa...");
        taTipsBelajar=Theme.createTextArea(); taTipsBelajar.setRows(5); taTipsBelajar.setEditable(false);
        taKegiatanRek=Theme.createTextArea(); taKegiatanRek.setRows(4); taKegiatanRek.setEditable(false);

        JPanel rightContent=new JPanel(new GridLayout(3,1,0,10)); rightContent.setOpaque(false);
        JPanel c1=Theme.createAccentCard(Theme.ACCENT_PURPLE); c1.setLayout(new BorderLayout(0,6));
        JLabel l1=new JLabel("💬 Catatan Konselor"); l1.setFont(Theme.FONT_BOLD); l1.setForeground(Theme.ACCENT_PURPLE);
        c1.add(l1,BorderLayout.NORTH); c1.add(Theme.createScrollPane(taCatatanKonselor),BorderLayout.CENTER);

        JPanel c2=Theme.createAccentCard(Theme.ACCENT_TEAL); c2.setLayout(new BorderLayout(0,6));
        JLabel l2=new JLabel("💡 Tips Belajar (Auto)"); l2.setFont(Theme.FONT_BOLD); l2.setForeground(Theme.ACCENT_TEAL);
        c2.add(l2,BorderLayout.NORTH); c2.add(Theme.createScrollPane(taTipsBelajar),BorderLayout.CENTER);

        JPanel c3=Theme.createAccentCard(Theme.ACCENT_ORANGE); c3.setLayout(new BorderLayout(0,6));
        JLabel l3=new JLabel("🎯 Rekomendasi Kegiatan"); l3.setFont(Theme.FONT_BOLD); l3.setForeground(Theme.ACCENT_ORANGE);
        c3.add(l3,BorderLayout.NORTH); c3.add(Theme.createScrollPane(taKegiatanRek),BorderLayout.CENTER);

        rightContent.add(c1); rightContent.add(c2); rightContent.add(c3);
        right.add(rt,BorderLayout.NORTH); right.add(rightContent,BorderLayout.CENTER);
        outer.add(spLeft,BorderLayout.CENTER); outer.add(right,BorderLayout.EAST);
        return outer;
    }

    private void runForwardChaining() {
        // Collect answers
        Map<Integer,Integer> jawabanMap=new LinkedHashMap<>();
        for(Map.Entry<Integer,ButtonGroup> e:answerGroups.entrySet()){
            int pid=e.getKey(); ButtonGroup bg=e.getValue();
            String sel=bg.getSelection()!=null?bg.getSelection().getActionCommand():null;
            if(sel!=null && jawabanIdMap.containsKey(pid)){Integer jid=jawabanIdMap.get(pid).get(sel);if(jid!=null)jawabanMap.put(pid,jid);}
        }

        // Build fakta from siswa nilai
        ForwardChaining.Fakta fakta=ForwardChaining.hitungSkorDariJawaban(jawabanMap);
        int sidx=cbSiswa.getSelectedIndex();
        if(sidx>=0&&!siswaIds.isEmpty()){
            int sid=siswaIds.get(sidx);
            try{
                ResultSet rs=DBConnection.executeQuery("SELECT nilai_matematika,nilai_ipa,nilai_ips,nilai_bahasa_ind,nilai_bahasa_ing,nilai_seni,jurusan_sma FROM siswa WHERE id=?",sid);
                if(rs.next()){fakta.nilaiMtk=rs.getDouble("nilai_matematika");fakta.nilaiIpa=rs.getDouble("nilai_ipa");fakta.nilaiIps=rs.getDouble("nilai_ips");fakta.nilaiBahInd=rs.getDouble("nilai_bahasa_ind");fakta.nilaiBahIng=rs.getDouble("nilai_bahasa_ing");fakta.nilaiSeni=rs.getDouble("nilai_seni");fakta.jurusanSma=rs.getString("jurusan_sma");}
                rs.getStatement().close();
            }catch(Exception ex){ex.printStackTrace();}
        }
        faktaResult=fakta;

        // Run FC
        ForwardChaining fc=new ForwardChaining();
        hasilRekList=fc.rekomendasikan(fakta);

        // Render results
        hasilPanel.removeAll();
        JLabel ht=new JLabel("🎓  Hasil Rekomendasi Jurusan (Forward Chaining)"); ht.setFont(Theme.FONT_SUBTITLE); ht.setForeground(Theme.TEXT_WHITE); ht.setAlignmentX(LEFT_ALIGNMENT); ht.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        hasilPanel.add(ht);

        // Score summary
        JPanel scoreCard=Theme.createAccentCard(Theme.ACCENT_BLUE); scoreCard.setLayout(new GridLayout(2,4,12,6)); scoreCard.setMaximumSize(new Dimension(Integer.MAX_VALUE,90));
        String[][] scores={{"💻",String.valueOf(fakta.skorTeknologi),"Teknologi"},{"🔬",String.valueOf(fakta.skorSains),"Sains"},{"🤝",String.valueOf(fakta.skorSosial),"Sosial"},{"🎨",String.valueOf(fakta.skorSeni),"Seni"},{"📊",String.valueOf(fakta.skorBisnis),"Bisnis"},{"📚",String.valueOf(fakta.skorBahasa),"Bahasa"},{"🏥",String.valueOf(fakta.skorKesehatan),"Kesehatan"},{"","",""}};
        for(String[] sc:scores){JPanel sp2=new JPanel(new FlowLayout(FlowLayout.CENTER,4,2));sp2.setOpaque(false);if(!sc[0].isEmpty()){JLabel ico=new JLabel(sc[0]);ico.setFont(new Font(Font.DIALOG,Font.PLAIN,16));JLabel sval=new JLabel(sc[1]);sval.setFont(new Font(Font.DIALOG,Font.BOLD,16));sval.setForeground(Theme.ACCENT_BLUE);JLabel sname=new JLabel(sc[2]);sname.setFont(Theme.FONT_SMALL);sname.setForeground(Theme.TEXT_MUTED);sp2.add(ico);sp2.add(sval);sp2.add(sname);}scoreCard.add(sp2);}
        scoreCard.setAlignmentX(LEFT_ALIGNMENT); hasilPanel.add(scoreCard); hasilPanel.add(Box.createVerticalStrut(10));

        Color[] rekColors={Theme.ACCENT_BLUE,Theme.ACCENT_PURPLE,Theme.ACCENT_TEAL,Theme.ACCENT_ORANGE,Theme.ACCENT_GREEN};
        int rank=1;
        for(ForwardChaining.HasilRekomendasi rek:hasilRekList){
            JPanel rekCard=Theme.createAccentCard(rekColors[Math.min(rank-1,4)]); rekCard.setLayout(new BorderLayout(0,8)); rekCard.setMaximumSize(new Dimension(Integer.MAX_VALUE,200));
            JPanel rankRow=new JPanel(new BorderLayout(8,0)); rankRow.setOpaque(false);
            JLabel rankBadge=Theme.createBadge("#"+rank,rekColors[Math.min(rank-1,4)]);
            JLabel jNama=new JLabel(rek.namaJurusan); jNama.setFont(new Font(Font.DIALOG,Font.BOLD,15)); jNama.setForeground(Theme.TEXT_WHITE);
            JLabel conf=Theme.createBadge(String.format("Confidence: %.1f%%",rek.confidence),Theme.SUCCESS);
            rankRow.add(rankBadge,BorderLayout.WEST); rankRow.add(jNama,BorderLayout.CENTER); rankRow.add(conf,BorderLayout.EAST);
            JLabel kat=new JLabel("Kategori: "+rek.kategori+" | Aturan: "+rek.kodeAturan); kat.setFont(Theme.FONT_SMALL); kat.setForeground(Theme.TEXT_MUTED);

            // Top univ
            JPanel univRow=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); univRow.setOpaque(false);
            int uc=0;
            for(ForwardChaining.HasilRekomendasi.RekomendasiUniv ru:rek.universitas){
                if(uc++>3) break;
                Color pc=ru.peluangPersonal>=15?Theme.SUCCESS:ru.peluangPersonal>=8?Theme.WARNING:Theme.DANGER;
                JLabel ul=Theme.createBadge(ru.namaUniv.replace("Universitas","Univ.")+String.format(" %.1f%%",ru.peluangPersonal),pc);
                univRow.add(ul);
            }
            JLabel univTitle=new JLabel("🏛️ Peluang Masuk PTN:"); univTitle.setFont(Theme.FONT_SMALL); univTitle.setForeground(Theme.TEXT_SECONDARY);
            JPanel univSection=new JPanel(new BorderLayout(0,4)); univSection.setOpaque(false);
            univSection.add(univTitle,BorderLayout.NORTH); univSection.add(univRow,BorderLayout.CENTER);

            rekCard.add(rankRow,BorderLayout.NORTH);
            JPanel body=new JPanel(new BorderLayout(0,6)); body.setOpaque(false);
            body.add(kat,BorderLayout.NORTH); body.add(univSection,BorderLayout.CENTER);
            rekCard.add(body,BorderLayout.CENTER);
            rekCard.setAlignmentX(LEFT_ALIGNMENT);
            hasilPanel.add(rekCard); hasilPanel.add(Box.createVerticalStrut(8)); rank++;
        }

        if(hasilRekList.isEmpty()){JLabel noResult=new JLabel("⚠️ Tidak ada rekomendasi yang cocok. Pastikan jawaban psikotest sudah lengkap.");noResult.setFont(Theme.FONT_BODY);noResult.setForeground(Theme.WARNING);hasilPanel.add(noResult);}

        // Auto-fill tips
        if(!hasilRekList.isEmpty()){
            taTipsBelajar.setText(ForwardChaining.generateTipsBelajar(hasilRekList.get(0)));
            taKegiatanRek.setText(ForwardChaining.generateRekomendasiKegiatan(hasilRekList.get(0)));
        }

        hasilPanel.revalidate(); hasilPanel.repaint();
    }

    // ── STEP 4: NOTA ──────────────────────────────────────────
    private JPanel buildStep4() {
        JPanel p=new JPanel(new BorderLayout(0,16)); p.setOpaque(false);
        notaPanel=new JPanel(new BorderLayout(16,16)); notaPanel.setOpaque(false);
        JScrollPane sp=Theme.createScrollPane(notaPanel);
        p.add(sp,BorderLayout.CENTER);
        return p;
    }

    private void buildNota() {
        notaPanel.removeAll();

        JPanel nota=Theme.createGlassCard(); nota.setLayout(new BorderLayout(0,16));

        // Header nota
        JPanel header=new JPanel(new BorderLayout(0,4)); header.setOpaque(false);
        JLabel school=new JLabel("SMAN 1 CONTOH KOTA", SwingConstants.CENTER); school.setFont(new Font(Font.DIALOG,Font.BOLD,18)); school.setForeground(Theme.TEXT_WHITE);
        JLabel sub=new JLabel("NOTA HASIL KONSULTASI KARIR & REKOMENDASI JURUSAN", SwingConstants.CENTER); sub.setFont(Theme.FONT_BODY); sub.setForeground(Theme.TEXT_SECONDARY);
        JSeparator sep=Theme.createSeparator();
        header.add(school,BorderLayout.NORTH); header.add(sub,BorderLayout.CENTER); header.add(sep,BorderLayout.SOUTH);

        // Siswa info
        JPanel info=new JPanel(new GridLayout(0,2,8,8)); info.setOpaque(false);
        int sidx=cbSiswa.getSelectedIndex();
        String sNama=sidx>=0&&cbSiswa.getItemCount()>0?(String)cbSiswa.getSelectedItem():"";
        String kNama=cbKonselor.getSelectedIndex()>=0&&cbKonselor.getItemCount()>0?(String)cbKonselor.getSelectedItem():"";
        String noKons="KNS-"+new SimpleDateFormat("yyyyMMdd-HHmm").format(new java.util.Date());

        addInfoRow(info,"No. Konsultasi:",noKons,Theme.ACCENT_BLUE);
        addInfoRow(info,"Tanggal:",new SimpleDateFormat("dd MMMM yyyy HH:mm",new Locale("id","ID")).format(new java.util.Date()),Theme.TEXT_PRIMARY);
        addInfoRow(info,"Nama Siswa:",sNama,Theme.TEXT_PRIMARY);
        addInfoRow(info,"Konselor / Guru BK:",kNama,Theme.TEXT_PRIMARY);

        // Hasil rekomendasi
        JPanel rekSection=Theme.createAccentCard(Theme.ACCENT_BLUE); rekSection.setLayout(new BorderLayout(0,12));
        JLabel rekTitle=new JLabel("🎓  HASIL REKOMENDASI JURUSAN"); rekTitle.setFont(new Font(Font.DIALOG,Font.BOLD,15)); rekTitle.setForeground(Theme.ACCENT_BLUE);
        JPanel rekList=new JPanel(new GridLayout(0,1,0,8)); rekList.setOpaque(false);
        Color[] cs={Theme.ACCENT_BLUE,Theme.ACCENT_PURPLE,Theme.ACCENT_TEAL,Theme.ACCENT_ORANGE,Theme.ACCENT_GREEN};
        int r=1;
        for(ForwardChaining.HasilRekomendasi rek:hasilRekList){
            JPanel rr=new JPanel(new BorderLayout(8,0)); rr.setOpaque(false);
            JLabel ri=Theme.createBadge("#"+r,cs[Math.min(r-1,4)]);
            JLabel rn=new JLabel(rek.namaJurusan+" – "+rek.kategori); rn.setFont(Theme.FONT_BOLD); rn.setForeground(Theme.TEXT_WHITE);
            JLabel rc=Theme.createBadge(String.format("%.1f%%",rek.confidence),Theme.SUCCESS);
            JPanel univs=new JPanel(new FlowLayout(FlowLayout.LEFT,4,2)); univs.setOpaque(false);
            for(int i=0;i<Math.min(3,rek.universitas.size());i++){
                ForwardChaining.HasilRekomendasi.RekomendasiUniv u=rek.universitas.get(i);
                Color pc=u.peluangPersonal>=15?Theme.SUCCESS:u.peluangPersonal>=8?Theme.WARNING:Theme.DANGER;
                univs.add(Theme.createBadge(u.namaUniv.replace("Universitas","Univ.")+String.format(" %.1f%%",u.peluangPersonal),pc));
            }
            JPanel lft=new JPanel(new BorderLayout(4,2)); lft.setOpaque(false); lft.add(rn,BorderLayout.NORTH); lft.add(univs,BorderLayout.CENTER);
            rr.add(ri,BorderLayout.WEST); rr.add(lft,BorderLayout.CENTER); rr.add(rc,BorderLayout.EAST);
            rekList.add(rr); r++;
        }
        rekSection.add(rekTitle,BorderLayout.NORTH); rekSection.add(rekList,BorderLayout.CENTER);

        // Catatan konselor
        JPanel catCard=Theme.createAccentCard(Theme.ACCENT_PURPLE); catCard.setLayout(new BorderLayout(0,8));
        JLabel catTitle=new JLabel("📝  Catatan Konselor"); catTitle.setFont(Theme.FONT_BOLD); catTitle.setForeground(Theme.ACCENT_PURPLE);
        JTextArea catTA=new JTextArea(taCatatanKonselor.getText().isEmpty()?"(Tidak ada catatan tambahan dari konselor.)":taCatatanKonselor.getText());
        catTA.setEditable(false); catTA.setWrapStyleWord(true); catTA.setLineWrap(true); catTA.setOpaque(false); catTA.setFont(new Font(Font.DIALOG,Font.PLAIN,13)); catTA.setForeground(Theme.TEXT_PRIMARY);
        catCard.add(catTitle,BorderLayout.NORTH); catCard.add(catTA,BorderLayout.CENTER);

        // Tips belajar
        JPanel tipsCard=Theme.createAccentCard(Theme.ACCENT_TEAL); tipsCard.setLayout(new BorderLayout(0,8));
        JLabel tipsTitle=new JLabel("💡  Tips Belajar & Rekomendasi Kegiatan"); tipsTitle.setFont(Theme.FONT_BOLD); tipsTitle.setForeground(Theme.ACCENT_TEAL);
        JTextArea tipsTA=new JTextArea(taTipsBelajar.getText()+"\n\n"+taKegiatanRek.getText());
        tipsTA.setEditable(false); tipsTA.setWrapStyleWord(true); tipsTA.setLineWrap(true); tipsTA.setOpaque(false); tipsTA.setFont(new Font(Font.DIALOG,Font.PLAIN,12)); tipsTA.setForeground(Theme.TEXT_PRIMARY);
        tipsCard.add(tipsTitle,BorderLayout.NORTH); tipsCard.add(tipsTA,BorderLayout.CENTER);

        // Action buttons
        JPanel actionRow=new JPanel(new FlowLayout(FlowLayout.CENTER,12,0)); actionRow.setOpaque(false);
        JButton btnSave=Theme.createSuccessButton("💾 Simpan Konsultasi");
        JButton btnPDF =Theme.createPrimaryButton("📄 Export PDF Lengkap");
        JButton btnNew =Theme.createSecondaryButton("🆕 Konsultasi Baru");
        btnSave.addActionListener(e->saveKonsultasi());
        btnPDF.addActionListener(e->exportNotaPDF());
        btnNew.addActionListener(e->resetAll());
        actionRow.add(btnSave); actionRow.add(btnPDF); actionRow.add(btnNew);

        JPanel content=new JPanel(); content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS)); content.setOpaque(false);
        content.add(header); content.add(Box.createVerticalStrut(12));
        content.add(info); content.add(Box.createVerticalStrut(12));
        content.add(rekSection); content.add(Box.createVerticalStrut(12));
        content.add(catCard); content.add(Box.createVerticalStrut(12));
        content.add(tipsCard);
        nota.add(content,BorderLayout.CENTER);
        nota.add(actionRow,BorderLayout.SOUTH);

        notaPanel.add(nota,BorderLayout.CENTER);
        notaPanel.revalidate(); notaPanel.repaint();
    }

    private void addInfoRow(JPanel p, String label, String value, Color valColor) {
        JLabel l=Theme.createBoldLabel(label); JLabel v=new JLabel(value); v.setFont(Theme.FONT_BODY); v.setForeground(valColor);
        p.add(l); p.add(v);
    }

    private void saveKonsultasi() {
        try {
            int sidx=cbSiswa.getSelectedIndex(), kidx=cbKonselor.getSelectedIndex();
            if(sidx<0||kidx<0){JOptionPane.showMessageDialog(this,"Pilih siswa dan konselor!","Validasi",JOptionPane.WARNING_MESSAGE);return;}
            int sid=siswaIds.get(sidx), kid=konselorIds.get(kidx);
            String noK="KNS-"+new SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
            Integer rekId=hasilRekList.isEmpty()?null:hasilRekList.get(0).jurusanId;
            Integer alt1=hasilRekList.size()<2?null:hasilRekList.get(1).jurusanId;
            Integer alt2=hasilRekList.size()<3?null:hasilRekList.get(2).jurusanId;

            savedKonsultasiId=DBConnection.executeInsertGetKey(
                "INSERT INTO konsultasi(no_konsultasi,siswa_id,konselor_id,skor_teknologi,skor_sains,skor_sosial,skor_seni,skor_bisnis,skor_bahasa,skor_kesehatan,rekomendasi_utama_id,rekomendasi_alt1_id,rekomendasi_alt2_id,catatan_konselor,tips_belajar,rekomendasi_kegiatan,status)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'selesai')",
                noK,sid,kid,faktaResult.skorTeknologi,faktaResult.skorSains,faktaResult.skorSosial,faktaResult.skorSeni,faktaResult.skorBisnis,faktaResult.skorBahasa,faktaResult.skorKesehatan,rekId,alt1,alt2,taCatatanKonselor.getText(),taTipsBelajar.getText(),taKegiatanRek.getText()
            );

            // Save univ recommendations
            for(int i=0;i<Math.min(3,hasilRekList.size());i++){
                ForwardChaining.HasilRekomendasi rek=hasilRekList.get(i);
                for(ForwardChaining.HasilRekomendasi.RekomendasiUniv ru:rek.universitas.subList(0,Math.min(2,rek.universitas.size()))){
                    DBConnection.executeUpdate("INSERT INTO rekomendasi_univ(konsultasi_id,universitas_jurusan_id,peluang_personal,rank_urutan)VALUES(?,?,?,?)",savedKonsultasiId,ru.univJurusanId,ru.peluangPersonal,i+1);
                }
            }

            JOptionPane.showMessageDialog(this,"✅ Konsultasi berhasil disimpan!\nNo: "+noK,"Sukses",JOptionPane.INFORMATION_MESSAGE);
            lblStatus.setText("✅ Konsultasi tersimpan – No: "+noK);
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Gagal menyimpan: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    private void exportNotaPDF() {
        if(savedKonsultasiId == -1) {
            JOptionPane.showMessageDialog(this, "Harap simpan konsultasi terlebih dahulu sebelum meng-export nota.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fc=new JFileChooser(); fc.setDialogTitle("Simpan Nota Konsultasi PDF Lengkap");
        String sNama = cbSiswa.getSelectedIndex()>=0 ? cbSiswa.getSelectedItem().toString().replaceAll("\\[.*?\\] ", "") : "Siswa";
        fc.setSelectedFile(new java.io.File("Laporan_Detail_" + sNama.replace(" ","_") + "_" + new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".pdf"));
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
            try {
                // Call the advanced ReportGenerator instead of basic nota
                ReportGenerator.generatePremiumReport(savedKonsultasiId, fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this,"Laporan lengkap PDF berhasil disimpan!","Sukses",JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().open(fc.getSelectedFile());
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this,"Gagal export PDF: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── NAVIGATION ────────────────────────────────────────────
    private void nextStep(JButton btnNext) {
        if(currentStep==0){
            if(cbSiswa==null||cbSiswa.getSelectedIndex()<0){JOptionPane.showMessageDialog(this,"Pilih siswa terlebih dahulu!","Validasi",JOptionPane.WARNING_MESSAGE);return;}
            if(cbKonselor==null||cbKonselor.getSelectedIndex()<0){JOptionPane.showMessageDialog(this,"Pilih konselor terlebih dahulu!","Validasi",JOptionPane.WARNING_MESSAGE);return;}
            loadQuestions(questionsPanel);
            currentStep=1; stepLayout.show(stepContainer,"step2"); refreshProgress();
        } else if(currentStep==1){
            // Validate all answered
            int unanswered=0; for(ButtonGroup bg:answerGroups.values()) if(bg.getSelection()==null) unanswered++;
            if(unanswered>0){int c=JOptionPane.showConfirmDialog(this,unanswered+" pertanyaan belum dijawab.\nLanjutkan tetap?","Konfirmasi",JOptionPane.YES_NO_OPTION);if(c!=JOptionPane.YES_OPTION)return;}
            lblStatus.setText("⚙️ Sedang menganalisis dengan Forward Chaining...");
            new SwingWorker<Void,Void>(){
                @Override protected Void doInBackground(){runForwardChaining();return null;}
                @Override protected void done(){currentStep=2;stepLayout.show(stepContainer,"step3");refreshProgress();lblStatus.setText(" ");}
            }.execute();
        } else if(currentStep==2){
            if(hasilRekList.isEmpty()){JOptionPane.showMessageDialog(this,"Hasil rekomendasi kosong!","Error",JOptionPane.ERROR_MESSAGE);return;}
            buildNota(); currentStep=3; stepLayout.show(stepContainer,"step4"); refreshProgress();
            btnNext.setText("✅ Selesai");
        } else {
            if(savedKonsultasiId == -1) {
                int c = JOptionPane.showConfirmDialog(this, "Data konsultasi belum disimpan. Ingin simpan sekarang?", "Konfirmasi", JOptionPane.YES_NO_CANCEL_OPTION);
                if (c == JOptionPane.YES_OPTION) { saveKonsultasi(); }
                else if (c == JOptionPane.CANCEL_OPTION) return;
            }
            resetAll();
        }
    }

    private void prevStep() {
        if(currentStep==0)return;
        currentStep--;
        stepLayout.show(stepContainer,new String[]{"step1","step2","step3","step4"}[currentStep]);
        refreshProgress();
        if(currentStep < 3) btnNext.setText("Lanjut →");
    }

    private void refreshProgress() {
        // Repaint top section
        JPanel topBar=(JPanel)getComponent(0);
        remove(0); add(buildProgressBar(),BorderLayout.NORTH,0); revalidate(); repaint();
    }

    private void resetAll() {
        currentStep=0; savedKonsultasiId=-1; hasilRekList.clear(); faktaResult=null;
        taCatatanKonselor.setText(""); taTipsBelajar.setText(""); taKegiatanRek.setText("");
        taSiswaProfil.setText("");
        answerGroups.clear(); pertanyaanIds.clear(); jawabanIdMap.clear();
        
        loadSiswaKonselor();
        if (cbSiswa != null && cbSiswa.getItemCount() > 0) cbSiswa.setSelectedIndex(-1);
        if (cbKonselor != null && cbKonselor.getItemCount() > 0) cbKonselor.setSelectedIndex(-1);
        
        // Clear results panel
        if (hasilPanel != null) {
            hasilPanel.removeAll();
            hasilPanel.revalidate();
            hasilPanel.repaint();
        }
        
        if (btnNext != null) btnNext.setText("Lanjut →");

        stepLayout.show(stepContainer,"step1"); 
        refreshProgress();
        lblStatus.setText(" ");
    }
}
