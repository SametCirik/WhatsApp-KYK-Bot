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

<p align="center">
    <img width="256" height="256" alt="AppLogo" src="https://github.com/user-attachments/assets/d13d7218-50f4-470e-8560-338c7ff24c6c" />
</p>

<!--
<p align="center">
    <i>
       Application Icon
    </i>
</p>
-->

<p align="center">
  <img src="https://img.shields.io/badge/Java-21%2B-ed8b00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Selenium-4.x-43B02A?style=for-the-badge&logo=selenium&logoColor=white" alt="Selenium">
  <img src="https://img.shields.io/badge/Linux-Arch%2FDebian-FCC624?style=for-the-badge&logo=linux&logoColor=black" alt="Linux">
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="License">
</p>

---

# WhatsApp KYK Bot v2.0 (Linux Edition)

**WhatsApp KYK Bot**, KYK (Kredi ve Yurtlar Kurumu) yurtlarındaki haftalık yemek menülerini yönetmek, zamanlamak ve **WhatsApp** üzerinden otomatik olarak paylaşmak için geliştirilmiş güçlü bir otomasyon aracıdır.

**v2.0 Güncellemesi ile birlikte:** Artık **Linux terminallerinde** tam uyumluluk, **Çoklu Dil Desteği**, **Tema Motoru** ve gelişmiş bir **CLI (Komut Satırı)** deneyimi sunuyor.

---

## Yeni Özellikler (v2.0)

### Çoklu Dil Desteği (Multi-language)
Bot artık global! Tek bir komutla arayüz dilini değiştirebilirsiniz.

* **Desteklenen Diller:**

    * 🇹🇷 TR,
    * 🇺🇸 EN,
    * 🇯🇵 JA,
    * 🇩🇪 DE,
    * 🇷🇺 RU,
    * 🇫🇷 FR,
    * 🇵🇹 PT,
    * 🇨🇳 ZH.

* **Komut:** `set --lang [kod]` (Örn: `set --lang tr`)

### Dinamik Tema Motoru

Terminaliniz sıkıcı olmak zorunda değil. Ruh halinize uygun temayı seçin.

* **Temalar:** `Default` (Varsayılan), `Matrix` (Hacker Yeşili), `Cyberpunk` (Neon), `Dracula` (Karanlık Mod), `Ocean` (Mavi).
* **Komut:** `theme [ad]` (Örn: `theme matrix`)

### Gözlem Modu (Monitor Mode) & İlerleme Çubuğu
Zamanlayıcıyı kurduğunuzda, terminalde canlı akan bir **İlerleme Çubuğu (Progress Bar)** ve geri sayım sayacı belirir.

* **Özellik:** Arka planda çalışırken terminali bloklamaz. İstediğiniz zaman `monitor` yazarak durumu canlı izleyebilirsiniz.

### Akıllı Konfigürasyon

* **Kalıcı Ayarlar:** Dil, tema ve hedef grup tercihlerinizi hatırlar ve `config.properties` dosyasına kaydeder.
* **Linux Script:** `run_bot.sh` ile tek tıkla başlatma.

---

## Kurulum

### Gereksinimler

* **Java JDK 21+**
* **Google Chrome** (Tarayıcı yüklü olmalıdır)
* **Maven** (Derleme için)

### Adım Adım Kurulum (Linux/macOS)

1. **Depoyu Klonlayın:**

   ```bash
   git clone [https://github.com/SametCirik/WhatsApp-KYK-Bot.git](https://github.com/SametCirik/WhatsApp-KYK-Bot.git)
   cd WhatsApp-KYK-Bot
   ```

2. Projeyi Derleyin (Build)

   ```bash
   # Ana dizine dönüp scripti çalıştırmanız yeterlidir, script yoksa manuel derleyin:
   cd Bot/BotGUI
   mvn clean package
   cp target/whatsapp-kyk-bot-1.0-SNAPSHOT.jar ../../Whatsapp-KYK-Bot.jar
   cd ../../
   ```
   
3. Çalışturma izni Verin ve Başlatın:

   ```bash
   chmod +x run_bot.sh
   ./run_bot.sh
   ```

---

## Kullanım (CLI Modu)

Bot açıldığında interaktif bir **Shell** ortamına girersiniz. İşte temel komutlar:

Komut | Açıklama | Örnek Kullanım
---  | --- | ---
set | Hedef WhatsApp grubunu ayarlar. | set Yemek Grubu
set --lang | Arayüz dilini değiştirir. | set --lang tr
theme | Tema listesini açar veya değiştirir. | theme cyberpunk
send --now | Menüyü anında gönderir. | send --now
send --schedule | İleri tarihli zamanlayıcı kurar. | send --schedule -d 17-02-2026 -t 07:00
monitor | Aktif sayacı ve ilerleme çubuğunu gösterir.,monitor
cancel | Aktif zamanlayıcıyı iptal eder. | cancel
status | Mevcut ayarları ve durumu gösterir. | status

---

## Proje Yapısı

```bash
WhatsApp-KYK-Bot/
├── run_bot.sh             # Linux Başlatma Scripti
├── Whatsapp-KYK-Bot.jar   # Derlenmiş Uygulama
├── config.properties      # Ayar Dosyası (Otomatik oluşur)
├── lang/                  # Dil Dosyaları
│   ├── lang_tr.properties
│   ├── lang_en.properties
│   └── ...
├── data/
│   └── menu_listesi.csv    # Yemek Listesi Veritabanı
└── src/                    # Kaynak Kodlar
```

---

## Yasal Uyarı

Bu proje eğitim amaçlı geliştirilmiştir. **WhatsApp'ın hizmet koşullarına uyunuz.** Kötüye kullanım (spam vb.) durumunda sorumluluk kullanıcıya aittir.

---

## Geliştiriciler *(-ler)*

**[Samet Cırık](https://github.com/SametCirik)** tarafından geliştirilmiştir.

---

<p align="center">
  <img width="777" height="1047" alt="image" src="https://github.com/user-attachments/assets/c4aba39f-db57-45f8-8280-9f61d5d0a7a9" />
</p>
