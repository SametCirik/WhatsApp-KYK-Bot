<p align="center">
  <a href="./README.md">
    <img src="https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.5.0/flags/4x3/gb.svg" alt="English" width="40">
  </a>
  &nbsp;&nbsp;|&nbsp;&nbsp;
  <a href="./README_tr.md">
    <img src="https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.5.0/flags/4x3/tr.svg" alt="Türkçe" width="40">
  </a>
</p>

---

# KYK Yemek Menüsü Botu - WhatsApp Otomasyonu (v1.2)

Bu proje, **KYK (Kredi ve Yurtlar Kurumu)** yurtlarındaki haftalık yemek menülerini yönetmek ve bunları **WhatsApp** üzerinden otomatik olarak paylaşmak için **Java (Swing + Selenium)** kullanılarak geliştirilmiş, hem masaüstü hem de sunucu tarafında çalışabilen güçlü bir uygulamadır.

Modern bir veri giriş arayüzü (GUI), sunucu ortamları için Komut Satırı (CLI) modu ve **Selenium WebDriver** kullanan akıllı otomasyon yetenekleri içerir.

---

## Özellikler

### Temel Özellikler
* **Menü Yönetimi:** Kullanıcı dostu **Java Swing** arayüzü ile günlük menüleri (Kahvaltı ve Akşam Yemeği) girin, düzenleyin ve kaydedin.
* **Akıllı CSV Entegrasyonu:** `.csv` dosyalarını **Sürükle & Bırak** yöntemiyle anında yükleme desteği.
* **WhatsApp Otomasyonu:** WhatsApp Web'e bağlanır, (oturum sürekliliği sayesinde) her seferinde QR kodu okutma derdini ortadan kaldırır ve mesajları emojilerle formatlayarak gönderir.
* **Zamanlayıcı & Planlama:** Mesajları belirli bir tarih ve saatte gönderilecek şekilde zamanlayın veya anında gönderin.

### v1.2 Yenilikleri
* **Sunucu Sürümü (CLI Modu):** Botu "headless" (ekransız) sunucularda veya terminal üzerinden çalıştırmak için tam işlevli bir Komut Satırı Arayüzü (`kykbot.bat`).
* **Akıllı Navigasyon:** Veri girişini hızlandırmak için menü alanları arasında **Ok Tuşları (Yukarı/Aşağı)** ile gezinebilme özelliği.
* **Gelişmiş Medya Yönetimi:** JAR dosyalarından uygulama ikonlarının ve logoların yüklenmesiyle ilgili sorunlar giderildi.
* **Emoji Desteği:** Sistem Panosu (Clipboard) entegrasyonu kullanılarak Selenium BMP hataları aşıldı ve emojili mesaj formatı iyileştirildi.

---

## Gereksinimler & Ortam

Projeyi çalıştırmadan önce aşağıdakilerin yüklü olduğundan emin olun:

* **Java JDK 21+:** Uygulamayı çalıştırmak için gereklidir.
* **Google Chrome:** Bot, otomasyon için yüklü olan Chrome tarayıcısını kullanır.
* **Maven:** Bağımlılık yönetimi (Selenium, FlatLaf vb.) ve projeyi derlemek için kullanılır.

---

## Kurulum

### 1. Depoyu Klonlayın

```bash
git clone [https://github.com/SametCirik/WhatsApp-KYK-Bot.git](https://github.com/SametCirik/WhatsApp-KYK-Bot.git)
cd WhatsApp-KYK-Bot
```

### 2. Bağımlılıkları Yükleyin (Selenium vb.)

Bu proje Maven kullanır. JAR dosyalarını manuel olarak indirmenize gerek yoktur. `pom.xml` dosyasında tanımlı tüm kütüphaneleri indirmek için proje kök dizininde şu komutu çalıştırın:

```bash
mvn clean install
```

### 3. JAR Dosyasını Oluşturun

Tüm bağımlılıkları içeren çalıştırılabilir bir JAR dosyası oluşturmak için:

```Bash
mvn clean package
```

Çıktı dosyası `/target` klasöründe oluşturulacaktır.

---

## Kullanım

### A. GUI Modu (Masaüstü)

JAR dosyasını doğrudan çalıştırın veya aşağıdaki komutu kullanın. Bu, menüleri girebileceğiniz veya CSV dosyanızı sürükleyip bırakabileceğiniz görsel arayüzü açar.

```bash
java -jar target/Whatsapp-KYK-Bot.jar
```

1. **Menüyü Girin:** Kahvaltı ve akşam yemeği alanlarını doldurun (Gezinmek için Ok Tuşlarını kullanın!).

2. **Kaydet/Yükle:** Bir `.csv` dosyasını alana sürükleyin veya manuel girişinizi kaydedin.

3. **Botu Başlat:** WhatsApp Zamanlayıcısını açmak için "Botu Başlat" butonuna tıklayın.

### B. CLI Modu (Sunucu/Terminal)

GUI'nin mevcut olmadığı veya istenmediği sunucu ortamları için sağlanan toplu işlem (batch) dosyasını kullanın.

1. Proje klasörüne gidin.

2. `KykBotTerminal.bat` dosyasını çalıştırın.

3. İstendiğinde hedef WhatsApp Grup Adını girin.

4. Bot arka planda çalışacak ve bugünün menüsünü gönderecektir.

---

## Sorun Giderme & SSS

### WebDriver'ı nasıl güncellerim?

Bu proje, içinde "Selenium Manager" barındıran **Selenium 4.x** kullanır. Yüklü Chrome sürümünüzü **otomatik olarak** algılar ve eşleşen ChromeDriver'ı indirir.

* **Çözüm:** Sadece Google Chrome tarayıcınızı güncel tutun. Gerisini Selenium halleder.

### "AppLogo.png not found" Hatası

`mvn package` komutuyla oluşturulan JAR dosyasını çalıştırdığınızdan emin olun. Kaynak yükleme mantığı, dosyaları bulmak için hem `/bot/images/` klasörünü hem de kök dizini kontrol eder.

### WhatsApp Web QR Kodu

Bot, oturumunuzu kaydetmek için yerelleştirilmiş bir `ChromeProfile` klasörü kullanır.

1. İlk çalıştırmada QR kodunu taratmanız gerekecektir.

2. Sonraki çalıştırmalarda oturum otomatik olarak geri yüklenir (tekrar taratmaya gerek yoktur).

**Not:** Profil kilitlenme hatalarını önlemek için botu çalıştırmadan önce tüm Chrome pencerelerinin kapalı olduğundan emin olun.

---

## Katkıda Bulunma

Bu kişisel bir proje olduğu için dışarıdan katkılar şu an için kapalıdır. Ancak, depoyu fork'layabilir ve kod üzerinde dilediğiniz gibi denemeler yapabilirsiniz.

---

<p align="center">
    <img width="256" height="256" alt="AppLogo" src="https://github.com/user-attachments/assets/d13d7218-50f4-470e-8560-338c7ff24c6c" />
</p>

<p align="center">
    <i>
       Application Icon
    </i>
</p>

---

## Application Preview

<p align="center">
    <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/ba92c379-c58d-4526-ac38-bb8c4a197e34" />
    <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/1a12150c-c26a-4de9-9fc0-d17524dfab83" />
    <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/b908b38c-9f9c-499d-8782-770cf3e2f310" />
    <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/d8c6b7cb-4f18-43be-977e-84815b83b522" />
    <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/72df03eb-8876-48ce-b0e7-3abb0609b95d" />
    <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/f23dd470-5784-4434-9305-48a0c56bbba4" />
    <video src="https://github.com/user-attachments/assets/00c89376-7448-45bc-9ce5-49a5c71cd8b1"> width="700" controls>
       Your browser does not support the video tag.
    </video>
</p>

---

## Geliştiriciler *(-ler)*

**[Samet Cırık](https://github.com/SametCirik)** tarafından geliştirilmiştir.
