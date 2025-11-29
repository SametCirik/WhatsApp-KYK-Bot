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

# KYK Food Menu Bot - WhatsApp Automation

This is a personal desktop application developed entirely using the **Java** programming language to manage food menus for **KYK (Kredi ve Yurtlar Kurumu)** dormitories and automatically send them via WhatsApp on a specified date.

This project focuses on creating a functional Graphical User Interface (GUI) from scratch, providing event handling, and implementing browser automation logic with **Java Selenium** to create a reliable bot experience.

---

## Features

* **Menu Management Interface:** A desktop interface created using **Java Swing**, allowing users to enter, edit, and save food lists by date as a `.csv` file.
* **CSV Integration:** Automatically populating the interface fields by reading saved menus from a `.csv` file.
* **WhatsApp Automation:** Automatically connecting to WhatsApp Web using **Selenium WebDriver**, finding the specified contact or group, and sending the formatted menu message.
* **Scheduled Sending:** A timer feature that enables messages to be sent instantly or scheduled for automatic sending at a future date and time.
* **Persistent Session:** A persistent WhatsApp Web session that eliminates the need to scan a QR code every time by leveraging Chrome's user profile feature.

---

## Project Status and Planned Features (v1.0)

**Current Status:** The project's main functions (interface, CSV operations, and WhatsApp automation) are complete. `v1.0` version is stable and fully functional "for now", I do not recommend using `v1.1` version.

- [Download v1.0 Pre-Alpha Release](https://github.com/SametCirik/WhatsApp-KYK-Bot/releases/tag/v1.0)
- [Download v1.1 Pre-Alpha Release](https://github.com/SametCirik/WhatsApp-KYK-Bot/releases/tag/v1.1)

### Next Steps (Future Plans)

* **Advanced Error Handling:** Making the bot more resilient to future changes in the WhatsApp interface.
* **Settings Menu:** Allowing settings, such as the Chrome profile path, to be changed via the interface.
* **Multiple Recipients:** A feature to create a recipient list for sending the same message to multiple groups or contacts.
* **Activity Log:** Displaying the bot's steps (e.g., logged in, message sent) in a log area on the interface.

---

## Environment and Technologies

* **Language:** Java
* **Libraries:**
    * **Java Swing/AWT:** For the Graphical User Interface (GUI).
    * **Java Selenium:** For web browser automation.
* **Development Environment (IDE):** Eclipse IDE / Visual Studio Code

---

## Contributing

As this is a personal project, external contributions are currently closed.

However, if you are interested in the project or its codebase, feel free to follow future developments. I would be happy to collaborate on matters such as bug reporting or feature development.

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

## Developer *(-s)*

This project was developed by **[Samet Cırık](https://github.com/SametCirik)**.
