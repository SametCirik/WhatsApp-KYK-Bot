package bot.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import bot.WhatsappService;
import bot.csv.ReadCSV;

public class Cli {

    // --- GLOBAL DEĞİŞKENLER ---
    private static final Scanner scanner = new Scanner(System.in);
    private static String hedefGrupAdi = null;
    private static final ReadCSV csvReader = new ReadCSV();
    
    // Config ve Dil Dosyaları
    private static final String CONFIG_FILE = "config.properties";
    private static final String LANG_FOLDER = "lang"; // Dil dosyaları klasörü
    private static Properties appProps = new Properties();
    
    // Aktif Sözlük (Tüm metinler burada tutulacak)
    private static Properties dictionary = new Properties();
    private static String currentLang = "en"; // Varsayılanı İngilizce yaptık (istediğin gibi)

    // Zamanlayıcı
    private static Timer activeTimer;
    private static LocalDateTime activeTimerTarget = null;
    private static String activeTimerDateStr = null;

    private static String pathName = "data/menu_listesi.csv";

    // --- TEMA MOTORU ---
    static class Theme {
        String name;
        String prompt, success, error, info, bar, reset = "\033[0m";
        public Theme(String n, String p, String s, String e, String i, String b) {
            this.name=n; this.prompt=p; this.success=s; this.error=e; this.info=i; this.bar=b;
        }
    }

    // ANSI Renkler
    private static final String RED="\033[0;31m", GREEN="\033[0;32m", YELLOW="\033[0;33m", BLUE="\033[0;34m", PURPLE="\033[0;35m", CYAN="\033[0;36m", WHITE="\033[0;37m", BOLD_GREEN="\033[1;32m", BOLD_CYAN="\033[1;36m";

    private static final Map<String, Theme> themeMap = new HashMap<>();
    private static Theme currentTheme; 

    static {
        themeMap.put("default", new Theme("Default", WHITE, GREEN, YELLOW, BOLD_CYAN, YELLOW));
        themeMap.put("matrix", new Theme("Matrix", BOLD_GREEN, BOLD_GREEN, GREEN, "\033[0;32m", BOLD_GREEN));
        themeMap.put("cyberpunk", new Theme("Cyberpunk", "\033[1;35m", "\033[1;36m", "\033[1;33m", "\033[1;35m", "\033[1;36m"));
        themeMap.put("dracula", new Theme("Dracula", PURPLE, "\033[1;35m", RED, CYAN, PURPLE));
        themeMap.put("ocean", new Theme("Ocean", BLUE, BOLD_CYAN, RED, BLUE, BOLD_CYAN));
    }

    // --- BAŞLATMA ---
    public static void calistir(String varsayilanGrup) {
        loadConfig(); // Config'i yükle (Dil ve Tema buradan gelir)
        loadLanguage(currentLang); // Dili yükle (Dosyadan veya Fallback'ten)

        if (varsayilanGrup != null && !varsayilanGrup.isEmpty() && 
            !varsayilanGrup.equalsIgnoreCase("shell") && 
            !varsayilanGrup.equalsIgnoreCase("help")) {
            hedefGrupAdi = varsayilanGrup;
        }

        System.out.println(currentTheme.info + "[INFO] " + getMsg("welcome") + currentTheme.reset);
        System.out.println(currentTheme.info + "[INFO] " + getMsg("help_info") + "\n" + currentTheme.reset);

        while (true) {
            if (activeTimer != null) {
                System.out.print(currentTheme.bar + getMsg("timer_active") + currentTheme.reset);
            }

            printPrompt(); 
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue; 

            String[] parts = input.split("\\s+", 2); 
            String command = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1] : null;

            try {
                processCommand(command, arg);
            } catch (Exception e) {
                printError(getMsg("error_unexpected") + e.getMessage());
            }
        }
    }

    // --- DİL YÖNETİMİ (PROPERTIES DOSYASI DESTEKLİ) ---
    private static void loadLanguage(String langCode) {
        dictionary.clear();
        
        // 1. Önce "lang" klasöründe "lang_en.properties" gibi bir dosya var mı bak.
        File langFile = new File(LANG_FOLDER + File.separator + "lang_" + langCode + ".properties");
        
        if (langFile.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8)) {
                dictionary.load(reader);
                // Başarılı yüklenirse buradan devam eder, aksi halde aşağıya (fallback'e) düşer.
                return; 
            } catch (IOException e) {
                printWarning("Dil dosyası okunamadı, varsayılanlar yükleniyor: " + e.getMessage());
            }
        }

        // 2. Dosya yoksa (Henüz oluşturmadık), kod içindeki yedekleri kullan.
        // Burası sadece geliştirme aşamasında "properties dosyaları olmadan çalışsın" diye var.
        loadFallbackLanguage(langCode);
    }

    private static void loadFallbackLanguage(String code) {
        // Varsayılan İngilizce Fallback
        if (code.equals("tr")) {
            dictionary.setProperty("welcome", "Bot Shell Modu Aktif.");
            dictionary.setProperty("help_info", "Komut listesi için 'help' yazın.");
            dictionary.setProperty("timer_active", "[SAYAÇ AKTİF] ");
            dictionary.setProperty("prompt", ">> ");
            dictionary.setProperty("unknown_cmd", "Bilinmeyen komut: ");
            dictionary.setProperty("error_unexpected", "Beklenmeyen hata: ");
            dictionary.setProperty("exit_warn", "Aktif bir zamanlayıcı var! Çıkarsanız iptal edilecek.");
            dictionary.setProperty("exit_confirm", "Çıkmak istiyor musunuz?");
            dictionary.setProperty("goodbye", "Görüşmek üzere...");
            dictionary.setProperty("set_usage", "Kullanım: set [Grup] veya set --lang [dil]");
            dictionary.setProperty("group_set", "Grup ayarlandı: ");
            dictionary.setProperty("lang_set", "Dil değişti: ");
            dictionary.setProperty("timer_cancel", "Sayaç iptal edildi.");
            dictionary.setProperty("no_timer", "Aktif sayaç yok.");
            dictionary.setProperty("monitor_mode", "--- GÖZLEM MODU (Çıkış: ENTER) ---");
            dictionary.setProperty("monitor_exit", "Gözlem modu kapandı.");
            dictionary.setProperty("timer_started", "Sayaç başladı.");
            dictionary.setProperty("timer_confirm", "Başlatılsın mı?");
            dictionary.setProperty("target", "Hedef: ");
            dictionary.setProperty("remaining", "Kalan: ");
            dictionary.setProperty("past_time", "Geçmiş zaman!");
            dictionary.setProperty("missing_args", "Eksik bilgi! Örn: -d 16-12-2025 -t 18:30");
            dictionary.setProperty("menu_sent", "Gönderildi!");
            dictionary.setProperty("whatsapp_opening", "WhatsApp açılıyor...");
            dictionary.setProperty("theme_changed", "Tema değişti: ");
            dictionary.setProperty("theme_invalid", "Geçersiz tema.");
            dictionary.setProperty("theme_select", "--- TEMA SEÇİMİ ---");
            dictionary.setProperty("enter_theme", "Tema no/adı girin: ");
            dictionary.setProperty("lang_invalid", "Geçersiz dil kodu.");
            dictionary.setProperty("sending_msg", "Mesaj gönderiliyor...");
            dictionary.setProperty("preview", "--- ÖNİZLEME ---");
            dictionary.setProperty("send_confirm", "Gönderilsin mi?");
        } else {
            // Varsayılan ENGLISH (Diğer diller için de şimdilik bu çalışır)
            dictionary.setProperty("welcome", "Bot Shell Mode Active.");
            dictionary.setProperty("help_info", "Type 'help' for command list.");
            dictionary.setProperty("timer_active", "[TIMER ACTIVE] ");
            dictionary.setProperty("prompt", ">> ");
            dictionary.setProperty("unknown_cmd", "Unknown command: ");
            dictionary.setProperty("error_unexpected", "Unexpected error: ");
            dictionary.setProperty("exit_warn", "Active timer detected! It will be cancelled.");
            dictionary.setProperty("exit_confirm", "Do you want to exit?");
            dictionary.setProperty("goodbye", "See you later...");
            dictionary.setProperty("set_usage", "Usage: set [Group] or set --lang [code]");
            dictionary.setProperty("group_set", "Target group set: ");
            dictionary.setProperty("lang_set", "Language changed to: ");
            dictionary.setProperty("timer_cancel", "Timer cancelled.");
            dictionary.setProperty("no_timer", "No active timer found.");
            dictionary.setProperty("monitor_mode", "--- MONITOR MODE (Press ENTER to exit) ---");
            dictionary.setProperty("monitor_exit", "Monitor mode closed.");
            dictionary.setProperty("timer_started", "Timer started.");
            dictionary.setProperty("timer_confirm", "Start timer?");
            dictionary.setProperty("target", "Target: ");
            dictionary.setProperty("remaining", "Remaining: ");
            dictionary.setProperty("past_time", "Past time!");
            dictionary.setProperty("missing_args", "Missing info! Ex: -d 16-12-2025 -t 18:30");
            dictionary.setProperty("menu_sent", "Sent!");
            dictionary.setProperty("whatsapp_opening", "Opening WhatsApp...");
            dictionary.setProperty("theme_changed", "Theme changed: ");
            dictionary.setProperty("theme_invalid", "Invalid theme.");
            dictionary.setProperty("theme_select", "--- THEME SELECTION ---");
            dictionary.setProperty("enter_theme", "Enter theme number/name: ");
            dictionary.setProperty("lang_invalid", "Invalid language code.");
            dictionary.setProperty("sending_msg", "Sending message...");
            dictionary.setProperty("preview", "--- PREVIEW ---");
            dictionary.setProperty("send_confirm", "Send this?");
        }
    }

    private static String getMsg(String key) {
        // Eğer key yoksa, key'in kendisini döndür (örn: "MISSING_KEY")
        return dictionary.getProperty(key, "[!" + key + "!]");
    }

    // --- KOMUT İŞLEME ---
    private static void processCommand(String command, String arg) {
        switch (command) {
            case "help": case "h": showHelp(); break;
            case "exit": case "quit": case "q": 
                if (activeTimer != null) printWarning(getMsg("exit_warn"));
                if (getUserConfirmation(getMsg("exit_confirm"))) 
                {
                    if (activeTimer != null) activeTimer.cancel();
                    saveConfig(); 
                    System.out.println(getMsg("goodbye"));
                    System.exit(0);
                }
                break;
            case "clear": case "cls": clearScreen(); break;
            case "status": showStatus(); break;
            
            case "set": handleSetCommand(arg); break;
            case "theme": handleThemeCommand(arg); break;
            case "send": handleSend(arg); break;
            case "upload": handleUpload(arg); break;
            case "add": handleInteractiveAdd(); break;
            case "monitor": 
                if (activeTimer != null) startMonitorMode();
                else printError(getMsg("no_timer"));
                break;
            case "cancel":
                if (activeTimer != null) {
                    activeTimer.cancel(); activeTimer = null; activeTimerTarget = null;
                    printSuccess(getMsg("timer_cancel") + "\n");
                } else printWarning(getMsg("no_timer") + "\n");
                break;

            default: printError(getMsg("unknown_cmd") + command + "\n"); break;
        }
    }

    // --- CONFIG ---
    private static void loadConfig() {
        currentTheme = themeMap.get("default");
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            appProps.load(in);
            String savedGroup = appProps.getProperty("target_group");
            if (savedGroup != null && !savedGroup.isEmpty()) hedefGrupAdi = savedGroup;
            
            String savedTheme = appProps.getProperty("theme");
            if (savedTheme != null && themeMap.containsKey(savedTheme)) currentTheme = themeMap.get(savedTheme);

            String savedLang = appProps.getProperty("lang");
            if (savedLang != null) currentLang = savedLang;

        } catch (IOException e) {}
    }

    private static void saveConfig() {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            if (hedefGrupAdi != null) appProps.setProperty("target_group", hedefGrupAdi);
            appProps.setProperty("lang", currentLang);
            for (Map.Entry<String, Theme> entry : themeMap.entrySet()) {
                if (entry.getValue() == currentTheme) {
                    appProps.setProperty("theme", entry.getKey());
                    break;
                }
            }
            appProps.store(out, "Whatsapp KYK Bot Config");
        } catch (IOException e) {
            printError("Config Error: " + e.getMessage());
        }
    }

    // --- KOMUT MANTIKLARI ---
    private static void handleSetCommand(String arg) {
        if (arg == null || arg.isEmpty()) { printError(getMsg("set_usage")); return; }

        // DİL DEĞİŞTİRME: set --lang tr | set --lang ja | set --lang ru
        if (arg.startsWith("--lang") || arg.startsWith("--l") || arg.startsWith("--language")) {
            String[] parts = arg.split("\\s+");
            if(parts.length < 2) { printError(getMsg("lang_invalid")); return; }
            String langCode = parts[1].toLowerCase();
            
            // Dili güncelle, dosyayı yükle (yoksa fallback'e döner)
            currentLang = langCode;
            loadLanguage(currentLang);
            saveConfig();
            printSuccess(getMsg("lang_set") + currentLang.toUpperCase());
            return;
        }

        if (arg.startsWith("-theme")) {
            String[] parts = arg.split("\\s+", 2);
            if (parts.length > 1) applyThemeSelection(parts[1], null);
            else handleThemeCommand(null);
            return;
        }

        hedefGrupAdi = arg;
        saveConfig();
        printSuccess(getMsg("group_set") + hedefGrupAdi + "\n");
    }

    private static void handleThemeCommand(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("\n" + getMsg("theme_select"));
            int i = 0;
            List<String> keys = new ArrayList<>(themeMap.keySet());
            for (String key : keys) {
                Theme t = themeMap.get(key);
                System.out.println(i + ". " + t.info + t.name + t.reset + " (" + key + ")");
                i++;
            }
            System.out.print("\n" + getMsg("enter_theme"));
            String secim = scanner.nextLine().trim();
            applyThemeSelection(secim, keys);
            return;
        }
        String themeName = arg.startsWith("-") ? arg.substring(1) : arg;
        applyThemeSelection(themeName, null);
    }

    private static void applyThemeSelection(String input, List<String> keys) {
        Theme selected = null;
        try {
            int index = Integer.parseInt(input);
            if (keys == null) keys = new ArrayList<>(themeMap.keySet());
            if (index >= 0 && index < keys.size()) selected = themeMap.get(keys.get(index));
        } catch (NumberFormatException e) {
            if (themeMap.containsKey(input.toLowerCase())) selected = themeMap.get(input.toLowerCase());
        }

        if (selected != null) {
            currentTheme = selected;
            saveConfig();
            printSuccess(getMsg("theme_changed") + selected.name);
        } else {
            printError(getMsg("theme_invalid"));
        }
    }

    private static void startMonitorMode() {
        System.out.println(currentTheme.info + "\n" + getMsg("monitor_mode") + currentTheme.reset);
        
        Thread monitorThread = new Thread(() -> {
            try {
                long totalDuration = Duration.between(LocalDateTime.now(), activeTimerTarget).toMillis();
                long startTime = System.currentTimeMillis();
                
                while (activeTimer != null) {
                    long now = System.currentTimeMillis();
                    long elapsed = now - startTime;
                    long remaining = totalDuration - elapsed;
                    if (remaining <= 0) break;

                    int barLength = 30;
                    int progress = (int) ((elapsed * 100) / totalDuration);
                    if (progress > 100) progress = 100;
                    int filledLength = (int) ((barLength * progress) / 100);

                    StringBuilder bar = new StringBuilder("[");
                    for (int i = 0; i < filledLength; i++) bar.append("#");
                    for (int i = 0; i < barLength - filledLength; i++) bar.append(" ");
                    bar.append("]");

                    System.out.print("\r" + currentTheme.bar + bar + " %" + progress + " | " + getMsg("remaining") + formatDuration(remaining) + currentTheme.reset + "   ");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {}
        });

        monitorThread.start();
        scanner.nextLine();
        monitorThread.interrupt();
        System.out.print("\r" + " ".repeat(80) + "\r");
        System.out.println(currentTheme.info + "[INFO] " + getMsg("monitor_exit") + currentTheme.reset);
    }

    private static void setupSchedule(String arg) {
        try {
            String dStr = extractArgValue(arg, "-date", "-d");
            String tStr = extractArgValue(arg, "-time", "-t"); if(tStr==null) tStr = extractArgValue(arg, "-hour", "-h");
            
            if (dStr == null || tStr == null) { printError(getMsg("missing_args")); return; }
            
            LocalDate datePart = LocalDate.parse(dStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalTime timePart = LocalTime.parse(tStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime target = LocalDateTime.of(datePart, timePart);
            
            long delay = Duration.between(LocalDateTime.now(), target).toMillis();
            if (delay <= 0) { printError(getMsg("past_time")); return; }

            System.out.println(getMsg("target") + target.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + 
                               " | " + getMsg("remaining") + formatDuration(delay));
            
            if (getUserConfirmation(getMsg("timer_confirm"))) {
                activeTimerTarget = target;
                activeTimerDateStr = dStr;
                activeTimer = new Timer();
                activeTimer.schedule(new TimerTask() {
                    public void run() {
                        System.out.print("\r" + " ".repeat(80) + "\r");
                        System.out.println("\n[TIMER] " + getMsg("sending_msg"));
                        try { processMessageSending(activeTimerDateStr, true); } 
                        catch (Exception e) { printError(getMsg("error_unexpected") + e.getMessage()); }
                        activeTimer = null; activeTimerTarget = null;
                        System.out.print("\n" + getMsg("prompt"));
                    }
                }, delay);
                printSuccess(getMsg("timer_started"));
                startMonitorMode();
            }
        } catch (Exception e) { printError("Format error: " + e.getMessage()); }
    }

    // --- YARDIMCI METODLAR ---
    private static void handleInteractiveAdd() { System.out.println("Menu wizard..."); } // Önceki kod ile aynı
    private static void handleUpload(String filePath) { /* Önceki kod ile aynı */ }

    private static void handleSend(String arg) {
        if (arg == null) { printError("Usage: send --now OR send --schedule ..."); return; }
        if (hedefGrupAdi == null) {
            System.out.print("\n" + getMsg("prompt") + "Group Name: ");
            hedefGrupAdi = scanner.nextLine().trim();
            if (hedefGrupAdi.isEmpty()) return;
            saveConfig();
        }
        if (arg.equals("--now")) {
            String bugun = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            processMessageSending(bugun, false);
            return;
        }
        if (arg.startsWith("--schedule")) {
            if (activeTimer != null) { printError(getMsg("exit_warn")); return; }
            setupSchedule(arg);
            return;
        }
        printError(getMsg("unknown_cmd"));
    }

    private static void processMessageSending(String date, boolean automated) {
        List<String> data = csvReader.getMenuByDate(date);
        if (data == null || data.isEmpty()) { printError(date + " menu not found!"); return; }
        String msg = formatMenuClean(date, data);
        if (!automated) {
            System.out.println("\n" + getMsg("preview") + "\n" + msg + "\n----------------");
            if (!getUserConfirmation(getMsg("send_confirm"))) return;
        }
        try {
            System.out.println(currentTheme.info + "[INFO] " + getMsg("whatsapp_opening") + currentTheme.reset);
            new WhatsappService().sendMessage(hedefGrupAdi, msg, false);
            printSuccess(getMsg("menu_sent"));
        } catch (Exception e) { printError(getMsg("error_unexpected") + e.getMessage()); }
    }

    private static String extractArgValue(String input, String l, String s) {
        String[] t = input.split("\\s+");
        for(int i=0; i<t.length-1; i++) if(t[i].equalsIgnoreCase(l) || t[i].equalsIgnoreCase(s)) return t[i+1];
        return null;
    }

    private static String formatMenuClean(String tarih, List<String> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("*KYK Menü* - " + tarih + "\n\n*Kahvaltı*\n");
        for(int i=0; i<8 && i<data.size(); i++) if(!data.get(i).trim().isEmpty()) sb.append("- ").append(data.get(i)).append("\n");
        sb.append("\n\n*Akşam*\n");
        for(int i=8; i<data.size(); i++) if(!data.get(i).trim().isEmpty()) sb.append("- ").append(data.get(i)).append("\n");
        sb.append("\n\n---\nGitHub: SametCirik/WhatsApp-KYK-Bot");
        return sb.toString();
    }

    private static boolean getUserConfirmation(String q) {
        System.out.print(q + " [Y/n]: "); 
        String r = scanner.nextLine().trim().toLowerCase();
        return r.isEmpty() || r.equals("e") || r.equals("y") || r.equals("yes") || r.equals("evet");
    }

    private static String formatDuration(long m) { 
        long h = m / 3600000; long min = (m % 3600000) / 60000; long s = (m % 60000) / 1000;
        return String.format("%02d:%02d:%02d", h, min, s);
    }
    
    private static void printPrompt() { System.out.print("\n" + currentTheme.prompt + getMsg("prompt") + currentTheme.reset); }
    private static void printSuccess(String m) { System.out.println(currentTheme.success + "[OK] " + m + currentTheme.reset); }
    private static void printError(String m) { System.out.println(currentTheme.error + "[ERROR] " + m + currentTheme.reset); }
    private static void printWarning(String m) { System.out.println(currentTheme.info + "[!] " + m + currentTheme.reset); }
    
    private static void showHelp() {
        System.out.println("\n--- HELP / YARDIM ---");
        System.out.println("  theme             : Change theme.");
        System.out.println("  set --lang [code] : Change language (tr, en, ja, zh, de, ru, fr, pt).");
        System.out.println("  set [Group]       : Set target group.");
        System.out.println("  send --schedule   : Schedule message.");
        System.out.println("  monitor           : Show progress bar.");
        System.out.println("  exit              : Save & Exit.\n");
    }

    private static void showStatus() {
        System.out.println("Group: " + (hedefGrupAdi!=null?hedefGrupAdi:"-") + " | Date: " + LocalDate.now());
        System.out.println("Theme: " + currentTheme.name + " | Lang: " + currentLang.toUpperCase());
        System.out.println("Timer: " + (activeTimer!=null?"ACTIVE":"None") + "\n\n");
    }

    private static void clearScreen() {
        try { 
            if(System.getProperty("os.name").contains("Windows")) new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor(); 
            else { System.out.print("\033[H\033[2J"); System.out.flush(); }
        } catch(Exception e){}
    }
}