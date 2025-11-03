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

# KYK Yemek Menüsü Botu - WhatsApp Otomasyonu

Bu, **KYK (Kredi ve Yurtlar Kurumu)** yurtları için yemek menülerini yönetmek ve belirlenen bir tarihte otomatik olarak WhatsApp üzerinden göndermek amacıyla tamamen **Java** programlama dili kullanılarak geliştirilmiş kişisel bir masaüstü uygulamasıdır.

Bu proje, sıfırdan işlevsel bir Grafiksel Kullanıcı Arayüzü (GUI) oluşturmaya, olay yönetimini (event handling) sağlamaya ve **Java Selenium** ile tarayıcı otomasyonu mantığını uygulayarak güvenilir bir bot deneyimi yaratmaya odaklanmaktadır.

---

## Özellikler

* **Menü Yönetim Arayüzü:** **Java Swing** kullanılarak oluşturulmuş, kullanıcıların yemek listelerini tarihe göre girmesine, düzenlemesine ve `.csv` dosyası olarak kaydetmesine olanak tanıyan bir masaüstü arayüzü.
* **CSV Entegrasyonu:** Kaydedilmiş menüleri `.csv` dosyasından okuyarak arayüzdeki alanları otomatik olarak doldurma.
* **WhatsApp Otomasyonu:** **Selenium WebDriver** kullanarak WhatsApp Web'e otomatik olarak bağlanma, belirtilen kişi veya grubu bulma ve formatlanmış menü mesajını gönderme.
* **Zamanlayıcı ile Gönderim:** Mesajların anlık olarak veya gelecekteki bir tarih ve saate ayarlanarak otomatik olarak gönderilmesini sağlayan zamanlayıcı özelliği.
* **Kalıcı Oturum:** Chrome'un kullanıcı profili özelliğinden faydalanarak, her seferinde QR kod okutma gereksinimini ortadan kaldıran kalıcı WhatsApp Web oturumu.

---

## Proje Durumu ve Planlanan Özellikler (v1.0)

**Mevcut Durum:** Projenin ana fonksiyonları (arayüz, CSV işlemleri ve WhatsApp otomasyonu) tamamlanmıştır. Bu **v1.0** sürümü, kararlı ve tam işlevsel bir yapıya sahiptir.

- [v1.0 Pre-Alpha Sürümünü İndir](https://github.com/SametCirik/WhatsApp-KYK-Bot/releases/tag/v1.0)
- [v1.1 Pre-Alpha Sürümünü İndir](https://github.com/SametCirik/WhatsApp-KYK-Bot/releases/tag/v1.1) 

### Sonraki Adımlar (Gelecek Planları)

* **Gelişmiş Hata Yönetimi:** WhatsApp arayüzünde gelecekte olabilecek değişikliklere karşı botu daha dayanıklı hale getirmek.
* **Ayarlar Menüsü:** Chrome profil yolu gibi ayarların arayüz üzerinden değiştirilebilmesi.
* **Çoklu Gönderim:** Aynı mesajın birden fazla gruba veya kişiye gönderilebilmesi için bir alıcı listesi oluşturma özelliği.
* **İşlem Günlüğü (Log):** Botun adımlarının (giriş yapıldı, mesaj gönderildi vb.) arayüz üzerinde bir log alanında gösterilmesi.

---

## Geliştirme Ortamı ve Teknolojiler

* **Dil:** Java
* **Kütüphaneler:**
    * **Java Swing/AWT:** Grafiksel Kullanıcı Arayüzü (GUI) için.
    * **Java Selenium:** Web tarayıcı otomasyonu için.
* **Geliştirme Ortamı (IDE):** Eclipse IDE / Visual Studio Code

---

## Katkıda Bulunma

Bu proje kişisel bir proje olduğu için dışarıdan katkılar şu an için kapalıdır.

Ancak projeyle veya kod yapısıyla ilgileniyorsanız, gelecekteki geliştirmeleri takip etmekten çekinmeyin. Hata bildirimi veya özellik geliştirme gibi konularda iş birliği yapmaktan memnuniyet duyarım.

---

<p align="center">
   <img width="256" height="256" alt="AppLogo" src="https://github.com/user-attachments/assets/d13d7218-50f4-470e-8560-338c7ff24c6c" />
</p>

<p align="center">
   <i>
      Uygulama İkonu
   </i>
</p>

---

## Uygulama Önizlemesi

<p align="center">
   <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/ba92c379-c58d-4526-ac38-bb8c4a197e34" />
   <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/1a12150c-c26a-4de9-9fc0-d17524dfab83" />
   <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/b908b38c-9f9c-499d-8782-770cf3e2f310" />
   <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/d8c6b7cb-4f18-43be-977e-84815b83b522" />
   <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/72df03eb-8876-48ce-b0e7-3abb0609b95d" />
   <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/f23dd470-5784-4434-9305-48a0c56bbba4" />
   <video src="https://github.com/user-attachments/assets/00c89376-7448-45bc-9ce5-49a5c71cd8b1"> width="700" controls>
      Tarayıcınız video etiketini desteklemiyor.
   </video>
</p>

---

##  Geliştirici *(-ler)*

Bu proje, **[Samet Cırık](https://github.com/SametCirik)** tarafından geliştirilmiştir.

---

## Katkıda Bulunanlar (!)

Bu projeye kodlarıyla katkıda bulunanların tam listesini [GitHub'ın katkıda bulunanlar sayfasında](https://github.com/SametCirik/WhatsApp-KYK-Bot/graphs/contributors) görebilirsiniz.
