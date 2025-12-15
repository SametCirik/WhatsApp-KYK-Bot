package bot;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image; // Navigasyon sınıfını import ettik
import java.awt.Insets;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon; // ArrayList importu eklendi
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import bot.csv.ReadCSV;
import bot.gui.Gui;
import bot.gui.elements.PlaceholderText;
import bot.gui.util.FocusNavigator;

// Bu yeni arayüz, sadece WhatsApp otomasyon ayarlarını yönetmek için kullanılır.
public class WhatsappGui extends JFrame 
{
    private PlaceholderText tarihField;
    private LocalDate currentDate;
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private PlaceholderText whatsappLinkField;
    private PlaceholderText scheduledDateField;
    private PlaceholderText scheduledTimeField;
    private JButton gonderButton;
    private JButton scheduleButton;
    private JTextArea messagePreviewArea; 
    private final ReadCSV csvReader = new ReadCSV();
    private Gui mainGuiInstance; 
    private Timer countdownTimer;

    // Constructor
    public WhatsappGui(Gui mainGuiInstance) 
    {
        this.mainGuiInstance = mainGuiInstance;
        this.currentDate = LocalDate.now();

        setTitle("WhatsApp Otomasyon Ayarları"); 
        setSize(550, 700); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null); 
        
        // İkon ayarları (DÜZELTİLDİ: /bot/images/ yolunu kullanır)
        try {
            URL iconURL = getClass().getResource("/bot/images/BotLogo.png");
            if (iconURL == null) {
                // Eğer /bot/images/ altında yoksa kök dizini dene (Yedek)
                iconURL = getClass().getResource("/BotLogo.png");
            }
            
            if (iconURL != null) {
                Image originalImage = new ImageIcon(iconURL).getImage(); 
                Image scaledImage = originalImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                this.setIconImage(scaledImage); 
            } 
        } catch (Exception e) { /* İkon bulunamazsa sorun değil */ }
        
        // 1. Ana Paneli Oluştur
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); 
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); 

        // 2. Üst Bölümü (Tarih ve Geri Butonu) Oluştur ve Ekle
        JPanel ustBolumFrame = createUstBolum(); 
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        mainPanel.add(ustBolumFrame, gbc);

        // 3. İçerik ve Ayarlar Bölümü
        JPanel contentPanel = createContentPanel();
        gbc.gridy = 1;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH; 
        mainPanel.add(contentPanel, gbc);
        
        // 4. GÖNDER Butonu (Artık sadece Şimdi Gönder)
        gonderButton = new JButton("Mesajı Şimdi Gönder"); 
        gonderButton.addActionListener(e -> gonderAction(false));
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.gridx = 0; gbc_button.gridy = 2; 
        gbc_button.weightx = 1.0;
        gbc_button.weighty = 0.0;
        gbc_button.fill = GridBagConstraints.HORIZONTAL;
        gbc_button.insets = new Insets(10, 5, 5, 5); 
        mainPanel.add(gonderButton, gbc_button);
        
        // --- NAVİGASYON ENTEGRASYONU ---
        setupNavigation();

        add(mainPanel);
        setVisible(true);
        
        updateTarihField(this.currentDate); 
    }
    
    // --- NAVİGASYON METODU ---
    private void setupNavigation() {
        List<PlaceholderText> fields = new ArrayList<>();
        
        // Sırasıyla hangi alanlara gidileceğini belirliyoruz
        if (tarihField != null) fields.add(tarihField);
        if (whatsappLinkField != null) fields.add(whatsappLinkField);
        if (scheduledDateField != null) fields.add(scheduledDateField);
        if (scheduledTimeField != null) fields.add(scheduledTimeField);
        
        FocusNavigator.enableArrowNavigation(fields);
    }

    /**
     * Tarih navigasyonu ve ana menüye geri dönüş butonunu içeren üst paneli oluşturur.
     */
    private JPanel createUstBolum() {
        JPanel ustBolumFrame = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        tarihField = new PlaceholderText(""); 
        tarihField.setColumns(10);
        tarihField.setEditable(true); 
        tarihField.addActionListener(e -> {
            try {
                LocalDate inputDate = LocalDate.parse(tarihField.getText(), DATE_FORMATTER);
                updateTarihField(inputDate);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Geçersiz tarih formatı! DD-MM-YYYY kullanın.", "Hata", JOptionPane.ERROR_MESSAGE);
                updateTarihField(currentDate); 
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; 
        ustBolumFrame.add(tarihField, gbc); 

        JButton prevButton = new JButton("<");
        prevButton.setMargin(new Insets(1, 4, 1, 4));
        prevButton.addActionListener(e -> updateTarihField(currentDate.minusDays(1))); 
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        ustBolumFrame.add(prevButton, gbc);
        
        JButton nextButton = new JButton(">");
        nextButton.setMargin(new Insets(1, 4, 1, 4));
        nextButton.addActionListener(e -> updateTarihField(currentDate.plusDays(1))); 
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        ustBolumFrame.add(nextButton, gbc);

        JButton geriDonButton = new JButton();
        try {
            // DÜZELTİLDİ: /bot/images/AppLogo.png
            URL iconURL = getClass().getResource("/bot/images/AppLogo.png"); 
            if (iconURL == null) {
                iconURL = getClass().getResource("/AppLogo.png");
            }

            if (iconURL != null) {
                Image iconImage = new ImageIcon(iconURL).getImage();
                Image scaledIcon = iconImage.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                geriDonButton.setIcon(new ImageIcon(scaledIcon));
                geriDonButton.setMargin(new Insets(1, 1, 1, 1)); 
                geriDonButton.setText("Listeye Dön");
            } else {
                geriDonButton.setText("Ana Menü"); 
            }
        } catch (Exception e) {
            geriDonButton.setText("Ana Menü");
        }
        
        geriDonButton.addActionListener(e -> {
            this.dispose();
            if (mainGuiInstance != null) {
                mainGuiInstance.setVisible(true);
            }
        });

        gbc.gridx = 3; gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        ustBolumFrame.add(geriDonButton, gbc);

        return ustBolumFrame;
    }
    
    /**
     * Mesaj önizlemesi ve otomasyon ayarları panellerini oluşturur.
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // --- 1. Mesaj Önizlemesi ---
        JLabel previewLabel = new JLabel("WhatsApp Mesaj Önizlemesi:");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(previewLabel, gbc);

        messagePreviewArea = new JTextArea(10, 40);
        messagePreviewArea.setEditable(false);
        messagePreviewArea.setLineWrap(true);
        messagePreviewArea.setWrapStyleWord(true);
        messagePreviewArea.setFont(new Font("SansSerif", Font.PLAIN, 12)); 

        messagePreviewArea.setBorder(BorderFactory.createLineBorder(UIManager.getColor("controlShadow")));
        JScrollPane scrollPane = new JScrollPane(messagePreviewArea);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weighty = 0.6; 
        panel.add(scrollPane, gbc);
        
        // --- 2. Ayarlar Paneli ---
        JPanel settingsPanel = createSettingsPanel();
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Otomasyon Ayarları"));
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weighty = 0.4; 
        panel.add(settingsPanel, gbc);
        
        return panel;
    }

    /**
     * Otomasyon ayarları için giriş alanlarını ve ZAMANLAYICI butonunu oluşturur.
     */
    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0; 
        int row = 0;
        
        // 1. WhatsApp Kanal Linki
        whatsappLinkField = new PlaceholderText("WhatsApp Kanal/Grup Adı (örn: ... Erkek Öğrenci Yurdu)");
        settingsPanel.add(whatsappLinkField, gbc(0, row++));
        
        // 2. Zamanlama Başlığı
        JLabel scheduleLabel = new JLabel("Zamanlama:");
        gbc.gridx = 0; gbc.gridy = row++; 
        gbc.anchor = GridBagConstraints.WEST;
        settingsPanel.add(scheduleLabel, gbc);
        
        // 4. Zamanlama Tarih ve Saat
        JPanel scheduleInputPanel = new JPanel(new GridBagLayout());
        
        scheduledDateField = new PlaceholderText("GG-AA-YYYY");
        scheduledTimeField = new PlaceholderText("SS:DD (24 Saat)");
        
        // Tarih ve Saat Girdileri
        scheduleInputPanel.add(new JLabel("Tarih:"), gbc(0, 0, 0.0, GridBagConstraints.WEST));
        scheduleInputPanel.add(scheduledDateField, gbc(1, 0, 1.0, GridBagConstraints.HORIZONTAL));
        scheduleInputPanel.add(Box.createHorizontalStrut(10), gbc(2, 0, 0.0, GridBagConstraints.NONE)); // Boşluk
        scheduleInputPanel.add(new JLabel("Saat:"), gbc(3, 0, 0.0, GridBagConstraints.WEST));
        scheduleInputPanel.add(scheduledTimeField, gbc(4, 0, 1.0, GridBagConstraints.HORIZONTAL));
        
        gbc.gridx = 0; gbc.gridy = row++; 
        gbc.weightx = 1.0;
        settingsPanel.add(scheduleInputPanel, gbc);
        
        // Zamanlayıcı Kur
        scheduleButton = new JButton("Zamanlayıcı Kur"); 
        scheduleButton.addActionListener(e -> gonderAction(true));
        gbc.gridx = 0; gbc.gridy = row++; 
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 5, 5); // Üstten boşluk ekle
        settingsPanel.add(scheduleButton, gbc);
        
        // Dikeyde genişleme için Glue (En alta itmek için)
        gbc.gridy = row++; gbc.weighty = 1.0; gbc.gridx = 0; gbc.fill = GridBagConstraints.VERTICAL;
        settingsPanel.add(Box.createVerticalGlue(), gbc);
        
        return settingsPanel;
    }
    
    // GridBagConstraints helper metotları
    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y;
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        return c;
    }

    private GridBagConstraints gbc(int x, int y, double weightx, int fill) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y;
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = fill;
        c.weightx = weightx;
        return c;
    }


    /**
     * Tarihi günceller, menüyü yükler ve mesaj önizlemesini yeniler.
     */
    private void updateTarihField(LocalDate date) {
        String formattedDate = date.format(DATE_FORMATTER);
        tarihField.setText(formattedDate);
        this.currentDate = date;
        
        loadAndFormatMenu(formattedDate); 
    }
    
    /**
     * CSV'den menüyü çeker ve formatlayıp önizleme alanına yazar.
     */
    private void loadAndFormatMenu(String tarih) {
        // Bu kısım, Gui'de kullanılan csvReader'a bağlıdır.
        List<String> menuData = csvReader.getMenuByDate(tarih); 
        
        String formattedMessage = formatMenuForWhatsapp(tarih, menuData);
        messagePreviewArea.setText(formattedMessage);
        messagePreviewArea.setCaretPosition(0); 
    }
    
    /**
     * Menü verisini WhatsApp'a uygun formatta birleştirir.
     */
    private String formatMenuForWhatsapp(String tarih, List<String> menuData) {
        if (menuData.isEmpty()) {
            return "UYARI: " + tarih + " tarihi için menü verisi bulunamadı. Lütfen ana menüden kontrol edin/girin.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(tarih).append(" KYK Yemek Menüsü* \n\n");
        
        // --- 1. Kahvaltı Bölümü (İlk 8 Eleman) ---
        sb.append("*KAHVALTI*\n");
        int kahvaltiSize = 8;
        for (int i = 0; i < kahvaltiSize && i < menuData.size(); i++) {
            if (!menuData.get(i).trim().isEmpty()) {
                sb.append("• ").append(menuData.get(i)).append("\n");
            }
        }
        sb.append("\n"); 
        
        // --- 2. Akşam Yemeği Bölümü (Geriye Kalanlar) ---
        sb.append("*AKŞAM YEMEĞİ*\n");
        for (int i = kahvaltiSize; i < menuData.size(); i++) {
            if (!menuData.get(i).trim().isEmpty()) {
                // Çorba ve Ana yemeklere emoji eklemek için basit bir mantık
                // (Gui.java'daki formatlaMenuMesaji ile aynı mantığı kullanmak isterseniz
                // o mantığı buraya taşıyabilirsiniz, şimdilik düz liste yapıyorum)
                sb.append("• ").append(menuData.get(i)).append("\n");
            }
        }
        
        // --- 3. Otomasyon Uyarısı Ekleme ---
        sb.append("\n Bu bir otomasyon mesajıdır. Daha fazla bilgi için https://github.com/SametCirik/WhatsApp-KYK-Bot adresine göz atın.\n");

        return sb.toString();
    }
    
    /**
     * Gönder butonuna basıldığında tetiklenir.
     * @param isScheduled Zamanlanmış gönderim modu olup olmadığı.
     */
    private void gonderAction(boolean isScheduled) {
        String hedefSohbet = whatsappLinkField.getText().trim();
        String mesajIcerigi = messagePreviewArea.getText();

        if (hedefSohbet.isEmpty() || mesajIcerigi.contains("UYARI")) {
            showError("Lütfen Kişi/Grup Adı alanını doldurun ve menü verisinin yüklü olduğundan emin olun.");
            return;
        }
        
        if (isScheduled) {
            // --- YENİ ZAMANLAYICI MANTIĞI ---
            String zamanTarih = scheduledDateField.getText().trim();
            String zamanSaat = scheduledTimeField.getText().trim();

            if (zamanTarih.isEmpty() || zamanSaat.isEmpty()) {
                showError("Zamanlamak için tarih ve saat alanları zorunludur.");
                return;
            }

            try {
                String zamanStr = zamanTarih + " " + zamanSaat;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                LocalDateTime scheduledDateTime = LocalDateTime.parse(zamanStr, formatter);
                
                long totalSeconds = Duration.between(LocalDateTime.now(), scheduledDateTime).toSeconds();

                if (totalSeconds < 0) {
                    showError("Zamanlama geçmiş bir tarih/saat olamaz.");
                    return;
                }
                
                // Geri sayım sırasında butonları devre dışı bırak
                setButtonsEnabled(false);
                showInfo("Zamanlayıcı kuruldu. Geri sayım başlıyor...");

                // Her saniye çalışacak olan Swing Timer'ı kur
                countdownTimer = new Timer(1000, e -> {
                    long remainingSeconds = Duration.between(LocalDateTime.now(), scheduledDateTime).toSeconds();
                    
                    if (remainingSeconds > 0) {
                        // Butonun metnini güncelle
                        scheduleButton.setText("Gönderiliyor (" + remainingSeconds + "sn)...");
                    } else {
                        // Süre doldu, timer'ı durdur ve otomasyonu başlat
                        ((Timer)e.getSource()).stop();
                        scheduleButton.setText("Zamanlayıcı Kur");
                        
                        showInfo("Zamanı geldi! Mesaj şimdi gönderiliyor...");
                        // Otomasyonu ayrı bir Thread'de başlat
                        new Thread(() -> {
                            try {
                                new WhatsappService().sendMessage(hedefSohbet, mesajIcerigi, false); // false = Görünür mod
                                showInfo("Mesaj başarıyla zamanlanmış olarak gönderildi!");
                            } catch (Exception ex) {
                                showError("Mesaj gönderilirken bir hata oluştu: " + ex.getMessage());
                            } finally {
                                setButtonsEnabled(true); // İşlem bitince butonları tekrar aktif et
                            }
                        }).start();
                    }
                });
                countdownTimer.start(); // Geri sayımı başlat

            } catch (DateTimeParseException ex) {
                showError("Zamanlama formatı hatalı! GG-AA-YYYY SS:DD kullanın.");
            }
            
        } else {
            // --- ANLIK GÖNDERİM (Bu kısım aynı) ---
            setButtonsEnabled(false);
            new Thread(() -> {
                try {
                    showInfo("Mesaj şimdi gönderiliyor...");
                    new WhatsappService().sendMessage(hedefSohbet, mesajIcerigi, false); // false = Görünür mod
                    showInfo("Mesaj başarıyla gönderildi!");
                } catch (Exception e) {
                    showError("Mesaj gönderilirken bir hata oluştu: " + e.getMessage());
                } finally {
                    setButtonsEnabled(true);
                }
            }).start();
        }
    }

    // Butonları Arayüz Thread'inde aktif/pasif yapmak için yardımcı metot
    private void setButtonsEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            gonderButton.setEnabled(enabled);
            scheduleButton.setEnabled(enabled);
        });
    }
    
    // Hata ve bilgi mesajlarını arayüz thread'inde göstermek için yardımcı metotlar
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(WhatsappGui.this, message, "Hata", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void showInfo(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(WhatsappGui.this, message, "Bilgi", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}