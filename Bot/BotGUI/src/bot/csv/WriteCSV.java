package bot.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WriteCSV {

    private static final String CSV_PATH = "data/menu_listesi.csv";

    /**
     * Verilen tarih ve menü verilerini CSV'ye yazar.
     * Eğer tarih varsa günceller, yoksa yeni satır ekler.
     * @param tarih (dd-mm-yyyy)
     * @param menuItems (17 elemanlı liste: Kahvaltı(8) + Akşam(9))
     */
    public boolean saveMenu(String tarih, List<String> menuItems) {
        File file = new File(CSV_PATH);
        List<String> lines = new ArrayList<>();
        boolean dateFound = false;

        // Dosya varsa oku
        if (file.exists()) {
            try {
                lines = Files.readAllLines(Paths.get(CSV_PATH));
            } catch (IOException e) {
                System.err.println("Dosya okuma hatası: " + e.getMessage());
                return false;
            }
        } else {
            // Dosya yoksa başlık satırı ekle
            file.getParentFile().mkdirs();
            lines.add("Tarih,K1,K2,K3,K4,K5,K6,K7,K8,Corba1,Corba2,Ana1,Ana2,Yan1,Salata,Tatli,Ekmek,Su");
        }

        // CSV Satırını Oluştur (Kaçış karakterleri ile)
        StringBuilder newRow = new StringBuilder();
        newRow.append(escapeCSV(tarih));
        for (String item : menuItems) {
            newRow.append(",").append(escapeCSV(item));
        }

        // Var olan tarihi güncelle veya yeni ekle
        List<String> newLines = new ArrayList<>();
        // Başlık satırını koru
        if (!lines.isEmpty()) newLines.add(lines.get(0));

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] cols = line.split(",", -1);
            if (cols.length > 0 && cols[0].equalsIgnoreCase(tarih)) {
                newLines.add(newRow.toString()); // Satırı güncelle
                dateFound = true;
            } else {
                newLines.add(line); // Satırı koru
            }
        }

        if (!dateFound) {
            newLines.add(newRow.toString()); // Yeni tarih ekle
        }

        // Dosyaya Yaz
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : newLines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Dosya yazma hatası: " + e.getMessage());
            return false;
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        value = value.trim();
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            value = "\"" + value + "\"";
        }
        return value;
    }
}