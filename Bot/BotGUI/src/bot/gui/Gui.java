package bot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import bot.WhatsappGui;
import bot.cli.Cli;
import bot.csv.OpenAndSaveCSV;
import bot.csv.ReadCSV;
import bot.gui.elements.PlaceholderText;

public class Gui extends JFrame 
{
    private JCheckBox uploadModeCheckBox;
    private JPanel fileUploadPanel; // Dropzone
    private JPanel menuEntryPanel; 
    
    private PlaceholderText tarihField;
    private LocalDate currentDate; 
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    private List<PlaceholderText> kahvaltiFields; 
    private List<PlaceholderText> aksamYemegiFields;

    private File loadedCSVFile = null;

    private final OpenAndSaveCSV csvHandler = new OpenAndSaveCSV();
    private final ReadCSV csvReader = new ReadCSV();
    
    private List<String> getKahvaltiData() {
        List<String> data = new ArrayList<>();
        for (PlaceholderText field : kahvaltiFields) {
            data.add(field.getText());
        }
        return data;
    }

    private List<String> getAksamYemegiData() {
        List<String> data = new ArrayList<>();
        for (PlaceholderText field : aksamYemegiFields) {
            data.add(field.getText());
        }
        return data;
    }
    
    private final String[] SABIT_KAHVALTI = {
        "Siyah/Yeşil Zeytin", "Çeyrek Ekmek", "500 mL Su", "Çay/Bitki Çayı"
    };
    private final int DUZENLENEBILIR_KAHVALTI_SAYISI = 4;
    private final String[] SABIT_AKSAM_YEMEGI = {
        "Çeyrek Ekmek", "500 mL Su"
    };
    private final int DUZENLENEBILIR_AKSAM_SAYISI = 7;
    private final Map<String, Integer> AKŞAM_YEMEĞİ_YAPISI = new LinkedHashMap<>() {{
        put("Çorba 1:", 1); put("Çorba 2:", 0); 
        put("Ana Yemek 1:", 1); put("Ana Yemek 2:", 0);
        put("Pilav/Makarna:", 1); put("Salata/Meze:", 1);
        put("Tatlı/Meyve:", 1); 
    }};

    public Gui() 
    {
        this.currentDate = LocalDate.now();
        
        setTitle("KYK Yemek Listesi Girişi"); 
        setSize(600, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        // --- RESİM YÜKLEME KISMI (Düzeltildi) ---
        try {
            // 1. Önce "/images/" klasörünü dene (Doğru olması gereken)
            URL iconURL = getClass().getResource("/images/AppLogo.png");
            
            // 2. Bulamazsa kök dizini dene (Bazen Maven dışarı atabiliyor)
            if (iconURL == null) {
                iconURL = getClass().getResource("/AppLogo.png");
            }

            if (iconURL != null) {
                Image originalImage = new ImageIcon(iconURL).getImage(); 
                Image scaledImage = originalImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                this.setIconImage(scaledImage); 
            } else {
                System.err.println("UYARI: AppLogo.png bulunamadı! (/images/ ve / kök dizini kontrol edildi)");
            }
        } catch (Exception e) {
            System.err.println("HATA: Uygulama ikonu yüklenemedi: " + e.getMessage());
        }
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); 
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); 

        JPanel ustBolumFrame = createTarihBolumu(); 
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        mainPanel.add(ustBolumFrame, gbc);

        JPanel menuFrame = createYemekMenuleriBolumu();
        
        gbc.gridy = 1;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH; 
        mainPanel.add(menuFrame, gbc);

        // Navigasyon Entegrasyonu (Ok Tuşları)
        setupArrowNavigation();

        add(mainPanel);
        setVisible(true);
        
        updateTarihField(this.currentDate);
        updateMenuMode(); 
    }
    
    private void updateTarihField(LocalDate date) {
        String formattedDate = date.format(DATE_FORMATTER);
        tarihField.setText(formattedDate);
        this.currentDate = date;
        loadMenuForCurrentDate(); 
    }
    
    private void loadMenuForCurrentDate() {
        String tarih = tarihField.getText();
        List<String> menuData = csvReader.getMenuByDate(tarih);
        fillMenuFields(menuData);
    }
    
    private JPanel createTarihBolumu() {
        JPanel ustBolumFrame = new JPanel(new GridBagLayout()); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        
        // 1. Tarih Alanı
        tarihField = new PlaceholderText(""); 
        tarihField.setColumns(10);
        tarihField.setEditable(true); 
        tarihField.addActionListener(e -> {
            try {
                LocalDate inputDate = LocalDate.parse(tarihField.getText(), DATE_FORMATTER);
                updateTarihField(inputDate);
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Geçersiz tarih formatı! Lütfen DD-MM-YYYY formatını kullanın.", "Hata", JOptionPane.ERROR_MESSAGE);
                updateTarihField(currentDate); 
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; 
        ustBolumFrame.add(tarihField, gbc); 

        // 2. Butonlar
        JButton prevButton = new JButton("<");
        prevButton.setMargin(new Insets(1, 4, 1, 4));
        prevButton.addActionListener(e -> updateTarihField(currentDate.minusDays(1)));
        gbc.gridx = 1; gbc.gridy = 0; 
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        ustBolumFrame.add(prevButton, gbc);
        
        JButton nextButton = new JButton(">");
        nextButton.setMargin(new Insets(1, 4, 1, 4));
        nextButton.addActionListener(e -> updateTarihField(currentDate.plusDays(1)));
        gbc.gridx = 2; gbc.gridy = 0; 
        ustBolumFrame.add(nextButton, gbc);
        
        // 4. Checkbox
        uploadModeCheckBox = new JCheckBox("CSV'den Menü Yükle");
        uploadModeCheckBox.addActionListener(e -> updateMenuMode());
        gbc.gridx = 3; gbc.gridy = 0;
        ustBolumFrame.add(uploadModeCheckBox, gbc);

        // 5. WhatsApp Butonu
        JButton whatsappButton = new JButton();
        
        try {
            // --- BOT LOGOSU İÇİN DE ÇİFT KONTROL ---
            URL iconURL = getClass().getResource("/images/BotLogo.png");
            if (iconURL == null) {
                iconURL = getClass().getResource("/BotLogo.png");
            }

            if (iconURL != null) {
                Image iconImage = new ImageIcon(iconURL).getImage();
                Image scaledIcon = iconImage.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                whatsappButton.setIcon(new ImageIcon(scaledIcon));
                whatsappButton.setMargin(new Insets(1, 1, 1, 1)); 
                whatsappButton.setText("Botu Başlat"); 
            } else {
                whatsappButton.setText("WA Bot"); 
                System.err.println("UYARI: BotLogo.png bulunamadı.");
            }
        } catch (Exception e) {
            whatsappButton.setText("WA Bot Hata");
            e.printStackTrace();
        }
        
        whatsappButton.addActionListener(e -> {
            this.setVisible(false);
            new WhatsappGui(this); 
        });

        gbc.gridx = 4; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        ustBolumFrame.add(whatsappButton, gbc);

        return ustBolumFrame;
    }

    private JPanel createYemekMenuleriBolumu() {
        JPanel menuFrame = new JPanel(new GridBagLayout());
        menuFrame.setBorder(BorderFactory.createTitledBorder("Günlük Menü"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; 
        
        menuEntryPanel = createMenuEntryPanel(); 
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weighty = 1.0; 
        menuFrame.add(menuEntryPanel, gbc);
        
        fileUploadPanel = createFileUploadDropzone();
        gbc.gridx = 0; gbc.gridy = 1; 
        gbc.weighty = 1.0; 
        fileUploadPanel.setVisible(false); 
        menuFrame.add(fileUploadPanel, gbc);

        JButton kaydetButton = new JButton("Menüyü CSV Olarak Kaydet (Gönderim WA Bot panelinden yapılır)");
        kaydetButton.addActionListener(e -> {
            String csvFilePath = null; 
            try {
                if (uploadModeCheckBox.isSelected()) {
                    if (loadedCSVFile == null) {
                        JOptionPane.showMessageDialog(this, "Lütfen önce bir CSV dosyasını sürükleyip bırakın.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    csvFilePath = csvHandler.getLoadedCSVPath(loadedCSVFile);
                    System.out.println("Yüklenen CSV kullanılacak: " + csvFilePath);
                } else {
                    String tarih = tarihField.getText();
                    if (tarih.isEmpty() || tarih.equals(tarihField.getPlaceholder())) {
                        JOptionPane.showMessageDialog(this, "Lütfen bir tarih giriniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    csvFilePath = csvHandler.saveManualDataToCSV(tarih, getKahvaltiData(), getAksamYemegiData());
                    System.out.println("Manuel giriş CSV olarak kaydedildi.");
                }
                JOptionPane.showMessageDialog(this, 
                    "Menü başarıyla CSV dosyasına kaydedildi. WhatsApp üzerinden göndermek için 'Botu Başlat' butonunu kullanın.", 
                    "Kaydedildi", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Hata: CSV kaydı sırasında bir sorun oluştu.\n" + ex.getMessage(), "İşlem Hatası", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.gridx = 0; gbc_button.gridy = 2; 
        gbc_button.weightx = 1.0;
        gbc_button.weighty = 0.0;
        gbc_button.fill = GridBagConstraints.HORIZONTAL;
        gbc_button.insets = new Insets(10, 5, 5, 5); 
        menuFrame.add(kaydetButton, gbc_button);

        return menuFrame;
    }
    
    private JPanel createMenuEntryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; 

        JLabel kahvaltiLabel = new JLabel("Kahvaltı Menüsü:");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.0;
        panel.add(kahvaltiLabel, gbc);

        JPanel kahvaltiFrame = createKahvaltiBolumu();
        gbc.gridx = 0; gbc.gridy = 1; gbc.weighty = 0.0; 
        panel.add(kahvaltiFrame, gbc);

        JLabel aksamYemegiLabel = new JLabel("Akşam Yemeği Menüsü:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.weighty = 0.0;
        panel.add(aksamYemegiLabel, gbc);

        JPanel aksamYemegiContent = createAksamYemegiBolumu();
        JScrollPane aksamYemegiScrollPane = new JScrollPane(
            aksamYemegiContent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        aksamYemegiScrollPane.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 0; gbc.gridy = 3; gbc.weighty = 1.0; 
        panel.add(aksamYemegiScrollPane, gbc);

        return panel;
    }

    private JPanel createFileUploadDropzone() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("CSV Dosyası Yükle (Sürükle-Bırak)"),
            BorderFactory.createLineBorder(Color.GRAY, 2, true) 
        ));
        
        JTextArea dropText = new JTextArea("CSV Dosyasını buraya sürükleyip bırakın (Tarih, Kahvaltı..., Akşam Yemeği... formatında olmalıdır).");
        dropText.setEditable(false);
        dropText.setLineWrap(true);
        dropText.setWrapStyleWord(true);
        dropText.setBackground(new Color(240, 255, 240)); 
        dropText.setForeground(Color.DARK_GRAY);
        
        JScrollPane scrollPane = new JScrollPane(dropText);
        scrollPane.setPreferredSize(new Dimension(500, 150)); 
        panel.add(scrollPane, BorderLayout.CENTER);

        panel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) return false;
                try {
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (files.isEmpty()) return false;
                    File droppedFile = files.get(0);
                    if (droppedFile.getName().toLowerCase().endsWith(".csv")) {
                        loadedCSVFile = droppedFile; 
                        dropText.setText("YÜKLENDİ: " + droppedFile.getAbsolutePath() + "\n\nŞimdi 'Kaydet' butonuna basarak menüyü yükleyebilirsiniz.");
                        return true;
                    } else {
                        loadedCSVFile = null;
                        dropText.setText("HATA: Lütfen sadece CSV formatında bir dosya yükleyin.");
                        return false;
                    }
                } catch (Exception ex) {
                    loadedCSVFile = null;
                    dropText.setText("Yükleme Hatası: " + ex.getMessage());
                    return false;
                }
            }
        });
        return panel;
    }

    private void updateMenuMode() {
        boolean uploadModeSelected = uploadModeCheckBox.isSelected();
        if (menuEntryPanel != null) menuEntryPanel.setVisible(!uploadModeSelected);
        fileUploadPanel.setVisible(uploadModeSelected);
        revalidate(); 
        repaint();
    }

    private JPanel createKahvaltiBolumu() {
        JPanel kahvaltiFrame = new JPanel(); 
        kahvaltiFrame.setLayout(new BoxLayout(kahvaltiFrame, BoxLayout.Y_AXIS));
        kahvaltiFields = new ArrayList<>();
        
        for (int i = 0; i < DUZENLENEBILIR_KAHVALTI_SAYISI; i++) {
            PlaceholderText entry = new PlaceholderText("Kahvaltı " + (i + 1)); 
            entry.setAlignmentX(Component.LEFT_ALIGNMENT);
            kahvaltiFrame.add(entry);
            kahvaltiFields.add(entry);
            if (i < DUZENLENEBILIR_KAHVALTI_SAYISI - 1) kahvaltiFrame.add(Box.createVerticalStrut(5));
        }
        kahvaltiFrame.add(Box.createVerticalStrut(10));
        for (int i = 0; i < SABIT_KAHVALTI.length; i++) {
            PlaceholderText fixedEntry = new PlaceholderText(""); 
            fixedEntry.setText(SABIT_KAHVALTI[i]);
            fixedEntry.setEditable(false);
            fixedEntry.setBackground(UIManager.getColor("control")); 
            fixedEntry.setAlignmentX(Component.LEFT_ALIGNMENT);
            kahvaltiFrame.add(fixedEntry);
            kahvaltiFields.add(fixedEntry);
            if (i < SABIT_KAHVALTI.length - 1) kahvaltiFrame.add(Box.createVerticalStrut(5));
        }
        return kahvaltiFrame;
    }
    
    private JPanel createAksamYemegiBolumu() {
        JPanel aksamYemegiContent = new JPanel(new GridBagLayout()); 
        aksamYemegiFields = new ArrayList<>();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        int row = 0;

        for (Map.Entry<String, Integer> entry : AKŞAM_YEMEĞİ_YAPISI.entrySet()) {
            gbc.insets = new Insets((entry.getValue() == 1) ? 10 : 1, 5, 1, 5); 
            PlaceholderText field = new PlaceholderText(entry.getKey().replace(":", "").trim());
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 1.0;
            aksamYemegiContent.add(field, gbc);
            aksamYemegiFields.add(field);
            row++;
        }
        
        gbc.insets = new Insets(10, 5, 1, 5); 
        for (int i = 0; i < SABIT_AKSAM_YEMEGI.length; i++) {
            PlaceholderText fixedEntry = new PlaceholderText(""); 
            fixedEntry.setText(SABIT_AKSAM_YEMEGI[i]);
            fixedEntry.setEditable(false);
            fixedEntry.setBackground(UIManager.getColor("control")); 
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 1.0;
            aksamYemegiContent.add(fixedEntry, gbc);
            aksamYemegiFields.add(fixedEntry);
            gbc.insets = new Insets(5, 5, 1, 5); 
            row++;
        }

        gbc.gridy = row; gbc.weighty = 1.0; gbc.gridx = 0; gbc.gridwidth = 1; 
        aksamYemegiContent.add(Box.createVerticalGlue(), gbc);
        return aksamYemegiContent;
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--bot")) {
            String grupAdi = (args.length > 1) ? args[1] : null;
            if (grupAdi == null) {
                System.err.println("HATA: Grup adı belirtilmedi!");
                System.exit(1);
            }
            Cli.calistir(grupAdi);
        } else {
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) { e.printStackTrace(); }
                new Gui();
            });
        }
    }
    
    private void fillMenuFields(List<String> menuData) {
        if (menuData.isEmpty()) {
            clearAllMenuFields();
            return;
        }
        int dataIndex = 0;
        for (PlaceholderText field : kahvaltiFields) {
            if (dataIndex < menuData.size()) field.setText(menuData.get(dataIndex++));
        }
        for (PlaceholderText field : aksamYemegiFields) {
            if (dataIndex < menuData.size()) field.setText(menuData.get(dataIndex++));
        }
    }

    private void clearAllMenuFields() {
        for (int i = 0; i < kahvaltiFields.size(); i++) {
            kahvaltiFields.get(i).setText(i < DUZENLENEBILIR_KAHVALTI_SAYISI ? "" : SABIT_KAHVALTI[i - DUZENLENEBILIR_KAHVALTI_SAYISI]);
        }
        for (int i = 0; i < aksamYemegiFields.size(); i++) {
            aksamYemegiFields.get(i).setText(i < DUZENLENEBILIR_AKSAM_SAYISI ? "" : SABIT_AKSAM_YEMEGI[i - DUZENLENEBILIR_AKSAM_SAYISI]);
        }
    }

    private void setupArrowNavigation() {
        List<PlaceholderText> allFields = new ArrayList<>();
        if (kahvaltiFields != null) allFields.addAll(kahvaltiFields);
        if (aksamYemegiFields != null) allFields.addAll(aksamYemegiFields);
        bot.gui.util.FocusNavigator.enableArrowNavigation(allFields);
    }
}