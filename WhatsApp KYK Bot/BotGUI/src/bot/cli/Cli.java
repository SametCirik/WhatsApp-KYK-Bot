package bot.cli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import bot.csv.ReadCSV;

public class Cli {

    /**
     * Sunucu tarafında çalışacak olan "Görünmez" Bot Mantığı
     * @param hedefGrupAdi Mesajın atılacağı grup ismi
     */
    public static void calistir(String hedefGrupAdi) {
        try {
            System.out.println("Bot Başlatılıyor... Hedef: " + hedefGrupAdi);

            // 1. Bugünün Tarihini Al
            LocalDate bugun = LocalDate.now();
            String tarihStr = bugun.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            System.out.println("İşlem Tarihi: " + tarihStr);

            // 2. CSV'den Menüyü Oku
            ReadCSV reader = new ReadCSV();
            List<String> menuData = reader.getMenuByDate(tarihStr);

            if (menuData == null || menuData.isEmpty()) {
                System.err.println("UYARI: Bugün (" + tarihStr + ") için CSV dosyasında menü bulunamadı!");
                return;
            }

            // 3. Mesajı Formatla
            String whatsappMesaji = formatlaMenuMesaji(tarihStr, menuData);
            System.out.println("Oluşturulan Mesaj Önizlemesi:\n" + whatsappMesaji);

            // 4. WhatsApp Servisini Başlat ve Gönder
            // true = Headless (Sunucu modu), false = Görünür
            // Şu an false yapıyorum ki sunucuda Chrome açılsın ve sen gör.
            bot.WhatsappService service = new bot.WhatsappService();
            service.sendMessage(hedefGrupAdi, whatsappMesaji, false); 

            System.out.println("Bot işlemi tamamladı ve kapanıyor.");
            System.exit(0); // Programı tamamen kapat

        } catch (Exception e) {
            System.err.println("Bot çalışırken kritik hata oluştu:");
            e.printStackTrace();
            System.exit(1); // Hata kodu ile çık
        }
    }

    /**
     * CSV'den gelen düz listeyi WhatsApp mesajına çevirir.
     */
    private static String formatlaMenuMesaji(String tarih, List<String> data) {
        StringBuilder sb = new StringBuilder();
        
        // Emojiler ve Başlık
        sb.append("📅 *").append(tarih).append(" KYK Yemek Menüsü* 📅\n\n");

        // --- KAHVALTI BÖLÜMÜ (İlk 8 Eleman) ---
        sb.append("*🧀 KAHVALTI*\n");
        for (int i = 0; i < 8; i++) {
            if (hasData(data, i)) {
                sb.append("• ").append(data.get(i)).append("\n");
            }
        }
        sb.append("\n"); 

        // --- AKŞAM YEMEĞİ BÖLÜMÜ (8. İndeksten sonrası) ---
        sb.append("*🥘 AKŞAM YEMEĞİ*\n");
        
        if (hasData(data, 8)) sb.append("🥣 ").append(data.get(8)).append("\n"); // Çorba 1
        if (hasData(data, 9)) sb.append("🥣 ").append(data.get(9)).append("\n"); // Çorba 2

        if (hasData(data, 10)) sb.append("🍛 ").append(data.get(10)).append("\n"); // Ana Yemek 1
        if (hasData(data, 11)) sb.append("🍛 ").append(data.get(11)).append("\n"); // Ana Yemek 2
        
        if (hasData(data, 12)) sb.append("🍝 ").append(data.get(12)).append("\n"); // Pilav
        if (hasData(data, 13)) sb.append("🥗 ").append(data.get(13)).append("\n"); // Salata
        if (hasData(data, 14)) sb.append("🍰 ").append(data.get(14)).append("\n"); // Tatlı

        // Ekmek ve Su
        if (hasData(data, 15)) sb.append("🥖 ").append(data.get(15)).append("\n");
        if (hasData(data, 16)) sb.append("💧 ").append(data.get(16)).append("\n");

        sb.append("\n_Afiyet olsun!_ 🤖");
        
        return sb.toString();
    }

    private static boolean hasData(List<String> list, int index) {
        return index < list.size() && list.get(index) != null && !list.get(index).trim().isEmpty();
    }
}