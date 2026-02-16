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

# WhatsApp KYK Bot (v2.0 - Linux Edition)

**WhatsApp KYK Bot** is a powerful automation tool developed for scheduling and automatically sharing weekly meal menus from KYK (Credit and Dormitories Institution) dormitories via WhatsApp.

**With the v2.0 update:** It now offers full compatibility with **Linux terminals**, **Multi-language support**, a **Theme Engine**, and an improved **CLI (Command Line)** experience.

---

## New Features (v2.0)

### Multi-language Support
The bot is now global! You can change the interface language with a single command.

* **Supported Languages:**

    * 🇹🇷 TR,
    * 🇺🇸 EN,
    * 🇯🇵 JA,
    * 🇩🇪 DE,
    * 🇷🇺 RU,
    * 🇫🇷 FR,
    * 🇵🇹 PT,
    * 🇨🇳 ZH.

* **Command:** `set --lang [code]` (e.g.: `set --lang tr`)

### Dynamic Theme Engine

Your terminal doesn't have to be boring. Choose a theme that suits your mood.

* **Themes:** `Default`, `Matrix` (Hacker Green), `Cyberpunk` (Neon), `Dracula` (Dark Mode), `Ocean` (Blue).
* **Command:** `theme [name]` (e.g., `theme matrix`)

### Monitor Mode & Progress Bar
When you set the timer, a live **Progress Bar** and countdown timer will appear in the terminal.

* **Feature:** It doesn't block the terminal while running in the background. You can monitor the status live at any time by typing `monitor`.

### Smart Configuration

* **Persistent Settings:** Remembers your language, theme, and target group preferences and saves them to the `config.properties` file. * **Linux Script:** One-click startup with `run_bot.sh`.

---

## Installation

### Requirements

* **Java JDK 21+**

* **Google Chrome** (Browser must be installed)

* **Maven** (For compilation)

### Step-by-Step Installation (Linux/macOS)

1. **Clone the Repository:**

   ```bash
   git clone [https://github.com/SametCirik/WhatsApp-KYK-Bot.git](https://github.com/SametCirik/WhatsApp-KYK-Bot.git)
   cd WhatsApp-KYK-Bot
   ```

2. Compile the Project (Build)

   ```bash
   # Simply return to the main directory and run the script; if the script doesn't exist, compile it manually.
   cd Bot/BotGUI
   mvn clean package
   cp target/whatsapp-kyk-bot-1.0-SNAPSHOT.jar ../../Whatsapp-KYK-Bot.jar
   cd ../../
   ```

3. Grant and Start the Run:

   ```bash
   chmod +x run_bot.sh
   ./run_bot.sh
   ```

---

When the bot starts, you enter an interactive **Shell** environment. Here are the basic commands:

Command | Description | Example Usage
--- | --- | ---
set | Sets the target WhatsApp group. | set Food Group
set --lang | Changes the interface language. | set --lang tr
theme | Opens or changes the theme list. | theme cyberpunk
send --now | Sends the menu instantly. | send --now
send --schedule | Sets a future timer. | send --schedule -d 17-02-2026 -t 07:00
monitor | Shows the active timer and progress bar.,monitor
cancel | Cancels the active timer. | cancel
status | Shows the current settings and status. | status

---

---

## Project Structure

```bash
WhatsApp-KYK-Bot/
├── run_bot.sh            # Linux Startup Script
├── Whatsapp-KYK-Bot.jar  # Compiled Application
├── config.properties     # Configuration File (Automatically generated)
├── lang/                 # Language Files
│ ├── lang_tr.properties
│ ├── lang_en.properties
│ └── ...
├── data/
│ └── menu_listesi.csv    # Menu Database
└── src/                  # Source Codes
```

---

## Legal Notice

This project is developed for educational purposes. **Please comply with WhatsApp's Terms of Service.** Users are responsible for any misuse (spam, etc.).

---

## Developers *(-s)*

Developed by **[Samet Cırık](https://github.com/SametCirik)**

---

---

<p align="center">
  <img width="777" height="1047" alt="image" src="https://github.com/user-attachments/assets/c4aba39f-db57-45f8-8280-9f61d5d0a7a9" />
</p>
