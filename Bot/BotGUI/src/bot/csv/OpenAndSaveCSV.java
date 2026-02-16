package bot.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenAndSaveCSV {

    private static final String DEFAULT_CSV_PATH = "data/menu_listesi.csv"; 
    private static final String CSV_HEADER = "Tarih,Kahvaltı1,Kahvaltı2,Kahvaltı3,Kahvaltı4,SabitKahvaltı1,SabitKahvaltı2,SabitKahvaltı3,SabitKahvaltı4,Çorba1,Çorba2,AnaYemek1,AnaYemek2,PilavMakarna,SalataMeze,TatlıMeyve,ÇeyrekEkmek,Su";

    /**
     * Manuel giriş verilerini toplar, CSV formatında hazırlar, 
     * aynı tarih varsa üzerine yazar ve dosyaya kaydeder.
     */
    public String saveManualDataToCSV(String tarih, List<String> kahvaltiListesi, List<String> aksamYemegiListesi) throws IOException {
        
        File csvFile = new File(DEFAULT_CSV_PATH);
        
        // Klasörün varlığını kontrol et ve oluştur
        File parentDir = csvFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 1. Yeni menü satırını hazırla (Yeni eklenecek veri)
        String newMenuLine = prepareNewMenuLine(tarih, kahvaltiListesi, aksamYemegiListesi);
        
        // 2. Mevcut dosyadan verileri oku ve düzeltilmesi gereken tarihi atla
        List<String> existingLines = new ArrayList<>();
        boolean fileExists = csvFile.exists();

        if (fileExists) {
            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean isHeader = true;
                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        // Başlık satırını her zaman koru
                        existingLines.add(line);
                        isHeader = false;
                        continue;
                    }
                    
                    // Satırın tarihini al (İlk virgülden önceki kısım)
                    if (!line.trim().isEmpty()) {
                        String existingTarih = line.split(",")[0].trim();
                        
                        // Eğer mevcut satırdaki tarih, girilen tarih ile aynı DEĞİLSE, listeye ekle.
                        if (!existingTarih.equalsIgnoreCase(tarih)) {
                            existingLines.add(line);
                        }
                        // Eğer aynıysa, bu satırı atla (Yani üzerine yazma işlemi yapılmış olur.)
                    }
                }
            }
        } else {
            // Dosya hiç yoksa, başlık satırını listeye ekle.
            existingLines.add(CSV_HEADER);
        }
        
        // 3. Geçici listeyi ve yeni satırı dosyaya yaz (append: false, yani üzerine tamamen yaz)
        // Mevcut dosyanın üzerine, düzeltilmiş tüm satırları ve yeni satırı yazıyoruz.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, false))) { // false: Üzerine yaz
            
            // Tüm eski (ve filtrelenmiş) satırları yaz
            for (String line : existingLines) {
                writer.write(line);
                writer.newLine();
            }
            
            // Yeni menü satırını sona ekle
            writer.write(newMenuLine);
            writer.newLine();
        }

        return csvFile.getAbsolutePath();
    }
    
    /**
     * Yeni menü satırını CSV formatında hazırlar.
     */
    private String prepareNewMenuLine(String tarih, List<String> kahvaltiListesi, List<String> aksamYemegiListesi) {
        StringBuilder sb = new StringBuilder();
        sb.append(tarih);
        
        // Kahvaltı verilerini ekle
        for (String item : kahvaltiListesi) {
            sb.append(",").append(escapeCSV(item));
        }
        
        // Akşam yemeği verilerini ekle
        for (String item : aksamYemegiListesi) {
            sb.append(",").append(escapeCSV(item));
        }
        return sb.toString();
    }
    

    /**
     * CSV formatında özel karakterleri yönetir.
     */
    private String escapeCSV(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        String escaped = value.trim();
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            escaped = escaped.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
    
    public String getLoadedCSVPath(File loadedCSVFile) {
        return loadedCSVFile.getAbsolutePath();
    }
}