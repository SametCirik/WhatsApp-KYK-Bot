package bot.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReadCSV {

    private static final String DEFAULT_CSV_PATH = "data/menu_listesi.csv"; 
    // Toplam 18 sütun: Tarih (1) + Kahvaltı (4 düzenlenebilir + 4 sabit) + Akşam (9)
    private static final int EXPECTED_COLUMN_COUNT = 18; 

    /**
     * CSV dosyasını okur ve belirtilen tarihe ait menü bilgilerini döndürür.
     * * @param targetTarih Aranacak tarih (gg-aa-yyyy formatında)
     * @return Tarihe ait menü verilerini içeren String Listesi (Tarih hariç, 17 eleman). 
     * Eğer tarih bulunamazsa veya dosya yoksa boş liste döndürülür.
     */
    public List<String> getMenuByDate(String targetTarih) {
        
        File csvFile = new File(DEFAULT_CSV_PATH);
        
        // Dosya mevcut değilse veya boşsa, aramaya gerek yok.
        if (!csvFile.exists() || csvFile.length() == 0) {
            return Collections.emptyList();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                if (isHeader) {
                    isHeader = false; // Başlık satırını atla
                    continue;
                }
                
                // Satırı virgülle ayır
                // Not: Tırnak içindeki virgülleri doğru ayırmak için daha gelişmiş bir CSV parser gerekir, 
                // ancak basit bir split işlemi çoğu senaryo için yeterlidir.
                String[] columns = line.split(",", -1); 

                if (columns.length < EXPECTED_COLUMN_COUNT) {
                    System.err.println("HATA: CSV satırında eksik sütunlar var: " + line);
                    continue;
                }
                
                String existingTarih = columns[0].trim();
                
                // Aranan tarih bulunduysa
                if (existingTarih.equalsIgnoreCase(targetTarih)) {
                    // İlk sütunu (Tarihi) hariç tutarak diğer sütunları Listeye dönüştür.
                    // Listenin boyutu 17 olacaktır (18 - 1 = 17)
                    List<String> menuData = new ArrayList<>(Arrays.asList(columns));
                    menuData.remove(0); // Tarihi listeden çıkar
                    
                    // Listeyi temizle ve tırnak işaretlerini kaldır (basit unescape işlemi)
                    List<String> cleanedData = new ArrayList<>();
                    for(String item : menuData) {
                        cleanedData.add(unescapeCSV(item));
                    }
                    
                    return cleanedData; // Menü verilerini döndür ve metottan çık
                }
            }
            
        } catch (IOException e) {
            System.err.println("CSV okuma hatası oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        return Collections.emptyList(); // Tarih bulunamazsa boş liste döndür
    }

    /**
     * CSV'den okunan değerlerden tırnak işaretlerini ve çift tırnakları temizler.
     */
    private String unescapeCSV(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        String unescaped = value.trim();
        // Eğer tırnak işaretleri ile başlıyor ve bitiyorsa, onları kaldır.
        if (unescaped.startsWith("\"") && unescaped.endsWith("\"")) {
            unescaped = unescaped.substring(1, unescaped.length() - 1);
            // Çift tırnakları tek tırnağa çevir (içerideki kaçış işlemi)
            unescaped = unescaped.replace("\"\"", "\"");
        }
        return unescaped;
    }
}