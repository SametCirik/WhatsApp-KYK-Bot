package bot;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WhatsappService {

    // Terminal Renk Kodları (Linux uyumlu)
    private static final String RESET = "\033[0m";
    private static final String CYAN = "\033[1;36m";
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";

    public void sendMessage(String hedefSohbetAdi, String message) throws Exception {
        sendMessage(hedefSohbetAdi, message, false);
    }

    public void sendMessage(String hedefSohbetAdi, String message, boolean isHeadless) throws Exception {
        System.out.println(CYAN + "--- WhatsappService Başlatılıyor (Copy-Paste Modu) ---" + RESET);

        // 1. Sürücüyü Otomatik Ayarla (Chrome 144 Uyumlu)
        WebDriverManager.chromedriver().browserVersion("144").setup();

        ChromeOptions options = new ChromeOptions();
        // Arch Linux Chrome yolu genellikle burasıdır, sistemine göre değişebilir.
        options.setBinary("/usr/bin/google-chrome-stable");
        
        String userDir = System.getProperty("user.dir");
        String profilePath = userDir + File.separator + "ChromeProfile";
        
        File profileDir = new File(profilePath);
        if (!profileDir.exists()) {
            boolean created = profileDir.mkdirs();
            if(created) System.out.println("Yeni profil klasörü oluşturuldu: " + profilePath);
        }
        
        options.addArguments("user-data-dir=" + profilePath);
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            System.out.println("Tarayıcı (Google Chrome) başarıyla başlatıldı.");

            driver.get("https://web.whatsapp.com/");
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

            // 1. GİRİŞ KONTROLÜ
            System.out.println("Giriş kontrol ediliyor...");
            try {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@contenteditable='true'][@data-tab='3']")),
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//canvas[@aria-label='Scan this QR code']"))
                ));
            } catch (Exception e) {
                System.out.println(YELLOW + "Zaman aşımı! Sayfa yüklenemedi." + RESET);
                throw e;
            }

            if (driver.findElements(By.xpath("//canvas[@aria-label='Scan this QR code']")).size() > 0) {
                System.out.println(YELLOW + ">>> QR KOD GEREKLİ! Lütfen okutun... <<<" + RESET);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@contenteditable='true'][@data-tab='3']")));
                System.out.println(GREEN + "Giriş Başarılı!" + RESET);
            }

            // 2. SOHBET SEÇİMİ
            System.out.println("'" + hedefSohbetAdi + "' aranıyor...");
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@contenteditable='true'][@data-tab='3']")));
            searchBox.click();
            // Temizleme işlemi bazen tek seferde çalışmayabilir, garanti olsun diye;
            searchBox.sendKeys(Keys.CONTROL + "a");
            searchBox.sendKeys(Keys.DELETE);
            Thread.sleep(500); // Kısa bekleme
            searchBox.sendKeys(hedefSohbetAdi);
            Thread.sleep(2000); 
            searchBox.sendKeys(Keys.ENTER);
            System.out.println("Sohbet seçildi.");

            // 3. MESAJ YAPIŞTIRMA (COPY-PASTE YÖNTEMİ)
            WebElement messageBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@contenteditable='true'][@data-tab='10']")));
            
            // --- Clipboard (Pano) İşlemleri ---
            StringSelection stringSelection = new StringSelection(message);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            
            System.out.println("Mesaj panoya kopyalandı, yapıştırılıyor...");
            
            messageBox.sendKeys(Keys.CONTROL + "v");
            Thread.sleep(1000); 
            messageBox.sendKeys(Keys.ENTER);
            System.out.println("Mesaj gönder komutu verildi.");

            // --- GÜVENLİ BEKLEME (KYK MODU & PROGRESS BAR) ---
            printProgressBar(60, "Mesajın iletilmesi bekleniyor (KYK Modu)");

            System.out.println(GREEN + "\nİşlem Tamamlandı." + RESET);

        } catch (Exception e) {
            System.err.println(YELLOW + "Kritik Hata: " + e.getMessage() + RESET);
            e.printStackTrace();
            throw e; 
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                    System.out.println("Tarayıcı kapatıldı.");
                } catch (Exception e) {
                    System.out.println("Tarayıcı kapatılırken uyarı: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Terminalde ilerleme çubuğu gösterir.
     * @param totalSeconds Toplam beklenecek saniye
     * @param taskName İşlem adı
     */
    private void printProgressBar(int totalSeconds, String taskName) {
        int barLength = 40; // Barın uzunluğu (karakter sayısı)
        
        System.out.println(CYAN + taskName + "..." + RESET);
        
        for (int i = 1; i <= totalSeconds; i++) {
            try {
                Thread.sleep(1000); // 1 saniye bekle
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Yüzde hesabı
            int percent = (int) ((i * 100.0f) / totalSeconds);
            // Barın dolu kısmı
            int filledLength = (int) ((barLength * i) / totalSeconds);
            
            StringBuilder bar = new StringBuilder();
            bar.append("[");
            for (int j = 0; j < filledLength; j++) {
                bar.append("#");
            }
            for (int j = 0; j < barLength - filledLength; j++) {
                bar.append("-");
            }
            bar.append("]");
            
            // \r (Carriage Return) ile satır başı yapıp üzerine yazıyoruz
            String color = (percent == 100) ? GREEN : YELLOW;
            System.out.print("\r" + color + bar.toString() + " " + percent + "%" + RESET);
        }
        System.out.println(); // Döngü bitince alt satıra geç
    }
}