package bot;

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

public class WhatsappService {

    // GUI'den çağrılınca hata vermemesi için bu köprüyü ekledim.
    // GUI her zaman "görünür" (headless = false) çalışır.
    public void sendMessage(String hedefSohbetAdi, String message) throws Exception {
        sendMessage(hedefSohbetAdi, message, false);
    }

    // Ana Metot
    public void sendMessage(String hedefSohbetAdi, String message, boolean isHeadless) throws Exception {
        System.out.println("Kod başlatıldı: WhatsappService.java (Revize Edilmiş)");

        ChromeOptions options = new ChromeOptions();
        
        // Windows/Linux uyumlu yol ayracı
        String profilePath = System.getProperty("user.dir") + File.separator + "ChromeProfile";
        
        // Profil klasörü yoksa oluştur
        File profileDir = new File(profilePath);
        if (!profileDir.exists()) {
            profileDir.mkdirs();
        }
        
        System.out.println("Profil Yolu: " + profilePath);

        options.addArguments("user-data-dir=" + profilePath);
        // options.addArguments("--profile-directory=Default"); // Bazen hata yapabilir, kapattım.
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        // Bot gibi görünmemek için User-Agent
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // --- SUNUCU İÇİN KRİTİK AYAR ---
        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080"); // Gizli modda ekran boyutu şarttır
        }

        WebDriver driver = null;
        try {
            // Sürücüyü sistem yolundan (PATH) bulacak.
            // Sunucuda Chrome ve ChromeDriver kurulu olmalı.
            driver = new ChromeDriver(options);
            System.out.println("Tarayıcı başlatıldı.");

            driver.get("https://web.whatsapp.com/");
            
            // Bekleme süresini biraz artırdım (Sunucular yavaş olabilir)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

            // 1. GİRİŞ KONTROLÜ
            System.out.println("Giriş kontrol ediliyor...");
            try {
                // Ya arama kutusu ya da QR kodu gelene kadar bekle
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@contenteditable='true'][@data-tab='3']")),
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//canvas[@aria-label='Scan this QR code']"))
                ));
            } catch (Exception e) {
                System.out.println("Sayfa yüklenemedi. İnternet bağlantısını kontrol edin.");
                throw e;
            }

            // QR Kod varsa uyar (Sadece Headless False iken işe yarar)
            if (driver.findElements(By.xpath("//canvas[@aria-label='Scan this QR code']")).size() > 0) {
                System.out.println("QR KOD EKRANI! Lütfen telefondan okutun...");
                // QR geçilene kadar bekle
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@contenteditable='true'][@data-tab='3']")));
                System.out.println("Giriş Başarılı!");
            }

            // 2. SOHBETİ BULMA (Arama Kutusu Yöntemi - Server İçin Daha Güvenli)
            System.out.println("'" + hedefSohbetAdi + "' aranıyor...");
            
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@contenteditable='true'][@data-tab='3']")));
            searchBox.click();
            
            // Kutuyu temizle
            searchBox.sendKeys(Keys.CONTROL + "a");
            searchBox.sendKeys(Keys.DELETE);
            
            searchBox.sendKeys(hedefSohbetAdi);
            Thread.sleep(2000); // Sonuçların gelmesini bekle
            searchBox.sendKeys(Keys.ENTER);

            System.out.println("Sohbet seçildi.");

            // 3. MESAJ KUTUSU VE GÖNDERİM
            // data-tab='10' mesaj yazma alanıdır
            WebElement messageBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@contenteditable='true'][@data-tab='10']")));
            
            String[] lines = message.split("\n");
            for (String line : lines) {
                messageBox.sendKeys(line);
                messageBox.sendKeys(Keys.SHIFT, Keys.ENTER);
            }
            
            Thread.sleep(500);
            messageBox.sendKeys(Keys.ENTER);
            System.out.println("Mesaj başarıyla gönderildi. ✅");

            // 120 saniye yerine 10 saniye yeterli
            System.out.println("Mesajın gitmesi için 10 saniye bekleniyor...");
            Thread.sleep(10000); 

        } catch (Exception e) {
            System.err.println("Hata oluştu: " + e.getMessage());
            e.printStackTrace();
            throw e; 
        } finally {
            if (driver != null) {
                driver.quit(); // Tarayıcıyı mutlaka kapat ki sunucu RAM'i şişmesin
                System.out.println("Tarayıcı kapatıldı.");
            }
        }
    }
}